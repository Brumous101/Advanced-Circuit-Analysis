package org.mathpiper.ui.gui.applications.circuitpiper.model.components.active;

import java.util.Stack;
import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import static org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.CurrentSource.componentCounter;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ScaledGraphics;

public class CCCS extends ControlledSource {
    
    public CCCS(int x, int y, String uid, Circuit circuit) {
        
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
        //primaryValue = .0345;
        //enteredPrimaryValue = "34.5";
    }
    
    
    
        public CCCS(int x, int y, String uid, Stack<String> attributes, Circuit circuit) throws Exception {
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
        
        handleControlledSourceAttributes(attributes);
    }
    
    

    public void init() {
        setPrimary("Current");
        setPrimaryUnit("Amp");
        setPrimaryUnitPlural("Amps");
        setPrimarySymbol("CCCS");
        setPrimaryUnitSymbol("A");
        setPreselectedPrimaryPrefix("mA");
    }    
    
    
    public void draw(ScaledGraphics sg) throws Exception {
        super.draw(sg);
        
        int headTerminalX = getHeadTerminal().getX();
        int headTerminalY = getHeadTerminal().getY();
        int tailTerminalX = getTailTerminal().getX();
        int tailTerminalY = getTailTerminal().getY();
        
        int rise = tailTerminalY - headTerminalY;
        int run = tailTerminalX - headTerminalX;
        
        double radius = 14;
        double hypotenuse = Math.sqrt(rise * rise + run * run); // Distance between terminals.
        double centerX = (headTerminalX + tailTerminalX) / 2;
        double centerY = (headTerminalY + tailTerminalY) / 2;
        double sine = run / hypotenuse;
        double cosine = rise / hypotenuse;
        double headX = headTerminalX + run / 2.0 - radius * sine;
        double headY = headTerminalY + rise / 2.0 - radius * cosine;
        double tailX = tailTerminalX - run / 2.0 + radius * sine;
        double tailY = tailTerminalY - rise / 2.0 + radius * cosine;
        
        if (hypotenuse >= 2 * radius) {
            sg.drawLine(headTerminalX, headTerminalY, headX, headY);
            sg.drawLine(tailX, tailY, tailTerminalX, tailTerminalY);
            
           
            sg.drawLine(headX,  headY, 
                    centerX + radius * cosine,
                    centerY - radius * sine);
            
            sg.drawLine(headX,  headY, 
                    centerX - radius * cosine,
                    centerY + radius * sine);
            
            sg.drawLine(tailX,  tailY, 
                    centerX + radius * cosine,
                    centerY - radius * sine);
            
            sg.drawLine(tailX,  tailY, 
                    centerX - radius * cosine,
                    centerY + radius * sine);            
        } else {
            
        }
        if (hypotenuse > 0) {
            double shaftScale = 7.0;
            double barbScale = 4.0;
            
            sg.drawLine(centerX + shaftScale * sine,
                     centerY + shaftScale * cosine,
                     centerX - shaftScale * sine,
                     centerY - shaftScale * cosine);
            
            sg.drawLine(centerX + shaftScale * sine,
                     centerY + shaftScale * cosine,
                     centerX + barbScale * sine + barbScale * cosine,
                     centerY + barbScale * cosine - barbScale * sine);
            
            /*
            sg.drawString(barbRadius * sine + ", " + barbRadius * cosine, 50, 50);
            sg.setColor(Color.red);
            sg.drawOval(centerX-1, centerY-1, 2, 2);
            sg.drawOval(centerX-1.414, centerY-1.414, 2.828, 2.828);
            sg.drawOval(centerX-radius, centerY-radius, radius * 2, radius * 2);
            sg.setColor(Color.black);
            */
            
            sg.drawLine(centerX + shaftScale * sine,
                     centerY + shaftScale * cosine,
                     centerX + barbScale * sine - barbScale  * cosine,
                     centerY + barbScale * cosine + barbScale * sine);
        }
        
        
        if(this.circuit.isCirSim && circuit.circuitPanel.isNotMovingAny())
        {
            this.getCircuitElm().doDots(sg);
        }
    }
    
    public String toString()
    {
        return super.toString();
    }
}
