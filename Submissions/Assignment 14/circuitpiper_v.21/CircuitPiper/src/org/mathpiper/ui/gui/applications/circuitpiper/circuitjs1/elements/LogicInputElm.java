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
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Font;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Graphics;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Rectangle;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;

public class LogicInputElm extends SwitchElm {

    final int FLAG_TERNARY = 1;
    final int FLAG_NUMERIC = 2;
    double hiV, loV;

    public LogicInputElm(int xx, int yy) {
        super(xx, yy, false);
        numHandles = 1;
        hiV = 5;
        loV = 0;

    }

    public LogicInputElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        numHandles = 1;
        try {
            hiV = Double.parseDouble(st.nextToken());
            loV = Double.parseDouble(st.nextToken());
        } catch (Exception e) {
            hiV = 5;
            loV = 0;
        }
        if (isTernary()) {
            posCount = 3;
        }
    }

    boolean isTernary() {
        return (flags & FLAG_TERNARY) != 0;
    }

    boolean isNumeric() {
        return (flags & (FLAG_TERNARY | FLAG_NUMERIC)) != 0;
    }

    int getDumpType() {
        return 'L';
    }

    public String dump() {
        return super.dump() + " " + hiV + " " + loV;
    }

    public int getPostCount() {
        return 1;
    }

    public void setPoints() {
        super.setPoints();
        lead1 = interpPoint(point1, point2, 1 - 12 / dn);
    }

    public void draw(Graphics g) {
        Font oldf = g.getFont();
        Font f = new Font("SansSerif", Font.BOLD, 20);
        g.setFont(f);
        g.setColor(needsHighlight() ? selectColor : whiteColor);
        String s = position == 0 ? "L" : "H";
        if (isNumeric()) {
            s = "" + position;
        }
        setBbox(point1, lead1, 0);
        drawCenteredText(g, s, x2, y2, true);
        setVoltageColor(g, nodeVoltages[0]);
        drawThickLine(g, point1, lead1);
        updateDotCount();
        drawDots(g, point1, lead1, curcount);
        drawPosts(g);
        g.setFont(oldf);
    }

    public Rectangle getSwitchRect() {
        return new Rectangle(x2 - 10, y2 - 10, 20, 20);
    }

    public void setCurrent(int vs, double c) {
        current = -c;
    }

    public void stamp() {
        double v = (position == 0) ? loV : hiV;
        if (isTernary()) {
            v = position * 2.5;
        }
        sim.stampVoltageSource(0, nodes[0], voltSource, v);
    }

    public int getVoltageSourceCount() {
        return 1;
    }

    public double getVoltageDiff() {
        return nodeVoltages[0];
    }

    public void getInfo(String arr[]) {
        arr[0] = "logic input";
        arr[1] = (position == 0) ? "low" : "high";
        if (isNumeric()) {
            arr[1] = "" + position;
        }
        arr[1] += " (" + getVoltageText(nodeVoltages[0]) + ")";
        arr[2] = "I = " + getCurrentText(getCurrent());
    }

    public boolean hasGroundConnection(int n1) {
        return true;
    }

    public EditInfo getEditInfo(int n) {
        if (n == 0) {
            EditInfo ei = new EditInfo("", 0, 0, 0);
            ei.checkbox = new JCheckBox("Momentary Switch", momentary);
            return ei;
        }
        if (n == 1) {
            return new EditInfo("High Voltage", hiV, 10, -10);
        }
        if (n == 2) {
            return new EditInfo("Low Voltage", loV, 10, -10);
        }
        if (n == 3) {
            EditInfo ei = new EditInfo("", 0, 0, 0);
            ei.checkbox = new JCheckBox("Numeric", isNumeric());
            return ei;
        }
        return null;
    }

    public void setEditValue(int n, EditInfo ei) {
        if (n == 0) {
            momentary = ei.checkbox.isSelected();
        }
        if (n == 1) {
            hiV = ei.value;
        }
        if (n == 2) {
            loV = ei.value;
        }
        if (n == 3) {
            if (ei.checkbox.isSelected()) {
                flags |= FLAG_NUMERIC;
            } else {
                flags &= ~FLAG_NUMERIC;
            }
        }
    }

    public int getShortcut() {
        return 'i';
    }

    public double getCurrentIntoNode(int n) {
        return -current;
    }
}
