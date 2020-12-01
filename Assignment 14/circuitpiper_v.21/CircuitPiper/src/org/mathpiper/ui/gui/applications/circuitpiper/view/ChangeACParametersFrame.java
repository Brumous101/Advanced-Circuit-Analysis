package org.mathpiper.ui.gui.applications.circuitpiper.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
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
public final class ChangeACParametersFrame extends JFrame implements ActionListener {
//private String ohm="\u2126";

    private String micro = "\u03BC";
    private String[] siprefixes = {"Y", "Z", "E", "P", "T", "G", "M", "k", "", "m", micro, "n", "p", "f", "a", "z", "y"};
    private JButton okayButton, cancelButton;
    private String okayButtonString = "OK";
    private String cancelButtonString = "Cancel";
    private JPanel buttonPanel;

    private JPanel peakPanel;
    private JTextField peakValueField;
    private JComboBox peakPrefixComboBox;
    private JLabel peakUnitLabel;

    private JPanel frequencyPanel;
    private JTextField frequencyField;
    private JComboBox frequencyPrefixComboBox;
    private JLabel frequencyUnitLabel;

    private JPanel phaseShiftPanel;
    private JTextField phaseShiftField;
    private JComboBox phaseShiftPrefixComboBox;
    private JLabel phaseShiftUnitLabel;

    private Component component;
    private String[] peakPrefixes;
    private HashMap peakPrefixMap;
    private String[] frequencyPrefixes;
    private HashMap frequencyPrefixMap;
    private String[] phaseShiftPrefixes;
    private HashMap phaseShiftPrefixMap;

    private CircuitPanel myParent;

