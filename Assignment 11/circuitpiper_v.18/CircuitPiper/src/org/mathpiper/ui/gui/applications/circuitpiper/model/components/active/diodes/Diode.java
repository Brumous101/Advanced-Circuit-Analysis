package org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.diodes;

import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ScaledGraphics;


public class Diode extends Component {
    protected static int componentCounter = 1;
    protected double radius = 14;
    protected int baseOrientation = 1;
    
    public Diode(int x, int y, String uid, Circuit circuit) {
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
        setPrimarySymbol("D");
    }
    
    
    public void draw(ScaledGraphics sg) throws Exception {
        super.draw(sg);
        
        int headTerminalX = getHeadTerminal().getX();
        int headTerminalY = getHeadTerminal().getY();
        int tailTerminalX = getTailTerminal().getX();
        int tailTerminalY = getTailTerminal().getY();
        
        int rise = tailTerminalY - headTerminalY;
        int run = tailTerminalX - headTerminalX;
        
        double radius2 = 8.0;
        double hypotenuse = Math.sqrt(rise * rise + run * run); // Distance between terminals.
        double centerX = (headTerminalX + tailTerminalX) / 2;
        double centerY = (headTerminalY + tailTerminalY) / 2;
        double sine = run / hypotenuse;
        double cosine = rise / hypotenuse;
        double headX = headTerminalX + run / 2.0 - radius2 * sine;
        double headY = headTerminalY + rise / 2.0 - radius2 * cosine;
        double tailX = tailTerminalX - run / 2.0 + radius2 * sine;
        double tailY = tailTerminalY - rise / 2.0 + radius2 * cosine;
        
        
        if (hypotenuse >= 2 * radius) {
            sg.drawLine(headTerminalX, headTerminalY, headX, headY);
            sg.drawLine(tailX, tailY, tailTerminalX, tailTerminalY);
            
            //int circleOffset = 0 * baseOrientation;
            //sg.drawOval( (centerX - (circleOffset * cosine) - radius ),  (centerY + (circleOffset * sine) - radius), 2 * radius, 2 * radius);
        } else {
            // sg.drawOval( (centerX - hypotenuse / 2),  (centerY - hypotenuse / 2),  hypotenuse,  hypotenuse); //todo:tk
        }
        
        if (hypotenuse > 0) {

            double baseLineLess = 8.0;
            double setback = 10;
            
            // Top line.
            sg.drawLine(centerX + (setback * cosine) - baseLineLess * sine,
                     centerY - (setback * sine) - baseLineLess * cosine,
                     centerX - (setback * cosine) - baseLineLess * sine,
                     centerY + (setback * sine) - baseLineLess * cosine);

            // Bottom line.
            sg.drawLine(centerX - (setback * cosine) + baseLineLess * sine, 
                    centerY + (setback * sine) + baseLineLess * cosine, 
                    centerX + (setback * cosine) + baseLineLess * sine, 
                    centerY - (setback * sine) + baseLineLess * cosine);
            
            // Right triangle side.
            sg.drawLine(centerX + (setback * cosine) - baseLineLess * sine,
                     centerY - (setback * sine) - baseLineLess * cosine,
                     tailX, tailY);
            
            sg.drawLine(centerX - (setback * cosine) - baseLineLess * sine,
            centerY + (setback * sine) - baseLineLess * cosine,
            tailX, tailY);
        }
        
        
        if(this.circuit.isCirSim && circuit.circuitPanel.isNotMovingAny())
        {
            this.getCircuitElm().doDots(sg);
        }
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
