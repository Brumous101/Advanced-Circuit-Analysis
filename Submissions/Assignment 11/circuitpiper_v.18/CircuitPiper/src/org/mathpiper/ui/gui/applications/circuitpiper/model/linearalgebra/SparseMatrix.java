package org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.mathpiper.ui.gui.applications.circuitpiper.model.Terminal;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ACCurrentSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ACVoltageSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Ammeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.CapacitanceMeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Capacitor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.CurrentIntegrator;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.CurrentSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ControlledSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.DCVoltageSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.VCCS;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.VCVS;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.InductanceMeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Inductor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Ohmmeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Resistor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Switch;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.VoltageIntegrator;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Voltmeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Wire;
import org.mathpiper.ui.gui.applications.circuitpiper.view.CircuitPanel;
import org.mathpiper.ui.gui.applications.circuitpiper.view.PrintVertical;

/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */
public final class SparseMatrix {

    public ArrayList<Vector> rows;
    public ArrayList<Vector> columns;
    public int firstTerminalVoltageIndex;
    public int horizontalLineIndex;

    private Boolean matrixError;
    private CircuitPanel myParent;
    private int numberOfKCLeqs;
    private static final String LAST = "-1";

    public SparseMatrix(CircuitPanel parent, int numRows, int numCols) {
        myParent = parent;
        rows = new ArrayList<Vector>(0);

        columns = new ArrayList<Vector>(0);

    }

    public Vector get(int row) {
        return rows.get(row);//[row];   
    }

    public void putElement(int row, int col, double x) {
        rows.get(row).put(col, x);
        columns.get(col).put(row, x);
    }

    public void swapRows(int row1, int row2) {
        
        for (Object obj : rows.get(row1).contents.keySet()) {//for each element in row1
            Integer colNum = (Integer) obj;
            if (rows.get(row2).contents.containsKey(colNum)) {//if row2 has an element in the same position
                double temp = columns.get(colNum).get(row1);//temp gets element in row1

                columns.get(colNum).put(row1, columns.get(colNum).get(row2));//row1 gets row2
                columns.get(colNum).put(row2, temp);//row2 gets temp
            } else {//if row2 doesn't have an element in the same position
                columns.get(colNum).put(row2, columns.get(colNum).get(row1));//row2 gets row1
                columns.get(colNum).remove(row1);//remove element in row1
            }
        }
        
        for (Object obj : rows.get(row2).contents.keySet()) {//for each element in row2
            Integer colNum = (Integer) obj;
            if (!rows.get(row1).contents.containsKey(colNum)) {//if row1 doesn't have an element in the same position
                columns.get(colNum).put(row1, rows.get(row2).get(colNum));//better than line below//##added Dec 26 2008
                columns.get(colNum).remove(row2);
            }
        }
        
        Vector temp = rows.get(row1);
        rows.set(row1, rows.get(row2));
        rows.set(row2, temp);
    }

    public void swapCols(int col1, int col2) {//swap cols
        for (Object obj : columns.get(col1).contents.keySet()) {//for each element in col1
            Integer rowNum = (Integer) obj;//i is the row num
            if (columns.get(col2).contents.containsKey(rowNum)) {//if col2 has an element in the same position
                double temp = rows.get(rowNum).get(col1);
                rows.get(rowNum).put(col1, rows.get(rowNum).get(col2));
                rows.get(rowNum).put(col2, temp);
            } else {//if col2 doesn't have an element in the same position
                rows.get(rowNum).put(col2, rows.get(rowNum).get(col1));
                rows.get(rowNum).remove(col1);
            }
        }
        for (Object obj : columns.get(col2).contents.keySet()) {//for each element in col2
            Integer rowNum = (Integer) obj;
            if (!columns.get(col1).contents.containsKey(rowNum)) {//if this isn't in col1
                rows.get(rowNum).put(col1, columns.get(col2).get(rowNum));
                rows.get(rowNum).remove(col2);
            }
        }
        Vector temp = columns.get(col1);
        columns.set(col1, columns.get(col2));
        //rows.get(row1)=rows.get(row2);
        columns.set(col2, temp);
        for (String obj : this.myParent.currentCircuit.getColumnNumbers().keySet()) {
            if (this.myParent.currentCircuit.getColumnNumbers().get(obj) == col1) {
                for (String obj2 : this.myParent.currentCircuit.getColumnNumbers().keySet()) {
                    if (this.myParent.currentCircuit.getColumnNumbers().get(obj2) == col2) {
                        this.myParent.currentCircuit.putNumbers(obj, col2);
                        this.myParent.currentCircuit.putNumbers(obj2, col1);
                        return;
                    }
                }
            }
        }
    }

    public double getMatrixElement(int row, int col) {
        if (rows.get(row).get(col) != columns.get(col).get(row)) {
            //System.out.println("sparse matrix error");
        }
        return rows.get(row).get(col);
    }

    public boolean rowHasVoltagePoint(int rownum) {
        //does this row include an entry for the voltage of a point
        for (Object obj : this.rows.get(rownum).contents.keySet()) {
            Integer column = (Integer) obj;
            if (column >= firstTerminalVoltageIndex && column < getLastColumnNumber()) {
                return true;
            }
        }
        return false;
    }
    
    
    private int getHeadTerminalColumnNumber(Component ec, String tag)
    {
        return this.myParent.currentCircuit.getColumnNumbers().get(ec.getHeadTerminal().getPosition() + tag);
    }
    
    private int getTailTerminalColumnNumber(Component ec, String tag)
    {
        return this.myParent.currentCircuit.getColumnNumbers().get(ec.getTailTerminal().getPosition() + tag);
    }
    
    private int getComponentColumnNumber(Component ec, String tag)
    {
        return this.myParent.currentCircuit.getColumnNumbers().get(ec.getID() + tag);
    }
    
    private int getLastColumnNumber()
    {
        return this.myParent.currentCircuit.getColumnNumbers().get(LAST);
    }
    
    
    private Double search(Component ec, String tag) {
        
        // Get single v point.
        Set columnsSet;
        
        if(columns == null || this.myParent.currentCircuit.getColumnNumbers().get(ec.getID() + tag) == null)
        {
            return Double.NaN;
        }
        
        Vector vector = columns.get(this.myParent.currentCircuit.getColumnNumbers().get(ec.getID() + tag));

        columnsSet = vector.contents.keySet();

        
        for (Object obj : columnsSet) {
            Integer columnIndex = (Integer) obj;
            if (this.getMatrixElement(columnIndex, this.myParent.currentCircuit.getColumnNumbers().get(ec.getID() + tag)) == 1.0) {

                if (rows.get(columnIndex).contents.size() == 1
                        || (rows.get(columnIndex).contents.size() == 2 && this.getMatrixElement(columnIndex, getLastColumnNumber()) != 0.0)
                        || (rows.get(columnIndex).contents.size() == 2 && rowHasVoltagePoint(columnIndex))
                        || (rows.get(columnIndex).contents.size() == 3 && rowHasVoltagePoint(columnIndex)
                        && this.getMatrixElement(columnIndex, getLastColumnNumber()) != 0.0)) {

                    // todo:tk:is this code for debugging?
                    if ((rows.get(columnIndex).contents.size() == 2 && rowHasVoltagePoint(columnIndex))
                            || (rows.get(columnIndex).contents.size() == 3 && rowHasVoltagePoint(columnIndex)
                            && this.getMatrixElement(columnIndex, getLastColumnNumber()) != 0.0)) {

                        this.printMatrixRow(this, columnIndex);
                    }
                    
                    return this.getMatrixElement(columnIndex, getLastColumnNumber());
                }
            }
        }
        
        return null;
    }

