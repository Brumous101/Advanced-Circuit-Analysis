package org.mathpiper.ui.gui.applications.circuitpiper.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Ammeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.CapacitanceMeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.CurrentIntegrator;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Capacitor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Inductor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.InductanceMeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Meter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Ohmmeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.VoltageIntegrator;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Voltmeter;
import org.mathpiper.ui.gui.consoles.ColorPane;

public class CircuitPiperMain extends JPanel {

    private CircuitPanel circuitPanel;
    
    public JCheckBoxMenuItem isCirSimCheckBox = null;
    
    private JInternalFrame schematicFrame = null;
    
    private JFrame frame;

    public CircuitPiperMain(JFrame frame) throws Exception {
        
        this.frame = frame;
        
        circuitPanel = new CircuitPanel();
        circuitPanel.circuitPiperMain = this;
        
        ReadoutPanel readoutPanel = new ReadoutPanel();
        circuitPanel.readoutPanel = readoutPanel;
        
        ScopesPanel scopesPanel = new ScopesPanel();
        circuitPanel.scopesPanel = scopesPanel;
        
        JDesktopPane desktopPane = new JDesktopPane();
        
        schematicFrame = new JInternalFrame();
        schematicFrame.getContentPane().add(circuitPanel);
        schematicFrame.setJMenuBar(createMenuBar());
        schematicFrame.setResizable(true);
        schematicFrame.setIconifiable(true);
        schematicFrame.setMaximizable(true);
        schematicFrame.pack();
        schematicFrame.setTitle("Schematic");
        //schematicFrame.setMaximum(true); // todo:tk:comment out for release.
        schematicFrame.setSize(645, 650); // todo:tk:make size relative to its container.
        schematicFrame.setLocation(0, 0);
        schematicFrame.setVisible(true);
        desktopPane.add(schematicFrame);
        
        JInternalFrame readoutFrame = new JInternalFrame();
        readoutFrame.getContentPane().add(readoutPanel);
        readoutFrame.setResizable(true);
        readoutFrame.setIconifiable(true);
        readoutFrame.setMaximizable(true);
        readoutFrame.pack();
        readoutFrame.setTitle("Readout");
        readoutFrame.setMaximum(false);
        readoutFrame.setSize(300, 250);
        readoutFrame.setLocation(650, 0);  // todo:tk:set location relative to the schematic frame.
        readoutFrame.setVisible(true);
        desktopPane.add(readoutFrame);
        
        JInternalFrame scopesFrame = new JInternalFrame();
        JScrollPane scrollPane = new JScrollPane(scopesPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scopesFrame.getContentPane().add(scrollPane);
        scopesFrame.setResizable(true);
        scopesFrame.setIconifiable(true);
        scopesFrame.setMaximizable(true);
        scopesFrame.pack();
        scopesFrame.setTitle("Scopes");
        scopesFrame.setMaximum(false);
        scopesFrame.setSize(300, 250);
        scopesFrame.setLocation(650, 300);
        scopesFrame.setVisible(true);
        desktopPane.add(scopesFrame);
        

        setLayout(new BorderLayout());
        add(desktopPane);
        //panel.add(createMenuBar(), BorderLayout.PAGE_START);
    }

    public JMenuBar createMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        // ============================== File Menu
        JMenu fileMenu = new JMenu("File");

        
        JMenuItem openMenuItem = new JMenuItem();
        openMenuItem.setText("Open");
        openMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                JFileChooser openCircuitFileChooser = new JFileChooser();

                //File file = new File("/home/tkosan/circuitpiper/problem_2_26.txt");// todo:tk
                //openCircuitFileChooser.setSelectedFile(file);
                
                FileFilter filter = new FileNameExtensionFilter("Circuit file", "cpcir");
                openCircuitFileChooser.addChoosableFileFilter(filter);

                int returnValue = openCircuitFileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File circuitFile = openCircuitFileChooser.getSelectedFile();
                    String filePath = null;
                    
                    try {
                        filePath = circuitFile.getCanonicalPath();
                        circuitPanel.isRunning = false;
                        circuitPanel.currentCircuit.open(filePath);
                        circuitPanel.currentCircuit.id = filePath;

                        if(! circuitPanel.currentCircuit.isCirSim)
                        {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        circuitPanel.currentCircuit.updateCircuit();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    //circuitEnginePanel.isRunning = true;
                                    //circuitEnginePanel.setIsDrawing(true);
                                    circuitPanel.setHintDrawing();

                                    circuitPanel.repaint();

                                    circuitPanel.waiting = false;
                                }
                            });
                        }
                        frame.setTitle("CircuitPiper - " + filePath);
                    } catch (NoSuchFileException e) {
                        JOptionPane.showMessageDialog(null, "The file " + filePath + " does not exist.");
                    }
                    catch (java.io.IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        fileMenu.add(openMenuItem);

        JMenuItem saveMenuItem = new JMenuItem();
        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser saveCircuitFileChooser = new JFileChooser();

                FileFilter filter = new FileNameExtensionFilter("Serialzed circuit file", "ser");
                saveCircuitFileChooser.addChoosableFileFilter(filter);

                int returnValue = saveCircuitFileChooser.showSaveDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File circuitFile = saveCircuitFileChooser.getSelectedFile();
                    try {
                        circuitPanel.currentCircuit.save(circuitFile.getCanonicalPath());
                        frame.setTitle("CircuitPiper - " + circuitFile.getCanonicalPath());
                    } catch (java.io.IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        });
        fileMenu.add(saveMenuItem);

        JMenuItem exportMenuItem = new JMenuItem("Export");
        exportMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JTextArea textArea = new JTextArea(30, 70);
                    textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
                    textArea.append(circuitPanel.currentCircuit.export());
                    textArea.setCaretPosition(0);
                    JOptionPane.showMessageDialog(null, new JScrollPane(textArea), "", JOptionPane.OK_OPTION);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        fileMenu.add(exportMenuItem);
        
        JMenuItem importMenuItem = new JMenuItem("Import");
        importMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JTextArea textArea = new JTextArea(30, 70);
                    textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
                    
                    switch (JOptionPane.showConfirmDialog(null, new JScrollPane(textArea), "", JOptionPane.OK_CANCEL_OPTION)) {
                        case JOptionPane.OK_OPTION:
                            circuitPanel.currentCircuit.readCircuit(textArea.getText());
                            break;
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        fileMenu.add(importMenuItem);

        JMenuItem clearMenuItem = new JMenuItem("New");
        clearMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try
                {
                    circuitPanel.currentCircuit.clear();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });
        fileMenu.add(clearMenuItem);

        JMenuItem solverOptionsMenuItem = new JMenuItem("Numerical Solver Options");
        solverOptionsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ChangeErrorFrame(circuitPanel, 0, 0);
            }
        });
        if(! this.circuitPanel.currentCircuit.isCirSim)
        {
            fileMenu.add(solverOptionsMenuItem);
        }

        JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Mark Capacitors and Inductors with +");
        cbMenuItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                circuitPanel.drawingPanel.drawPlus = e.getStateChange() == ItemEvent.SELECTED;
                circuitPanel.drawingPanel.repaint();
            }
        });
        cbMenuItem.doClick();
        fileMenu.add(cbMenuItem);

        JCheckBoxMenuItem timeMenuItem = new JCheckBoxMenuItem("Time Passes");
        timeMenuItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                circuitPanel.timeOption = e.getStateChange() == ItemEvent.SELECTED;
            }
        });
        timeMenuItem.doClick();
        fileMenu.add(timeMenuItem);
        
        JMenuItem resetAllMenuItem = new JMenuItem("Reset All");
        resetAllMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetTimeAndGraphs();
                resetMetersInductorsCapacitors();
            }
        });
        fileMenu.add(resetAllMenuItem);

        JMenuItem resetTimeMenuItem = new JMenuItem("Reset Time to 0 (Clears all Graphs)");
        resetTimeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetTimeAndGraphs();
            }
        });
        fileMenu.add(resetTimeMenuItem);
        
        JMenuItem resetCompsMenuItem = new JMenuItem("Reset all Meters, Inductors and Capacitors");
        resetCompsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetMetersInductorsCapacitors();
            }
        });

        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "v.20\n"
                        + "Parts of the code forked from CircuitEngine:\n"
                        + "Copyright \u00a9 2007-2008 Kevin Stueve kstueve@uw.edu.\n"
                        + "All rights reserved.\n"
                        + "IN NO EVENT SHALL KEVIN STUEVE BE LIABLE TO ANY PARTY\n"
                        + "FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES\n"
                        + "ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF\n"
                        + "KEVIN STUEVE HAS BEEN ADVISED OF THE POSSIBILITY OF\n"
                        + "SUCH DAMAGE.\n"
                        + "KEVIN STUEVE SPECIFICALLY DISCLAIMS ANY WARRANTIES,\n"
                        + "INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF\n"
                        + "MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE\n"
                        + "PROVIDED HEREUNDER IS ON AN \"AS IS\" BASIS, AND KEVIN\n"
                        + "STUEVE HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,\n"
                        + "ENHANCEMENTS, OR MODIFICATIONS.\n"
                        + "CircuitPiper incorporates the Ptplot 5.7 plotting software, which is released\n"
                        + "under the UC Berkeley copyright.\n"
                        + "Copyright (c) 1995-2008 The Regents of the University of California.\n"
                        + "All rights reserved.\n"
                        + "IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY\n"
                        + "FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES\n"
                        + "ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF\n"
                        + "THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF\n"
                        + "SUCH DAMAGE.\n"
                        + "THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,\n"
                        + "INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF\n"
                        + "MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE\n"
                        + "PROVIDED HEREUNDER IS ON AN \"AS IS\" BASIS, AND THE UNIVERSITY OF\n"
                        + "CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,\n"
                        + "ENHANCEMENTS, OR MODIFICATIONS.\n\n\n"
                        + "Parts of the code forked from CircuitJS:\n"
                        + "Copyright (C) Paul Falstad and Iain Sharp\n"
                        + "\n"
                        + "CircuitJS is free software: you can redistribute it and/or modify\n"
                        + "it under the terms of the GNU General Public License as published by\n"
                        + "the Free Software Foundation, either version 2 of the License, or\n"
                        + "(at your option) any later version.\n"
                        + "\n"
                        + "CircuitJS1 is distributed in the hope that it will be useful,\n"
                        + "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
                        + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
                        + "GNU General Public License for more details.\n"
                        + "\n"
                        + "See <http://www.gnu.org/licenses/> to obtain a copy of\n"
                        + "the GNU General Public License."
                        
                        ,
                        "About CircuitPiper",
                        JOptionPane.PLAIN_MESSAGE);

            }
        });

        fileMenu.add(resetCompsMenuItem);
        fileMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);


        // ============================== Board Menu
        JMenu boardMenu = new JMenu("Board");

        /*
        JCheckBoxMenuItem boardOutlineMenuItem = new JCheckBoxMenuItem("Board Outline");
        boardOutlineMenuItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                circuitEnginePanel.drawingPanel.isShowBoardOutline = e.getStateChange() == ItemEvent.SELECTED;
                circuitEnginePanel.drawingPanel.repaint();
            }
        });
        boardMenu.add(boardOutlineMenuItem);
         */
        JRadioButtonMenuItem rbMenuItem;
        ButtonGroup group = new ButtonGroup();

        rbMenuItem = new JRadioButtonMenuItem("None");
        rbMenuItem.setSelected(true);
