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
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;

public class LogicOutputElm extends CircuitElm {

    final int FLAG_TERNARY = 1;
    final int FLAG_NUMERIC = 2;
    final int FLAG_PULLDOWN = 4;
    double threshold;
    String value;

    public LogicOutputElm(int xx, int yy) {
        super(xx, yy);
        threshold = 2.5;
    }

    public LogicOutputElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        try {
            threshold = Double.parseDouble(st.nextToken());
        } catch (Exception e) {
            threshold = 2.5;
        }
    }

    public String dump() {
        return super.dump() + " " + threshold;
    }

    int getDumpType() {
        return 'M';
    }

    public int getPostCount() {
        return 1;
    }

    boolean isTernary() {
        return (flags & FLAG_TERNARY) != 0;
    }

    boolean isNumeric() {
        return (flags & (FLAG_TERNARY | FLAG_NUMERIC)) != 0;
    }

    boolean needsPullDown() {
        return (flags & FLAG_PULLDOWN) != 0;
    }

    public void setPoints() {
        super.setPoints();
        lead1 = interpPoint(point1, point2, 1 - 12 / dn);
    }

    public void draw(Graphics g) {
        Font oldf = g.getFont();
        Font f = new Font("SansSerif", Font.BOLD, 20);
        g.setFont(f);
        //g.setColor(needsHighlight() ? selectColor : lightGrayColor);
        g.setColor(lightGrayColor);
        String s = (nodeVoltages[0] < threshold) ? "L" : "H";
        if (isTernary()) {
            if (nodeVoltages[0] > 3.75) {
                s = "2";
            } else if (nodeVoltages[0] > 1.25) {
                s = "1";
            } else {
                s = "0";
            }
        } else if (isNumeric()) {
            s = (nodeVoltages[0] < threshold) ? "0" : "1";
        }
        value = s;
        setBbox(point1, lead1, 0);
        drawCenteredText(g, s, x2, y2, true);
        setVoltageColor(g, nodeVoltages[0]);
        drawThickLine(g, point1, lead1);
        drawPosts(g);
        g.setFont(oldf);
    }

    public void stamp() {
        if (needsPullDown()) {
            sim.stampResistor(nodes[0], 0, 1e6);
        }
    }

    public double getVoltageDiff() {
        return nodeVoltages[0];
    }

    public void getInfo(String arr[]) {
        arr[0] = "logic output";
        arr[1] = (nodeVoltages[0] < threshold) ? "low" : "high";
        if (isNumeric()) {
            arr[1] = value;
        }
        arr[2] = "V = " + getVoltageText(nodeVoltages[0]);
    }

    public EditInfo getEditInfo(int n) {
        if (n == 0) {
            return new EditInfo("Threshold", threshold, 10, -10);
        }
        if (n == 1) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new JCheckBox("Current Required", needsPullDown());
            return ei;
        }
        if (n == 2) {
            EditInfo ei = new EditInfo("", 0, 0, 0);
            ei.checkbox = new JCheckBox("Numeric", isNumeric());
            return ei;
        }
        return null;
    }

    public void setEditValue(int n, EditInfo ei) {
        if (n == 0) {
            threshold = ei.value;
        }
        if (n == 1) {
            if (ei.checkbox.isSelected()) {
                flags = FLAG_PULLDOWN;
            } else {
                flags &= ~FLAG_PULLDOWN;
            }
        }
        if (n == 2) {
            if (ei.checkbox.isSelected()) {
                flags |= FLAG_NUMERIC;
            } else {
                flags &= ~FLAG_NUMERIC;
            }
        }
    }

    public int getShortcut() {
        return 'o';
    }

//    void drawHandles(Graphics g, Color c) {
//    	g.setColor(c);
//		g.fillRect(x-3, y-3, 7, 7);
//    }
}