    public double getDeltaQ(Component ec) {
        Double result = search(ec, "dQ");
        
        if(result != null)
        {
            return result;
        }
        else
        {
            return 0;
        }
    }

    public double getDeltaFlux(Component ec) {
        Double result = search(ec, "SdVdt");
        
        if(result != null)
        {
            return result;
        }
        else
        {
            return 0;
        }
    }

    public double getDeltaCurrent(Component ec) {
        Double result = search(ec, "deltaI");
        
        if(result != null)
        {
            return result;
        }
        else
        {
            return 0;
        }
    }

    public Double getCurrent(Component ec) {
        
        Double result = search(ec, "I");
        
        if(result != null)
        {
            return result;
        }

        // Get current error.
        for (Object obj : columns.get(this.myParent.currentCircuit.getColumnNumbers().get(ec.getID() + "I")).contents.keySet()) {
            Integer i = (Integer) obj;
            if (this.getMatrixElement(i, this.myParent.currentCircuit.getColumnNumbers().get(ec.getID() + "I")) == 1.0) {
                this.printMatrixRow(this, i);
            }
        }
        
        ec.setColor(Color.RED);
        
        return 0.0;
    }

    public double getCurrentPrime(Component ec) {
                
        Double result = search(ec, "I'");
        
        if(result != null)
        {
            return result;
        }

        ec.setColor(Color.RED);
        
        return 0.0;
    }

    
    
    public String getCurrentString(Component ec, boolean simple) {
        Double result = search(ec, "I");
        
        if(result != null)
        {
            if (simple) {
                return Component.formatValue(result) + "A";
            } else {
                return result + "A";
            }
        }
        else
        {
            return "??? A";
        }        
        
    }

    public double getComponentDeltaV(Component ec) {
        Double result = search(ec, "dV");
        
        if(result != null)
        {
            return result;
        }
        else
        {
            return 0;
        }
    }

    //for voltmeters
    public String getVoltageDeltaVString(Component electricComponent, boolean simple) {
        Double result = search(electricComponent, "dV");
        
        if(result != null)
        {
            if (simple) {
                return Component.formatValue(result) + "V";
            } else {
                return result + "V";
            }
        }
        else
        {
            // Get voltage error.
            for (Object obj : columns.get(this.myParent.currentCircuit.getColumnNumbers().get(electricComponent.getID() + "dV")).contents.keySet()) {
                Integer i = (Integer) obj;
                if (this.getMatrixElement(i, this.myParent.currentCircuit.getColumnNumbers().get(electricComponent.getID() + "dV")) == 1.0) {
                    //this.printMatrixRow(this, i);
                }
            }

            return "??? V";
        } 
    }



    public String getResistanceDeltaVString(Component ec, boolean simple) {
       
        Double result = search(ec, "dV");
        
        if(result != null)
        {
            if (simple) {
                return Component.formatValue(result) + Resistor.ohm;
            } else {
                return result + Resistor.ohm;
            }
        }
        else
        {
            return "??? " + Resistor.ohm;
        }
    }

    public double getCapacitance(Component ec) {

        Double result = search(ec, "I");
        
        if(result != null)
        {
            return result;
        }
        else
        {
            return 0;
        }
    }

    public String getCapacitanceString(Component ec, boolean simple) {

        Double result = search(ec, "I");
        
        if(result != null)
        {
            if (simple) {
                return Component.formatValue(result) + "F";
            } else {
                return result + "F";
            }
        }
        else
        {
            return "??? " + "F";
        }
    }



    public double getInductanceDeltaV(Component ec) {
        Double result = search(ec, "dV");
        
        if(result != null)
        {
            return result;
        }
        else
        {
            return 0;
        }
    }

    public String getInductanceDeltaVString(Component ec, boolean simple) {
    
        Double result = search(ec, "dV");
        
        if(result != null)
        {
            if (simple) {
                return Component.formatValue(result) + "H";
            } else {
                return result + "H";
            }
        }
        else
        {
            return "??? " + "H";
        }        
    }

// ============================

