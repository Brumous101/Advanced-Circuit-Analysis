package org.mathpiper.ui.gui.applications.circuitpiper.model.components.active;

import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import static org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.CurrentSource.componentCounter;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ScaledGraphics;

public class CCVS extends ControlledSource {
    
    public CCVS(int x, int y, String uid, Circuit circuit) {
        
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
    
    

    public void init() {
        setPrimary("Voltage");
        setPrimaryUnit("Volt");
        setPrimaryUnitPlural("Volts");
        setPrimarySymbol("CCVS");
        setSiPrefix("");
        setPrimaryUnitSymbol("V");
        setPreselectedPrimaryPrefix(getSiPrefix() + getPrimaryUnitSymbol());
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
            double shaftScale = 9.0;
            double shaftScale2 = 5.0;
            double shaftScale3 = 7.0;
            double setback = 2.0;
            
            double oldLineWidth = sg.getLineWidth();
            sg.setLineWidth(3);
            
            // + at top.
            sg.drawLine(centerX - shaftScale2 * sine,
                     centerY - shaftScale2 * cosine,
                     centerX - shaftScale * sine,
                     centerY - shaftScale * cosine);
            
            sg.drawLine(centerX - (setback * cosine) - shaftScale3 * sine, 
                    centerY + (setback * sine) - shaftScale3 * cosine, 
                     centerX + (setback * cosine) - shaftScale3 * sine,
                     centerY - (setback * sine) - shaftScale3 * cosine);
            
            
            // - at bottom.
            sg.drawLine(centerX - (setback * cosine) + shaftScale3 * sine, 
                    centerY + (setback * sine) + shaftScale3 * cosine, 
                     centerX + (setback * cosine) + shaftScale3 * sine,
                     centerY - (setback * sine) + shaftScale3 * cosine);
            
            sg.setLineWidth(oldLineWidth);
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
