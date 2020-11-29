package org.mathpiper.ui.gui.applications.circuitpiper.model.components.active;

import java.util.Stack;
import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
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
public final class CurrentSource extends Component {

    public static int componentCounter = 1;
    
    public CurrentSource(int x, int y, String uid, Circuit circuit) {
        
        super(x, y, circuit);
        
        if(uid == null)
        {
            componentSubscript = componentCounter++ + "";
        }
        else
        {
            componentSubscript = uid;
            
            try{
                int number = Integer.parseInt(uid);
        
                if(number >= componentCounter)
                {
                    componentCounter = number + 1;
                }
            }
            catch (NumberFormatException nfe)
            {
            }
        }
        init();
        primaryValue = .0345;
        enteredPrimaryValue = "34.5";
    }
    
    public CurrentSource(int x, int y, String uid, Stack<String> attributes, Circuit circuit) throws Exception {
        super(x, y, circuit);
        if(uid == null)
        {
            componentSubscript = componentCounter++ + "";
        }
        else
        {
            componentSubscript = uid;
            
            try{
                int number = Integer.parseInt(uid);
        
                if(number >= componentCounter)
                {
                    componentCounter = number + 1;
                }
            }
            catch (NumberFormatException nfe)
            {
            }
        }
        init();

        handleAttribute(attributes);
    }

    public void init() {
        setPrimary("Current");
        setPrimaryUnit("Amp");
        setPrimaryUnitPlural("Amps");
        setPrimarySymbol("I");
        setPrimaryUnitSymbol("A");
        setPreselectedPrimaryPrefix("mA");
    }

    public void draw(ScaledGraphics sg) throws Exception {
        super.draw(sg);
        int x1 = getHeadTerminal().getX();
        int x2 = getTailTerminal().getX();
        int y1 = getHeadTerminal().getY();
        int y2 = getTailTerminal().getY();
        int rise = y2 - y1;
        int run = x2 - x1;
        int currentSourceSize = 13;
        double radius;
        double distance = Math.sqrt(rise * rise + run * run);
        double xm = (x1 + x2) / 2;
        double ym = (y1 + y2) / 2;
        if (distance >= 2 * currentSourceSize) {
            sg.drawLine(x1, y1,  (x1 + run / 2.0 - currentSourceSize * run / distance),  (y1 + rise / 2 - currentSourceSize * rise / distance));
            sg.drawLine( (x2 - run / 2.0 + currentSourceSize * run / distance),  (y2 - rise / 2.0 + currentSourceSize * rise / distance), x2, y2);
            sg.drawOval( (xm - currentSourceSize),  (ym - currentSourceSize), 2 * currentSourceSize, 2 * currentSourceSize);
            radius = currentSourceSize;
            //g.drawString("+",(x1+run/2.0-(METER_SIZE+10)*run/distance-10*rise/distance),(y1+rise/2-(METER_SIZE+10)*rise/distance+10*run/distance));
        } else {
            radius = distance / 2;
            sg.drawOval( (xm - distance / 2),  (ym - distance / 2),  distance,  distance);
        }
        if (distance > 0) {
            double a = 7.0;
            double b = 4.0;
            sg.drawLine( (xm + a / currentSourceSize * radius * run / distance),
                     (ym + a / currentSourceSize * radius * rise / distance),
                     (xm - a / currentSourceSize * radius * run / distance),
                     (ym - a / currentSourceSize * radius * rise / distance));
            sg.drawLine( (xm + a / currentSourceSize * radius * run / distance),
                     (ym + a / currentSourceSize * radius * rise / distance),
                     (xm + b / currentSourceSize * radius * run / distance + b / currentSourceSize * radius * rise / distance),
                     (ym + b / currentSourceSize * radius * rise / distance - b / currentSourceSize * radius * run / distance));
            sg.drawLine( (xm + a / currentSourceSize * radius * run / distance),
                     (ym + a / currentSourceSize * radius * rise / distance),
                     (xm + b / currentSourceSize * radius * run / distance - b / currentSourceSize * radius * rise / distance),
                     (ym + b / currentSourceSize * radius * rise / distance + b / currentSourceSize * radius * run / distance));
        }
        
        
        if(this.circuit.isCirSim && circuit.circuitPanel.isNotMovingAny())
        {
            this.getCircuitElm().doDots(sg);
        }
    }
    
    public String toString()
    {
        return super.toString() +  " " + this.getPrimaryValue();
    }
}
