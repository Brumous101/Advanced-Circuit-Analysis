package org.mathpiper.ui.gui.applications.circuitpiper.model.components.integratedcircuits;

import java.util.Stack;
import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ScaledGraphics;


public class LogicalPackage  extends Component {

    public static int componentCounter = 1;

    public LogicalPackage(int x, int y, String uid, Circuit circuit) {
        
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
    
    public LogicalPackage(int x, int y, String uid, Stack<String> attributes, Circuit circuit) throws Exception {
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
        setPrimarySymbol("U");
    }

    public void draw(ScaledGraphics sg) throws Exception {
        super.draw(sg);
        
        int x1 = getHeadTerminal().getX();
        int x2 = getTailTerminal().getX();
        int y1 = getHeadTerminal().getY();
        int y2 = getTailTerminal().getY();
        
        sg.drawRectangle(x1, y1, 144, 288);
    }
    
    public int getLabelDistance()
    {
        return 20;
    }
    
    public String toString()
    {
        return super.toString();
    }
    
}
