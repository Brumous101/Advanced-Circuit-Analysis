package org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive;

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

public final class Wire extends Component {
    
    public static int componentCounter = 1;

    public Wire(int x, int y, String uid, Circuit circuit) {
        
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
        
        primarySymbol = "W";
    }

    public void draw(ScaledGraphics sg) throws Exception {
        super.draw(sg);
        sg.drawLine(getHeadTerminal().getX(), getHeadTerminal().getY(), getTailTerminal().getX(), getTailTerminal().getY());
        //the_graphics.fillOval(head.getX() - 1, head.getY() - 1, 3, 3);
        //the_graphics.fillOval(tail.getX() - 1, tail.getY() - 1, 3, 3);
        
        
        if(this.circuit.isCirSim && circuit.circuitPanel.isNotMovingAny())
        {
            this.getCircuitElm().doDots(sg);
        }
    }
    
    public String getLabel()
    {
        return "";
    }
    
    public int getLabelDistance()
    {
        return 12;
    }
    
/*  todo:tk:a better way needs to be developed to not have wire labels shown.
    public String getID()
    {
        return "";
    }*/
}
