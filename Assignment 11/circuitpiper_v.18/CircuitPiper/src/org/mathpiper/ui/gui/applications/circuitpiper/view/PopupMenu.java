package org.mathpiper.ui.gui.applications.circuitpiper.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class PopupMenu extends JPopupMenu {

    private CircuitPanel circuitPanel;
    

    public PopupMenu(CircuitPanel circuitPanel) {
        this.circuitPanel = circuitPanel;
        
        final CircuitPanel finalCircuitPanel = circuitPanel;
          
        JMenuItem anItem = new JMenuItem("Cancel");
        anItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                finalCircuitPanel.setIsDrawing(false);
                finalCircuitPanel.setHintStarting();
                finalCircuitPanel.repaint();
            }
        });

        add(anItem);
    }

}
