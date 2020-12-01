package org.mathpiper.ui.gui.applications.circuitpiper.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import org.mathpiper.ui.gui.consoles.ColorPane;



/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */

public final class ButtonPanel extends JPanel {

    private WrapLayout wrapLayout;
    static final String[] componentNames = {"Wire", "Resistor", "Capacitor", "Inductor", "DC Voltage Source", "Current Source",
        "Ohmmeter", "Capacitance Meter", "Inductance Meter", "Switch", "Current Integrator", 
        "Voltage Integrator", "AC Voltage Source", "AC Current Source", "VCVS", "VCCS", "CCVS", "CCCS", "Block", "Diode", 
        "Transistor NPN", "Transistor PNP", "Transistor JFET N", "Transistor JFET P", "Logical Package"};

    String[] unsupportedInCirSim = {"Voltmeter", "Ammeter", "Ohmmeter", "Capacitance Meter", "Inductance Meter", "Current Integrator", 
        "Voltage Integrator", "AC Current Source", "CCVS", "Block", "Logical Package"};
    
    
    public JComboBox componentList;
    public List<String> componentNamesList;
    
    public ButtonPanel(final CircuitPanel parentCircuitPanel) {
        super();
        
        componentNamesList = new ArrayList<String>(Arrays.asList(componentNames));

        if(parentCircuitPanel.drawingPanel.circuitPanel.currentCircuit.isCirSim)
        {
            for(String name : unsupportedInCirSim)
            {
                componentNamesList.remove(name);
            }
        }
        
        wrapLayout = new WrapLayout();
        this.setLayout(wrapLayout);
        this.add(new JLabel("Component: "));
        componentList = new JComboBox(componentNamesList.toArray(new String[componentNamesList.size()]));
        componentList.setMaximumRowCount(25);
        this.add(componentList);
        componentList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                String componentName = (String) cb.getSelectedItem();
                parentCircuitPanel.setSelectedComponent(componentName);
                parentCircuitPanel.setIsDrawing(false);
                parentCircuitPanel.setIsMovingPoint(false);
                parentCircuitPanel.setHintStarting();
                parentCircuitPanel.repaint();
                parentCircuitPanel.revalidate();
            }
        });
        componentList.setSelectedIndex(0);

        parentCircuitPanel.setSelectedComponent("Wire");

        JToggleButton runButton = new JToggleButton("Run");
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JToggleButton toggleButton = (JToggleButton) e.getSource();
                if(toggleButton.isSelected())
                {
                    if(parentCircuitPanel.drawingPanel.circuitPanel.currentCircuit.isCirSim == true)
                    {
                        parentCircuitPanel.drawingPanel.circuitPanel.currentCircuit.cirSim.setSimRunning(true);
                    }
                    else
                    {
                        parentCircuitPanel.isRunning = true;
                    }
                }
                else
                {
                    if(parentCircuitPanel.drawingPanel.circuitPanel.currentCircuit.isCirSim == true)
                    {
                        parentCircuitPanel.drawingPanel.circuitPanel.currentCircuit.cirSim.setSimRunning(false);
                    }
                    else
                    {
                        parentCircuitPanel.isRunning = false;
                    }
                }
            }
        });
        this.add(runButton);
        
        JButton dumpButton = new JButton("Dump");
        dumpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try
                {
                    ColorPane textPane = new ColorPane();
                    textPane.setFont(new Font("monospaced", Font.PLAIN, 16));
                    
                    BufferedImage image = ScreenCapture.createImage(parentCircuitPanel.drawingPanel, null);
                    ImageIcon icon = new ImageIcon(image);
                    textPane.insertIcon(icon);
                    
                    textPane.append("\n\n");
                    
                    if(parentCircuitPanel.currentCircuit.isCirSim)
                    {
                        textPane.append(parentCircuitPanel.currentCircuit.cirSim.dumpNodeList());
                        //textPane.append("\n");
                        //textPane.append(parentCircuitPanel.currentCircuit.cirSim.dumpElementList());
                    }
                    else
                    {
                        textPane.append(parentCircuitPanel.currentCircuit.dumpMatrix());
                    }
                    
                    textPane.append("\n\n");
                    
                    textPane.append(parentCircuitPanel.currentCircuit.toString());
                    
                    
                    if(parentCircuitPanel.currentCircuit.isCirSim)
                    {                    
                        textPane.append("\n\nMatrix:\n");

                        parentCircuitPanel.currentCircuit.cirSim.analyzeFlag = true;
                        parentCircuitPanel.currentCircuit.cirSim.isDumpMatrix = true;
                        parentCircuitPanel.currentCircuit.cirSim.updateCircuit();
                        textPane.append(parentCircuitPanel.currentCircuit.cirSim.matrixDump);
                    }
            
                    
                    JFrame frame = new JFrame();
                    Container contentPane = frame.getContentPane();
                    contentPane.add(new JScrollPane(textPane));
                    
                    JMenu fileMenu = new JMenu("File");

                    JMenuItem saveAsImageAction = new JMenuItem();
                    saveAsImageAction.setText("Save As Image");
                    saveAsImageAction.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent ae)
                        {
                            org.mathpiper.ui.gui.Utility.saveImageOfComponent(textPane);
                        }
                    });
                    fileMenu.add(saveAsImageAction);

                    JMenuBar menuBar = new JMenuBar();

                    menuBar.add(fileMenu);

                    frame.setJMenuBar(menuBar);

                    
                    frame.setResizable(true);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.pack();
                    //frame.setSize(400,300);
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    int height = screenSize.height;
                    int width = screenSize.width;
                    //frame.setSize(width/2, height/2);
                    frame.setLocationRelativeTo( null );
                    frame.setVisible(true);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });
        this.add(dumpButton);
        
                

        // playButton.doClick(); todo:tk.
        
        /*
        JCheckBox probeCheckBox = new JCheckBox("Probe");
        //probeCheckBox.setMnemonic(KeyEvent.VK_C); 
        probeCheckBox.setSelected(false);
        probeCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED)
                {
                    parentCircuitEnginePanel.isProbe = true;
                }
                else
                {
                    parentCircuitEnginePanel.isProbe = false;
                }
            }
        });
        this.add(probeCheckBox);
        */
        
        JCheckBox showGridCheckBox = new JCheckBox("Grid");
        //probeCheckBox.setMnemonic(KeyEvent.VK_C); 
        showGridCheckBox.setSelected(true);
        showGridCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED)
                {
                    parentCircuitPanel.drawingPanel.isDrawGrid = true;
                    parentCircuitPanel.repaint();
                }
                else
                {
                    parentCircuitPanel.drawingPanel.isDrawGrid = false;
                    parentCircuitPanel.repaint();
                }
            }
        });
        parentCircuitPanel.showGridCheckBox = showGridCheckBox;
        this.add(showGridCheckBox);

        
        JCheckBox showWiresCheckBox = new JCheckBox("Wires");
        //probeCheckBox.setMnemonic(KeyEvent.VK_C); 
        showWiresCheckBox.setSelected(false);
        showWiresCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED)
                {
                    parentCircuitPanel.drawingPanel.isDrawWireLabels = true;
                    parentCircuitPanel.repaint();
                }
                else
                {
                    parentCircuitPanel.drawingPanel.isDrawWireLabels = false;
                    parentCircuitPanel.repaint();
                }
            }
        });
        this.add(showWiresCheckBox);
        
        JCheckBox showTerminalsCheckBox = new JCheckBox("Terminals");
        //probeCheckBox.setMnemonic(KeyEvent.VK_C); 
        showTerminalsCheckBox.setSelected(false);
        showTerminalsCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED)
                {
                    parentCircuitPanel.drawingPanel.isDrawTerminalLabels = true;
                    parentCircuitPanel.repaint();
                }
                else
                {
                    parentCircuitPanel.drawingPanel.isDrawTerminalLabels = false;
                    parentCircuitPanel.repaint();
                }
            }
        });
        this.add(showTerminalsCheckBox);
        
        JCheckBox isShowComponentValues = new JCheckBox("Values");
        //probeCheckBox.setMnemonic(KeyEvent.VK_C); 
        isShowComponentValues.setSelected(true);
        isShowComponentValues.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED)
                {
                    parentCircuitPanel.drawingPanel.isDrawComponentValues = true;
                    parentCircuitPanel.repaint();
                }
                else
                {
                    parentCircuitPanel.drawingPanel.isDrawComponentValues = false;
                    parentCircuitPanel.repaint();
                }
            }
        });
        this.add(isShowComponentValues);
        
        JCheckBoxMenuItem componentHighlightingModeCheckBox = new JCheckBoxMenuItem("Highlight");
        //probeCheckBox.setMnemonic(KeyEvent.VK_C); 
        componentHighlightingModeCheckBox.setSelected(false);
        componentHighlightingModeCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED)
                {
                    parentCircuitPanel.drawingPanel.isComponentHighlightingMode = true;
                }
                else
                {
                    parentCircuitPanel.drawingPanel.isComponentHighlightingMode = false;
                }
            }
        });
        this.add(componentHighlightingModeCheckBox);
  
        
        /*
        JButton moveLeftButton = new JButton("Left");
        moveLeftButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    for (Terminal terminal : parentCircuitEnginePanel.circuit.myTerminals.values()) {
                       terminal.setX(terminal.getX() - parentCircuitEnginePanel.xDistanceBetweenTerminalsPixels);
                    }               
                    parentCircuitEnginePanel.repaint();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        this.add(moveLeftButton);
        
        
        JButton moveRightButton = new JButton("Right");
        moveRightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    for (Terminal terminal : parentCircuitEnginePanel.circuit.myTerminals.values()) {
                       terminal.setX(terminal.getX() + parentCircuitEnginePanel.xDistanceBetweenTerminalsPixels);
                    }               
                    parentCircuitEnginePanel.repaint();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        this.add(moveRightButton);
        
        
        JButton moveUpButton = new JButton("Up");
        moveUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    for (Terminal terminal : parentCircuitEnginePanel.circuit.myTerminals.values()) {
                       terminal.setY(terminal.getY() - parentCircuitEnginePanel.yDistanceBetweenTerminalsPixels);
                    }               
                    parentCircuitEnginePanel.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        this.add(moveUpButton);
        
        
        JButton moveDownButton = new JButton("Down");
        moveDownButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    for (Terminal terminal : parentCircuitEnginePanel.circuit.myTerminals.values()) {
                       terminal.setY(terminal.getY() + parentCircuitEnginePanel.yDistanceBetweenTerminalsPixels);
                    }               
                    parentCircuitEnginePanel.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        this.add(moveDownButton);
        */
        
  }
}
