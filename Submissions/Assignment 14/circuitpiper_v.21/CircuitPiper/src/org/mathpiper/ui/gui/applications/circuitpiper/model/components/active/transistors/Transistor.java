
package org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.transistors;

import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ScaledGraphics;


public abstract class Transistor extends Component {
    public static int componentCounter = 1;
    protected double radius = 14;
    protected int baseOrientation = 1;
    
    public Transistor(int x, int y, String uid, Circuit circuit) {
        
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
        
        /*
        baseTerminal = new Terminal(x,y);
        
        Point headPoint = new Point(component.headTerminal.getX(), component.headTerminal.getY());
        if (!myCircuit.myTerminals.containsKey(headPoint)) {
            myCircuit.myTerminals.put(headPoint, component.headTerminal);
        };
        */
    }
    
    public void init()
    {
        setPrimarySymbol("Q");
    }
    
    
    public void draw(ScaledGraphics sg) throws Exception
    {
        super.draw(sg);
        
        /*
        double centerX = (headTerminal.getX() + tailTerminal.getX()) / 2;
        double centerY = (headTerminal.getY() + tailTerminal.getY()) / 2;
        sg.setColor(Color.red);
        sg.drawOval(centerX-1, centerY-1, 2, 2);
        sg.setColor(Color.black);
        */
    }
    
    public int getLabelDistance()
    {
        return 20;
    }
    
    public void mirror()
    {

        baseOrientation = baseOrientation * -1;

    }
}