    public boolean isCurrentZero(Component ec) {
        if (ec instanceof Voltmeter
                || ec instanceof VoltageIntegrator
                || (ec instanceof Switch
                && ((Switch) ec).isOpen)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDeltaQZero(Component ec) {
        return isCurrentPrimeZero(ec) || ec instanceof Resistor
                || ec instanceof Inductor || ec instanceof CapacitanceMeter
                || ec instanceof ACCurrentSource;
    }

    public boolean isCurrentPrimeZero(Component ec) {
        return isCurrentZero(ec) || ec instanceof CurrentSource
                || ec instanceof Ohmmeter;
    }

    public boolean isDeltaCurrentZero(Component ec) {//not used, only inductors have deltaI
        return isCurrentZero(ec) || ec instanceof CurrentSource || ec instanceof Ohmmeter;
    }
    
// ============================
    
    
    public void makePivotOne(int row, int col) {
        Set s = new HashSet(rows.get(row).contents.keySet());
        for (Object obj : s) {//for each colnum used in this row
            Integer colNum = (Integer) obj;//was called i before Dec 25 2008
            if (colNum >= col && colNum != this.columns.size() - 1) {//don't care about stuff to left of pivot, it should have already been zeroed out by zerobelow//was colNum<col+1//##changed on Dec 25 2008//##changed to < on Dec 26 20
                if (colNum > col && colNum != this.columns.size() - 1) {
                    //System.out.println("make pivot one misused");
                    this.printMatrixRow(this, row);
                }
                continue;
            }

            this.putElement(row, colNum, rows.get(row).get(colNum) / rows.get(row).get(col));//divide each element by pivot
        }
        this.putElement(row, col, 1.0);//put exactly 1 in pivot position
    }

    public void makePivotPositive(int row, int col) {
        Set s = new HashSet(rows.get(row).contents.keySet());
        if (this.getMatrixElement(row, col) < 0) {
            for (Object obj : s) {//for each colnum used in this row
                Integer colNum = (Integer) obj;
                if (colNum < col) {//don't care about stuff to left of pivot, it should have already been zeroed out by zerobelow//was colNum<col+1//##changed on Dec 25 2008//##changed to < on Dec 26 20
                    if (colNum < col) {
                        //System.out.println("make pivot positive misused");
                        this.printMatrixRow(this, row);
                    }
                    continue;
                }

                this.putElement(row, colNum, -rows.get(row).get(colNum));//divide each element by pivot
            }
        }
    }

    public void zeroAbove(int row, int col) {
        Set s = new HashSet(columns.get(col).contents.keySet());
        //makePivotPositive(row,col);
        for (Object obj : s) {//for each row
            Integer rowNum = (Integer) obj;//i is rownum
            if (rowNum >= row) {
                continue;
            }
            Set s2 = new HashSet(rows.get(row).contents.keySet());//for each entry in this row
            for (Object obj2 : s2) {
                Integer colNum = (Integer) obj2;//i2 is colnum
                if (colNum == col) {
                    continue;//##removed on Dec 26 2008
                }
                this.putElement(rowNum, colNum, rows.get(rowNum).get(colNum) * rows.get(row).get(col) - rows.get(row).get(colNum) * rows.get(rowNum).get(col));
            }
            this.putElement(rowNum, col, 0.0);
        }
    }

    public void zeroBelow(int row, int col) {//,int numberOfKCLeqs){
        Set s = new HashSet(columns.get(col).contents.keySet());
        //makePivotPositive(row,col);
        for (Object obj : s) {//for each row
            Integer rowNum = (Integer) obj;//i is rownum
            if (rowNum <= row) { //||rowNum>=numberOfKCLeqs){
                continue;
            }//code below only works if pivot is 1.  we need to iterate over both rows
            Set s2 = new HashSet(rows.get(row).contents.keySet());//for each entry in this row
            for (Object obj2 : s2) {
                Integer colNum = (Integer) obj2;//i2 is colnum of entry in orw
                if (colNum == col) {
                    continue;//##removed on Dec 26 2008
                }
                this.putElement(rowNum, colNum, rows.get(rowNum).get(colNum) * rows.get(row).get(col) - rows.get(row).get(colNum) * rows.get(rowNum).get(col));
            }
            this.putElement(rowNum, col, 0.0);
        }
    }

    
// ============================
    
    //Sets up instantaneous charge movement equations (Kirchoff's current law).
    public int setUpInstantaneousChargeMovementEquations(int rowNum) {
        Iterator terminals = myParent.currentCircuit.terminals.values().iterator();

        while (terminals.hasNext()) {
            Terminal terminal = (Terminal) terminals.next();
            Iterator componentsIn = terminal.in.iterator();

            while (componentsIn.hasNext()) {
                Component electricComponentIn = (Component) componentsIn.next();
                if (!isDeltaQZero(electricComponentIn)) {
                    this.putElement(rowNum, this.myParent.currentCircuit.getColumnNumbers().get(electricComponentIn.getID() + "dQ"), -1);
                }
            }
            
            if (myParent.draggedTerminal != null
                    && myParent.draggedTerminal.getPosition().equals(terminal.getPosition())
                    && myParent.draggedTerminal != terminal) {
                //System.out.println("draggedtiepoint"); // todo:tk:is this code used?
                for (Component ec : myParent.draggedTerminal.in) {
                    if (!isDeltaQZero(ec)) {
                        this.putElement(rowNum, getComponentColumnNumber(ec, "dQ"), -1);
                    }
                }
                for (Component ec : myParent.draggedTerminal.out) {
                    if (!isDeltaQZero(ec)) {
                        this.putElement(rowNum, getComponentColumnNumber(ec, "dQ"), 1);
                    }
                }
            }
            
            Iterator componentsOutIterator = terminal.out.iterator();
            while (componentsOutIterator.hasNext()) {
                Component electricComponentOut = (Component) componentsOutIterator.next();
                if (!isDeltaQZero(electricComponentOut)) {
                    this.putElement(rowNum, this.myParent.currentCircuit.getColumnNumbers().get(electricComponentOut.getID() + "dQ"), 1);
                }
            }
            rowNum++;
        }
        return rowNum;
    }

    
    
    /*
     * Used for instantaneous charge movement-eg caps discharging/charging.
     */

    public void setUpInstantaneousChargeMovementMatrix(double t) throws Exception {

        this.myParent.currentCircuit.setColumnNumbers(new HashMap<String, Integer>());
        Iterator components = myParent.currentCircuit.components.values().iterator();
        int count = 0;

        while (components.hasNext()) {

            Component ec = (Component) components.next();

            if (!isDeltaQZero(ec)) {
                columns.add(new UnknownVector(ec.getPrimarySymbol() + ec.getComponentSubscript() + " dQ"));//This component's current
                this.myParent.currentCircuit.putNumbers(ec, "dQ", count);
                count++;
            }

            if (ec instanceof Voltmeter || ec instanceof Ohmmeter) {
                columns.add(new MeterEquationVector(ec.getPrimarySymbol() + ec.getComponentSubscript() + " dV"));//The voltage difference across this component
                this.myParent.currentCircuit.putNumbers(ec, "dV", count);
                count++;//we make room for deltav
            }
        }
        
        firstTerminalVoltageIndex = count;
        
        Iterator<Point> terminalIterator;
        terminalIterator = myParent.currentCircuit.terminals.keySet().iterator();

        while (terminalIterator.hasNext()) {
            Point point = terminalIterator.next();
            rows.add(new UnknownVector("Terminal KCL equation"));
            columns.add(new UnknownVector("T" + myParent.currentCircuit.terminals.get(point).terminalNumber + " V"));
            this.myParent.currentCircuit.putNumbers(point, "V", count);
            count++;
        }
        
        this.myParent.currentCircuit.putNumbers(LAST, count);
        columns.add(new ColumnVector("TV-CI")); //Far right column of the matrix.
        components = myParent.currentCircuit.components.values().iterator();
        int rowNum = 0;
        
        rowNum = setUpInstantaneousChargeMovementEquations(rowNum);
        this.numberOfKCLeqs = rowNum;
        
        // Set up component equations.
        while (components.hasNext()) {
            
            Component ec = (Component) components.next();
            // Batteries have a voltage difference between electrodes
            if (ec instanceof DCVoltageSource && ec.getPrimaryValue() != null) {
                    rows.add(new SourceEquationVector("Voltage source equation"));
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                    this.putElement(rowNum, getLastColumnNumber(), ec.getPrimaryValue());
                    rowNum++; 
            } else if (ec instanceof ACVoltageSource && ec.getPrimaryValue() != null && ec.getFrequency() != null && ec.getPhaseShift() != null) {
                    rows.add(new SourceEquationVector("AC voltage source equation"));
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                    this.putElement(rowNum, getLastColumnNumber(), ec.getPrimaryValue()
                            * Math.sin(2 * Math.PI * ec.getFrequency() * (t + ec.getPhaseShift())));
                    rowNum++; }
            else if (ec instanceof VCVS) {
                    rows.add(new SourceEquationVector("VCVS Equation"));
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                    rowNum++;
            }
            else if (ec instanceof VCCS) {
                    rows.add(new SourceEquationVector("VCCS Equation"));
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                    rowNum++;
            } else if (ec instanceof Wire) {
                //Wires have 0 voltage difference between electrodes
                String componentName = ec.getID() + " V";
                Vector vector = new WireEquationVector(componentName);
                vector.setComponentName(componentName);
                rows.add(vector);

                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                rowNum++;
            } else if (ec instanceof CurrentIntegrator) {
                rows.add(new IntegratorEquationVector("Current integrator equation"));
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                rowNum++;
            } else if (ec instanceof Switch) {
                if (!((Switch) ec).isOpen) {
                    rows.add(new UnknownVector("Switch equation"));
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                    rowNum++;
                }
            } else if (ec instanceof Resistor) {
                //Resistors have 0 instantaneous charge movement.
            } else if (ec instanceof Inductor) {
                //Inductors have 0 instantaneous charge movement
            } else if (ec instanceof Capacitor && ec.getPrimaryValue() != null) {
                //dV across electrodes=charge/capacitance+dQ/capacitance
                    rows.add(new CapacitorEquationVector("Capacitor equation"));
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                    this.putElement(rowNum, getComponentColumnNumber(ec, "dQ"),
                            -1 / ec.getPrimaryValue());
                    this.putElement(rowNum, getLastColumnNumber(),
                            ec.getSecondaryValue() / ec.getPrimaryValue());
                    rowNum++;
            } else if (ec instanceof Voltmeter) {
                //0 instantaneous charge movement,voltage difference defined by endpoints
                rows.add(new MeterEquationVector("Voltmeter equation"));
                this.putElement(rowNum, getComponentColumnNumber(ec, "dV"), 1.0);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), -1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), 1.0);
                rowNum++;
            } else if (ec instanceof VoltageIntegrator) {
                //dQ=0 is expressed elsewhere
            } else if (ec instanceof Ohmmeter) {
                //Instantaneus charge movement in an ohmmeter is zero, voltage difference defined by endpoints. todo:tk:why is there code here then?
                rows.add(new MeterEquationVector("Ohmmeter equation"));
                this.putElement(rowNum, getComponentColumnNumber(ec, "dV"), 1.0);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), -1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), 1.0);
                rowNum++;
            } else if (ec instanceof Ammeter) {
                rows.add(new MeterEquationVector("Ammeter equation"));
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                rowNum++;
            }
        }

        if (myParent.draggedTerminal != null) {

        }
        
        horizontalLineIndex = myParent.currentCircuit.terminals.size() - 1;
    }
    

