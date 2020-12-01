package org.mathpiper.ui.gui.applications.circuitpiper.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */
public final class HintPanel extends JPanel {

    CircuitPanel circuitPanel;
    String hint = "";
    private JPanel statePanel = new JPanel();
    private JTextField messageField = new JTextField();

    public HintPanel(final CircuitPanel theParentCircuitEnginePanel) {
        super();
        circuitPanel = theParentCircuitEnginePanel;
        
        statePanel.setLayout(new BoxLayout(statePanel, BoxLayout.Y_AXIS));
        
        this.setLayout(new BorderLayout());
        
        this.add(statePanel);
        
        this.add(messageField, BorderLayout.SOUTH);
    }

    public void startingHint() {
        hint = "starting";
        this.statePanel.removeAll();

        JLabel label = new JLabel("Click: Create a new " + circuitPanel.mySelectedComponent.toLowerCase());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.statePanel.add(label);
        
        label = new JLabel("Right Click:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.statePanel.add(label);
        label = new JLabel("Drag:");
        
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.statePanel.add(label);
        //myParentCircuitEnginePanel.setDrawing(false);
        //myParentCircuitEnginePanel.setMoving(false);
        circuitPanel.validate();
    }

    public void drawingHint() {
        hint = "drawing";
        this.statePanel.removeAll();

        //label.setAlignmentX(Component.CENTER_ALIGNMENT);
        //this.statePanel.add(label);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("Click: Create a " + circuitPanel.mySelectedComponent.toLowerCase()));
        JButton b = new JButton("Cancel");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                circuitPanel.setIsDrawing(false);
                //startingHint();
                circuitPanel.repaint();
            }
        });
        panel.add(b);
        this.statePanel.add(panel);
        JLabel label = new JLabel("Right Click:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.statePanel.add(label);
        label = new JLabel("Drag:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.statePanel.add(label);
        circuitPanel.validate();
    }

    public void nearTerminalHint() {
        hint = "tiepoint";
        this.statePanel.removeAll();
        JLabel label = new JLabel("Click: Create a new " + circuitPanel.mySelectedComponent.toLowerCase());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.statePanel.add(label);
        label = new JLabel("Right Click: Tiepoint options");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.statePanel.add(label);
        label = new JLabel("Drag: Move tiepoint");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.statePanel.add(label);
        //myParentCircuitEnginePanel.setDrawing(false);
        //myParentCircuitEnginePanel.setMoving(false);
        circuitPanel.validate();
    }

    public void nearSwitchHint() {
        hint = "switch";
        this.statePanel.removeAll();
        JLabel label = new JLabel("Click: Toggle switch");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.statePanel.add(label);
        label = new JLabel("Right Click: Component options");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.statePanel.add(label);
        label = new JLabel("Drag:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.statePanel.add(label);
        //myParentCircuitEnginePanel.setDrawing(false);
        //myParentCircuitEnginePanel.setMoving(false);
        circuitPanel.validate();
    }

    public void nearComponentHint() {
        hint = "component";
        this.statePanel.removeAll();
        JLabel label = new JLabel("Click: Create a new " + circuitPanel.mySelectedComponent.toLowerCase());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.statePanel.add(label);
        label = new JLabel("Right Click: Component options");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.statePanel.add(label);
        label = new JLabel("Drag: Move component");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.statePanel.add(label);
        //myParentCircuitEnginePanel.setDrawing(false);
        //myParentCircuitEnginePanel.setMoving(false);
        circuitPanel.validate();
    }

    public void movingHint() {

    }
    
    public void message(String message)
    {
        this.messageField.setText(message);
    }
}
