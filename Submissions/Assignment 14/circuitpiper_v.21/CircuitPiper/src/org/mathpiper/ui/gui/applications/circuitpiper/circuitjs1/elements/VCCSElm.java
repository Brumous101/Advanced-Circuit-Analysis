/*    
    Copyright (C) Paul Falstad
    
    This file is part of CircuitJS1.

    CircuitJS1 is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    CircuitJS1 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CircuitJS1.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements;

import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.CustomLogicModel;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.EditInfo;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Expr;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.ExprParser;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.ExprState;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Graphics;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ControlledSource;

public class VCCSElm extends ChipElm {

    //double gain;
    Expr expr;
    ExprState exprState;
    public String exprString = ".1*(a-b)";;
    public boolean broken;
    
    Integer inputCount = 2;
    
    public WireElm wireControl0;
    public WireElm wireControl1;

    public VCCSElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) throws Exception {
        super(xa, ya, xb, yb, f, st);
        inputCount = Integer.parseInt(st.nextToken());
        allocNodes();
        exprString = CustomLogicModel.unescape(st.nextToken());
        parseExpr();
        setupPins();
    }

    public VCCSElm(int xx, int yy) throws Exception {
        super(xx, yy);
        inputCount = 2;
        allocNodes();
        parseExpr();
        setupPins();
    }

    public String dump() {
        return super.dump() + " " + inputCount + " " + CustomLogicModel.escape(exprString);
    }

    double lastnodeVoltages[];

    public void setupPins() {
        sizeX = 2;
        sizeY = inputCount > 2 ? inputCount : 2;
        pins = new Pin[inputCount + 2];
        int i;
        for (i = 0; i != inputCount; i++) {
            pins[i] = new Pin(i, SIDE_WEST, Character.toString((char) ('A' + i)));
        }
        pins[inputCount] = new Pin(0, SIDE_EAST, "C+");
        pins[inputCount + 1] = new Pin(1, SIDE_EAST, "C-");
        lastnodeVoltages = new double[inputCount];
        exprState = new ExprState(inputCount);
    }

    String getChipName() {
        return "VCCS~";
    } // ~ is for localization 

    public boolean nonLinear() {
        return true;
    }

    public void stamp() {
        sim.stampNonLinear(nodes[inputCount]);
        sim.stampNonLinear(nodes[inputCount + 1]);
    }
    double lastvd;

    double sign(double a, double b) {
        return a > 0 ? b : -b;
    }

    double getLimitStep() {
        // get limit on changes in voltage per step.  be more lenient the more iterations we do
        if (sim.subIterations < 4) {
            return 10;
        }
        if (sim.subIterations < 10) {
            return 1;
        }
        if (sim.subIterations < 20) {
            return .1;
        }
        if (sim.subIterations < 40) {
            return .01;
        }
        return .001;
    }

    double getConvergeLimit() {
        // get maximum change in voltage per step when testing for convergence.  be more lenient over time
        if (sim.subIterations < 10) {
            return .001;
        }
        if (sim.subIterations < 200) {
            return .01;
        }
        return .1;
    }

    public boolean hasCurrentOutput() {
        return true;
    }

    public int getOutputNode(int n) {
        return nodes[n + inputCount];
    }

    public void doStep() {
        int i;

        // no current path?  give up
        if (broken) {
            pins[inputCount].current = 0;
            pins[inputCount + 1].current = 0;
            // avoid singular matrix errors
            sim.stampResistor(nodes[inputCount], nodes[inputCount + 1], 1e8);
            return;
        }

        // converged yet?
        double limitStep = getLimitStep();
        double convergeLimit = getConvergeLimit();
        for (i = 0; i != inputCount; i++) {
            if (Math.abs(nodeVoltages[i] - lastnodeVoltages[i]) > convergeLimit) {
                sim.converged = false;
            }
            if (Double.isNaN(nodeVoltages[i])) {
                nodeVoltages[i] = 0;
            }
            if (Math.abs(nodeVoltages[i] - lastnodeVoltages[i]) > limitStep) {
                nodeVoltages[i] = lastnodeVoltages[i] + sign(nodeVoltages[i] - lastnodeVoltages[i], limitStep);
            }
        }
        if (expr != null) {
            // calculate output
            for (i = 0; i != inputCount; i++) {
                exprState.values[i] = nodeVoltages[i];
            }
            exprState.simulationTime = sim.simulationTime;
            double v0 = -expr.eval(exprState);
//        	if (Math.abs(nodeVoltages[inputCount]-v0) > Math.abs(v0)*.01 && sim.subIterations < 100)
//        	    sim.converged = false;
            double rs = v0;

            // calculate and stamp output derivatives
            for (i = 0; i != inputCount; i++) {
                double dv = 1e-6;
                exprState.values[i] = nodeVoltages[i] + dv;
                double v = -expr.eval(exprState);
                exprState.values[i] = nodeVoltages[i] - dv;
                double v2 = -expr.eval(exprState);
                double dx = (v - v2) / (dv * 2);
                if (Math.abs(dx) < 1e-6) {
                    dx = sign(dx, 1e-6);
                }
                sim.stampVCCurrentSource(nodes[inputCount], nodes[inputCount + 1], nodes[i], 0, dx);
//            	sim.console("ccedx " + i + " " + dx);
                // adjust right side
                rs -= dx * nodeVoltages[i];
                exprState.values[i] = nodeVoltages[i];
            }
//        	sim.console("ccers " + rs);
            sim.stampCurrentSource(nodes[inputCount], nodes[inputCount + 1], rs);
            pins[inputCount].current = -v0;
            pins[inputCount + 1].current = v0;
        }

        for (i = 0; i != inputCount; i++) {
            lastnodeVoltages[i] = nodeVoltages[i];
        }
    }

    public void draw(Graphics g) {
        drawChip(g);
    }

    public int getPostCount() {
        return inputCount == null ? 4 : inputCount + 2;
    }

    public int getVoltageSourceCount() {
        return 0;
    }

    int getDumpType() {
        return 213;
    }

    public boolean getConnection(int n1, int n2) {
        return comparePair(inputCount, inputCount + 1, n1, n2);
    }

    public boolean hasGroundConnection(int n1) {
        return false;
    }

    @Override
    public EditInfo getEditInfo(int n) {
        if (n == 0) {
            EditInfo ei = new EditInfo("<a href=\"customfunction.html\" target=\"_blank\">Output Function</a>", 0, -1, -1);
            ei.text = exprString;
            return ei;
        }
        if (n == 1) {
            return new EditInfo("# of Inputs", inputCount, 1, 8).
                    setDimensionless();
        }
        return null;
    }

    public void setEditValue(int n, EditInfo ei) throws Exception {
        if (n == 0) {
            exprString = ei.textf.getText();
            parseExpr();
            return;
        }
        if (n == 1) {
            if (ei.value < 0 || ei.value > 8) {
                return;
            }
            inputCount = (int) ei.value;
            setupPins();
            allocNodes();
            setPoints();
        }
    }

    public void setExpr(String expr) throws Exception {
        exprString = expr;
        parseExpr();
    }

    void parseExpr() throws Exception {
        ExprParser parser = new ExprParser(exprString);
        expr = parser.parseExpression();
        
        if(parser.gotError())
        {
            throw new Exception("Parsing error.");
        }
    }
    
    public double getVoltageDiff() {
        return nodeVoltages[2] - nodeVoltages[3];
    }
    
    public double getCurrent() {
        return pins[inputCount].current;
    }
    
    double getPower() {
        return getVoltageDiff() * getCurrent();
    }

    public void getInfo(String arr[]) {
        super.getInfo(arr);
        int i;
        for (i = 0; arr[i] != null; i++) ;
        arr[i] = "I = " + getCurrentText(pins[inputCount].current);
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append(super.toString());
        sb.append("\n\n");
        
        sb.append("Expression: " + expr);
        sb.append("\n");
        sb.append("Expression State: " + exprState);
        sb.append("\n");
        sb.append("Expression String: " + exprString);
        sb.append("\n");
        sb.append("Wire Control 0: " + wireControl0);
        sb.append("\n");
        sb.append("Wire Control 1: " + wireControl1);
        sb.append("\n");
        sb.append("Controllng Component: " + ((ControlledSource) this.component).getControllingComponentLabel());
        
        return sb.toString();
    }
}
