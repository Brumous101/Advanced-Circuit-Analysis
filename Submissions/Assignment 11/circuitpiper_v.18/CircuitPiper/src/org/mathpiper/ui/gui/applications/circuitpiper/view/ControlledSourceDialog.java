package org.mathpiper.ui.gui.applications.circuitpiper.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ControlledSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Meter;


public class ControlledSourceDialog extends JFrame {
    
    public ControlledSourceDialog(ControlledSource controlledSource, Map<String, Component> components)
    {
        setAlwaysOnTop(true);

        
        List<String> optionsList = new ArrayList();

        
        Iterator<Component> itr = components.values().iterator();
        while (itr.hasNext()) {
            Component component = itr.next();
            
            if(component instanceof Meter)
            {
                continue;
            }

            optionsList.add(component.getID());
        }
        
        optionsList.remove(controlledSource.getID());
        
        Collections.sort(optionsList, new Comparator<String>() {
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        
        optionsList.add(0, "None");
        
        String[] options = optionsList.toArray(new String[0]);
        
        String initialSelectionID;
        
        int existingComponentIndex = optionsList.indexOf(controlledSource.getControllingComponentLabel());
        
        if( existingComponentIndex == -1)
        {
            initialSelectionID = options[0];
        }
        else
        {
            initialSelectionID = options[existingComponentIndex];
        }
        
        Object selectionObject = JOptionPane.showInputDialog(this, "Choose", "Menu", JOptionPane.PLAIN_MESSAGE, null, options, initialSelectionID);
        
        if(selectionObject != null)
        {
            Component component = components.get(selectionObject);
            
            controlledSource.setControllingComponent(component.getID());
        }
    }
   
}
