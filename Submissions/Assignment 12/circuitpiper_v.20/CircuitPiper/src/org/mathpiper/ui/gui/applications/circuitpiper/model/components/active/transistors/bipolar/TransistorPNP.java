package org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.transistors.bipolar;
import java.awt.Color;
import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import org.mathpiper.ui.gui.applications.circuitpiper.model.Terminal;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.transistors.Transistor;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ScaledGraphics;


public final class TransistorPNP extends Transistor {
    
    protected Terminal baseTerminal;
    
    public static int componentCounter = 1;

    public TransistorPNP(int x, int y, String uid, Circuit circuit) {
        super(x, y, uid, circuit);
        
        init();
    }
    

    public void init() {
        super.init();
    }

    public void draw(ScaledGraphics sg) throws Exception {
        super.draw(sg);
        
        int headTerminalX = getHeadTerminal().getX();
        int headTerminalY = getHeadTerminal().getY();
        int tailTerminalX = getTailTerminal().getX();
        int tailTerminalY = getTailTerminal().getY();
        
        int rise = tailTerminalY - headTerminalY;
        int run = tailTerminalX - headTerminalX;
        
        double radius2 = 9;
        double hypotenuse = Math.sqrt(rise * rise + run * run); // Distance between terminals.
        double centerX = (headTerminalX + tailTerminalX) / 2;
        double centerY = (headTerminalY + tailTerminalY) / 2;
        double sine = run / hypotenuse;
        double cosine = rise / hypotenuse;
        double headX = headTerminalX + run / 2.0 - radius2 * sine;
        double headY = headTerminalY + rise / 2.0 - radius2 * cosine;
        double tailX = tailTerminalX - run / 2.0 + radius2 * sine;
        double tailY = tailTerminalY - rise / 2.0 + radius2 * cosine;
        int circleOffset = 5 * baseOrientation;
        
        if (hypotenuse >= 2 * radius) {
            sg.drawLine(headTerminalX, headTerminalY, headX, headY);
            sg.drawLine(tailX, tailY, tailTerminalX, tailTerminalY);
            
            sg.drawOval( (centerX - (circleOffset * cosine) - radius ),  (centerY + (circleOffset * sine) - radius), 2 * radius, 2 * radius);
        } else {
            // sg.drawOval( (centerX - hypotenuse / 2),  (centerY - hypotenuse / 2),  hypotenuse,  hypotenuse); //todo:tk
        }
        if (hypotenuse > 0) {
            double shaftScale = 7.0;
            double shaftScale2 = 3.0;
            double setback = (baseOrientation  == 1) ? 9.0 : -9.0;
            double setback2 = (baseOrientation  == 1) ? 30.0 : -30.0;
            
            // Vertical line.
            sg.drawLine(centerX - (setback * cosine) + shaftScale * sine,
                     centerY + (setback * sine) + shaftScale * cosine,
                     centerX - (setback * cosine) - shaftScale * sine,
                     centerY + (setback * sine) - shaftScale * cosine);
            
            // Top line.
            sg.drawLine(headX,
                     headY,
                     centerX - (setback * cosine) - shaftScale2 * sine,
                     centerY + (setback * sine) - shaftScale2 * cosine);

            // Bottom line.
            drawArrowLine(sg, tailX, tailY, centerX - (setback * cosine) + shaftScale2 * sine, centerY + (setback * sine) + shaftScale2 * cosine ,6 ,2);
            
            // Base line.
            sg.drawLine(centerX - (setback * cosine),
                     centerY + (setback * sine),
                     centerX - (setback2 * cosine),
                     centerY + (setback2 * sine));
            
            // Base terminal.
            sg.setColor(Color.black);
            sg.fillOval(centerX - (setback2 * cosine) - 3, centerY + (setback2 * sine) - 3, 6, 6);
            
        }
        
        // Draws inaccurately.
        //drawArrowLine(sg, 25.51471862576143, 59.757359312880716, 27.63603896932107, 70.36396103067892,6,2);
        
    }
    

    public String toString()
    {
        return super.toString();
    }

}
