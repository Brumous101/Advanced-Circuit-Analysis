package org.mathpiper.ui.gui.applications.circuitpiper.model.components.active;

import java.util.Stack;
import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ScaledGraphics;


public final class Ground extends Component {
    
    public static int componentCounter = 1;

    public Ground(int x, int y, String uid, Circuit circuit) {
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
        primaryValue = 4.56;
        enteredPrimaryValue = "" + siToValue.get(siPrefix) * primaryValue;
    }
    
    public Ground(int x, int y, String uid, Stack<String> attributes, Circuit circuit) throws Exception {
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
        setPrimary("");
        setPrimaryUnit("");
        setPrimaryUnitPlural("");
        setPrimarySymbol("Gnd");
        setPrimaryUnitSymbol("");
        setPreselectedPrimaryPrefix("");
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
        double divisor;
        
        divisor = Math.sqrt(distanceSquared) / 16.0;
        
        if (distanceSquared < 16 * 16) {
            divisor = 1.0;
        }
        
        double middleX1 = x2 + run * .25 / divisor;
        double middleY1 = y2 + rise * .25 / divisor;
        
        double bottomX1 = x2 + run * .5 / divisor;
        double bottomY1 = y2 + rise * .5 / divisor;
        
        
        sg.drawLine(x1, y1, x2, y2); // Vertical line.

        
        sg.drawLine( (x2 - rise / divisor / 1.2),  (y2 + run / divisor/ 1.2),  (x2 + rise / divisor / 1.2),
                 (y2 - run / divisor / 1.2));
        
        sg.drawLine( (middleX1 - rise / divisor / 2),  (middleY1 + run / divisor / 2),
                 (middleX1 + rise / divisor / 2),  (middleY1 - run / divisor / 2));
        
        
        sg.drawLine( (bottomX1 - rise / divisor / 5),  (bottomY1 + run / divisor / 5),
                 (bottomX1 + rise / divisor / 5),  (bottomY1 - run / divisor / 5));
        
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
