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

public class TimerElm extends ChipElm {

    final int FLAG_RESET = 2;
    final int FLAG_GROUND = 4;
    final int N_DIS = 0;
    final int N_TRIG = 1;
    final int N_THRES = 2;
    final int N_VIN = 3;
    final int N_CTL = 4;
    final int N_OUT = 5;
    final int N_RST = 6;
    final int N_GND = 7;

    int getDefaultFlags() {
        return FLAG_RESET | FLAG_GROUND;
    }
    int ground;

    public TimerElm(int xx, int yy) {
        super(xx, yy);
    }

    public TimerElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
    }

    String getChipName() {
        return "555 Timer";
    }

    public void setupPins() {
        sizeX = 3;
        sizeY = 5;
        pins = new Pin[8];
        pins[N_DIS] = new Pin(1, SIDE_WEST, "dis");
        pins[N_TRIG] = new Pin(3, SIDE_WEST, "tr");
        pins[N_TRIG].lineOver = true;
        pins[N_THRES] = new Pin(4, SIDE_WEST, "th");
        pins[N_VIN] = new Pin(1, SIDE_NORTH, "Vin");
        pins[N_CTL] = new Pin(1, SIDE_SOUTH, "ctl");
        pins[N_OUT] = new Pin(2, SIDE_EAST, "out");
        pins[N_OUT].state = true;
        pins[N_RST] = new Pin(1, SIDE_EAST, "rst");
        pins[N_GND] = new Pin(2, SIDE_SOUTH, "gnd");
    }

    public boolean nonLinear() {
        return true;
    }

    boolean hasReset() {
        return (flags & FLAG_RESET) != 0 || hasGroundPin();
    }

    boolean hasGroundPin() {
        return (flags & FLAG_GROUND) != 0;
    }

    public void stamp() {
        ground = hasGroundPin() ? nodes[N_GND] : 0;
        // stamp voltage divider to put ctl pin at 2/3 V
        sim.stampResistor(nodes[N_VIN], nodes[N_CTL], 5000);
        sim.stampResistor(nodes[N_CTL], ground, 10000);
        // discharge, output, and Vin pins change in doStep()
        sim.stampNonLinear(nodes[N_DIS]);
        sim.stampNonLinear(nodes[N_OUT]);
        sim.stampNonLinear(nodes[N_VIN]);
        if (hasGroundPin()) {
            sim.stampNonLinear(nodes[N_GND]);
        }
    }

    void calculateCurrent() {
        // need current for V, discharge, control, ground; output current is
        // calculated for us, and other pins have no current.
        pins[N_VIN].current = (nodeVoltages[N_CTL] - nodeVoltages[N_VIN]) / 5000;
        double groundVolts = hasGroundPin() ? nodeVoltages[N_GND] : 0;
        pins[N_CTL].current = -(nodeVoltages[N_CTL] - groundVolts) / 10000 - pins[N_VIN].current;
        pins[N_DIS].current = (!out) ? -(nodeVoltages[N_DIS] - groundVolts) / 10 : 0;
        pins[N_OUT].current = -(nodeVoltages[N_OUT] - (out ? nodeVoltages[N_VIN] : groundVolts));
        if (out) {
            pins[N_VIN].current -= pins[N_OUT].current;
        }
        if (hasGroundPin()) {
            pins[N_GND].current = (nodeVoltages[N_CTL] - groundVolts) / 10000;
            if (!out) {
                pins[N_GND].current += (nodeVoltages[N_DIS] - groundVolts) / 10 + (nodeVoltages[N_OUT] - groundVolts);
            }
        }
    }
    boolean out;

    public void startIteration() {
        out = nodeVoltages[N_OUT] > nodeVoltages[N_VIN] / 2;
        // check comparators
        if (nodeVoltages[N_THRES] > nodeVoltages[N_CTL]) {
            out = false;
        }

        // trigger overrides threshold
        if (nodeVoltages[N_CTL] / 2 > nodeVoltages[N_TRIG]) {
            out = true;
        }

        double groundVolts = hasGroundPin() ? nodeVoltages[N_GND] : 0;

        // reset overrides trigger
        if (hasReset() && nodeVoltages[N_RST] < .7 + groundVolts) {
            out = false;
        }
    }

    public void doStep() {
        // if output is low, discharge pin 0.  we use a small
        // resistor because it's easier, and sometimes people tie
        // the discharge pin to the trigger and threshold pins.
        if (!out) {
            sim.stampResistor(nodes[N_DIS], ground, 10);
        }

        // if output is high, connect Vin to output with a small resistor.  Otherwise connect output to ground.
        sim.stampResistor(out ? nodes[N_VIN] : ground, nodes[N_OUT], 1);
    }

    public int getPostCount() {
        return hasGroundPin() ? 8 : hasReset() ? 7 : 6;
    }

    public int getVoltageSourceCount() {
        return 0;
    }

    int getDumpType() {
        return 165;
    }

    public EditInfo getEditInfo(int n) {
        if (n == 2) {
            EditInfo ei = new EditInfo("", 0, 0, 0);
            ei.checkbox = new JCheckBox("Ground Pin", hasGroundPin());
            return ei;
        }
        return super.getEditInfo(n);
    }

    public void setEditValue(int n, EditInfo ei) throws Exception {
        if (n == 2) {
            flags = ei.changeFlag(flags, FLAG_GROUND);
            allocNodes();
            setPoints();
            return;
        }
        super.setEditValue(n, ei);
    }

}
