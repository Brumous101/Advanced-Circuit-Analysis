package org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive;

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

public final class Inductor extends Component {
    
    public static int componentCounter = 1;

    public Inductor(int x, int y, String uid, Circuit circuit) {
        
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
        primaryValue = 0.00234;
        enteredPrimaryValue = "" + 1/siToValue.get(siPrefix) * primaryValue;
    }
    
    public Inductor(int x, int y, String uid, Stack<String> attributes, Circuit circuit) throws Exception {
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
        setSecondary("Current");
        setPrimary("Inductance");
        setPrimaryUnit("Henry");
        setPrimaryUnitPlural("Henries");
        setPrimarySymbol("L");
        setSiPrefix("m");
        setPrimaryUnitSymbol("H");
        setSecondaryUnitSymbol("A");
        setPreselectedPrimaryPrefix(getSiPrefix() + getPrimaryUnitSymbol());
    }

    public void draw(ScaledGraphics sg) throws Exception {
        super.draw(sg);
        int x1 = getHeadTerminal().getX();
        int x2 = getTailTerminal().getX();
        int y1 = getHeadTerminal().getY();
        int y2 = getTailTerminal().getY();
        int rise = y2 - y1;
        int run = x2 - x1;
        int distanceSquared = (rise * rise + run * run);
        double distance = Math.sqrt(distanceSquared);
        double divisor;
        double seperationRise, seperationRun;

        divisor = Math.sqrt(distanceSquared) / 35.0;
        if (distanceSquared < 35 * 35) {
            divisor = 1.0;
        }
        double middleX1 = x1 + run / 2.0 - run / 2.0 / divisor;
        double middleY1 = y1 + rise / 2.0 - rise / 2.0 / divisor;
        double middleX2 = x2 - run / 2.0 + run / 2.0 / divisor;
        double middleY2 = y2 - rise / 2.0 + rise / 2.0 / divisor;
        seperationRise = middleY2 - middleY1;
        seperationRun = middleX2 - middleX1;
        double seperationDistance = Math.sqrt(sqr(seperationRun) + sqr(seperationRise));
        sg.drawLine(x1, y1,  Math.round(middleX1),  Math.round(middleY1));
        sg.drawLine( Math.round(middleX2),  Math.round(middleY2), x2, y2);
        for (int i = 0; i < 4; i++) {
            sg.drawArc( Math.round((2 * i + 1) * (middleX2 - middleX1) / 8.0 + middleX1 - seperationDistance / 8.0),
                     Math.round((2 * i + 1) * (middleY2 - middleY1) / 8.0 + middleY1 - seperationDistance / 8.0),
                     Math.round(seperationDistance / 4.0),
                     Math.round(seperationDistance / 4.0),
                     (int) Math.round(-Math.atan2(rise, run) / Math.PI * 180),
                     (180));
        }
        /* g.drawLine( (middleX1 - rise / divisor),  (middleY1 + run / divisor),
          (middleX1 + rise / divisor),
          (middleY1 - run / divisor));
         g.drawLine( (middleX2 - rise / divisor),  (middleY2 + run / divisor),
          (middleX2 + rise / divisor),
          (middleY2 - run / divisor));*/
        int a = 9;
        if (circuit.circuitPanel.drawingPanel.drawPlus) {
            sg.drawString("+",  (middleX1 - a * run / distance - a * rise / distance),
                     (middleY1 - a * rise / distance + a * run / distance));
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
