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

import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Color;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.EditInfo;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Graphics;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;

public class BoxElm extends GraphicElm {

    public BoxElm(int xx, int yy) {
        super(xx, yy);
        x2 = xx;
        y2 = yy;
        setBbox(x1, y1, x2, y2);
    }

    public BoxElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        x2 = xb;
        y2 = yb;
        setBbox(x1, y1, x2, y2);
    }

    public String dump() {
        return super.dump();
    }

    int getDumpType() {
        return 'b';
    }

    public void drag(int xx, int yy) {
        x2 = xx;
        y2 = yy;
    }

    public boolean creationFailed() {
        return Math.abs(x2 - x1) < 32 || Math.abs(y2 - y1) < 32;
    }

    public void draw(Graphics g) {
        //g.setColor(needsHighlight() ? selectColor : lightGrayColor);
        g.setColor(needsHighlight() ? selectColor : Color.GRAY);
        setBbox(x1, y1, x2, y2);
        g.setLineDash(16, 6);
        if (x1 < x2 && y1 < y2) {
            g.drawRect(x1, y1, x2 - x1, y2 - y1);
        } else if (x1 > x2 && y1 < y2) {
            g.drawRect(x2, y1, x1 - x2, y2 - y1);
        } else if (x1 < x2 && y1 > y2) {
            g.drawRect(x1, y2, x2 - x1, y1 - y2);
        } else {
            g.drawRect(x2, y2, x1 - x2, y1 - y2);
        }
        g.setLineDash(0, 0);
    }

    public EditInfo getEditInfo(int n) {
        return null;
    }

    public void setEditValue(int n, EditInfo ei) {
    }

    public void getInfo(String arr[]) {
    }

    @Override
    public int getShortcut() {
        return 0;
    }
}