    public ChangeACParametersFrame(CircuitPanel parent, int x, int y, Component ec) {
        super();
        component = ec;
        myParent = parent;
        //this.setSize(300,200);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        okayButton = new JButton(okayButtonString);
        cancelButton = new JButton(cancelButtonString);
        okayButton.addActionListener(this);
        cancelButton.addActionListener(this);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(okayButton);
        buttonPanel.add(cancelButton);
        peakPanel = new JPanel();
        peakPanel.setLayout(new FlowLayout());
        peakPanel.add(new JLabel(ec.getPrimarySymbol() + "peak" + " = "));
        peakValueField = new JTextField(15);

        frequencyPanel = new JPanel();
        frequencyPanel.setLayout(new FlowLayout());
        frequencyPanel.add(new JLabel(ec.getFrequencySymbol() + " = "));
        frequencyField = new JTextField(15);

        phaseShiftPanel = new JPanel();
        phaseShiftPanel.setLayout(new FlowLayout());
        phaseShiftPanel.add(new JLabel("phase shift" + " = "));
        phaseShiftField = new JTextField(15);

        if (ec.getEnteredPrimaryValue() != null) {
            peakValueField.setText(ec.getEnteredPrimaryValue());
        }
        peakPanel.add(peakValueField);
        peakPrefixes = new String[siprefixes.length];
        peakPrefixMap = new HashMap();
        int power = 24;
        for (int i = 0; i < siprefixes.length; i++) {
            peakPrefixes[i] = siprefixes[i] + ec.getPrimaryUnitSymbol();
            peakPrefixMap.put(peakPrefixes[i], power);
            //System.out.println(prefixes[i]);
            power -= 3;
        }
        peakPrefixComboBox = new JComboBox(peakPrefixes);
        if (ec.getSelectedPrimaryPrefix() != null) {
            peakPrefixComboBox.setSelectedItem(ec.getSelectedPrimaryPrefix());
        } else {
            peakPrefixComboBox.setSelectedItem(ec.getPreselectedPrimaryPrefix());
        }
        peakPrefixComboBox.addActionListener(this);
        peakPanel.add(peakPrefixComboBox);
        peakUnitLabel = new JLabel();//"("+electricComponent.primaryUnitPlural+")");
        String s = (String) peakPrefixComboBox.getSelectedItem();
        if (s.equals(ec.getPrimaryUnitSymbol())) {
            peakUnitLabel.setText("(" + ec.getPrimaryUnitPlural() + ")");
        } else {
            peakUnitLabel.setText("(" + "10E" + (Integer) peakPrefixMap.get(s) + " " + ec.getPrimaryUnitPlural() + ")");
        }
        peakPanel.add(peakUnitLabel);

        if (ec.getEnteredFrequency() != null) {
            frequencyField.setText(ec.getEnteredFrequency());
        }
        frequencyPanel.add(frequencyField);
        frequencyPrefixes = new String[siprefixes.length];
        frequencyPrefixMap = new HashMap();
        power = 24;
        for (int i = 0; i < siprefixes.length; i++) {
            frequencyPrefixes[i] = siprefixes[i] + "Hz";
            frequencyPrefixMap.put(frequencyPrefixes[i], power);
            //System.out.println(prefixes[i]);
            power -= 3;
        }
        frequencyPrefixComboBox = new JComboBox(frequencyPrefixes);
        if (ec.getSelectedFrequencyPrefix() != null) {
            frequencyPrefixComboBox.setSelectedItem(ec.getSelectedFrequencyPrefix());
        } else {
            frequencyPrefixComboBox.setSelectedItem(ec.getPreselectedFrequencyPrefix());
        }
        frequencyPrefixComboBox.addActionListener(this);
        frequencyPanel.add(frequencyPrefixComboBox);
        frequencyUnitLabel = new JLabel();//"("+electricComponent.primaryUnitPlural+")");
        s = (String) frequencyPrefixComboBox.getSelectedItem();
        if (s.equals("Hz")) {
            frequencyUnitLabel.setText("(Hz)");
        } else {
            frequencyUnitLabel.setText("(" + "10E" + (Integer) frequencyPrefixMap.get(s) + " Hz)");
        }
        frequencyPanel.add(frequencyUnitLabel);

        if (ec.getEnteredPhaseShift() != null) {
            phaseShiftField.setText(ec.getEnteredPhaseShift());
        }
        phaseShiftPanel.add(phaseShiftField);
        phaseShiftPrefixes = new String[siprefixes.length];
        phaseShiftPrefixMap = new HashMap();
        power = 24;
        for (int i = 0; i < siprefixes.length; i++) {
            phaseShiftPrefixes[i] = siprefixes[i] + "s";
            phaseShiftPrefixMap.put(phaseShiftPrefixes[i], power);
            //System.out.println(prefixes[i]);
            power -= 3;
        }
        phaseShiftPrefixComboBox = new JComboBox(phaseShiftPrefixes);
        if (ec.getSelectedPhaseShiftPrefix() != null) {
            phaseShiftPrefixComboBox.setSelectedItem(ec.getSelectedPhaseShiftPrefix());
        } else {
            phaseShiftPrefixComboBox.setSelectedItem(ec.getPreselectedPhaseShiftPrefix());
        }
        phaseShiftPrefixComboBox.addActionListener(this);
        phaseShiftPanel.add(phaseShiftPrefixComboBox);
        phaseShiftUnitLabel = new JLabel();//"("+electricComponent.primaryUnitPlural+")");
        s = (String) phaseShiftPrefixComboBox.getSelectedItem();
        if (s.equals("s")) {
            phaseShiftUnitLabel.setText("(s)");
        } else {
            phaseShiftUnitLabel.setText("(" + "10E" + (Integer) phaseShiftPrefixMap.get(s) + " s)");
        }
        phaseShiftPanel.add(phaseShiftUnitLabel);

        this.add(new JLabel(ec.getPrimarySymbol() + " = " + ec.getPrimarySymbol() + "peak * sin (2 * \u03C0 * f * (t + phaseshift))"));
        this.add(peakPanel);
        this.add(frequencyPanel);
        this.add(phaseShiftPanel);
        this.add(buttonPanel);
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
                    String text = peakValueField.getText();
                    if (text.equals("")) {
                        component.setPrimaryValue(null);
                        component.setEnteredPrimaryValue(text);
                        component.setSelectedPrimaryPrefix((String) peakPrefixComboBox.getSelectedItem());
                    } else {
                        try {
                            String s = (String) peakPrefixComboBox.getSelectedItem();
                            component.setPrimaryValue((Double) Double.parseDouble(text) * Math.pow(10, (Integer) peakPrefixMap.get(s)));
                            component.setEnteredPrimaryValue(text);
                            component.setSelectedPrimaryPrefix((String) peakPrefixComboBox.getSelectedItem());
                        } catch (NumberFormatException errorexcept) {
                            //System.out.println("invalid number exception");
                            JOptionPane.showMessageDialog(this, "Peak value not a valid number.");
                            return;
                        }
                    }

                    text = frequencyField.getText();
                    if (text.equals("")) {
                        component.setFrequency(null);
                        component.setEnteredFrequency(text);
                        component.setSelectedFrequencyPrefix((String) frequencyPrefixComboBox.getSelectedItem());
                    } else {
                        try {
                            String s = (String) frequencyPrefixComboBox.getSelectedItem();
                            component.setFrequency((Double) Double.parseDouble(text) * Math.pow(10, (Integer) frequencyPrefixMap.get(s)));
                            component.setEnteredFrequency(text);
                            component.setSelectedFrequencyPrefix((String) frequencyPrefixComboBox.getSelectedItem());
                        } catch (NumberFormatException errorexcept) {
                            //System.out.println("invalid number exception");
                            JOptionPane.showMessageDialog(this, "Frequency not a valid number.");
                            return;
                        }
                    }

                    text = phaseShiftField.getText();
                    if (text.equals("")) {
                        component.setPhaseShift(null);
                        component.setEnteredPhaseShift(text);
                        component.setSelectedPhaseShiftPrefix((String) phaseShiftPrefixComboBox.getSelectedItem());

                    } else {
                        try {
                            String s = (String) phaseShiftPrefixComboBox.getSelectedItem();
                            component.setPhaseShift((Double) Double.parseDouble(text) * Math.pow(10, (Integer) phaseShiftPrefixMap.get(s)));
                            component.setEnteredPhaseShift(text);
                            component.setSelectedPhaseShiftPrefix((String) phaseShiftPrefixComboBox.getSelectedItem());
                        } catch (NumberFormatException errorexcept) {
                            //System.out.println("invalid number exception");
                            JOptionPane.showMessageDialog(this, "Phase shift not a valid number.");
                            return;
                        }
                    }
                    dispose();
                    myParent.repaint();

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
            String s = (String) peakPrefixComboBox.getSelectedItem();
            //System.out.println(s);
            if (s.equals(component.getPrimaryUnitSymbol())) {
                peakUnitLabel.setText("(" + component.getPrimaryUnitPlural() + ")");
            } else {
                peakUnitLabel.setText("(" + "10E" + (Integer) peakPrefixMap.get(s) + " " + component.getPrimaryUnitPlural() + ")");
            }

            s = (String) frequencyPrefixComboBox.getSelectedItem();
            //System.out.println(s);
            if (s.equals("Hz")) {
                frequencyUnitLabel.setText("(Hz)");
            } else {
                frequencyUnitLabel.setText("(" + "10E" + (Integer) frequencyPrefixMap.get(s) + " Hz)");
            }

            s = (String) phaseShiftPrefixComboBox.getSelectedItem();
            //System.out.println(s);
            if (s.equals("s")) {
                phaseShiftUnitLabel.setText("(s)");
            } else {
                phaseShiftUnitLabel.setText("(" + "10E" + (Integer) phaseShiftPrefixMap.get(s) + " s)");
            }
            pack();
        } else {
            //System.out.println("Unexpected Error in Configure Resistor Window");
        }
    }
}
