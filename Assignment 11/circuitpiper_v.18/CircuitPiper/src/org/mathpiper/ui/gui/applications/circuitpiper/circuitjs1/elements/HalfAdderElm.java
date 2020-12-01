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

import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;

public class HalfAdderElm extends ChipElm {

    boolean hasReset() {
        return false;
    }

    public HalfAdderElm(int xx, int yy) {
        super(xx, yy);
    }

    public HalfAdderElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
    }

    String getChipName() {
        return "Half Adder";
    }

    public void setupPins() {
        sizeX = 2;
        sizeY = 2;
        pins = new Pin[getPostCount()];

        pins[0] = new Pin(0, SIDE_EAST, "S");
        pins[0].output = true;
        pins[1] = new Pin(1, SIDE_EAST, "C");
        pins[1].output = true;
        pins[2] = new Pin(0, SIDE_WEST, "A");
        pins[3] = new Pin(1, SIDE_WEST, "B");

    }

    public int getPostCount() {
        return 4;
    }

    public int getVoltageSourceCount() {
        return 2;
    }

    void execute() {

        pins[0].value = pins[2].value ^ pins[3].value;
        pins[1].value = pins[2].value && pins[3].value;
    }

    int getDumpType() {
        return 195;
    }

}
