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

import javax.swing.JCheckBox;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.EditInfo;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;

public class MonostableElm extends ChipElm {

    //Used to detect rising edge
    private boolean prevInputValue = false;
    private boolean retriggerable = false;
    private boolean triggered = false;
    private double lastRisingEdge = 0;
    private double delay = 0.01;

    public MonostableElm(int xx, int yy) {
        super(xx, yy);
    }

    public MonostableElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        retriggerable = Boolean.parseBoolean(st.nextToken());
        delay = Double.parseDouble(st.nextToken());
    }

    String getChipName() {
        return "Monostable";
    }

    public void setupPins() {
        sizeX = 2;
        sizeY = 2;
        pins = new Pin[getPostCount()];
        pins[0] = new Pin(0, SIDE_WEST, "");
        pins[0].clock = true;
        pins[1] = new Pin(0, SIDE_EAST, "Q");
        pins[1].output = true;
        pins[2] = new Pin(1, SIDE_EAST, "Q");
        pins[2].output = true;
        pins[2].lineOver = true;
    }

    public int getPostCount() {
        return 3;
    }

    public int getVoltageSourceCount() {
        return 2;
    }

    void execute() {

        if (pins[0].value && prevInputValue != pins[0].value && (retriggerable || !triggered)) {
            lastRisingEdge = sim.simulationTime;
            pins[1].value = true;
            pins[2].value = false;
            triggered = true;
        }

        if (triggered && sim.simulationTime > lastRisingEdge + delay) {
            pins[1].value = false;
            pins[2].value = true;
            triggered = false;
        }
        prevInputValue = pins[0].value;
    }

    public String dump() {
        return super.dump() + " " + retriggerable + " " + delay;
    }

    int getDumpType() {
        return 194;
    }

    public EditInfo getEditInfo(int n) {
        if (n == 2) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new JCheckBox("Retriggerable", retriggerable);
            return ei;
        }
        if (n == 3) {
            EditInfo ei = new EditInfo("Period (s)", delay, 0.001, 0.1);
            return ei;
        }
        return super.getEditInfo(n);
    }

    public void setEditValue(int n, EditInfo ei) throws Exception {
        if (n == 2) {
            retriggerable = ei.checkbox.isSelected();
        }
        if (n == 3) {
            delay = ei.value;
        }
        super.setEditValue(n, ei);
    }
}
