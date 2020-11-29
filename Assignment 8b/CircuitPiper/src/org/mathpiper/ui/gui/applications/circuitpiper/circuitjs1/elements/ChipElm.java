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
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Color;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.EditInfo;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Font;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Graphics;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Point;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;

public abstract class ChipElm extends CircuitElm {

    int csize, cspc, cspc2;
    int bits;
    final int FLAG_SMALL = 1;
    final int FLAG_FLIP_X = 1024;
    final int FLAG_FLIP_Y = 2048;

    public ChipElm(int xx, int yy) {
        super(xx, yy);
        if (needsBits()) {
            bits = (this instanceof RingCounterElm) ? 10 : 4;
        }
        noDiagonal = true;
        setupPins();
        setSize(sim.smallGridCheckItem.getState() ? 1 : 2);
    }

    public ChipElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        if (needsBits()) {
            bits = Integer.parseInt(st.nextToken());
        }
        noDiagonal = true;
        setupPins();
        setSize((f & FLAG_SMALL) != 0 ? 1 : 2);
        int i;
        for (i = 0; i != getPostCount(); i++) {
            if (pins == null) {
                nodeVoltages[i] = Double.parseDouble(st.nextToken());
            } else if (pins[i].state) {
                nodeVoltages[i] = Double.parseDouble(st.nextToken());
                pins[i].value = nodeVoltages[i] > 2.5;
            }
        }
    }

    boolean needsBits() {
        return false;
    }

    void setSize(int s) {
        csize = s;
        cspc = 8 * s;
        cspc2 = cspc * 2;
        flags &= ~FLAG_SMALL;
        flags |= (s == 1) ? FLAG_SMALL : 0;
    }

    public abstract void setupPins();

    public void draw(Graphics g) {
        drawChip(g);
    }

    void drawChip(Graphics g) {
        int i;
        Font oldfont = g.getFont();
        Font f = new Font("SansSerif", 0, 10 * csize);
//	    FontMetrics fm = g.getFontMetrics();
        for (i = 0; i != getPostCount(); i++) {
            g.setFont(f);
            Pin p = pins[i];
            setVoltageColor(g, nodeVoltages[i]);
            Point a = p.post;
            Point b = p.stub;
            drawThickLine(g, a, b);
            p.curcount = updateDotCount(p.current, p.curcount);
            drawDots(g, b, a, p.curcount);
            if (p.bubble) {
                g.setColor(sim.printableCheckItem.getState()
                        ? Color.white : Color.black);
                drawThickCircle(g, p.bubbleX, p.bubbleY, 1);
                g.setColor(lightGrayColor);
                drawThickCircle(g, p.bubbleX, p.bubbleY, 3);
            }
            g.setColor(p.selected ? selectColor : whiteColor);
            int fsz = 10 * csize;
            while (true) {
                int sw = (int) g.context.measureText(p.text).getWidth();
                // scale font down if it's too big
                if (sw > 10 * csize) {
                    fsz -= 2;
                    Font f2 = new Font("SansSerif", 0, fsz);
                    g.setFont(f2);
                    continue;
                }
                int asc = (int) g.currentFontSize;
                g.drawString(p.text, p.textloc.x - sw / 2,
                        p.textloc.y + asc / 2);
                if (p.lineOver) {
                    int ya = p.textloc.y - asc / 2;
                    g.drawLine(p.textloc.x - sw / 2, ya, p.textloc.x + sw / 2, ya);
                }
                break;
            }
        }
        g.setColor(needsHighlight() ? selectColor : lightGrayColor);
        drawThickPolygon(g, rectPointsX, rectPointsY, 4);
        if (clockPointsX != null) {
            g.drawPolyline(clockPointsX, clockPointsY, 3);
        }
        drawPosts(g);
        g.setFont(oldfont);
    }
    int rectPointsX[], rectPointsY[];
    int clockPointsX[], clockPointsY[];
    public Pin pins[];
    public int sizeX, sizeY;
    boolean lastClock;

    public void drag(int xx, int yy) {
        yy = sim.snapGrid(yy);
        if (xx < x1) {
            xx = x1;
            yy = y1;
        } else {
            y1 = y2 = yy;
            x2 = sim.snapGrid(xx);
        }
        setPoints();
    }

    public void setPoints() {
        clockPointsX = null;
        if (x2 - x1 > sizeX * cspc2 && this == sim.dragElm) {
            setSize(2);
        }
        int hs = cspc;
        int x0 = x1 + cspc2;
        int y0 = y1;
        int xr = x0 - cspc;
        int yr = y0 - cspc;
        int xs = sizeX * cspc2;
        int ys = sizeY * cspc2;
        rectPointsX = new int[]{xr, xr + xs, xr + xs, xr};
        rectPointsY = new int[]{yr, yr, yr + ys, yr + ys};
        setBbox(xr, yr, rectPointsX[2], rectPointsY[2]);
        int i;
        for (i = 0; i != getPostCount(); i++) {
            Pin p = pins[i];
            switch (p.side) {
                case SIDE_NORTH:
                    p.setPoint(x0, y0, 1, 0, 0, -1, 0, 0);
                    break;
                case SIDE_SOUTH:
                    p.setPoint(x0, y0, 1, 0, 0, 1, 0, ys - cspc2);
                    break;
                case SIDE_WEST:
                    p.setPoint(x0, y0, 0, 1, -1, 0, 0, 0);
                    break;
                case SIDE_EAST:
                    p.setPoint(x0, y0, 0, 1, 1, 0, xs - cspc2, 0);
                    break;
            }
        }
    }

    // see if we can move pin to position xp, yp, and return the new position
    boolean getPinPos(int xp, int yp, int pin, int pos[]) {
        int x0 = x1 + cspc2;
        int y0 = y1;
        int xr = x0 - cspc;
        int yr = y0 - cspc;
        double xd = (xp - xr) / (double) cspc2 - .5;
        double yd = (yp - yr) / (double) cspc2 - .5;
        if (xd < .25 && yd > 0 && yd < sizeY - 1) {
            pos[0] = (int) Math.max(Math.round(yd), 0);
            pos[1] = SIDE_WEST;
        } else if (xd > sizeX - .75) {
            pos[0] = (int) Math.min(Math.round(yd), sizeY - 1);
            pos[1] = SIDE_EAST;
        } else if (yd < .25) {
            pos[0] = (int) Math.max(Math.round(xd), 0);
            pos[1] = SIDE_NORTH;
        } else if (yd > sizeY - .75) {
            pos[0] = (int) Math.min(Math.round(xd), sizeX - 1);
            pos[1] = SIDE_SOUTH;
        } else {
            return false;
        }

        if (pos[0] < 0) {
            return false;
        }
        if ((pos[1] == SIDE_NORTH || pos[1] == SIDE_SOUTH) && pos[0] >= sizeX) {
            return false;
        }
        if ((pos[1] == SIDE_WEST || pos[1] == SIDE_EAST) && pos[0] >= sizeY) {
            return false;
        }

        for (int i = 0; i != getPostCount(); i++) {
            if (pin == i) {
                continue;
            }
            if (pins[i].overlaps(pos[0], pos[1])) {
                return false;
            }
        }
        return true;
    }

    public Point getPost(int n) {
        return pins[n].post;
    }

    public abstract int getVoltageSourceCount(); // output count

    public void setVoltageSource(int j, int vs) {
        int i;
        for (i = 0; i != getPostCount(); i++) {
            Pin p = pins[i];
            if (p.output && j-- == 0) {
                p.voltSource = vs;
                return;
            }
        }
        System.out.println("setVoltageSource failed for " + this);
    }

    public void stamp() {
        int i;
        for (i = 0; i != getPostCount(); i++) {
            Pin p = pins[i];
            if (p.output) {
                sim.stampVoltageSource(0, nodes[i], p.voltSource);
            }
        }
    }

    void execute() {
    }

    public void doStep() {
        int i;
        for (i = 0; i != getPostCount(); i++) {
            Pin p = pins[i];
            if (!p.output) {
                p.value = nodeVoltages[i] > 2.5;
            }
        }
        execute();
        for (i = 0; i != getPostCount(); i++) {
            Pin p = pins[i];
            if (p.output) {
                sim.updateVoltageSource(0, nodes[i], p.voltSource,
                        p.value ? 5 : 0);
            }
        }
    }

    public void reset() {
        int i;
        for (i = 0; i != getPostCount(); i++) {
            pins[i].value = false;
            pins[i].curcount = 0;
            nodeVoltages[i] = 0;
        }
        lastClock = false;
    }

    public String dump() {
        String s = super.dump();
        if (needsBits()) {
            s += " " + bits;
        }
        int i;
        for (i = 0; i != getPostCount(); i++) {
            if (pins[i].state) {
                s += " " + nodeVoltages[i];
            }
        }
        return s;
    }

    public void getInfo(String arr[]) {
        arr[0] = getChipName();
        int i, a = 1;
        for (i = 0; i != getPostCount(); i++) {
            Pin p = pins[i];
            if (arr[a] != null) {
                arr[a] += "; ";
            } else {
                arr[a] = "";
            }
            String t = p.text;
            if (p.lineOver) {
                t += '\'';
            }
            if (p.clock) {
                t = "Clk";
            }
            arr[a] += t + " = " + getVoltageText(nodeVoltages[i]);
            if (i % 2 == 1) {
                a++;
            }
        }
    }

    public void setCurrent(int x, double c) {
        int i;
        for (i = 0; i != getPostCount(); i++) {
            if (pins[i].output && pins[i].voltSource == x) {
                pins[i].current = c;
            }
        }
    }

    String getChipName() {
        return "chip";
    }

    public boolean getConnection(int n1, int n2) {
        return false;
    }

    public boolean hasGroundConnection(int n1) {
        return pins[n1].output;
    }

    public double getCurrentIntoNode(int n) {
        return pins[n].current;
    }

    @Override
    public EditInfo getEditInfo(int n) {
        if (n == 0) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new JCheckBox("Flip X", (flags & FLAG_FLIP_X) != 0);
            return ei;
        }
        if (n == 1) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new JCheckBox("Flip Y", (flags & FLAG_FLIP_Y) != 0);
            return ei;
        }
        return null;
    }

    public void setEditValue(int n, EditInfo ei) throws Exception {
        if (n == 0) {
            if (ei.checkbox.isSelected()) {
                flags |= FLAG_FLIP_X;
            } else {
                flags &= ~FLAG_FLIP_X;
            }
            setPoints();
        }
        if (n == 1) {
            if (ei.checkbox.isSelected()) {
                flags |= FLAG_FLIP_Y;
            } else {
                flags &= ~FLAG_FLIP_Y;
            }
            setPoints();
        }
    }

    public static final int SIDE_NORTH = 0;
    public static final int SIDE_SOUTH = 1;
    public static final int SIDE_WEST = 2;
    public static final int SIDE_EAST = 3;

    public class Pin {

        Pin(int p, int s, String t) {
            pos = p;
            side = s;
            text = t;
        }
        
        Point post, stub;
        Point textloc;
        public int pos, side, voltSource, bubbleX, bubbleY;
        String text;
        public boolean lineOver, bubble, clock, output, value, state, selected;
        double curcount, current;

        void setPoint(int px, int py, int dx, int dy, int dax, int day, int sx, int sy) {
            if ((flags & FLAG_FLIP_X) != 0) {
                dx = -dx;
                dax = -dax;
                px += cspc2 * (sizeX - 1);
                sx = -sx;
            }
            if ((flags & FLAG_FLIP_Y) != 0) {
                dy = -dy;
                day = -day;
                py += cspc2 * (sizeY - 1);
                sy = -sy;
            }
            int xa = px + cspc2 * dx * pos + sx; 
            int ya = py + cspc2 * dy * pos + sy;
            post = new Point(xa + dax * cspc2, ya + day * cspc2);
            stub = new Point(xa + dax * cspc, ya + day * cspc);
            textloc = new Point(xa, ya);
            if (bubble) {
                bubbleX = xa + dax * 10 * csize;
                bubbleY = ya + day * 10 * csize;
            }
            if (clock) {
                clockPointsX = new int[3];
                clockPointsY = new int[3];
                clockPointsX[0] = xa + dax * cspc - dx * cspc / 2;
                clockPointsY[0] = ya + day * cspc - dy * cspc / 2;
                clockPointsX[1] = xa;
                clockPointsY[1] = ya;
                clockPointsX[2] = xa + dax * cspc + dx * cspc / 2;
                clockPointsY[2] = ya + day * cspc + dy * cspc / 2;
            }
        }

        // convert position, side to a grid position (0=top left) so we can detect overlaps
        int toGrid(int p, int s) {
            if (s == SIDE_NORTH) {
                return p;
            }
            if (s == SIDE_SOUTH) {
                return p + sizeX * (sizeY - 1);
            }
            if (s == SIDE_WEST) {
                return p * sizeX;
            }
            if (s == SIDE_EAST) {
                return p * sizeX + sizeX - 1;
            }
            return -1;
        }

        boolean overlaps(int p, int s) {
            int g = toGrid(p, s);
            if (g == -1) {
                return true;
            }
            return toGrid(pos, side) == g;
        }
    }
}
