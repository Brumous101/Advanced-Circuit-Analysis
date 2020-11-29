package org.mathpiper.ui.gui.applications.circuitpiper.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Capacitor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Inductor;

/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */
public final class ChangePrimaryValueFrame extends JFrame implements ActionListener {

    //private String ohm="\u2126";
    private String micro = "\u03BC";
    private String[] siprefixes = {"Y", "Z", "E", "P", "T", "G", "M", "k", "", "m", micro, "n", "p", "f", "a", "z", "y"};
    private JButton okayButton, cancelButton;
    private String okayButtonString = "OK";
    private String cancelButtonString = "Cancel";
    private JPanel buttonPanel;
    private JPanel entryPanel;
    private JTextField primaryValueField;
    private JComboBox unitComboBox;
    private Component component;
    private String[] prefixes;
    private HashMap prefixMap;
    private JLabel unitLabel;
    private CircuitPanel myParent;

    public ChangePrimaryValueFrame(CircuitPanel parent, int x, int y, Component ec) {
        super();
        component = ec;
        myParent = parent;
        //this.setSize(300,200);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout());
        okayButton = new JButton(okayButtonString);
        cancelButton = new JButton(cancelButtonString);
        okayButton.addActionListener(this);
        cancelButton.addActionListener(this);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(okayButton);
        buttonPanel.add(cancelButton);
        entryPanel = new JPanel();
        entryPanel.setLayout(new FlowLayout());
        entryPanel.add(new JLabel(ec.getPrimarySymbol() + " = "));
        primaryValueField = new JTextField(15);
        if (ec.getEnteredPrimaryValue() != null) {
            primaryValueField.setText(ec.getEnteredPrimaryValue());
        }
        entryPanel.add(primaryValueField);
        prefixes = new String[siprefixes.length];
        prefixMap = new HashMap();
        int power = 24;
        for (int i = 0; i < siprefixes.length; i++) {
            prefixes[i] = siprefixes[i] + ec.getPrimaryUnitSymbol();
            prefixMap.put(prefixes[i], power);
            //System.out.println(prefixes[i]);
            power -= 3;
        }
        unitComboBox = new JComboBox(prefixes);
        if (ec.getSelectedPrimaryPrefix() != null) {
            unitComboBox.setSelectedItem(ec.getSelectedPrimaryPrefix());
        } else {
            unitComboBox.setSelectedItem(ec.getPreselectedPrimaryPrefix());
        }
        unitComboBox.addActionListener(this);
        entryPanel.add(unitComboBox);
        unitLabel = new JLabel();//"("+electricComponent.primaryUnitPlural+")");
        String s = (String) unitComboBox.getSelectedItem();
        if (s.equals(ec.getPrimaryUnitSymbol())) {
            unitLabel.setText("(" + ec.getPrimaryUnitPlural() + ")");
        } else {
            unitLabel.setText("(" + "10E" + (Integer) prefixMap.get(s) + " " + ec.getPrimaryUnitPlural() + ")");
        }
        entryPanel.add(unitLabel);
        this.add(entryPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.getRootPane().setDefaultButton(okayButton);
        this.pack();
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals("Cancel")) {
            dispose();
        } else if (actionCommand.equals("OK")) {
            synchronized (myParent.drawingPanel) {
                if (myParent.currentCircuit.components.containsValue(component)) {
                    String text = primaryValueField.getText();
                    if (text.equals("")) {
                        component.setPrimaryValue(null);
                        component.setEnteredPrimaryValue(text);
                        component.setSelectedPrimaryPrefix((String) unitComboBox.getSelectedItem());
                        dispose();
                        myParent.repaint();
                    } else {
                        try {
                            String s = (String) unitComboBox.getSelectedItem();
                            if ((component instanceof Capacitor || component instanceof Inductor)
                                    && Double.parseDouble(text) == 0.0) {
                                JOptionPane.showMessageDialog(this, "Cannot be zero.");
                                return;
                            }
                            component.setPrimaryValue((Double) Double.parseDouble(text) * Math.pow(10, (Integer) prefixMap.get(s)));
                            component.setEnteredPrimaryValue(text);
                            component.setSelectedPrimaryPrefix((String) unitComboBox.getSelectedItem());
                            dispose();
                            myParent.repaint();
                        } catch (NumberFormatException errorexcept) {
                            //System.out.println("invalid number exception");
                            JOptionPane.showMessageDialog(this, "Not a valid number.");
                        }
                    }
                    //Double.parseDouble(aString)System.out.println(5);
                } else {
                    //System.out.println("no such component");
                    JOptionPane.showMessageDialog(this, "That component no longer exists.");
                    dispose();
                }
            }
        } else if (actionCommand.equals("comboBoxChanged")) {
            //System.out.println(actionCommand);
            //System.out.println("changed");
            String s = (String) unitComboBox.getSelectedItem();
            //System.out.println(s);
            if (s.equals(component.getPrimaryUnitSymbol())) {
                unitLabel.setText("(" + component.getPrimaryUnitPlural() + ")");
            } else {
                unitLabel.setText("(" + "10E" + (Integer) prefixMap.get(s) + " " + component.getPrimaryUnitPlural() + ")");
            }
            pack();
        } else {
            //System.out.println("Unexpected Error in Configure Resistor Window");
        }
    }
}