// ============================    
    
        //Sets up current equations (Kirchoff's current law).
    public int setUpKCLEquations(int rowNum) {
        Iterator terminalIterator = myParent.currentCircuit.terminals.values().iterator();
        
        while (terminalIterator.hasNext()) {
            
            Terminal terminal = (Terminal) terminalIterator.next();
            
            Iterator terminalInComponents = terminal.in.iterator();
            while (terminalInComponents.hasNext()) {
                Component ec = (Component) terminalInComponents.next();
                if (!isCurrentZero(ec)) {
                    this.putElement(rowNum, getComponentColumnNumber(ec, "I"), -1);
                }
            }
            
            if (myParent.draggedTerminal != null
                    && myParent.draggedTerminal.getPosition().equals(terminal.getPosition())
                    && myParent.draggedTerminal != terminal) {
                //System.out.println("draggedtiepoint"); // todo:tk:is this code used?
                for (Component ec : myParent.draggedTerminal.in) {
                    if (!isCurrentZero(ec)) {
                        this.putElement(rowNum, getComponentColumnNumber(ec, "I"), -1);
                    }
                }
                for (Component ec : myParent.draggedTerminal.out) {
                    if (!isCurrentZero(ec)) {
                        this.putElement(rowNum, getComponentColumnNumber(ec, "I"), 1);
                    }
                }
            }
            
            Iterator terminalOutComponents = terminal.out.iterator();
            
            while (terminalOutComponents.hasNext()) {
                Component electricComponent = (Component) terminalOutComponents.next();
                if (!isCurrentZero(electricComponent)) {
                    this.putElement(rowNum, this.myParent.currentCircuit.getColumnNumbers().get(electricComponent.getID() + "I"), 1);
                }
            }
            rowNum++;
        }
        return rowNum;
    }

    
    //Used for instantaneous change in current or magnetic flux.
    public void setUpInstantaneousCurrentOrMagneticFluxMatrix(double t) throws Exception {

        this.myParent.currentCircuit.setColumnNumbers(new HashMap<String, Integer>());
        Iterator components;
        components = myParent.currentCircuit.components.values().iterator();
        int count = 0;

        while (components.hasNext()) {

            Component ec = (Component) components.next();

            if (!isCurrentZero(ec)) {
                columns.add(new UnknownVector(ec.getPrimarySymbol() + ec.getComponentSubscript() + " I"));//This component's current
                this.myParent.currentCircuit.putNumbers(ec, "I", count);//count~current, count+1~deltacurrent
                count++;
            }

            if (ec instanceof Inductor) {
                columns.add(new InductorEquationVector("Inductor component deltaI"));//This components's change in current.
                this.myParent.currentCircuit.putNumbers(ec, "deltaI", count);
                count++;
            }

            if (ec instanceof VoltageIntegrator) {
                columns.add(new IntegratorEquationVector("Voltage integrator component SdVdt"));//The voltage difference across this component.
                this.myParent.currentCircuit.putNumbers(ec, "SdVdt", count);
                count += 1;//we make room for SdVdt
            }

        }
        
        firstTerminalVoltageIndex = count;
        
        Iterator<Point> terminalIterator;
        terminalIterator = myParent.currentCircuit.terminals.keySet().iterator();

        while (terminalIterator.hasNext()) {
            
            Point point = terminalIterator.next();
            
            rows.add(new UnknownVector("Terminal KCL equation"));
            columns.add(new UnknownVector("T" + myParent.currentCircuit.terminals.get(point).terminalNumber + " SVdt")); // This terminal's Integral voltage dT

            this.myParent.currentCircuit.putNumbers(point, "SVdt", count);
            count++;//for SVdt
        }

        this.myParent.currentCircuit.putNumbers(LAST, count);
        columns.add(new ColumnVector("TV-CI")); //Far right column of the matrix.
        components = myParent.currentCircuit.components.values().iterator();
        int rowNum = 0;
        
        rowNum = setUpKCLEquations(rowNum);
        this.numberOfKCLeqs = rowNum;
        
        // Set up component equations.
        while (components.hasNext()) {
            
            Component ec = (Component) components.next();
            
            if (ec instanceof DCVoltageSource && ec.getPrimaryValue() != null) {
                //Batteries have a voltage difference between electrodes
                    rows.add(new SourceEquationVector("Voltage source SVdt"));
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "SVdt"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "SVdt"), -1.0);
                    rowNum++;
            } else if (ec instanceof ACVoltageSource && ec.getPrimaryValue() != null && ec.getFrequency() != null && ec.getPhaseShift() != null) {
                    rows.add(new SourceEquationVector("AC voltage source SVdt"));
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "SVdt"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "SVdt"), -1.0);
                    rowNum++;
            } else if (ec instanceof VCVS) {
                    rows.add(new SourceEquationVector("VCVS SVdt"));
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "SVdt"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "SVdt"), -1.0);
                    rowNum++;
            } else if (ec instanceof VCCS) {
                    rows.add(new SourceEquationVector("VCCS SVdt"));
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "SVdt"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "SVdt"), -1.0);
                    rowNum++;
            } else if (ec instanceof ACCurrentSource && ec.getPrimaryValue() != null && ec.getFrequency() != null && ec.getPhaseShift() != null) {
                    rows.add(new SourceEquationVector("AC current source SVdt"));
                    this.putElement(rowNum, getComponentColumnNumber(ec, "I"), 1.0);
                    this.putElement(rowNum, getLastColumnNumber(), ec.getPrimaryValue()
                            * Math.sin(2 * Math.PI * ec.getFrequency() * (t + ec.getPhaseShift())));
                    rowNum++;
            } else if (ec instanceof Wire) {
                //Wires have 0 voltage difference between electrodes. todo:tk:why is there code here then?
                String componentName = ec.getID() + "SVdt";
                Vector vector = new WireEquationVector(componentName);
                vector.setComponentName(componentName);
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "SVdt"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "SVdt"), -1.0);
                rowNum++;
            } else if (ec instanceof InductanceMeter) {
                rows.add(new MeterEquationVector("Inductance meter SVdt"));
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "SVdt"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "SVdt"), -1.0);
                rowNum++;
            } else if (ec instanceof CurrentIntegrator) {
                rows.add(new IntegratorEquationVector("Current integrator SVdt"));
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "SVdt"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "SVdt"), -1.0);
                rowNum++;
            } else if (ec instanceof Switch) {
                if (!((Switch) ec).isOpen) {
                    rows.add(new UnknownVector("Switch SVdt"));
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "SVdt"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "SVdt"), -1.0);
                    rowNum++;
                }
            } else if (ec instanceof Resistor && ec.getPrimaryValue() != null) {
                    rows.add(new ResistorEquationVector("Resistor SVdt"));
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "SVdt"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "SVdt"), -1.0);
                    rowNum++;
            } else if (ec instanceof Inductor && ec.getPrimaryValue() != null) {
                //Inew = Iold+deltaI#1/L*deltaflux
                    rows.add(new InductorEquationVector("Inductor SVdt and deltaI"));
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "SVdt"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "SVdt"), -1.0);
                    this.putElement(rowNum, getComponentColumnNumber(ec, "deltaI"), -ec.getPrimaryValue());
                    rowNum++;

                    rows.add(new InductorEquationVector("Inductor I and deltaI"));
                    this.putElement(rowNum, getComponentColumnNumber(ec, "I"), 1);
                    this.putElement(rowNum, getComponentColumnNumber(ec, "deltaI"), -1);
                    this.putElement(rowNum, getLastColumnNumber(), ec.getSecondaryValue() / ec.getPrimaryValue());
                    rowNum++;
            } else if (ec instanceof Capacitor && ec.getPrimaryValue() != null) {
                //0 instantaneous flux change,V=1/C*Q
                    rows.add(new CapacitorEquationVector("Capacitor SVdt"));
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "SVdt"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "SVdt"), -1.0);
                    rowNum++;
            } else if (ec instanceof Voltmeter) {
                //0 current,0 instantaneous flux change,voltage difference defined by endpoints
            } else if (ec instanceof VoltageIntegrator) {
                rows.add(new IntegratorEquationVector("Voltage integrator SdVdt and SVdt"));
                this.putElement(rowNum, this.myParent.currentCircuit.getColumnNumbers().get(ec.getID() + "SdVdt"), 1.0);//v diff across electrodes
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "SVdt"), -1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "SVdt"), 1.0);
                rowNum++;
            } else if (ec instanceof Ohmmeter) {
                //voltage difference defined by endpoints,
                rows.add(new MeterEquationVector("Ohmmeter I"));
                this.putElement(rowNum, getComponentColumnNumber(ec, "I"), 1.0);//current
                this.putElement(rowNum, getLastColumnNumber(), 1);
                rowNum++;
            } else if (ec instanceof Ammeter) {
                rows.add(new MeterEquationVector("Ammeter SVdt"));
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "SVdt"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "SVdt"), -1.0);
                rowNum++;
            } else if (ec instanceof CurrentSource && ec.getPrimaryValue() != null) {
                    rows.add(new SourceEquationVector("Current source I"));
                    this.putElement(rowNum, getComponentColumnNumber(ec, "I"), 1.0);
                    this.putElement(rowNum, getLastColumnNumber(), ec.getPrimaryValue());
                    rowNum++;
            }
        }

        if (myParent.draggedTerminal != null) {

        }
        
        horizontalLineIndex = myParent.currentCircuit.terminals.size() - 1;
    }

    
