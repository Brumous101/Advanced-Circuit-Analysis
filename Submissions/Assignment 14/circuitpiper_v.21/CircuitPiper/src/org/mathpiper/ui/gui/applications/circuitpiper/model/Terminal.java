package org.mathpiper.ui.gui.applications.circuitpiper.model;

import java.awt.Point;
import java.util.HashSet;

import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Turtle;

/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */
public final class Terminal implements Turtle {
    
    public static int terminalCounter = 1;
    public static int hiddenTerminalCounter = 1;

    private int x, y;
    public HashSet<Terminal> myConnectedTo;
    public HashSet<Component> in, out;
    public int terminalNumber = 0;
    private Circuit circuit;
    public String name = "";
    public boolean isHighlight = false;
    public boolean isInactive = false;


    public Terminal(final int x, final int y, Circuit circuit) {
        this.circuit = circuit;
        myConnectedTo = new HashSet<Terminal>();
        in = new HashSet<Component>();
        out = new HashSet<Component>();

        this.x = x;
        this.y = y;
        
        if(circuit == null)
        {            
            terminalNumber = hiddenTerminalCounter++;       
        }
        else
        {
            terminalNumber = terminalCounter;

            if(! circuit.terminals.containsKey(new Point(x,y)))
            {
                terminalCounter++;
            }
        }
    }
    
    public Point getPosition() {
        return new Point(x, y);
    }

    public int getX() {
        return x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public boolean isConnectedTo(final Terminal other) {
        if (myConnectedTo.contains(other)) {
            //System.out.println("connected 1");
        }
        return myConnectedTo.contains(other);
    }

    public boolean isConnectedToOrder2(final Terminal other) {
        HashSet<Terminal> intersection = new HashSet<Terminal>(other.myConnectedTo);
        intersection.retainAll(this.myConnectedTo);
        if (intersection.size() > 0) {
            //System.out.println("connected 2");
        }
        return intersection.size() > 0;
    }
    
    public String getID()
    {
        return "T" + terminalNumber;
    }
    
    public String getTurtle()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append(Circuit.turtleIndent + "cp:id \"" + getID() + "\" ;");
        sb.append(Circuit.turtleIndent + "cp:x " + this.x + " ;");
        sb.append(Circuit.turtleIndent + "cp:y " + this.y + " .");
        
        return sb.toString();
    }
    
    public String toString()
    {
        return getID();
    }
}
