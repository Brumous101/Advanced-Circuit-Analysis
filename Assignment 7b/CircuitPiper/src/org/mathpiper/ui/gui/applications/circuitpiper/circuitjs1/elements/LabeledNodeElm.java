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

import java.util.HashMap;
import javax.swing.JCheckBox;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.CustomLogicModel;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.EditInfo;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Graphics;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;

public class LabeledNodeElm extends CircuitElm {

    final int FLAG_ESCAPE = 4;
    final int FLAG_INTERNAL = 1;

    public LabeledNodeElm(int xx, int yy) {
        super(xx, yy);
        text = "label";
    }

    public LabeledNodeElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        text = st.nextToken();
        if ((flags & FLAG_ESCAPE) == 0) {
            // old-style dump before escape/unescape
            while (st.hasMoreTokens()) {
                text += ' ' + st.nextToken();
            }
        } else {
            // new-style dump
            text = CustomLogicModel.unescape(text);
        }
    }

    public String dump() {
        flags |= FLAG_ESCAPE;
        return super.dump() + " " + CustomLogicModel.escape(text);
    }

    public String text;
    public static HashMap<String, Integer> nodeList;
    int nodeNumber;

    public boolean isInternal() {
        return (flags & FLAG_INTERNAL) != 0;
    }

    public static native void console(String text) /*-{
	    console.log(text);
	}-*/;

    public static void resetNodeList() {
        nodeList = new HashMap<String, Integer>();
    }
    final int circleSize = 17;

    public void setPoints() {
        super.setPoints();
        lead1 = interpPoint(point1, point2, 1 - circleSize / dn);
    }

    public void setNode(int p, int n) {
        super.setNode(p, n);
        if (p == 1) {
            // assign new node
            nodeList.put(text, n);
            nodeNumber = n;
        }
    }

    int getDumpType() {
        return 207;
    }

    public int getPostCount() {
        return 1;
    }

    // this is basically a wire, since it just connects two nodes together
    public boolean isWire() {
        return true;
    }

    // get connection node (which is the same as regular nodes for all elements but this one).
    // node 0 is the terminal, node 1 is the internal node shared by all nodes with same name
    public int getConnectionNode(int n) {
        if (n == 0) {
            return nodes[0];
        }
        return nodeNumber;
    }

    public int getConnectionNodeCount() {
        return 2;
    }

    public int getInternalNodeCount() {
        // this can happen at startup
        if (nodeList == null) {
            return 0;
        }

        Integer nn = nodeList.get(text);

        // node assigned already?
        if (nn != null) {
            nodeNumber = nn.intValue();
            return 0;
        }

        // allocate a new one
        return 1;
    }

    public void draw(Graphics g) {
        setVoltageColor(g, nodeVoltages[0]);
        drawThickLine(g, point1, lead1);
        g.setColor(needsHighlight() ? selectColor : whiteColor);
        setPowerColor(g, false);
        drawCenteredText(g, text, x2, y2, true);
        curcount = updateDotCount(current, curcount);
        drawDots(g, point1, lead1, curcount);
        interpPoint(point1, point2, ps2, 1 + 11. / dn);
        setBbox(point1, ps2, circleSize);
        drawPosts(g);
    }

    public double getCurrentIntoNode(int n) {
        return -current;
    }

    public void setCurrent(int x, double c) {
        current = -c;
    }

    public void stamp() {
        sim.stampVoltageSource(nodeNumber, nodes[0], voltSource, 0);
    }

    public double getVoltageDiff() {
        return nodeVoltages[0];
    }

    public int getVoltageSourceCount() {
        return 1;
    }

    public void getInfo(String arr[]) {
        arr[0] = text;
        arr[1] = "I = " + getCurrentText(getCurrent());
        arr[2] = "V = " + getVoltageText(nodeVoltages[0]);
    }

    public EditInfo getEditInfo(int n) {
        if (n == 0) {
            EditInfo ei = new EditInfo("Text", 0, -1, -1);
            ei.text = text;
            return ei;
        }
        if (n == 1) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new JCheckBox("Internal Node", isInternal());
            return ei;
        }
        return null;
    }

    public void setEditValue(int n, EditInfo ei) {
        if (n == 0) {
            text = ei.textf.getText();
        }
        if (n == 1) {
            flags = ei.changeFlag(flags, FLAG_INTERNAL);
        }
    }
}
