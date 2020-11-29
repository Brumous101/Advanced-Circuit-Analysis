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
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Graphics;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Point;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Polygon;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Scope;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;

public class TransistorElm extends CircuitElm {
    // node 0 = base
    // node 1 = collector
    // node 2 = emitter

    public int pnp;
    double beta;
    double fgain, inv_fgain;
    double gmin;
    final int FLAG_FLIP = 1;
    
    Point rectangle[], collector[], emitter[], base;

    TransistorElm(int xx, int yy, boolean pnpflag) {
        super(xx, yy);
        pnp = (pnpflag) ? -1 : 1;
        beta = 100;
        setup();
    }

    public TransistorElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        pnp = Integer.parseInt(st.nextToken());
        beta = 100;
        try {
            lastvbe = Double.parseDouble(st.nextToken());
            lastvbc = Double.parseDouble(st.nextToken());
            nodeVoltages[0] = 0;
            nodeVoltages[1] = -lastvbe;
            nodeVoltages[2] = -lastvbc;
            beta = Double.parseDouble(st.nextToken());
        } catch (Exception e) {
        }
        setup();
    }

    void setup() {
        vcrit = vt * Math.log(vt / (Math.sqrt(2) * leakage));
        fgain = beta / (beta + 1);
        inv_fgain = 1 / fgain;
        noDiagonal = true;
    }

    public boolean nonLinear() {
        return true;
    }

    public void reset() {
        nodeVoltages[0] = nodeVoltages[1] = nodeVoltages[2] = 0;
        lastvbc = lastvbe = curcount_c = curcount_e = curcount_b = 0;
    }

    int getDumpType() {
        return 't';
    }

    public String dump() {
        return super.dump() + " " + pnp + " " + (nodeVoltages[0] - nodeVoltages[1]) + " "
                + (nodeVoltages[0] - nodeVoltages[2]) + " " + beta;
    }
    double ic, ie, ib, curcount_c, curcount_e, curcount_b;

    Polygon rectPoly, arrowPoly;

    public void draw(Graphics g) {
        setBbox(point1, point2, 16);
        setPowerColor(g, true);
        // draw collector
        setVoltageColor(g, nodeVoltages[1]);
        drawThickLine(g, collector[0], collector[1]);
        // draw emitter
        setVoltageColor(g, nodeVoltages[2]);
        drawThickLine(g, emitter[0], emitter[1]);
        // draw arrow
        g.setColor(lightGrayColor);
        g.fillPolygon(arrowPoly);
        // draw base
        setVoltageColor(g, nodeVoltages[0]);
        if (sim.powerCheckItem.getState()) {
            g.setColor(Color.gray);
        }
        drawThickLine(g, point1, base);
        // draw dots
        curcount_b = updateDotCount(-ib, curcount_b);
        drawDots(g, base, point1, curcount_b);
        curcount_c = updateDotCount(-ic, curcount_c);
        drawDots(g, collector[1], collector[0], curcount_c);
        curcount_e = updateDotCount(-ie, curcount_e);
        drawDots(g, emitter[1], emitter[0], curcount_e);
        // draw base rectangle
        setVoltageColor(g, nodeVoltages[0]);
        setPowerColor(g, true);
        g.fillPolygon(rectPoly);

        if ((needsHighlight() || sim.dragElm == this) && dy == 0) {
            g.setColor(Color.white);
// IES
//		g.setFont(unitsFont);
            int ds = sign(dx);
            g.drawString("B", base.x - 10 * ds, base.y - 5);
            g.drawString("C", collector[0].x - 3 + 9 * ds, collector[0].y + 4); // x+6 if ds=1, -12 if -1
            g.drawString("E", emitter[0].x - 3 + 9 * ds, emitter[0].y + 4);
        }
        drawPosts(g);
    }

    public Point getPost(int n) {
        return (n == 0) ? point1 : (n == 1) ? collector[0] : emitter[0];
    }

    public int getPostCount() {
        return 3;
    }

    double getPower() {
        return (nodeVoltages[0] - nodeVoltages[2]) * ib + (nodeVoltages[1] - nodeVoltages[2]) * ic;
    }

    public void setPoints() {
        super.setPoints();
        int hs = 16;
        if ((flags & FLAG_FLIP) != 0) {
            dsign = -dsign;
        }
        int hs2 = hs * dsign * pnp;
        // calc collector, emitter posts
        collector = newPointArray(2);
        emitter = newPointArray(2);
        interpPoint2(point1, point2, collector[0], emitter[0], 1, hs2);
        // calc rectangle edges
        rectangle = newPointArray(4);
        interpPoint2(point1, point2, rectangle[0], rectangle[1], 1 - 16 / dn, hs);
        interpPoint2(point1, point2, rectangle[2], rectangle[3], 1 - 13 / dn, hs);
        // calc points where collector/emitter leads contact rectangle
        interpPoint2(point1, point2, collector[1], emitter[1], 1 - 13 / dn, 6 * dsign * pnp);
        // calc point where base lead contacts rectangle
        base = new Point();
        interpPoint(point1, point2, base, 1 - 16 / dn);
        // rectangle
        rectPoly = createPolygon(rectangle[0], rectangle[2], rectangle[3], rectangle[1]);

        // arrow
        if (pnp == 1) {
            arrowPoly = calcArrow(emitter[1], emitter[0], 8, 4);
        } else {
            Point pt = interpPoint(point1, point2, 1 - 11 / dn, -5 * dsign * pnp);
            arrowPoly = calcArrow(emitter[0], pt, 8, 4);
        }
    }

    public static final double leakage = 1e-13; // 1e-6;
    // Electron thermal voltage at SPICE's default temperature of 27 C (300.15 K):
    public static final double vt = 0.025865;
    public static final double vdcoef = 1 / vt;
    public static final double rgain = .5;
    public static final double inv_rgain = 1 / rgain;
    double vcrit;
    double lastvbc, lastvbe;

    double limitStep(double vnew, double vold) {
        double arg;
        double oo = vnew;

        if (vnew > vcrit && Math.abs(vnew - vold) > (vt + vt)) {
            if (vold > 0) {
                arg = 1 + (vnew - vold) / vt;
                if (arg > 0) {
                    vnew = vold + vt * Math.log(arg);
                } else {
                    vnew = vcrit;
                }
            } else {
                vnew = vt * Math.log(vnew / vt);
            }
            sim.converged = false;
            //System.out.println(vnew + " " + oo + " " + vold);
        }
        return (vnew);
    }

    public void stamp() {
        sim.stampNonLinear(nodes[0]);
        sim.stampNonLinear(nodes[1]);
        sim.stampNonLinear(nodes[2]);
    }

    public void doStep() {
        double vbc = nodeVoltages[0] - nodeVoltages[1]; // typically negative
        double vbe = nodeVoltages[0] - nodeVoltages[2]; // typically positive
        if (Math.abs(vbc - lastvbc) > .01
                || // .01
                Math.abs(vbe - lastvbe) > .01) {
            sim.converged = false;
        }
        // To prevent a possible singular matrix, put a tiny conductance in parallel
        // with each P-N junction.
        gmin = leakage * 0.01;
        if (sim.subIterations > 100) {
            // if we have trouble converging, put a conductance in parallel with all P-N junctions.
            // Gradually increase the conductance value for each iteration.
            gmin = Math.exp(-9 * Math.log(10) * (1 - sim.subIterations / 300.));
            if (gmin > .1) {
                gmin = .1;
            }
//		sim.console("gmin " + gmin + " vbc " + vbc + " vbe " + vbe);
        }

        //System.out.print("T " + vbc + " " + vbe + "\n");
        vbc = pnp * limitStep(pnp * vbc, pnp * lastvbc);
        vbe = pnp * limitStep(pnp * vbe, pnp * lastvbe);
        lastvbc = vbc;
        lastvbe = vbe;
        double pcoef = vdcoef * pnp;
        double expbc = Math.exp(vbc * pcoef);
        /*if (expbc > 1e13 || Double.isInfinite(expbc))
	      expbc = 1e13;*/
        double expbe = Math.exp(vbe * pcoef);
        /*if (expbe > 1e13 || Double.isInfinite(expbe))
	      expbe = 1e13;*/
        ie = pnp * leakage * (-inv_fgain * (expbe - 1) + (expbc - 1));
        ic = pnp * leakage * ((expbe - 1) - inv_rgain * (expbc - 1));
        ib = -(ie + ic);
        //System.out.println("gain " + ic/ib);
        //System.out.print("T " + vbc + " " + vbe + " " + ie + " " + ic + "\n");
        double gee = -leakage * vdcoef * expbe * inv_fgain;
        double gec = leakage * vdcoef * expbc;
        double gce = -gee * fgain;
        double gcc = -gec * inv_rgain;

        // add minimum conductance (gmin) between b,e and b,c
        gcc -= gmin;
        gee -= gmin;

        // stamps from page 302 of Pillage.  Node 0 is the base,
        // node 1 the collector, node 2 the emitter.
        sim.stampMatrix(nodes[0], nodes[0], -gee - gec - gce - gcc);
        sim.stampMatrix(nodes[0], nodes[1], gec + gcc);
        sim.stampMatrix(nodes[0], nodes[2], gee + gce);
        sim.stampMatrix(nodes[1], nodes[0], gce + gcc);
        sim.stampMatrix(nodes[1], nodes[1], -gcc);
        sim.stampMatrix(nodes[1], nodes[2], -gce);
        sim.stampMatrix(nodes[2], nodes[0], gee + gec);
        sim.stampMatrix(nodes[2], nodes[1], -gec);
        sim.stampMatrix(nodes[2], nodes[2], -gee);

        // we are solving for v(k+1), not delta v, so we use formula
        // 10.5.13 (from Pillage), multiplying J by v(k)
        sim.stampRightSide(nodes[0], -ib - (gec + gcc) * vbc - (gee + gce) * vbe);
        sim.stampRightSide(nodes[1], -ic + gce * vbe + gcc * vbc);
        sim.stampRightSide(nodes[2], -ie + gee * vbe + gec * vbc);
    }

    @Override
    public String getScopeText(int x) {
        String t = "";
        switch (x) {
            case Scope.VAL_IB:
                t = "Ib";
                break;
            case Scope.VAL_IC:
                t = "Ic";
                break;
            case Scope.VAL_IE:
                t = "Ie";
                break;
            case Scope.VAL_VBE:
                t = "Vbe";
                break;
            case Scope.VAL_VBC:
                t = "Vbc";
                break;
            case Scope.VAL_VCE:
                t = "Vce";
                break;
            case Scope.VAL_POWER:
                t = "P";
                break;
        }
        return sim.LS("transistor") + ", " + t;
    }

    public void getInfo(String arr[]) {
        arr[0] = sim.LS("transistor") + " (" + ((pnp == -1) ? "PNP)" : "NPN)") + " \u03b2=" + /*///showFormat.format(beta)*/ beta;
        double vbc = nodeVoltages[0] - nodeVoltages[1];
        double vbe = nodeVoltages[0] - nodeVoltages[2];
        double vce = nodeVoltages[1] - nodeVoltages[2];
        if (vbc * pnp > .2) {
            arr[1] = vbe * pnp > .2 ? "saturation" : "reverse active";
        } else {
            arr[1] = vbe * pnp > .2 ? "fwd active" : "cutoff";
        }
        arr[1] = sim.LS(arr[1]);
        arr[2] = "Ic = " + getCurrentText(ic);
        arr[3] = "Ib = " + getCurrentText(ib);
        arr[4] = "Vbe = " + getVoltageText(vbe);
        arr[5] = "Vbc = " + getVoltageText(vbc);
        arr[6] = "Vce = " + getVoltageText(vce);
        arr[7] = "P = " + getUnitText(getPower(), "W");
    }

    public double getScopeValue(int x) {
        switch (x) {
            case Scope.VAL_IB:
                return ib;
            case Scope.VAL_IC:
                return ic;
            case Scope.VAL_IE:
                return ie;
            case Scope.VAL_VBE:
                return nodeVoltages[0] - nodeVoltages[2];
            case Scope.VAL_VBC:
                return nodeVoltages[0] - nodeVoltages[1];
            case Scope.VAL_VCE:
                return nodeVoltages[1] - nodeVoltages[2];
            case Scope.VAL_POWER:
                return getPower();
        }
        return 0;
    }

    public int getScopeUnits(int x) {
        switch (x) {
            case Scope.VAL_IB:
            case Scope.VAL_IC:
            case Scope.VAL_IE:
                return Scope.UNITS_A;
            case Scope.VAL_POWER:
                return Scope.UNITS_W;
            default:
                return Scope.UNITS_V;
        }
    }

    public EditInfo getEditInfo(int n) {
        if (n == 0) {
            return new EditInfo("Beta/hFE", beta, 10, 1000).
                    setDimensionless();
        }
        if (n == 1) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new JCheckBox("Swap E/C", (flags & FLAG_FLIP) != 0);
            return ei;
        }
        return null;
    }

    public void setEditValue(int n, EditInfo ei) {
        if (n == 0) {
            beta = ei.value;
            setup();
        }
        if (n == 1) {
            if (ei.checkbox.isSelected()) {
                flags |= FLAG_FLIP;
            } else {
                flags &= ~FLAG_FLIP;
            }
            setPoints();
        }
    }

    void setBeta(double b) {
        beta = b;
        setup();
    }

    public void stepFinished() {
        // stop for huge currents that make simulator act weird
        if (Math.abs(ic) > 1e12 || Math.abs(ib) > 1e12) {
            sim.stop("max current exceeded", this);
        }
    }

    boolean canViewInScope() {
        return true;
    }

    public double getCurrentIntoNode(int n) {
        if (n == 0) {
            return -ib;
        }
        if (n == 1) {
            return -ic;
        }
        return -ie;
    }
}
