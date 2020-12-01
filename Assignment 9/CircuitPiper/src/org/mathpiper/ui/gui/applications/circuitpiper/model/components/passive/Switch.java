package org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive;

import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.SwitchElm;
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

public final class Switch extends Component {
    
    public static int componentCounter = 1;

    public boolean isOpen = true;

    public void flip() {
        isOpen = !isOpen;
    }

    public Switch(int x, int y, String uid, Circuit circuit) {
        
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
    }
    
    public void init()
    {
        setPrimarySymbol("SW");
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
        if (!isOpen) {
            sg.drawLine( middleX1,  middleY1,  middleX2,
                     middleY2);
        } else {
            sg.drawLine( middleX1,  middleY1,  (middleX1 + seperationRun - seperationRise / 2.7),
                     (middleY1 + seperationRise + seperationRun / 2.5));
        }
        sg.drawOval( middleX1 - 5,  middleY1 - 5, 10, 10);
        sg.drawOval( middleX2 - 5,  middleY2 - 5, 10, 10);
        
        
        if(this.circuit.isCirSim && circuit.circuitPanel.isNotMovingAny())
        {
            if(((SwitchElm) this.getCircuitElm()).position == 0)
            {
                this.getCircuitElm().doDots(sg);
            }
        }
    }
    
    public int getLabelDistance()
    {
        return 25;
    }
}
