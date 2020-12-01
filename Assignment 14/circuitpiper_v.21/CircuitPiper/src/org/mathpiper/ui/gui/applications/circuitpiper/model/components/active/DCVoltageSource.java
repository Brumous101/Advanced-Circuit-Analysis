package org.mathpiper.ui.gui.applications.circuitpiper.model.components.active;

import java.util.Stack;
import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import static org.mathpiper.ui.gui.applications.circuitpiper.view.DrawingPanel.choiceFlag;
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

public final class DCVoltageSource extends VoltageSource {
    
    public static int componentCounter = 1;

    public DCVoltageSource(int x, int y, String uid, Circuit circuit) {
        
        super(x, y, uid, circuit);
        
        init();
        
        primaryValue = 4.56;
        enteredPrimaryValue = "" + siToValue.get(siPrefix) * primaryValue;
    }
    
    public DCVoltageSource(int x, int y, String uid, Stack<String> attributes, Circuit circuit) throws Exception {
        super(x, y, uid, circuit);
        
        init();
        
        handleAttribute(attributes);
    }

    public void init() {
        setPreselectedPrimaryPrefix(getSiPrefix() + getPrimaryUnitSymbol());
    }

    public void draw(ScaledGraphics sg) throws Exception {
        if(choiceFlag == true){
            super.draw(sg);

            double size = 18;

            int x1 = getHeadTerminal().getX();
            int x2 = getTailTerminal().getX();
            int y1 = getHeadTerminal().getY();
            int y2 = getTailTerminal().getY();
            int rise = y2 - y1;
            int run = x2 - x1;
            double distance = Math.sqrt(rise * rise + run * run);
            double xm = (x1 + x2) / 2;
            double ym = (y1 + y2) / 2;
            if (distance >= 2 * size) {
                sg.drawLine(x1, y1,  (x1 + run / 2.0 - size * run / distance),  (y1 + rise / 2 - size * rise / distance));
                sg.drawLine( (x2 - run / 2.0 + size * run / distance),  (y2 - rise / 2.0 + size * rise / distance), x2, y2);
                sg.drawOval( (xm - size),  (ym - size), 2 * size, 2 * size);

                sg.drawString("+",  (x1 + run / 2.0 - (size + 10) * run / distance - 10 * rise / distance),
                         (y1 + rise / 2 - (size + 10) * rise / distance + 10 * run / distance));
            } else {
                sg.drawOval( (xm - distance / 2),  (ym - distance / 2),  distance,  distance);
            }


            if(this.circuit.isCirSim && circuit.circuitPanel.isNotMovingAny())
            {
                this.getCircuitElm().doDots(sg);
            }
        }
        else{
            super.draw(sg);
            int x1 = getHeadTerminal().getX();
            int x2 = getTailTerminal().getX();
            int y1 = getHeadTerminal().getY();
            int y2 = getTailTerminal().getY();
            int rise = y2 - y1;
            int run = x2 - x1;
            int distanceSquared = (rise * rise + run * run);
            double divisor;
            divisor = Math.sqrt(distanceSquared) / 16.0;
            if (distanceSquared < 16 * 16) {
                divisor = 1.0;
            }
            double middleX1 = x1 + run / 2.0 - run / 4.0 / divisor;
            double middleY1 = y1 + rise / 2.0 - rise / 4.0 / divisor;
            double middleX2 = x2 - run / 2.0 + run / 4.0 / divisor;
            double middleY2 = y2 - rise / 2.0 + rise / 4.0 / divisor;

            sg.drawLine(x1, y1,  middleX1,  middleY1);
            sg.drawLine( middleX2,  middleY2, x2, y2);
            sg.drawLine( (middleX1 - rise / divisor),  (middleY1 + run / divisor),  (middleX1 + rise / divisor),
                     (middleY1 - run / divisor));
            sg.drawLine( (middleX2 - rise / divisor / 2),  (middleY2 + run / divisor / 2),
                     (middleX2 + rise / divisor / 2),  (middleY2 - run / divisor / 2));

            sg.drawString("+", middleX1, middleY1);


            if(this.circuit.isCirSim && circuit.circuitPanel.isNotMovingAny())
            {
                this.getCircuitElm().doDots(sg);
            }
        }
    }
    
    public String toString()
    {
        return super.toString() + " " + this.getPrimaryValue();
    }
    
}
