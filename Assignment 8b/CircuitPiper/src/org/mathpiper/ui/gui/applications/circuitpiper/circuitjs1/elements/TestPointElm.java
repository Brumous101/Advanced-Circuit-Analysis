package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements;

import javax.swing.JComboBox;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.EditInfo;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Font;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Graphics;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Point;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;

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
    
    TestPointElm by Bill Collis
    
 */
public class TestPointElm extends CircuitElm {

    int meter;
    final int TP_VOL = 0;
    final int TP_RMS = 1;
    final int TP_MAX = 2;
    final int TP_MIN = 3;
    final int TP_P2P = 4;
    final int TP_BIN = 5;
    final int TP_FRQ = 6;
    final int TP_PER = 7;
    final int TP_PWI = 8;
    final int TP_DUT = 9; //mark to space ratio
    int zerocount = 0;
    double rmsV = 0, total, count;
    double maxV = 0, lastMaxV;
    double minV = 0, lastMinV;
    double frequency = 0;
    double period = 0;
    double binaryLevel = 0;//0 or 1 - double because we only pass doubles back to the web page
    double pulseWidth = 0;
    double dutyCycle = 0;
    double selectedValue = 0;

    double voltages[];
    boolean increasingV = true, decreasingV = true;
    long periodStart, periodLength, pulseStart;//time between consecutive max values

    public TestPointElm(int xx, int yy) {
        super(xx, yy);
        meter = TP_VOL;
    }