// ============================    
    
    public int setUpKCLEquations2(int rowNum) {
        
        Iterator terminalIterator = myParent.currentCircuit.terminals.values().iterator();
        
        while (terminalIterator.hasNext()) {
            
            Terminal terminal = (Terminal) terminalIterator.next();
            
            String componentName = "T" + terminal.terminalNumber;
            Vector vector = new CurrentEquationVector(componentName + " I equation"); // This terminal's I equation.
            vector.setComponentName(componentName);
            rows.add(vector); 

            vector = new CurrentEquationVector(componentName + " I equation'"); // This terminal's I' equation.
            vector.setComponentName(componentName + "'");
            rows.add(vector);
            
            Iterator terminalInComponents = terminal.in.iterator();
            while (terminalInComponents.hasNext()) {
                Component ec = (Component) terminalInComponents.next();
                
                if (!isCurrentZero(ec)) {
                    this.putElement(rowNum, getComponentColumnNumber(ec, "I"), -1);  
                }
                
                if (!isCurrentPrimeZero(ec)) {
                    this.putElement(rowNum + 1, getComponentColumnNumber(ec, "I'"), -1);
                }
            }
            
            Iterator terminalOutComponents = terminal.out.iterator();
            while (terminalOutComponents.hasNext()) {
                Component ec = (Component) terminalOutComponents.next();
                
                if (!isCurrentZero(ec)) {
                    this.putElement(rowNum, getComponentColumnNumber(ec, "I"), 1);//row.put((Integer)this.myParent.myCircuit.getNumbers().get(electricComponent),-1.0);
                }
                
                if (!isCurrentPrimeZero(ec)) {
                    this.putElement(rowNum + 1, getComponentColumnNumber(ec, "I'"), 1);
                }
            }
            
            if (myParent.draggedTerminal != null
                    && myParent.draggedTerminal.getPosition().equals(terminal.getPosition())
                    && myParent.draggedTerminal != terminal) {
                
                for (Component ec : myParent.draggedTerminal.in) {
                    if (!isCurrentZero(ec)) {
                        this.putElement(rowNum, getComponentColumnNumber(ec, "I"), -1);
                    }
                    if (!isCurrentPrimeZero(ec)) {
                        this.putElement(rowNum + 1, getComponentColumnNumber(ec, "I'"), -1);
                    }
                }
                
                for (Component ec : myParent.draggedTerminal.out) {
                    if (!isCurrentZero(ec)) {
                        this.putElement(rowNum, getComponentColumnNumber(ec, "I"), 1);
                    }
                    if (!isCurrentPrimeZero(ec)) {
                        this.putElement(rowNum + 1, getComponentColumnNumber(ec, "I'"), 1);
                    }
                }
            }
            
            rowNum++;
            rowNum++;
        }
        return rowNum;
    }
    

    public void setUpMainMatrix(double t) throws Exception {
        
        // ============ Set up columns.
        this.myParent.currentCircuit.setColumnNumbers(new HashMap());
        Iterator components = myParent.currentCircuit.components.values().iterator();
        int count = 0;
        
        while (components.hasNext()) {

            Component ec = (Component) components.next();

            if (!isCurrentZero(ec)) {
                columns.add(new ColumnVector(ec.getPrimarySymbol() + ec.getComponentSubscript() + " I"));
                this.myParent.currentCircuit.putNumbers(ec, "I", count);
                count++;
            }
            
            if (!isCurrentPrimeZero(ec)) {
                columns.add(new ColumnVector(ec.getPrimarySymbol() + ec.getComponentSubscript() + " I'"));
                this.myParent.currentCircuit.putNumbers(ec, "I'", count);
                count++;
            }

            if (ec instanceof Voltmeter 
                    || ec instanceof Ohmmeter
                    || ec instanceof InductanceMeter
                    || ec instanceof VoltageIntegrator
                    || ec instanceof ACVoltageSource)
            {
                columns.add(new EquationVector(ec.getPrimarySymbol() + ec.getComponentSubscript() + " dV"));//This component's voltage difference.
                
                this.myParent.currentCircuit.putNumbers(ec, "dV", count);
                count++;//we make room for deltav
            }
        }
        
        firstTerminalVoltageIndex = count;
        
        Iterator<Point> terminalIterator = myParent.currentCircuit.terminals.keySet().iterator();

        while (terminalIterator.hasNext()) {
            Point terminal = terminalIterator.next();
            columns.add(new UnknownVector("T" + myParent.currentCircuit.terminals.get(terminal).terminalNumber + " V")); //This terminal's voltage.
            this.myParent.currentCircuit.putNumbers(terminal, "V", count);
            count++;
            columns.add(new UnknownVector("T" + myParent.currentCircuit.terminals.get(terminal).terminalNumber + " V'")); //This terminals's dV/dt.
            this.myParent.currentCircuit.putNumbers(terminal, "V'", count);
            count++;
        }

        this.myParent.currentCircuit.putNumbers(LAST, count);
        
        
        columns.add(new ColumnVector("TV-CI")); //Far right column of the matrix todo:tk
        
        // ============ Set up rows.
        
        int rowNum = 0;
        rowNum = setUpKCLEquations2(rowNum);
        this.numberOfKCLeqs = rowNum;
        
        Iterator componentIterator2 = myParent.currentCircuit.components.values().iterator();

        while (componentIterator2.hasNext()) {
            
            Component ec = (Component) componentIterator2.next();
            
            if (ec instanceof DCVoltageSource && ec.getPrimaryValue() != null) { 
                String componentName = ec.getID() + " V";
                Vector vector = new SourceEquationVector(componentName);
                vector.setComponentName(componentName);
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                this.putElement(rowNum, getLastColumnNumber(), ec.getPrimaryValue());
                rowNum++;

                vector = new SourceEquationVector(ec.getID() + " V'");
                vector.setComponentName(componentName + "'");
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V'"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V'"), -1.0);
                rowNum++;
            } else if (ec instanceof ACVoltageSource && ec.getPrimaryValue() != null && ec.getFrequency() != null && ec.getPhaseShift() != null) {
                String componentName = ec.getID() + " V";
                Vector vector = new SourceEquationVector(componentName);
                vector.setComponentName(componentName);
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                this.putElement(rowNum, getLastColumnNumber(), ec.getPrimaryValue()
                        * Math.sin(2 * Math.PI * ec.getFrequency() * (t + ec.getPhaseShift())));
                rowNum++;

                vector = new SourceEquationVector(ec.getID() + " V'");
                vector.setComponentName(componentName + "'");
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V'"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V'"), -1.0);
                this.putElement(rowNum, getLastColumnNumber(),
                        2 * Math.PI * ec.getFrequency() * ec.getPrimaryValue()
                        * Math.cos(2 * Math.PI * ec.getFrequency() * (t + ec.getPhaseShift())));
                rowNum++;

                vector = new SourceEquationVector(ec.getID() + " dV");
                vector.setComponentName(ec.getID() + " dV");
                rows.add(vector);
                this.putElement(rowNum, getComponentColumnNumber(ec, "dV"), 1.0);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), -1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), 1.0);
                rowNum++;
            } else if (ec instanceof VCVS) {
                ControlledSource controlledSource = (ControlledSource) ec;
                Component controllingComponent = myParent.currentCircuit.components.get(controlledSource.getControllingComponentLabel());

                String componentName = ec.getID() + " V";
                Vector vector = new ControlledSourceEquationVector(componentName, controlledSource);
                vector.setComponentName(componentName);
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(controllingComponent, "V"), -controlledSource.getMultiplier());
                this.putElement(rowNum, getTailTerminalColumnNumber(controllingComponent, "V"), controlledSource.getMultiplier());
                this.putElement(rowNum, getHeadTerminalColumnNumber(controlledSource, "V"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(controlledSource, "V"), -1.0);
                rowNum++;

                vector = new ControlledSourceEquationVector(ec.getID() + " V'", controlledSource);
                vector.setComponentName(componentName + "'");
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V'"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V'"), -1.0);
                rowNum++;
            } else if (ec instanceof VCCS) {
                ControlledSource controlledSource = (ControlledSource) ec;
                Component controllingComponent = myParent.currentCircuit.components.get(controlledSource.getControllingComponentLabel());

                String componentName = ec.getID() + " I";
                Vector vector = new SourceEquationVector(componentName);
                vector.setComponentName(componentName);
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(controllingComponent, "V"), -controlledSource.getMultiplier());
                this.putElement(rowNum, getTailTerminalColumnNumber(controllingComponent, "V"), controlledSource.getMultiplier());
                this.putElement(rowNum, getComponentColumnNumber(ec, "I"), 1.0);
                rowNum++;
            } else if (ec instanceof ACCurrentSource && ec.getPrimaryValue() != null && ec.getFrequency() != null && ec.getPhaseShift() != null) {
                rows.add(new CurrentEquationVector("AC current source I"));
                this.putElement(rowNum, getComponentColumnNumber(ec, "I"), 1.0);
                this.putElement(rowNum, getLastColumnNumber(), ec.getPrimaryValue()
                        * Math.sin(2 * Math.PI * ec.getFrequency() * (t + ec.getPhaseShift())));
                rowNum++;

                rows.add(new CurrentEquationVector("AC current source I'"));
                this.putElement(rowNum, getComponentColumnNumber(ec, "I'"), 1.0);
                this.putElement(rowNum, getLastColumnNumber(),
                        2 * Math.PI * ec.getFrequency() * ec.getPrimaryValue()
                        * Math.cos(2 * Math.PI * ec.getFrequency() * (t + ec.getPhaseShift())));
                rowNum++;
            } else if (ec instanceof Wire) {
                String componentName = ec.getID() + " V";
                Vector vector = new WireEquationVector(componentName);
                vector.setComponentName(componentName);
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                rowNum++;

                vector = new WireEquationVector(componentName);
                vector.setComponentName(componentName + "'");
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V'"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V'"), -1.0);
                rowNum++;
            } else if (ec instanceof CurrentIntegrator) {
                rows.add(new IntegratorEquationVector("Current integrator V"));
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                rowNum++;

                rows.add(new IntegratorEquationVector("Current integrator V'"));
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V'"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V'"), -1.0);
                rowNum++;
            } else if (ec instanceof Switch) {
                if (!((Switch) ec).isOpen) {
                    String componentName = ec.getID() + " V";
                    Vector vector = new UnknownVector(componentName);
                    vector.setComponentName(componentName);
                    rows.add(vector);
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                    rowNum++;

                    vector = new UnknownVector(componentName + "'");
                    vector.setComponentName(componentName + "'");
                    rows.add(vector);
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V'"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V'"), -1.0);
                    rowNum++;
                }
            } else if (ec instanceof Resistor && ec.getPrimaryValue() != null) {
                String componentName = ec.getID() + " V";
                Vector vector = new ResistorEquationVector(componentName);
                vector.setComponentName(componentName);
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                this.putElement(rowNum, getComponentColumnNumber(ec, "I"), -ec.getPrimaryValue());
                rowNum++;

                vector = new ResistorEquationVector(componentName + "'");
                vector.setComponentName(componentName + "'");
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V'"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V'"), -1.0);
                this.putElement(rowNum, getComponentColumnNumber(ec, "I'"), -ec.getPrimaryValue());
                rowNum++;
            } else if (ec instanceof Capacitor && ec.getPrimaryValue() != null) {
                String componentName = ec.getID() + " V";
                if (!ec.isIsHeldConstant()) {
                    Vector vector = new CapacitorEquationVector(componentName);
                    vector.setComponentName(componentName);
                    rows.add(vector);
                    this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                    this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                    this.putElement(rowNum, getLastColumnNumber(), ec.getSecondaryValue() / ec.getPrimaryValue());
                    rowNum++;
                }

                componentName =  componentName + "'";
                Vector vector = new CapacitorEquationVector(componentName);
                vector.setComponentName(componentName);
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V'"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V'"), -1.0);
                this.putElement(rowNum, getComponentColumnNumber(ec, "I"), -1 / ec.getPrimaryValue());
                rowNum++;
            } else if (ec instanceof Inductor && ec.getPrimaryValue() != null) {
                String componentName = ec.getID() + " V";
               Vector vector = new InductorEquationVector(componentName);
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                this.putElement(rowNum, getComponentColumnNumber(ec, "I'"), -ec.getPrimaryValue());
                rowNum++;

                componentName = ec.getID() + " I";
                vector = new InductorEquationVector(componentName);
                rows.add(vector);
                this.putElement(rowNum, getComponentColumnNumber(ec, "I"), 1);
                this.putElement(rowNum, getLastColumnNumber(), ec.getSecondaryValue() / ec.getPrimaryValue());
                rowNum++;
            } else if (ec instanceof Voltmeter) {
                String componentName = "Voltmeter dV and V";
                Vector vector = new MeterEquationVector(componentName);
                vector.setComponentName(componentName);
                rows.add(vector);
                
                this.putElement(rowNum, getComponentColumnNumber(ec, "dV"), 1.0);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), -1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), 1.0);
                rowNum++;
            } else if (ec instanceof VoltageIntegrator) {
                //I=0 and I'=0 are expressed elsewhere
                rows.add(new IntegratorEquationVector("Voltage integrator dV and V"));
                this.putElement(rowNum, getComponentColumnNumber(ec, "dV"), 1.0);//dV is integrated to get magnetic flux
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), -1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), 1.0);
                rowNum++;
            } else if (ec instanceof Ohmmeter) {
                String componentName = "Ohmmeter" + " I";
                Vector vector = new MeterEquationVector(componentName);
                vector.setComponentName(componentName);
                rows.add(vector);
                this.putElement(rowNum, getComponentColumnNumber(ec, "I"), 1.0);
                this.putElement(rowNum, getLastColumnNumber(), 1);
                rowNum++;

                vector = new MeterEquationVector("Ohmmeter dV and V");
                vector.setComponentName("Ohmmeter dV and V");
                rows.add(vector);
                this.putElement(rowNum, getComponentColumnNumber(ec, "dV"), 1.0);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                rowNum++;
            } else if (ec instanceof Ammeter) {
                String componentName = "Ammeter" + " V";
                Vector vector = new MeterEquationVector(componentName);
                vector.setComponentName(componentName);
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), -1.0);
                rowNum++;

                vector = new MeterEquationVector(componentName + "'");
                vector.setComponentName(componentName + "'");
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V'"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V'"), -1.0);
                rowNum++;
            } else if (ec instanceof CurrentSource && ec.getPrimaryValue() != null) {
                    String componentName = ec.getID() + " I";
                    Vector vector = new SourceEquationVector(componentName);
                    vector.setComponentName(componentName);
                    rows.add(vector);
                    this.putElement(rowNum, getComponentColumnNumber(ec, "I"), 1.0);
                    this.putElement(rowNum, getLastColumnNumber(), ec.getPrimaryValue());
                    rowNum++;
            } else if (ec instanceof CapacitanceMeter) {
                String componentName = "Capacitance meter" + " V";
                Vector vector = new UnknownVector(componentName);
                vector.setComponentName(componentName);
                rows.add(vector);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V'"), 1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V'"), -1.0);
                this.putElement(rowNum, getLastColumnNumber(), -1);
                rowNum++;
            } else if (ec instanceof InductanceMeter) {
                String componentName = "Inductance meter" + " I";
                Vector vector = new MeterEquationVector(componentName);
                vector.setComponentName(componentName);
                rows.add(vector);
                this.putElement(rowNum, getComponentColumnNumber(ec, "I'"), 1.0);
                this.putElement(rowNum, getLastColumnNumber(), -1);
                rowNum++;
                
                componentName = "Inductance meter dV and ";
                vector = new MeterEquationVector(componentName);
                vector.setComponentName(componentName);
                rows.add(vector);
                this.putElement(rowNum, getComponentColumnNumber(ec, "dV"), 1.0);
                this.putElement(rowNum, getHeadTerminalColumnNumber(ec, "V"), -1.0);
                this.putElement(rowNum, getTailTerminalColumnNumber(ec, "V"), 1.0);
                rowNum++;
            }
        }
        
        horizontalLineIndex = myParent.currentCircuit.terminals.size() * 2 - 2;
    }


