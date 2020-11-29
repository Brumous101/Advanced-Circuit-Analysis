
package org.mathpiper.ui.gui.applications.circuitpiper.model.components;

import java.awt.Color;
import java.util.Stack;
import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ScaledGraphics;

public final class Block extends Component {
    
    public static int componentCounter = 1;

    private String text = "";

    public Block(int x, int y, String uid, Circuit circuit) {
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
    
    
        public Block(int x, int y, String uid, Stack<String> attributes, Circuit circuit) throws Exception {
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

        if(attributes != null && attributes.size() == 1)
        {
            text = attributes.pop();
            text = text.replace("\"", "");
            text = text.replace("\\n", "\n");
        }
        else
        {
            throw new Exception("Wrong number of attibutes.");
        }
    }


    public void init() {
        setPrimarySymbol("B");
    }

    public void draw(ScaledGraphics sg) throws Exception {
        super.draw(sg);
        
        double size = 30;
        double leftMargin = 2;
        
        int x1 = getHeadTerminal().getX();
        int x2 = getTailTerminal().getX();
        int y1 = getHeadTerminal().getY();
        int y2 = getTailTerminal().getY();
        
        int rise = y2 - y1;
        int run = x2 - x1;
        double distance = Math.sqrt(rise * rise + run * run);
        double xm = (x1 + x2) / 2;
        double ym = (y1 + y2) / 2;

        if (distance >= 2 * size) {
            sg.drawLine(x1, y1,  (x1 + run / 2.0 - size * run / distance),  (y1 + rise / 2 - size * rise / distance));
            sg.drawLine( (x2 - run / 2.0 + size * run / distance),  (y2 - rise / 2.0 + size * rise / distance), x2, y2);
            sg.setColor(new Color(220,220,220));
            sg.fillRect( (xm - size),  (ym - size), 2 * size, 2 * size);
            sg.setColor(Color.BLACK);
            sg.drawRectangle( (xm - size),  (ym - size), 2 * size, 2 * size);
            
            double textYOffset = 0;
            String[] lines = text.split("\n");
            for(String line: lines)
            {
                textYOffset += sg.getScaledTextHeight(line);
                sg.drawString(line,  (xm - size + leftMargin),  (ym - size + textYOffset));
            }  
        } else {
            sg.setColor(new Color(220,220,220));
            sg.fillRect( (xm - distance / 2),  (ym - distance / 2),  distance,  distance);
            sg.setColor(Color.BLACK);
            sg.drawRectangle( (xm - distance / 2),  (ym - distance / 2),  distance,  distance);
        }
        
    }
    
    
    public void setText(String text)
    {
        this.text = text;
    }
    
    public int getLabelDistance()
    {
        return 32;
    }
    
    
    public String toString()
    {
        return super.toString() + " " + "\"" + this.text.replaceAll("\n", "\\\\n") + "\"";
    }
}