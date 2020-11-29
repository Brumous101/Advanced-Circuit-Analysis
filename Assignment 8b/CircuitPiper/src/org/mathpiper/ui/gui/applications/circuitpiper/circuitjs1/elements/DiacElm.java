/*    
    Copyright (C) Paul Falstad and Iain Sharp
    
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

import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Diode;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.EditInfo;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Graphics;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Point;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Polygon;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;

public class DiacElm extends CircuitElm {

    // resistor from 0 to 2, 3
    // diodes from 2, 3 to 1
    double onresistance, offresistance, breakdown, holdcurrent;
    boolean state;
    Diode diode1, diode2;

    public DiacElm(int xx, int yy) {
        super(xx, yy);
        offresistance = 1e8;
        onresistance = 500;
        breakdown = 30;
        holdcurrent = .01;
        state = false;
        createDiodes();
    }

    public DiacElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        onresistance = Double.parseDouble(st.nextToken());
        offresistance = Double.parseDouble(st.nextToken());
        breakdown = Double.parseDouble(st.nextToken());
        holdcurrent = Double.parseDouble(st.nextToken());
        createDiodes();
    }

    void createDiodes() {
        diode1 = new Diode(sim);
        diode2 = new Diode(sim);
        diode1.setupForDefaultModel();
        diode2.setupForDefaultModel();
    }

    public boolean nonLinear() {
        return true;
    }

    int getDumpType() {
        return 203;
    }

    public String dump() {
        return super.dump() + " " + onresistance + " " + offresistance + " "
                + breakdown + " " + holdcurrent;
    }

    Polygon arrows[];
    Point plate1[], plate2[];

    public void setPoints() {
        super.setPoints();
        calcLeads(16);

        plate1 = newPointArray(2);
        plate2 = newPointArray(2);
        interpPoint2(lead1, lead2, plate1[0], plate1[1], 0, 16);
        interpPoint2(lead1, lead2, plate2[0], plate2[1], 1, 16);

        arrows = new Polygon[2];

        int i;
        for (i = 0; i != 2; i++) {
            int sgn = -1 + i * 2;
            Point p1 = interpPoint(lead1, lead2, i, 8 * sgn);
            Point p2 = interpPoint(lead1, lead2, 1 - i, 16 * sgn);
            Point p3 = interpPoint(lead1, lead2, 1 - i, 0 * sgn);
            arrows[i] = createPolygon(p1, p2, p3);
        }
    }

    public void draw(Graphics g) {
        double v1 = nodeVoltages[0];
        double v2 = nodeVoltages[1];
        setBbox(point1, point2, 6);
        draw2Leads(g);
        setVoltageColor(g, v1);
        setPowerColor(g, true);
        drawThickLine(g, plate1[0], plate1[1]);
        setVoltageColor(g, v2);
        setPowerColor(g, true);
        drawThickLine(g, plate2[0], plate2[1]);
        g.fillPolygon(arrows[0]);
        setVoltageColor(g, v1);
        setPowerColor(g, true);
        g.fillPolygon(arrows[1]);
        setPowerColor(g, true);
        doDots(g);
        drawPosts(g);
    }

    void calculateCurrent() {
        double r = (state) ? onresistance : offresistance;
        current = (nodeVoltages[0] - nodeVoltages[2]) / r + (nodeVoltages[0] - nodeVoltages[3]) / r;
    }

    public void startIteration() {
        double vd = nodeVoltages[0] - nodeVoltages[1];
        if (Math.abs(current) < holdcurrent) {
            state = false;
        }
        if (Math.abs(vd) > breakdown) {
            state = true;
        }
    }

    public void doStep() {
        double r = (state) ? onresistance : offresistance;
        sim.stampResistor(nodes[0], nodes[2], r);
        sim.stampResistor(nodes[0], nodes[3], r);
        diode1.doStep(nodeVoltages[2] - nodeVoltages[1]);
        diode2.doStep(nodeVoltages[1] - nodeVoltages[3]);
    }

    public void stamp() {
        sim.stampNonLinear(nodes[0]);
        sim.stampNonLinear(nodes[1]);
        diode1.stamp(nodes[2], nodes[1]);
        diode2.stamp(nodes[1], nodes[3]);
    }

    public int getInternalNodeCount() {
        return 2;
    }

    public void getInfo(String arr[]) {
        arr[0] = "DIAC";
        getBasicInfo(arr);
        arr[3] = state ? "on" : "off";
        arr[4] = "Ron = " + getUnitText(onresistance, sim.ohmString);
        arr[5] = "Roff = " + getUnitText(offresistance, sim.ohmString);
        arr[6] = "Vbrkdn = " + getUnitText(breakdown, "V");
        arr[7] = "Ihold = " + getUnitText(holdcurrent, "A");
        arr[8] = "P = " + getUnitText(getPower(), "W");
    }

    public EditInfo getEditInfo(int n) {
        if (n == 0) {
            return new EditInfo("On resistance (ohms)", onresistance, 0, 0);
        }
        if (n == 1) {
            return new EditInfo("Off resistance (ohms)", offresistance, 0, 0);
        }
        if (n == 2) {
            return new EditInfo("Breakdown voltage (volts)", breakdown, 0, 0);
        }
        if (n == 3) {
            return new EditInfo("Hold current (amps)", holdcurrent, 0, 0);
        }
        return null;
    }

    public void setEditValue(int n, EditInfo ei) {
        if (ei.value > 0 && n == 0) {
            onresistance = ei.value;
        }
        if (ei.value > 0 && n == 1) {
            offresistance = ei.value;
        }
        if (ei.value > 0 && n == 2) {
            breakdown = ei.value;
        }
        if (ei.value > 0 && n == 3) {
            holdcurrent = ei.value;
        }
    }
}