// ============================
    
    public void reduceKCLeqs(SparseMatrix sparseMatrix) {
        int pivotCol = 0;
        int pivotRow = 0;
        pivotCol = sparseMatrix.columns.size() - 2;
        whilelabel:
        while (pivotCol >= 0 && pivotRow < sparseMatrix.numberOfKCLeqs) {

            if (sparseMatrix.columns.get(pivotCol).contents.size() > 0) {
                Set s = new HashSet(sparseMatrix.columns.get(pivotCol).contents.keySet());
                double maxSize = 0;
                //int row=-1;
                for (Object obj : s) {//for each element in this col
                    Integer index = (Integer) obj;
                    if (index >= pivotRow && index < sparseMatrix.numberOfKCLeqs) {//we are not considering rows above the pivot row, or outside of the KCL eqs
                        if (index != pivotRow) {//don't swap a row with itself
                            sparseMatrix.swapRows(pivotRow, index);
                        }//this brace-see below
                        if (rows.get(pivotRow).get(pivotCol) != 1.0) {
                            makePivotOne(pivotRow, pivotCol);
                        }
                        sparseMatrix.zeroBelow(pivotRow, pivotCol);//,numberOfKCLeqs);
                        sparseMatrix.zeroAbove(pivotRow, pivotCol);

                        pivotRow++;
                        pivotCol--;//++;
                        continue whilelabel;
                    }
                }
            }
            pivotCol--;//++;
        }
    }

    public void rowEchelonFormMatrix(SparseMatrix sparseMatrix) {
        
        reduceKCLeqs(sparseMatrix);
        //System.out.println("after reduce kcl eqs:");

        int pivotCol = 0;
        int pivotRow = 0;


        pivotCol = sparseMatrix.columns.size() - 2;
        whilelabel:
        while (pivotCol >= 0 && pivotRow < sparseMatrix.rows.size()) {

            if (sparseMatrix.columns.get(pivotCol).contents.size() > 0) {//if this col has stuff in it
                Set s = new HashSet(sparseMatrix.columns.get(pivotCol).contents.keySet());
                double scaledMaxSizeInCol = 0;
                int maxRow = -1;
                for (Object obj : s) {//itereate over elements in this col to find the biggest element in this col
                    Integer rowNum = (Integer) obj;
                    Set s2 = new HashSet(sparseMatrix.rows.get(rowNum).contents.keySet());
                    double maxSizeInRow = 0;//we know this row isn't empty-this will become nonzero
                    for (Object obj2 : s2) {//iterate over elements in this row to find the biggest element in this row
                        Integer colNum = (Integer) obj2;//was obj //##changed Dec 26 2008
                        if (Math.abs(sparseMatrix.getMatrixElement(rowNum, colNum)) > maxSizeInRow) {
                            maxSizeInRow = Math.abs(sparseMatrix.getMatrixElement(rowNum, colNum));
                        }
                    }
                    if (rowNum >= pivotRow && Math.abs(sparseMatrix.getMatrixElement(rowNum, pivotCol) / maxSizeInRow) > scaledMaxSizeInCol) {//we are not considering rows above the pivot row
                        scaledMaxSizeInCol = Math.abs(sparseMatrix.getMatrixElement(rowNum, pivotCol) / maxSizeInRow);
                        maxRow = rowNum;
                    }
                }
                if (maxRow == -1) {
                    //if execution reaches this point, then all the elements in this col are above the pivot row and we can go to the next col
                    pivotCol--;//++;
                    continue whilelabel;
                }
                if (maxRow != pivotRow) {//don't swap a row with itself
                    sparseMatrix.swapRows(pivotRow, maxRow);
                }


                if (rows.get(pivotRow).get(pivotCol) != 1.0) {//make sure we have a 1 in the pivot position before moving on
                    makePivotOne(pivotRow, pivotCol);
                }
                sparseMatrix.zeroBelow(pivotRow, pivotCol);//,sparseMatrix.rows.size());

                sparseMatrix.zeroAbove(pivotRow, pivotCol);

                pivotRow++;
                pivotCol--;//++;
            } else {//this col is empty
                pivotCol--;//++;
            }
        }
    }

    public void rowReducedEchelonFormMatrix(SparseMatrix sparseMatrix) {
        matrixError = false;
        if (true) {
            return;
        }
        for (int rownum = sparseMatrix.rows.size() - 1; rownum >= 0; rownum--) {//start at the bottom row
            if (sparseMatrix.rows.get(rownum).contents.size() == 1//if the bottom row has one entry
                    && sparseMatrix.rows.get(rownum).get(sparseMatrix.columns.size() - 1) != 0.0) {//and it is in the rightmost col

                continue;
            }
            
            if (sparseMatrix.rows.get(rownum).contents.size() == 0) {//if this row is empty
                continue;
            }
            int leftmostentry = sparseMatrix.columns.size();//initialize to far right-we're going to decrease to find the leftmost. note: we know that there is an entry to the left of the rightmost col, we could have initialized to Integer.MAX if we wanted to
            for (Object obj : sparseMatrix.rows.get(rownum).contents.keySet()) {//for each colnum used in this row
                Integer colnum = (Integer) obj;
                if (colnum < leftmostentry) {
                    leftmostentry = colnum;
                }
            }
            sparseMatrix.zeroAbove(rownum, leftmostentry);
        }
    }

    public static void printMatrixRow(SparseMatrix sparseMatrix, int i) {
        String result = "";
        for (int i2 = 0; i2 < sparseMatrix.columns.size(); i2++) {
            if (sparseMatrix.rows.get(i).get(i2) >= 0) {
                result += " ";
            }
            result += (sparseMatrix.rows.get(i).get(i2)) + " ";
            //System.out.print(sparseMatrix.rows[i].get(i2)+" ");
        }
        result += "\n";//System.out.println();
        System.out.println(result);
    }

    public static LinkedHashMap<String, Integer> sortHashMapByValues(HashMap<String, Integer> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, Integer> sortedMap
                = new LinkedHashMap<>();

        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Integer val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Integer comp1 = passedMap.get(key);
                Integer comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    
    public static String dumpMatrix(SparseMatrix sparseMatrix) throws Exception
    {
        return dumpMatrix(sparseMatrix, false);
    }
    
    
    public static String dumpMatrix(SparseMatrix sparseMatrix, boolean isRowDividerByClass) throws Exception {
        
        if (sparseMatrix == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("Rows:" + sparseMatrix.rows.size() + ", Columns:" + sparseMatrix.columns.size() + "\n");
        
        sb.append("Components:" + sparseMatrix.myParent.currentCircuit.components.size() + ", Terminals:" + sparseMatrix.myParent.currentCircuit.terminals.size() + "\n");

        sb.append("\n");
        
        String spaces = "  ";

        sb.append("   ");
        
        String columnString = "";

        // Print column numbers.
        for (int i = 1; i <= sparseMatrix.columns.size(); i++) {

            if(i == sparseMatrix.firstTerminalVoltageIndex + 1 || i == sparseMatrix.columns.size())
            {
                columnString = columnString.substring(0, columnString.length() - 1);
                columnString += ("  ");
            }
            
            if (i == 9) {
                spaces = " ";
            }
           
            columnString += (i + spaces);

        }

        sb.append(columnString + "\n");

        String result = "";
        boolean isPrintRowDivider = false;
        boolean isNotRowDividerPrinted = true;
        for (int rowIndex = 0; rowIndex < sparseMatrix.rows.size(); rowIndex++) {
            
            if(isNotRowDividerPrinted)
            {
                if(isRowDividerByClass)
                {
                    if(rowIndex > 0)
                    {
                        if(sparseMatrix.rows.get(rowIndex).getClass() != sparseMatrix.rows.get(rowIndex - 1).getClass())
                        {
                            isPrintRowDivider = true;
                        }
                    }
                }
                else if(rowIndex == sparseMatrix.horizontalLineIndex)
                {
                    isPrintRowDivider = true;
                }
            }
            
            if(isPrintRowDivider)
            {
                result += "   ";
                
                for(int x = 0; x <= sparseMatrix.columns.size() * 3 + 2; x++)
                {
                   result += "-";
                }
                result += "\n";
                
                isNotRowDividerPrinted = false;
                isPrintRowDivider = false;
            }

            result += String.format("%2d", rowIndex + 1);
            
            String valueString = "";
            
            for (int columnIndex = 0; columnIndex < sparseMatrix.columns.size(); columnIndex++) {
                
                if(columnIndex == sparseMatrix.firstTerminalVoltageIndex ||
                   columnIndex == sparseMatrix.columns.size()-1)
                {                            
                    result += ("|");
                }

                if (sparseMatrix.rows.get(rowIndex).get(columnIndex) >= 0) {
                    result += " ";
                }

                double value = sparseMatrix.rows.get(rowIndex).get(columnIndex);

                valueString = String.format("%-7.4f", value).trim();
                valueString = valueString.contains(".") ? valueString.replaceAll("0*$","").replaceAll("\\.$","") : valueString;
                
                if(columnIndex == sparseMatrix.columns.size() - 1)
                {
                    while(valueString.length() < 7)
                    {
                        valueString += " ";
                    }
                    
                    if(valueString.startsWith("-"))
                    {
                        valueString += " ";
                    }
                }
                   
                result += valueString;
                
                result += " ";
            }
            

            result += "(" + sparseMatrix.rows.get(rowIndex).description + ") " + sparseMatrix.rows.get(rowIndex).getClass().getSimpleName();

            result += "\n";
        }
        sb.append(result + "\n");


        
        String[] columnDescriptions = new String[sparseMatrix.columns.size()];
        for (int colIndex = 0; colIndex < sparseMatrix.columns.size(); colIndex++)
        {
            columnDescriptions[colIndex] = sparseMatrix.columns.get(colIndex).description;
        }
        
        sb.append(PrintVertical.formatWordsVertically(columnDescriptions, sparseMatrix.firstTerminalVoltageIndex, sparseMatrix.columns.size() - 1));
        
        sb.append("\n");
        
        //=================================
        /*
        // Dump component names and column numbers.
        Set s = (sortHashMapByValues(sparseMatrix.myParent.circuit.getColumnNumbers())).keySet();
        
        Iterator<String> it = s.iterator();
        while (it.hasNext()) {
            String keyName = it.next();
            
            String componentName = sparseMatrix.myParent.circuit.componentNames.get(keyName);
            
            if(componentName == null)
            {
                componentName = keyName;
            }
            
            String padding = "   ";
            if(componentName.length() == 3) padding = "  ";
            if(componentName.length() == 4) padding = " ";

            sb.append(componentName + padding + sparseMatrix.myParent.circuit.getColumnNumbers().get(keyName) + "\n");
        }

        sb.append( "\n");
        */

        sb.append("# of KCL Equations:" + sparseMatrix.numberOfKCLeqs + "\n");
        
        sb.append("\n");
        
        return sb.toString();
    }

}