//rbMenuItem.setMnemonic(KeyEvent.VK_R);
        rbMenuItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                circuitPanel.drawingPanel.isShowBoardOutline = false;
                circuitPanel.drawingPanel.repaint();
            }
        });
        group.add(rbMenuItem);
        boardMenu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("6\" x 6\"");
        rbMenuItem.setSelected(false);
//rbMenuItem.setMnemonic(KeyEvent.VK_R);
        rbMenuItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                circuitPanel.drawingPanel.boardXPixels = 6 * 72;
                circuitPanel.drawingPanel.boardYPixels = 6 * 72;
                circuitPanel.drawingPanel.isShowBoardOutline = true;
                circuitPanel.drawingPanel.repaint();
            }
        });
        group.add(rbMenuItem);
        boardMenu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("8\" x 5.5\"");
//rbMenuItem.setMnemonic(KeyEvent.VK_O);
        rbMenuItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                circuitPanel.drawingPanel.boardXPixels = 8 * 72;
                circuitPanel.drawingPanel.boardYPixels = 5.5 * 72;
                circuitPanel.drawingPanel.isShowBoardOutline = true;
                circuitPanel.drawingPanel.repaint();
            }
        });
        group.add(rbMenuItem);
        boardMenu.add(rbMenuItem);

        menuBar.add(boardMenu);

        // ============================== Misc Menu
        JMenu miscMenu = new JMenu("Misc");

        JMenuItem autoLayoutMenuItem = new JMenuItem("Auto Layout");
        autoLayoutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                circuitPanel.currentCircuit.viewAutoLayout();
            }
        });
        miscMenu.add(autoLayoutMenuItem);

        JMenuItem exampleMenuItem = new JMenuItem("Example 1");
        exampleMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    circuitPanel.currentCircuit.exampleCircuit(1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        miscMenu.add(exampleMenuItem);
        
        exampleMenuItem = new JMenuItem("Example 2");
        exampleMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    circuitPanel.currentCircuit.exampleCircuit(2);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        miscMenu.add(exampleMenuItem);
        
        exampleMenuItem = new JMenuItem("Example 3");
        exampleMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    circuitPanel.currentCircuit.exampleCircuit(3);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        miscMenu.add(exampleMenuItem);
        
        JMenuItem testMenuItem = new JMenuItem("Test");
        testMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    
                    circuitPanel.currentCircuit.exampleCircuit(1);
                    
                    for (int steps = 0; steps < 20; steps++) {
                        circuitPanel.currentCircuit.updateCircuit();
                    }
                    
                    circuitPanel.drawingPanel.repaint();
                    
                    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("org/mathpiper/ui/gui/applications/circuitpiper/test_data.txt");
                    
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    int result = bis.read();
                    while(result != -1) {
                        buf.write((byte) result);
                        result = bis.read();
                    }

                    String testDataFile = buf.toString("UTF-8");

                    String testDataNew = circuitPanel.currentCircuit.dumpMatrix();
                    
                    System.out.println(testDataNew);
                    
                    if(testDataFile.equals(testDataNew))
                    {
                        System.out.println("PASS");
                    }
                    else
                    {
                        System.out.println("FAIL");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        miscMenu.add(testMenuItem);
        
        JMenuItem traceMenuItem = new JMenuItem("Trace");
        traceMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                if(circuitPanel.currentCircuit.components.size() == 0)
                {
                    JOptionPane.showMessageDialog(null, "No circuit has been specified.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                /*
                boolean isGraphs = true;
                GraphFrame currentGraph = new GraphFrame(circuitPanel, 0, 0, "R1_current", "I");
                GraphFrame currentPrimeGraph = new GraphFrame(circuitPanel, 0, 0, "C1_current'", "I'");
                */
                
                
                boolean isGraphs = false;
                GraphFrame currentGraph = null;
                GraphFrame currentPrimeGraph = null;
                
                
                int steps = 0;
                try {
                    //circuitPanel.circuit.exampleCircuit();
                    
                    circuitPanel.currentCircuit.updateCircuit();
                    
                    //int componentNumber = circuitPanel.currentCircuit.getColumnNumbers().get("R1I");
                    //Component ec = circuitPanel.currentCircuit.components.get(componentNumber);
                    
                    Component ec = circuitPanel.currentCircuit.components.get("R1");
                    
                    for (; steps < 15; steps++) {

                        circuitPanel.currentCircuit.updateCircuit();
                                           
                        if(isGraphs)
                        {
                            double current = circuitPanel.currentCircuit.mainMatrix.getCurrent(ec);
                            //System.out.println("CCC " + ec.getID() + ": " + current);
                            currentGraph.plotBox.addPoint(0, steps, current, true);
                            currentGraph.plotBox.fillPlot();

                            double currentPrime = circuitPanel.currentCircuit.mainMatrix.getCurrentPrime(ec);
                            //System.out.println("CCC' " + ec.getID() + ": " + currentPrime);
                            currentPrimeGraph.plotBox.addPoint(0, steps, currentPrime, true);
                            currentPrimeGraph.plotBox.fillPlot();
                        }
                    }
                    
                    
                    circuitPanel.currentCircuit.isTrace = true;
                    StringBuilder traceData = circuitPanel.currentCircuit.updateCircuit();
                    circuitPanel.currentCircuit.isTrace = false;
                    
                    ColorPane textPane = new ColorPane();

                    textPane.setFont(new Font("monospaced", Font.PLAIN, 16));
                    
                    circuitPanel.drawingPanel.isDrawGrid = false;
                    circuitPanel.drawingPanel.isDrawWireLabels = true;
                    circuitPanel.drawingPanel.isDrawTerminalLabels = true;
                    BufferedImage image = ScreenCapture.createImage(circuitPanel.drawingPanel, null);
                    ImageIcon icon = new ImageIcon(image);
                    textPane.insertIcon(icon);
                    
                    textPane.append("\n");
                    
                    textPane.append(circuitPanel.currentCircuit.export());
                    
                    textPane.append("\n\n");
                    
                    textPane.append(traceData.toString());
                    
                    JFrame frame = new JFrame();
                    frame.setTitle("Trace");
                    Container contentPane = frame.getContentPane();
                    
                    JPanel jPanel = new JPanel();
                    
                    jPanel.add(textPane);
                    
                    JScrollPane textScrollPane = new JScrollPane(jPanel);
                            
                    textScrollPane.getVerticalScrollBar().setUnitIncrement(15);
                    
                    contentPane.add(textScrollPane);
                    
                    JMenu fileMenu = new JMenu("File");

                    JMenuItem saveAsImageAction = new JMenuItem();
                    saveAsImageAction.setText("Save As Image");
                    saveAsImageAction.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent ae)
                        {
                            org.mathpiper.ui.gui.Utility.saveImageOfComponent(jPanel);
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

                    //frame.setSize(width/2, height/2);
                    frame.setLocationRelativeTo( null );
                    frame.setVisible(true);
                    
                    
                    if(isGraphs)
                    {
                        double current = circuitPanel.currentCircuit.mainMatrix.getCurrent(ec);
                        //System.out.println("CCC " + ec.getID() + ": " + current);
                        currentGraph.plotBox.addPoint(0, steps, current, true);
                        currentGraph.plotBox.fillPlot();

                        double currentPrime = circuitPanel.currentCircuit.mainMatrix.getCurrentPrime(ec);
                        //System.out.println("CCC' " + ec.getID() + ": " + currentPrime);
                        currentPrimeGraph.plotBox.addPoint(0, steps, currentPrime, true);
                        currentPrimeGraph.plotBox.fillPlot();
                    }
                    
                    circuitPanel.drawingPanel.repaint();
                    
                    //currentGraph.plotBox.export(System.out);
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        miscMenu.add(traceMenuItem);        

        JMenuItem imageMenuItem = new JMenuItem("Image");
        imageMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Utility.saveImageOfComponent(circuitPanel.screenCapturePanel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        miscMenu.add(imageMenuItem);
        
        JMenuItem imageMenuItem2 = new JMenuItem("Image2");
        imageMenuItem2.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    circuitPanel.drawingPanel.isDrawGrid = false;
  
                    JFileChooser saveImageFileChooser = new JFileChooser();

                    int returnValue = saveImageFileChooser.showSaveDialog(circuitPanel.drawingPanel);

                    if (returnValue == JFileChooser.APPROVE_OPTION)
                    {
                        File exportImageFile = saveImageFileChooser.getSelectedFile();
                        
                        BufferedImage circuitImage = circuitPanel.drawingPanel.getImageOfCircuit();
                        
                        ScreenCapture.writeImage(circuitImage, exportImageFile.getAbsolutePath());
                    }
                }
                catch (Throwable ex)
                {
                    ex.printStackTrace();
                }
            }
        });
        miscMenu.add(imageMenuItem2);
        
        JMenuItem epsMenuItem = new JMenuItem("EPS");
        epsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    //circuitPanel.circuit.exampleCircuit(1);
                    //circuitPanel.circuit.readCircuit("Resistor_1 162 67 256 67 5.67");
                    
                    circuitPanel.drawingPanel.isDrawGrid = false;
                    
                    circuitPanel.drawingPanel.toEPS();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        miscMenu.add(epsMenuItem);

        JMenuItem printMenuItem = new JMenuItem("Print");
        printMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    circuitPanel.print();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        miscMenu.add(printMenuItem);
        
        isCirSimCheckBox = new JCheckBoxMenuItem("CirSim");
        //probeCheckBox.setMnemonic(KeyEvent.VK_C); 
        isCirSimCheckBox.setSelected(true);
        isCirSimCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED)
                {
                    CircuitPiperMain.this.circuitPanel.drawingPanel.circuitPanel.currentCircuit.isCirSim = true;
                    
                    CircuitPiperMain.this.circuitPanel.buttonPanel.componentList.removeAllItems();
                    
                    for(String item : CircuitPiperMain.this.circuitPanel.buttonPanel.componentNamesList)
                    {
                        CircuitPiperMain.this.circuitPanel.buttonPanel.componentList.addItem(item);
                    }
                }
                else
                {
                    CircuitPiperMain.this.circuitPanel.drawingPanel.circuitPanel.currentCircuit.isCirSim = false;
                    
                    CircuitPiperMain.this.circuitPanel.buttonPanel.componentList.removeAllItems();
                    
                    for(String item : CircuitPiperMain.this.circuitPanel.buttonPanel.componentNames)
                    {
                        CircuitPiperMain.this.circuitPanel.buttonPanel.componentList.addItem(item);
                    }
                    
                    CircuitPiperMain.this.isCirSimCheckBox.setEnabled(false);
                    
                    CircuitPiperMain.this.circuitPanel.currentCircuit.cirSim = null;
                    
                    for(Component component : CircuitPiperMain.this.circuitPanel.currentCircuit.components.values())
                    {
                        component.circuitElm = null;
                    }
                }
            }
        });
        miscMenu.add(isCirSimCheckBox);
        
        JCheckBoxMenuItem dotsCheckItem = CircuitPiperMain.this.circuitPanel.currentCircuit.cirSim.dotsCheckItem;
        miscMenu.add(dotsCheckItem);
        
        JMenuItem ascMenuItem = new JMenuItem();
        ascMenuItem.setText("Open .asc");
        ascMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                
                circuitPanel.drawingPanel.isDrawGrid = false;
                circuitPanel.showGridCheckBox.setSelected(false);
                
                circuitPanel.currentCircuit.isCirSim = false;
                circuitPanel.currentCircuit.cirSim = null;
                isCirSimCheckBox.setSelected(false);

                JFileChooser openCircuitFileChooser = new JFileChooser();

                //File file = new File("/home/tkosan/Desktop/ltspice/one.asc");// todo:tk
                File file = new File("/home/tkosan/Documents/LTspice/LTspiceXVII/examples/LTC6268/LTC6268.asc");
                openCircuitFileChooser.setSelectedFile(file);
                
                FileFilter filter = new FileNameExtensionFilter(".asc file", "asc");
                openCircuitFileChooser.addChoosableFileFilter(filter);

                int returnValue = openCircuitFileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    
                    File circuitFile = openCircuitFileChooser.getSelectedFile();
                    
                    try {
                        circuitPanel.isRunning = false;
                        

                        circuitPanel.currentCircuit.readAscFormat(circuitFile.getCanonicalPath());
                        //circuitPanel.circuit.readAscFormat("/home/tkosan/Documents/LTspice/LTspiceXVII/examples/Personal/00 - Oscillators/01 - Sinewave/05 - Twin Tee/TwinTee.asc");

                        CircuitPiperMain.this.schematicFrame.setTitle("Schematic [" + circuitFile.getName() + "]");

                        if(! circuitPanel.currentCircuit.isCirSim)
                        {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        //circuitPanel.circuit.updateCircuit();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    //circuitEnginePanel.isRunning = true;
                                    //circuitEnginePanel.setIsDrawing(true);
                                    circuitPanel.setHintDrawing();

                                    circuitPanel.repaint();

                                    circuitPanel.waiting = false;
                                }
                            });
                        }
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        miscMenu.add(ascMenuItem);
        
        JMenuItem openCirSim = new JMenuItem();
        openCirSim.setText("OpenCirSim");
        openCirSim.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                JFileChooser openCircuitFileChooser = new JFileChooser();

                //File file = new File("/home/tkosan/bat_and_two_resistors.ser");// todo:tk
                //openCircuitFileChooser.setSelectedFile(file);
                
                FileFilter filter = new FileNameExtensionFilter("Serialzed circuit file", "ser");
                openCircuitFileChooser.addChoosableFileFilter(filter);

                int returnValue = openCircuitFileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File circuitFile = openCircuitFileChooser.getSelectedFile();
                    try {
                        
                        circuitPanel.isRunning = false;
                        String fileContents = new String(Files.readAllBytes(Paths.get(circuitFile.getCanonicalPath())));
                        
                        //circuitPanel.circuit.readCirSimCircuit(fileContents);
                        
                        circuitPanel.currentCircuit.cirSim.setSimRunning(false);
                        circuitPanel.currentCircuit.cirSim.readSetup(fileContents.getBytes(), false, false, false);

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {

                                circuitPanel.repaint();

                            }
                        });
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });
        
        miscMenu.add(openCirSim);
        
        
        JMenuItem exportTurtleMenuItem = new JMenuItem("Export Turtle");
        exportTurtleMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    
                    // todo:tk:for debugging.
                    FileOutputStream file = new FileOutputStream("/home/tkosan/a.ttl");
                    file.write(circuitPanel.currentCircuit.exportTurtle().getBytes());
                    file.close();
                    /*
                    Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("outfilename"), "UTF-8"));
                    try {
                        out.write(aString);
                    } finally {
                        out.close();
}
                    */
        
                    JTextArea textArea = new JTextArea(30, 70);
                    textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
                    textArea.append(circuitPanel.currentCircuit.exportTurtle());
                    textArea.setCaretPosition(0);
                    JOptionPane.showMessageDialog(null, new JScrollPane(textArea), "", JOptionPane.OK_OPTION);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        miscMenu.add(exportTurtleMenuItem);


        
        menuBar.add(miscMenu);

        return menuBar;
    }
    
    public void resetTimeAndGraphs()
    {
        synchronized (circuitPanel.drawingPanel) {
            circuitPanel.currentCircuit.time = 0;
            circuitPanel.drawingPanel.repaint();
            for (PhasePlane phasePlane : circuitPanel.phasePlanes) {
                phasePlane.pb.clear(0);
            }
            for (Component ec : circuitPanel.currentCircuit.components.values()) {
                if (ec instanceof CurrentIntegrator || ec instanceof Voltmeter
                        || ec instanceof Ammeter || ec instanceof VoltageIntegrator
                        || ec instanceof Ohmmeter || ec instanceof CapacitanceMeter
                        || ec instanceof InductanceMeter) {

                    List<ScopePanel> scopePanels = ec.getScopePanels();

                    for(ScopePanel scopePanel : scopePanels)
                    {
                        if (scopePanel.plotBox != null) {
                            scopePanel.plotBox.clear(0);
                        }
                    }
                }

                if(circuitPanel.currentCircuit.isCirSim)
                {
                    List<ScopePanel> scopePanels = ec.getScopePanels();

                    for(ScopePanel scopePanel : scopePanels)
                    {
                        if (scopePanel.plotBox != null) {
                            scopePanel.plotBox.clear(0);
                        }
                    }
                }
            }

            if(circuitPanel.currentCircuit.isCirSim)
            {
                circuitPanel.currentCircuit.cirSim.simulationTime = 0;

                for (int i = 0; i != circuitPanel.currentCircuit.cirSim.elmList.size(); i++) {
                    circuitPanel.currentCircuit.cirSim.getElm(i).reset();
                }
            }
        }
    }
    
    public void resetMetersInductorsCapacitors()
    {
        synchronized (circuitPanel.drawingPanel) {
            for (Component ec : circuitPanel.currentCircuit.components.values()) {

                ec.setSecondaryValue(0);

                if (ec instanceof Meter) {
                    ((Meter) ec).reset();
                }

                if(circuitPanel.currentCircuit.isCirSim)
                {
                    if (ec instanceof Inductor) {
                        ((Inductor) ec).getCircuitElm().reset();
                    }

                    if (ec instanceof Capacitor) {
                        ((Capacitor) ec).getCircuitElm().reset();
                    }
                }
            }
            circuitPanel.drawingPanel.repaint();
        }
    }

    public CircuitPanel getPanel() {
        return circuitPanel;
    }

    /*
    public static void main(String[] args) throws Exception {

        CircuitPiperMain circuitPiperMain = new CircuitPiperMain();

        JFrame frame = new javax.swing.JFrame();

        frame.setBackground(Color.WHITE);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (frame.getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE) {
                    System.exit(0);
                } else if (frame.getDefaultCloseOperation() == JFrame.DISPOSE_ON_CLOSE) {
                    circuitPiperMain.getPanel().isRunning = false;
                    circuitPiperMain.getPanel().myTimer.stop();
                    frame.dispose();
                }
            }
        });

        frame.getContentPane().add(circuitPiperMain.getPanel());

        frame.setJMenuBar(circuitPiperMain.createMenuBar());

        frame.pack();

        frame.setTitle("CircuitPiper");
        frame.setSize(new Dimension(1400, 800));
        //frame.setResizable(false);
        //frame.setPreferredSize(new Dimension(700, 700));
        frame.setLocationRelativeTo(null); // added

        frame.setVisible(true);
    }
*/
        public static void main(String[] args) throws Exception {
            
        final JFrame frame = new javax.swing.JFrame();
            
        final CircuitPiperMain circuitPiperMain = new CircuitPiperMain(frame);
        
        frame.setTitle("CircuitPiper");

        frame.setBackground(Color.WHITE);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (frame.getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE) {
                    System.exit(0);
                } else if (frame.getDefaultCloseOperation() == JFrame.DISPOSE_ON_CLOSE) {
                    circuitPiperMain.getPanel().isRunning = false;
                    circuitPiperMain.getPanel().myTimer.stop();
                    frame.dispose();
                }
            }
        });

        frame.getContentPane().add(circuitPiperMain);
        
        //frame.setSize(new Dimension(1400, 800));
        
        frame.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width/2, (int) Math.round(height/1.5)); // todo:tk:find a better way to set the size of the JFrame.
        frame.setLocationRelativeTo(null); // added

        frame.setVisible(true);
        
        /*
        circuitPiperMain.circuitPanel.isRunning = false;
        circuitPiperMain.circuitPanel.drawingPanel.isDrawGrid = false;
        circuitPiperMain.circuitPanel.currentCircuit.open("/home/tkosan/circuitpiper/problem_2_26.txt");
        circuitPiperMain.circuitPanel.currentCircuit.id = "/home/tkosan/circuitpiper/problem_2_26.txt";
        */
    }
}
