package org.mathpiper.ui.gui.applications.circuitpiper.model.components.active;

import java.util.Stack;
import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ScaledGraphics;

/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */
public final class ACVoltageSource extends VoltageSource {

    public static int componentCounter = 1;

    public ACVoltageSource(int x, int y, String uid, Circuit circuit) {
        super(x, y, uid, circuit);
        
        init();
        
        primaryValue = 32.6;
        enteredPrimaryValue = "32.6";
        frequency = 60.0;
        enteredFrequency = "60";
        phaseShift = 0.0;
        enteredPhaseShift = "0";
    }
    
    public ACVoltageSource(int x, int y, String uid, Stack<String> attributes, Circuit circuit) throws Exception {
        super(x, y, uid, circuit);
        
        init();
        
        handleACSourceAttributes(attributes);
    }

    public void init() {
        setPreselectedPrimaryPrefix("V");
        setPreselectedFrequencyPrefix("Hz");
        setFrequencySymbol("f");
        setPreselectedPhaseShiftPrefix("s");
    }

    public void draw(ScaledGraphics sg) throws Exception {
        super.draw(sg);
        int x1 = getHeadTerminal().getX();
        int x2 = getTailTerminal().getX();
        int y1 = getHeadTerminal().getY();
        int y2 = getTailTerminal().getY();
        int rise = y2 - y1;
        int run = x2 - x1;
        int currentSourceSize = 11;
        double radius;
        double distance = Math.sqrt(rise * rise + run * run);
        double xm = (x1 + x2) / 2;
        double ym = (y1 + y2) / 2;
        
        double middleX1 = (x1 + run / 2.0 - currentSourceSize * run / distance);
        double middleY1 = (y1 + rise / 2 - currentSourceSize * rise / distance);
        double middleX2 = (x2 - run / 2.0 + currentSourceSize * run / distance);
        double middleY2 = (y2 - rise / 2.0 + currentSourceSize * rise / distance);
        
        if (distance >= 2 * currentSourceSize) {
            sg.drawLine(x1, y1, middleX1,  middleY1);
            sg.drawLine(middleX2, middleY2, x2, y2);
            sg.drawOval( (xm - currentSourceSize),  (ym - currentSourceSize), 2 * currentSourceSize, 2 * currentSourceSize);
            sg.drawArc( Math.round(xm - currentSourceSize),  Math.round(ym - currentSourceSize / 2.0),
                    currentSourceSize, currentSourceSize, 180, -180);
            sg.drawArc( Math.round(xm),  Math.round(ym - currentSourceSize / 2.0),
                    currentSourceSize, currentSourceSize, 180, 180);
            radius = currentSourceSize;
            //g.drawString("+",(x1+run/2.0-(METER_SIZE+10)*run/distance-10*rise/distance),(y1+rise/2-(METER_SIZE+10)*rise/distance+10*run/distance));

            if(circuit.isCirSim && circuit.circuitPanel.isNotMovingAny())
            {
                getCircuitElm().updateDotCount();
                getCircuitElm().drawDots(sg, x1, y1, middleX1, middleY1, getCircuitElm().curcount);
                getCircuitElm().drawDots(sg, x2, y2, middleX2, middleY2, -getCircuitElm().curcount);
            }
        } else {
            radius = distance / 2;
            sg.drawOval( (xm - distance / 2),  (ym - distance / 2),  distance,  distance);
            sg.drawArc( Math.round(xm - distance / 2),  Math.round(ym - distance / 4.0),
                     Math.round(distance / 2),  Math.round(distance / 2), 180, -180);
            sg.drawArc( Math.round(xm),  Math.round(ym - distance / 4.0),
                     Math.round(distance / 2),  Math.round(distance / 2), 180, 180);
        }
        if (distance > 0) {
            double a = 7.0;
            double b = 4.0;
            sg.drawString("-",  (xm + a / currentSourceSize * radius * run / distance) - 1,
                     (ym + a / currentSourceSize * radius * rise / distance) + 4);
            sg.drawString("+",  (xm - a / currentSourceSize * radius * run / distance) - 3,
                     (ym - a / currentSourceSize * radius * rise / distance) + 5);
            // g.drawString("+", x1,y1);
            // g.drawString("-", x2,y2);
        }
    }
    
    public String toString()
    {
        return super.toString() +  " " + this.getPrimaryValue() + " " + this.getFrequency() + " " + this.getPhaseShift();
    }
}
