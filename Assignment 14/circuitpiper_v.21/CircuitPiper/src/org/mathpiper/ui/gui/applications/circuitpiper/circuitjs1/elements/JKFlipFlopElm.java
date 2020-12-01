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

public class JKFlipFlopElm extends ChipElm {

    final int FLAG_RESET = 2;
    final int FLAG_POSITIVE_EDGE = 4;

    boolean hasReset() {
        return (flags & FLAG_RESET) != 0;
    }

    boolean positiveEdgeTriggered() {
        return (flags & FLAG_POSITIVE_EDGE) != 0;
    }

    public JKFlipFlopElm(int xx, int yy) {
        super(xx, yy);
    }

    public JKFlipFlopElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        pins[4].value = !pins[3].value;
    }

    String getChipName() {
        return "JK flip-flop";
    }

    public void setupPins() {
        sizeX = 2;
        sizeY = 3;
        pins = new Pin[getPostCount()];
        pins[0] = new Pin(0, SIDE_WEST, "J");
        pins[1] = new Pin(1, SIDE_WEST, "");
        pins[1].clock = true;
        pins[1].bubble = !positiveEdgeTriggered();
        pins[2] = new Pin(2, SIDE_WEST, "K");
        pins[3] = new Pin(0, SIDE_EAST, "Q");
        pins[3].output = pins[3].state = true;
        pins[4] = new Pin(2, SIDE_EAST, "Q");
        pins[4].output = true;
        pins[4].lineOver = true;

        if (hasReset()) {
            pins[5] = new Pin(1, SIDE_EAST, "R");
        }
    }

    public int getPostCount() {
        return 5 + (hasReset() ? 1 : 0);
    }

    public int getVoltageSourceCount() {
        return 2;
    }

    void execute() {
        boolean transition;
        if (positiveEdgeTriggered()) {
            transition = pins[1].value && !lastClock;
        } else {
            transition = !pins[1].value && lastClock;
        }
        if (transition) {
            boolean q = pins[3].value;
            if (pins[0].value) {
                if (pins[2].value) {
                    q = !q;
                } else {
                    q = true;
                }
            } else if (pins[2].value) {
                q = false;
            }
            pins[3].value = q;
            pins[4].value = !q;
        }
        lastClock = pins[1].value;

        if (hasReset()) {
            if (pins[5].value) {
                pins[3].value = false;
                pins[4].value = true;
            }
        }
    }

    int getDumpType() {
        return 156;
    }

    public EditInfo getEditInfo(int n) {
        if (n == 2) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new JCheckBox("Reset Pin", hasReset());
            return ei;
        }

        if (n == 3) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new JCheckBox("Positive Edge Triggered", positiveEdgeTriggered());
            return ei;
        }

        return super.getEditInfo(n);
    }

    public void setEditValue(int n, EditInfo ei) throws Exception {
        if (n == 2) {
            if (ei.checkbox.isSelected()) {
                flags |= FLAG_RESET;
            } else {
                flags &= ~FLAG_RESET;
            }

            setupPins();
            allocNodes();
            setPoints();
        }
        if (n == 3) {
            flags = ei.changeFlag(flags, FLAG_POSITIVE_EDGE);
            pins[1].bubble = !positiveEdgeTriggered();
        }

        super.setEditValue(n, ei);
    }
}
