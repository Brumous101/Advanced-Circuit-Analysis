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

public final class Capacitor extends Component {

    private static final String micro = "\u03BC";

    public static int componentCounter = 1;

    public Capacitor(int x, int y, String uid, Circuit circuit) {
        
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
        primaryValue = 0.00000123;
        enteredPrimaryValue = "" + 1/siToValue.get(siPrefix) * primaryValue;
    }
    
    public Capacitor(int x, int y, String uid, Stack<String> attributes, Circuit circuit) throws Exception 
    {
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
        setSecondary("Charge");
        setPrimary("Capacitance");
        setPrimaryUnit("Farad");
        setPrimaryUnitPlural("Farads");
        setPrimarySymbol("C");
        setSiPrefix(micro);
        setPrimaryUnitSymbol("F");
        setSecondaryUnitSymbol("C");
        setPreselectedPrimaryPrefix(getSiPrefix() + "F");
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
        divisor = Math.sqrt(distanceSquared) / 16.0;
        if (distanceSquared < 16 * 16) {
            divisor = 1.0;
        }
        double middleX1 = x1 + run / 2.0 - run / 3.0 / divisor;
        double middleY1 = y1 + rise / 2.0 - rise / 3.0 / divisor;
        double middleX2 = x2 - run / 2.0 + run / 3.0 / divisor;
        double middleY2 = y2 - rise / 2.0 + rise / 3.0 / divisor;
        sg.drawLine(x1, y1,  middleX1,  middleY1);
        sg.drawLine( middleX2,  middleY2, x2, y2);
        sg.drawLine( (middleX1 - rise / divisor),  (middleY1 + run / divisor),  (middleX1 + rise / divisor),
                 (middleY1 - run / divisor));
        sg.drawLine( (middleX2 - rise / divisor),  (middleY2 + run / divisor),  (middleX2 + rise / divisor),
                 (middleY2 - run / divisor));
        int a = 9;
        if (circuit.circuitPanel.drawingPanel.drawPlus) {
            sg.drawString("+",  (middleX1 - a * run / distance - a * rise / distance),  (middleY1 - a * rise / distance + a * run / distance));
        }
        

        if(this.circuit.isCirSim && circuit.circuitPanel.isNotMovingAny())
        {
            //getCircuitElm().updateDotCount();
            getCircuitElm().drawDots(sg, x1, y1, middleX1, middleY1, getCircuitElm().curcount);
            getCircuitElm().drawDots(sg, x2, y2, middleX2, middleY2, -getCircuitElm().curcount);
        }

    }
    
    
    public int getLabelDistance()
    {
        return 26;
    }
    
    public String toString()
    {
        return super.toString() +  " " + this.getPrimaryValue();
    }
}