    public TestPointElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        meter = Integer.parseInt(st.nextToken()); //get meter type from saved dump
    }

    int getDumpType() {
        return 368;
    }

    public int getPostCount() {
        return 1;
    }

    public void setPoints() {
        super.setPoints();
        lead1 = new Point();
    }

    public String dump() {
        return super.dump() + " " + meter;
    }

    String getMeter() {
        switch (meter) {
            case TP_VOL:
                return "V";
            case TP_RMS:
                return "V(rms)";
            case TP_MAX:
                return "Vmax";
            case TP_MIN:
                return "Vmin";
            case TP_P2P:
                return "Peak to peak";
            case TP_BIN:
                return "Binary";
            case TP_FRQ:
                return "Frequency";
            case TP_PER:
                return "Period";
            case TP_PWI:
                return "Pulse width";
            case TP_DUT:
                return "Duty cycle";
        }
        return "";
    }

    public void draw(Graphics g) {
        boolean selected = needsHighlight();
        Font f = new Font("SansSerif", selected ? Font.BOLD : 0, 14);
        g.setFont(f);
        g.setColor(selected ? selectColor : whiteColor);
        //depending upon flags show voltage or TP

        String s = "TP";
        interpPoint(point1, point2, lead1, 1 - ((int) g.context.measureText(s).getWidth() / 2 + 8) / dn);
        setBbox(point1, lead1, 0);
        drawCenteredText(g, s, x2, y2, true); //draw label TPx
        //draw selected value
        switch (meter) {
            case TP_VOL:
                s = myGetUnitText(nodeVoltages[0], "V", false);
                break;
            case TP_RMS:
                s = myGetUnitText(rmsV, "V(rms)", false);
                break;
            case TP_MAX:
                s = myGetUnitText(lastMaxV, "Vpk", false);
                break;
            case TP_MIN:
                s = myGetUnitText(lastMinV, "Vmin", false);
                break;
            case TP_P2P:
                s = myGetUnitText(lastMaxV - lastMinV, "Vp2p", false);
                break;
            case TP_BIN:
                s = binaryLevel + "";
                break;
            case TP_FRQ:
                s = myGetUnitText(frequency, "Hz", false);
                break;
            case TP_PER:
//                s = "percent:"+period + " " + sim.timeStep + " " + sim.simTime + " " + sim.getIterCount();
                break;
            case TP_PWI:
                s = myGetUnitText(pulseWidth, "S", false);
                break;
            case TP_DUT:
                //s = showFormat.format(dutyCycle);
                s = dutyCycle + "";
                break;
        }
        drawCenteredText(g, s, x2, y2 + 12, true); //draw selected value TPx

        setVoltageColor(g, nodeVoltages[0]);
        if (selected) {
            g.setColor(selectColor);
        }
        drawThickLine(g, point1, lead1);
        drawPosts(g);
    }

    public void stepFinished() {
        count++;//how many counts are in a cycle    
        total += nodeVoltages[0] * nodeVoltages[0]; //sum of squares

        if (nodeVoltages[0] < 2.5) {
            binaryLevel = 0;
        } else {
            binaryLevel = 1;
        }

        //V going up, track maximum value with 
        if (nodeVoltages[0] > maxV && increasingV) {
            maxV = nodeVoltages[0];
            increasingV = true;
            decreasingV = false;
        }
        if (nodeVoltages[0] < maxV && increasingV) {//change of direction V now going down - at start of waveform
            lastMaxV = maxV; //capture last maximum 
            //capture time between
            periodLength = System.currentTimeMillis() - periodStart;
            periodStart = System.currentTimeMillis();
            period = periodLength;
            pulseWidth = System.currentTimeMillis() - pulseStart;
            dutyCycle = pulseWidth / periodLength;
            minV = nodeVoltages[0]; //track minimum value with V
            increasingV = false;
            decreasingV = true;

            //rms data
            total = total / count;
            rmsV = Math.sqrt(total);
            if (Double.isNaN(rmsV)) {
                rmsV = 0;
            }
            count = 0;
            total = 0;

        }
        if (nodeVoltages[0] < minV && decreasingV) { //V going down, track minimum value with V
            minV = nodeVoltages[0];
            increasingV = false;
            decreasingV = true;
        }

        if (nodeVoltages[0] > minV && decreasingV) { //change of direction V now going up
            lastMinV = minV; //capture last minimum
            pulseStart = System.currentTimeMillis();
            maxV = nodeVoltages[0];
            increasingV = true;
            decreasingV = false;

            //rms data
            total = total / count;
            rmsV = Math.sqrt(total);
            if (Double.isNaN(rmsV)) {
                rmsV = 0;
            }
            count = 0;
            total = 0;

        }
        //need to zero the rms value if it stays at 0 for a while
        if (nodeVoltages[0] == 0) {
            zerocount++;
            if (zerocount > 5) {
                total = 0;
                rmsV = 0;
                maxV = 0;
                minV = 0;
            }
        } else {
            zerocount = 0;
        }
        switch (meter) {
            case TP_VOL:
                selectedValue = nodeVoltages[0];
                break;
            case TP_RMS:
                selectedValue = rmsV;
                break;
            case TP_MAX:
                selectedValue = lastMaxV;
                break;
            case TP_MIN:
                selectedValue = lastMinV;
                break;
            case TP_P2P:
                selectedValue = lastMaxV - lastMinV;
                break;
            case TP_BIN:
                selectedValue = binaryLevel;
                break;
            case TP_FRQ:
                selectedValue = frequency;
                break;
            case TP_PER:
                selectedValue = period;
                break;
            case TP_PWI:
                selectedValue = pulseWidth;
                break;
            case TP_DUT:
                selectedValue = dutyCycle;
                break;
        }

    }

    //alert the user
    public static native void alert(String msg) /*-{
      $wnd.alert(msg);
    }-*/;

    public double getScopeValue(int x) {
        return selectedValue;
    }

    public double getVoltageDiff() {
        return nodeVoltages[0];
    }

    public void getInfo(String arr[]) {
        arr[0] = "Test Point";
        switch (meter) {
            case TP_VOL:
                arr[1] = "V = " + myGetUnitText(nodeVoltages[0], "V", false);
                break;
            case TP_RMS:
                arr[1] = "V(rms) = " + myGetUnitText(rmsV, "V", false);
                break;
            case TP_MAX:
                arr[1] = "Vmax = " + myGetUnitText(lastMaxV, "Vpk", false);
                break;
            case TP_MIN:
                arr[1] = "Vmin = " + myGetUnitText(lastMinV, "Vmin", false);
                break;
            case TP_P2P:
                arr[1] = "Vp2p = " + myGetUnitText(lastMaxV - lastMinV, "Vp2p", false);
                break;
            case TP_BIN:
                arr[1] = "Binary:" + binaryLevel + "";
                break;
            case TP_FRQ:
                arr[1] = "Freq = " + myGetUnitText(frequency, "Hz", false);
                break;
            case TP_PER:
                arr[1] = "Period = " + myGetUnitText(period * sim.timeStep / sim.getIterCount(), "S", false);
                break;
            case TP_PWI:
                arr[1] = "Pulse width = " + myGetUnitText(pulseWidth * sim.timeStep * sim.getIterCount(), "S", false);
                break;
            case TP_DUT:
                arr[1] = "Duty cycle = " + /*///showFormat.format(dutyCycle)*/ "" + dutyCycle;
                break;
        }
    }

//    void drawHandles(Graphics g, Color c) {
//        g.setColor(c);
//        g.fillRect(x-3, y-3, 7, 7);
//    }
    public EditInfo getEditInfo(int n) {
        if (n == 0) {
            EditInfo ei = new EditInfo("Value", selectedValue, -1, -1);
            ei.comboBox = new JComboBox();
            ei.comboBox.addItem("Voltage");
            ei.comboBox.addItem("RMS Voltage");
            ei.comboBox.addItem("Max Voltage");
            ei.comboBox.addItem("Min Voltage");
            ei.comboBox.addItem("P2P Voltage");
            ei.comboBox.addItem("Binary Value");
            //ei.comboBox.addItem("Frequency");
            //ei.comboBox.addItem("Period");
            //ei.comboBox.addItem("Pulse Width");
            //ei.comboBox.addItem("Duty Cycle");
            ei.comboBox.setSelectedItem(meter);
            return ei;
        }

        return null;
    }

    public void setEditValue(int n, EditInfo ei) {
        if (n == 0) {
            meter = ei.comboBox.getSelectedIndex();
        }
    }

}
