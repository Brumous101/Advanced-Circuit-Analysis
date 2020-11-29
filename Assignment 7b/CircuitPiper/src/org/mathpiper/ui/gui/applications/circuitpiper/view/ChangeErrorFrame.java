package org.mathpiper.ui.gui.applications.circuitpiper.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

public final class ChangeErrorFrame extends JFrame implements ActionListener {

    private JButton okayButton, cancelButton, defaultButton;
    private String okayButtonString = "OK";
    private String cancelButtonString = "Cancel";
    private String defaultButtonString = "Default";
    private JPanel buttonPanel;
    private JPanel capPanel;
    private JPanel indPanel;
    private JTextField capField;
    private JTextField indField;
    private CircuitPanel myParent;

    public ChangeErrorFrame(CircuitPanel parent, int x, int y) {
        super();
        myParent = parent;
        //this.setSize(300,200);
        this.setLocationRelativeTo(myParent);
        this.setLocation(100, 100);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout());
        okayButton = new JButton(okayButtonString);
        cancelButton = new JButton(cancelButtonString);
        defaultButton = new JButton(defaultButtonString);
        okayButton.addActionListener(this);
        cancelButton.addActionListener(this);
        defaultButton.addActionListener(this);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(okayButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(defaultButton);
        
        JPanel midPanel = new JPanel();
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));
        midPanel.add(new JLabel("The step-size will be adjusted to obtain the specified truncation error."));
        midPanel.add(new JLabel("If the values entered are too small, excessively small step-sizes"));
        midPanel.add(new JLabel("will result, causing slow simulation and increased rounding errors."));
        midPanel.add(new JLabel("If the values entered are too large, the simulation will run faster,"));
        midPanel.add(new JLabel("and rounding errors will decrease, but truncation errors will increase."));
        midPanel.add(new JLabel("Try changing one or both values by one or two powers of ten at a time."));
        
        midPanel.add(Box.createVerticalStrut(10));
        
        capPanel = new JPanel();
        //capPanel.setLayout(new FlowLayout());
        capPanel.setLayout(new BoxLayout(capPanel, BoxLayout.Y_AXIS));
        capField = new JTextField(15);
        capField.setText(myParent.capacitorErrorString);
        capPanel.add(new JLabel("Approximate allowed truncation error per step for the charge on"));
        capPanel.add(new JLabel("capacitors, current integrators, and AC current sources in coulombs:"));
        //capPanel.add(new JLabel("Maximum Fractional Increment Error Estimate:"));
        capPanel.add(capField);

        //midPanel.add(new JLabel("The second rule overrides the first to prevent excessively small step-sizes."));//,BorderLayout.NORTH);
        midPanel.add(capPanel);
        
        midPanel.add(Box.createVerticalStrut(10));
        
        indPanel = new JPanel();
        //indPanel.setLayout(new FlowLayout());
        indPanel.setLayout(new BoxLayout(indPanel, BoxLayout.Y_AXIS));
        indField = new JTextField(15);
        indField.setText(myParent.inductorErrorString);
        indPanel.add(new JLabel("Approximate allowed truncation error per step for the magnetic flux"));
        indPanel.add(new JLabel("through inductors, voltage integrators and AC voltage sources in webers:"));
        indPanel.add(indField);
        
        midPanel.add(indPanel);
        
        this.add(midPanel, BorderLayout.CENTER);
        
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
                try {
                    String texta = capField.getText();
                    double a = Double.parseDouble(texta);
                    String textb = indField.getText();
                    double b = Double.parseDouble(textb);
                    if (a <= 0 || b <= 0) {
                        JOptionPane.showMessageDialog(this, "Values must be positive.");
                        return;
                    }
                    myParent.capacitorErrorString = capField.getText();
                    myParent.inductorErrorString = indField.getText();
                    myParent.capacitorError = a;
                    myParent.inductanceError = b;
                    dispose();
                    myParent.repaint();
                } catch (NumberFormatException errorexcept) {
                    //System.out.println("invalid number exception");
                    JOptionPane.showMessageDialog(this, "Not a valid number.");
                }
            }
        } else if (actionCommand.equals("Default")) {
            capField.setText(myParent.defaultCapacitorErrorString);
            indField.setText(myParent.defaultInductorErrorString);
        } else {
            //System.out.println("Unexpected Error in Configure Resistor Window");
        }
    }

}
