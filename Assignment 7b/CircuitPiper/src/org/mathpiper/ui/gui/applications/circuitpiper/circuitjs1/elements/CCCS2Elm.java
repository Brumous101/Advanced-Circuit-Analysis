/*    
    Copyright (C) Paul Falstad and Iain Sharp
    
    This file is part of CircuitJS1.

    CircuitJS1 is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    CircuitJS1 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CircuitJS1.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.EditInfo;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.ExprState;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Point;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;
import static org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.ChipElm.SIDE_WEST;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ControlledSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Wire;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Meter;

/*
todo:tk:this element does not work yet because it is currently configured
to be placed into the input circuit.
*/

public class CCCS2Elm extends CCCSElm {
    
    public CCCS2Elm(int xx, int yy) throws Exception {
        super(xx, yy);
    }

    public CCCS2Elm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) throws Exception {
        super(xa, ya, xb, yb, f, st);
        
        st.nextToken(); // Disgard "w" symbol.
        
        WireElm wireControl = new WireElm(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), 0, null);

        wireControl.point1 = new Point(wireControl.x1, wireControl.y1);
        wireControl.point2 = new Point(wireControl.x2, wireControl.y2);
        
        Component dummyComponent = new Wire(-1, -1, "Hidden0", null);
        wireControl.component = dummyComponent;
        
        if(wireControl.x1 != -1)
        {
            wireControl0 = wireControl;
        }
        
        st.nextToken(); // Disgard flag.
        st.nextToken(); // Disgard "w" symbol.
        
        wireControl = new WireElm(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), 0, null);

        wireControl.point1 = new Point(wireControl.x1, wireControl.y1);
        wireControl.point2 = new Point(wireControl.x2, wireControl.y2);
        
        dummyComponent = new Wire(-1, -1, "Hidden1", null);
        wireControl.component = dummyComponent;
        
        if(wireControl.x1 != -1)
        {
            wireControl1 = wireControl;
        }
        
        allocNodes();
        setupPins();
    }
    
    
    
    public String dump() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(super.dump());
        
        sb.append(" ");
        
        if(this.wireControl0 != null)
        {
            sb.append(wireControl0.dump());
        }
        else
        {
            sb.append("w -1 -1 -1 -1 0");
        }
        
        sb.append(" ");
        
        if(this.wireControl1 != null)
        {
            sb.append(wireControl1.dump());
        }
        else
        {
            sb.append("w -1 -1 -1 -1 0");
        }
        
        return sb.toString();
    }
    

    public void setupPins() {
        sizeX = 1;
        sizeY = 4;
        pins = new ChipElm.Pin[4];

        pins[0] = new ChipElm.Pin(0, SIDE_WEST, "C+");
        pins[1] = new ChipElm.Pin(1, SIDE_WEST, "C-");
        pins[1].output = true;

        pins[2] = new ChipElm.Pin(2, SIDE_WEST, "O+");
        pins[2].output = true;
        pins[3] = new ChipElm.Pin(3, SIDE_WEST, "O-");

        exprState = new ExprState(1);
    }
    
    
    public void setPoints() {
        pins[0].setPoint(x1, y1-1, 0, 0, 0, 0, 0, 0);
        pins[1].setPoint(x1, y1-2, 0, 0, 0, 0, 0, 0);
        pins[2].setPoint(x1, y1, 0, 0, 0, 0, 0, 0);
        pins[3].setPoint(x2, y2, 0, 0, 0, 0, 0, 0);
    }
    
    
    
    @Override
    public EditInfo getEditInfo(int n) {
        if (n == 0) {
            EditInfo ei = new EditInfo("Expression", 0);
            ei.text = exprString;
            return ei;
        }
        if (n == 1) {
            EditInfo ei = new EditInfo("Controlling Component", 0).setDimensionless();
            
            List<String> optionsList = new ArrayList();

            Iterator<Component> itr = this.component.getCircuit().components.values().iterator();
            while (itr.hasNext()) {
                Component component = itr.next();

                if(component instanceof Meter)
                {
                    continue;
                }

                optionsList.add(component.getID());
            }

            optionsList.remove(((ControlledSource)component).getID());

            Collections.sort(optionsList, new Comparator<String>() {
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);
                }
            });

            optionsList.add(0, "None");

            String[] options = optionsList.toArray(new String[0]);

            String initialSelectionID;

            int existingComponentIndex = optionsList.indexOf(((ControlledSource)component).getControllingComponentLabel());

            if( existingComponentIndex == -1)
            {
                initialSelectionID = options[0];
            }
            else
            {
                initialSelectionID = options[existingComponentIndex];
            }

            JComboBox<String> comboBox = new JComboBox<String>(options);
            comboBox.setSelectedItem(initialSelectionID);
            
            ei.comboBox = comboBox;
        
            return ei;
        }
        return null;
    }

    public void setEditValue(int n, EditInfo ei) throws Exception {

        if (n == 0) {
            try
            {
                exprString = ei.textf.getText();
                parseExpr();
                        }
            catch(Throwable nfe)
            {
                JOptionPane.showMessageDialog(null, "The expression was not formatted correctly.", "", JOptionPane.ERROR_MESSAGE);
            }
            
            return;
        }
        if (n == 1) {

            String valueString = (String) ei.comboBox.getSelectedItem();

            if(valueString.equals("None"))
            {
                
                component.getCircuit().cirSim.elmList.remove(wireControl0);
                component.getCircuit().cirSim.elmList.remove(wireControl1);
            }
            
            ((ControlledSource)component).setControllingComponent(valueString);
        }
    }
    
}
