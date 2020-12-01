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

public final class Resistor extends Component {

    public static final String ohm = "\u03a9";
    public static int componentCounter = 1;

    public Resistor(int x, int y, String uid, Circuit circuit) {
        
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
        primaryValue = 5.67;
        enteredPrimaryValue = "" + 1/siToValue.get(siPrefix) * primaryValue;
    }
    
    public Resistor(int x, int y, String uid, Stack<String> attributes, Circuit circuit) throws Exception {
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
        setPrimary("Resistance");
        setPrimaryUnit("Ohm");
        setPrimaryUnitPlural("Ohms");
        setPrimarySymbol("R");
        setSiPrefix("");
        setPrimaryUnitSymbol(ohm);
        setPreselectedPrimaryPrefix(getSiPrefix() + getPrimaryUnitSymbol());
    }

    public void draw(ScaledGraphics sg) throws Exception {
        super.draw(sg);
        double size = 15;
        int x1 = getHeadTerminal().getX();
        int x2 = getTailTerminal().getX();
        int y1 = getHeadTerminal().getY();
        int y2 = getTailTerminal().getY();
        int rise = y2 - y1;
        int run = x2 - x1;
        int distanceSquared = (rise * rise + run * run);
        double distance = Math.sqrt(rise * rise + run * run);
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
        
        sg.drawLine(x1, y1,  middleX1,  middleY1);
        
        sg.drawLine( middleX2,  middleY2, x2, y2);
        
        sg.drawLine( middleX1,  middleY1,  (middleX1 + seperationRun / 12.0 - seperationRise / 4.0),
                 (middleY1 + seperationRise / 12.0 + seperationRun / 4.0));
        
        sg.drawLine( (middleX2 - seperationRun / 12.0 + seperationRise / 4.0),
                 (middleY2 - seperationRise / 12.0 - seperationRun / 4.0),  middleX2,  middleY2);
        
        for (int i = 0; i < 2; i++) {
            sg.drawLine( (middleX1 + 3 * seperationRun / 12.0 + seperationRise / 4.0 + 4 * i * seperationRun / 12.0),
                     (middleY1 + 3 * seperationRise / 12.0 - seperationRun / 4.0 + 4 * i * seperationRise / 12.0),
                     (middleX1 + 5 * seperationRun / 12.0 - seperationRise / 4.0 + 4 * i * seperationRun / 12.0),
                     (middleY1 + 5 * seperationRise / 12.0 + seperationRun / 4.0 + 4 * i * seperationRise / 12.0));
        }
        for (int i = 0; i < 3; i++) {
            sg.drawLine( (middleX1 + seperationRun / 12.0 - seperationRise / 4.0 + 4 * i * seperationRun / 12.0),
                     (middleY1 + seperationRise / 12.0 + seperationRun / 4.0 + 4 * i * seperationRise / 12.0),
                     (middleX1 + 3 * seperationRun / 12.0 + seperationRise / 4.0 + 4 * i * seperationRun / 12.0),
                     (middleY1 + 3 * seperationRise / 12.0 - seperationRun / 4.0 + 4 * i * seperationRise / 12.0));
        }
        
                    sg.drawString("+",  (x1 + run / 2.0 - (size + 10) * run / distance - 10 * rise / distance),
                     (y1 + rise / 2 - (size + 10) * rise / distance + 10 * run / distance));
                    

        if(this.circuit.isCirSim && circuit.circuitPanel.isNotMovingAny())
        {
            this.getCircuitElm().doDots(sg);
        }
    }
    
    public int getLabelDistance()
    {
        return 20;
    }
    
    public String getTurtle()
    {
        StringBuilder sb = new StringBuilder();
                
        sb.append(super.getTurtle());
        sb.append(Circuit.turtleIndent + "cp:value " + getPrimaryValue() + " .");
        
        return sb.toString();
    }
    
    public String toString()
    {
        return super.toString() + " " + this.getPrimaryValue();
    }
}
