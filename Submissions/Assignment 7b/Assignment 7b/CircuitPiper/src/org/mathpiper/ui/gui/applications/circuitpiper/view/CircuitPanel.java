package org.mathpiper.ui.gui.applications.circuitpiper.view;

import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CircuitElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CCVSElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.VCCSElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.InductorElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.VCVSElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CapacitorElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CCCSElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.SwitchElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.OhmMeterElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.DiodeElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.VoltageElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CurrentElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.AmmeterElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.ACVoltageElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.WireElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.ResistorElm;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.LinkedList;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.*;

import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import org.mathpiper.ui.gui.applications.circuitpiper.model.Terminal;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ACVoltageSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Ammeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Capacitor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.CurrentSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.CCCS;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.CCVS;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.DCVoltageSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Inductor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Ohmmeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Resistor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Switch;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.VCCS;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.VCVS;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.diodes.Diode;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Wire;

/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */
public final class CircuitPanel extends JPanel {

    boolean isProbe = false;
    boolean myIsMovingPoint;
    boolean myIsMovingComponent;
    private boolean myIsDrawing;
    int myMouseX, myMouseY;
    boolean myButtonState;//true if pressed
    boolean isMouseEntered;//true if mouse is in DrawingPanel
    public final int leftSideOffsetPixels = 4;
    public final int topSideYOffsetPixels = 4;
    public final double terminalXSpacing = 31.5;
    public final double terminalYSpacing = 31.5;
    public final int widthPixels = 1200;
    public final int heightPixels = 800;
    public int xDistanceBetweenTerminalsPixels = (int) ((widthPixels - leftSideOffsetPixels) / terminalXSpacing);
    public int yDistanceBetweenTerminalsPixels = (int) ((heightPixels - topSideYOffsetPixels) / terminalYSpacing);
    HintPanel hintPanel;
    public Component myTempComponent; //Holds a newly created component.
    public Component myNearestComponent;
    public Component myDraggedComponent;
    public Switch myNearestSwitch;
    public Terminal myNearestTerminal;
    public Terminal draggedTerminal;
    public String mySelectedComponent;
    public Circuit currentCircuit;
    public CircuitPiperMain circuitPiperMain;
    public LinkedList<PhasePlane> phasePlanes;

    public final DrawingPanel drawingPanel;
    public boolean isBusy;
    public boolean waiting;
    public boolean isRunning;
    public String defaultCapacitorErrorString = "1.0E-9";
    public String defaultInductorErrorString = "1.0E-9";
    public String capacitorErrorString = defaultCapacitorErrorString;
    public String inductorErrorString = defaultInductorErrorString;
    public double defaultCapacitorError = Math.pow(10, -9);
    public double defaultInductanceError = Math.pow(10, -9);//was 10^-11
    public double capacitorError = defaultCapacitorError;
    public double inductanceError = defaultInductanceError;
    public boolean timeOption = true;
    public JPanel screenCapturePanel;
    public Timer myTimer;
    public JCheckBox showGridCheckBox;
    
    public ReadoutPanel readoutPanel;
    public ScopesPanel scopesPanel;
    public ButtonPanel buttonPanel;

    public CircuitPanel() throws Exception {
        super();
        
        this.setBackground(Color.WHITE);
        currentCircuit = new Circuit("GUI", this);
        phasePlanes = new LinkedList<PhasePlane>();
        this.setLayout(new BorderLayout());
        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        hintPanel = new HintPanel(this);
        //JPanel bp = new ButtonPanel(this);
        //bp.setLayout(new BorderLayout());

        //JPanel outerdp=new JPanel();
        //outerdp.setLayout(new BorderLayout());
        //this.add(bp);
        drawingPanel = new DrawingPanel(this);
        
        currentCircuit.cirSim = new CirSim(drawingPanel);
        currentCircuit.cirSim.init2();
        
        buttonPanel = new ButtonPanel(this);
        this.add(buttonPanel, BorderLayout.NORTH);


        screenCapturePanel = new JPanel();
        screenCapturePanel.setBackground(Color.WHITE);

        screenCapturePanel.add(drawingPanel);

        JPanel circuitBox = new JPanel(new BorderLayout());

        PanelController treePanelScaler = new PanelController(drawingPanel, drawingPanel.viewScale);

        JScrollPane panelScrollPane = new JScrollPane(screenCapturePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panelScrollPane.getViewport().setBackground(Color.WHITE);
        
        circuitBox.add(panelScrollPane);

        
        
        Box slidersBox = Box.createVerticalBox();
        
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(treePanelScaler);
        horizontalBox.add(Box.createHorizontalGlue());
        slidersBox.add(horizontalBox);
        
        JPanel speedSliderPanel = new JPanel();
        speedSliderPanel.add(new JLabel("Sim Speed"));
        speedSliderPanel.add(currentCircuit.cirSim.speedSlider);
        horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(speedSliderPanel);
        horizontalBox.add(Box.createHorizontalGlue());
        slidersBox.add(horizontalBox);
        
        JPanel currentSliderPanel = new JPanel();
        currentSliderPanel.add(new JLabel("Current Speed"));
        currentSliderPanel.add(currentCircuit.cirSim.currentSlider);
        horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(currentSliderPanel);
        horizontalBox.add(Box.createHorizontalGlue());
        slidersBox.add(horizontalBox);
        

        circuitBox.add(slidersBox, BorderLayout.SOUTH);
        
        //todo:tk:simulation speed appears to be controlled here
        // and in Circuit.java timeStep = .05.
        // see CirSim.runCircuit().
        myTimer = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isBusy || !isRunning) {
                    return;
                }

                // System.out.println("a");
                synchronized (drawingPanel) {//##changed from synchronized this on Dec 29 2008
                    try {
                        CircuitPanel.this.currentCircuit.updateCircuit();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    drawingPanel.repaint();
                }
                //System.out.println("b");
            }
        });

        myTimer.setRepeats(true);
        myTimer.start();
        
        this.add(circuitBox, BorderLayout.CENTER);
        
        setHintStarting();
        //JPanel southPanel= new JPanel();
        //outerdp.add(dp,BorderLayout.CENTER);
        //this.add(outerdp);
        //southPanel.add(myHintPanel,BorderLayout.SOUTH);
        //this.add(myHintPanel);
        this.add(hintPanel, BorderLayout.SOUTH);
        //this.add(southPanel,BorderLayout.SOUTH);
        this.revalidate();
        setIsMovingPoint(false);
        setIsMovingComponent(false);
        setIsDrawing(false);
    }

    public void setIsMovingPoint(final boolean theIsMovingPoint) {
        this.myIsMovingPoint = theIsMovingPoint;
    }

    public boolean isMovingPoint() {
        return myIsMovingPoint;
    }

    public boolean isDrawing() {
        return myIsDrawing;
    }

    public void setIsDrawing(final boolean theIsDrawing) {
        this.myIsDrawing = theIsDrawing;
    }

    public boolean isMovingComponent() {
        return myIsMovingComponent;
    }

    public void setIsMovingComponent(final boolean theIsMovingComponent) {
        this.myIsMovingComponent = theIsMovingComponent;
    }

    public void setMouseX(final int theMouseX) {
        myMouseX = (int) Math.round(theMouseX / drawingPanel.viewScale);
    }

    public int getMouseX() {
        return myMouseX;
    }

    public void setMouseY(final int theMouseY) {
        myMouseY = (int) Math.round(theMouseY / drawingPanel.viewScale);
    }

    public int getMouseY() {
        return myMouseY;
    }

    public void setButtonState(final boolean theButtonState) {
        myButtonState = theButtonState;
    }

    public void setMouseEntered(final boolean theMouseEntered) {
        isMouseEntered = theMouseEntered;
    }

    public boolean getMouseEntered() {
        return isMouseEntered;
    }

    
    public void setHintDrawing() {
        if (!hintPanel.hint.equals("drawing")) {
            //hintPanel.drawingHint();
        }
    }

    public void setHintStarting() {
        if (!hintPanel.hint.equals("starting")) {
            //hintPanel.startingHint();
        }
    }

    public void hintNearTerminal() {
        if (!hintPanel.hint.equals("terminal")) {
            //hintPanel.nearTerminalHint();
        }
    }

    public void hintNearComponent() {
        if (!hintPanel.hint.equals("component")) {
            //hintPanel.nearComponentHint();
        }
    }

    public void hintNearSwitch() {
        if (!hintPanel.hint.equals("switch")) {
            //hintPanel.nearSwitchHint();
        }
    }
    
    public boolean isNotMovingAny()
    {
        return !isMovingPoint() && !isMovingComponent();
    }

    public Component newElementComponent(CircuitElm cirElm) throws Exception {
        Component newComponent = null;
        
        String componentType = "";
        String uid = null;
        
        // Note:tk:the head and the tail have been reversed in the CircuitPiper components because
        // CircuitPiper draws components head to tail while CirSim draws components tail to head

        if (cirElm instanceof AmmeterElm) {
            newComponent = new Ammeter(cirElm.x2, cirElm.y2, uid, currentCircuit);
        }else if (cirElm instanceof VoltageElm) {

            newComponent = new DCVoltageSource(cirElm.x2, cirElm.y2, uid, currentCircuit);

        } /*else if (cirElm instanceof "CapacitanceMeter")) {
            newComponent = new CapacitanceMeter(xPixels, yPixels, uid, this);
        }*/ else if (cirElm instanceof CapacitorElm) {

            newComponent = new Capacitor(cirElm.x2, cirElm.y2, uid, currentCircuit);

        } else if (cirElm instanceof CurrentElm) {

            newComponent = new CurrentSource(cirElm.x2, cirElm.y2, uid, currentCircuit);

        } /*else if (cirElm instanceof "CurrentIntegrator")) {
            newComponent = new CurrentIntegrator(xPixels, yPixels, uid, this);
        } else if (cirElm instanceof "VoltageIntegrator")) {
            newComponent = new VoltageIntegrator(xPixels, yPixels, uid, this);
        } else if (cirElm instanceof "InductanceMeter")) {
            newComponent = new InductanceMeter(xPixels, yPixels, uid, this);
        }*/ else if (cirElm instanceof InductorElm) {

            newComponent = new Inductor(cirElm.x2, cirElm.y2, uid, currentCircuit);

        } else if (cirElm instanceof OhmMeterElm) {
            newComponent = new Ohmmeter(cirElm.x2, cirElm.y2, uid, currentCircuit);
        } else if (cirElm instanceof ResistorElm) {

            newComponent = new Resistor(cirElm.x2, cirElm.y2, uid, currentCircuit);

        } else if (cirElm instanceof SwitchElm) {
            newComponent = new Switch(cirElm.x2, cirElm.y2, uid, currentCircuit);
        }/* else if (cirElm instanceof "Voltmeter")) {
            newComponent = new Voltmeter(xPixels, yPixels, uid, this);
        }*/ else if (cirElm instanceof WireElm) {
            newComponent = new Wire(cirElm.x2, cirElm.y2, uid, currentCircuit);
        } /*else if (cirElm instanceof "ACCurrentSource")) {
            if (attributes == null) {
                newComponent = new ACCurrentSource(xPixels, yPixels, uid, this);
            } else {
                newComponent = new ACCurrentSource(xPixels, yPixels, uid, attributes, this);
            }
        } */else if (cirElm instanceof ACVoltageElm) {
            newComponent = new ACVoltageSource(cirElm.x2, cirElm.y2, uid, currentCircuit);

        }/* else if (cirElm instanceof "Block")) {
            if (attributes == null) {
                newComponent = new Block(xPixels, yPixels, uid, this);
            } else {
                newComponent = new Block(xPixels, yPixels, uid, attributes, this);
            }
        }*/ else if (cirElm instanceof VCVSElm) {
            newComponent = new VCVS(cirElm.x2, cirElm.y2, uid, currentCircuit);

        } else if (cirElm instanceof VCCSElm) {
            newComponent = new VCCS(cirElm.x2, cirElm.y2, uid, currentCircuit);

        } else if (cirElm instanceof CCVSElm) {
            newComponent = new CCVS(cirElm.x2, cirElm.y2, uid, currentCircuit);

        }else if (cirElm instanceof CCCSElm) {
           newComponent = new CCCS(cirElm.x2, cirElm.y2, uid, currentCircuit);

        }/* else if (cirElm instanceof "TransistorNPN")) {
            newComponent = new TransistorNPN(xPixels, yPixels, uid, this);
        } else if (cirElm instanceof "TransistorPNP")) {
            newComponent = new TransistorPNP(xPixels, yPixels, uid, this);
        } else if (cirElm instanceof "TransistorJFETN")) {
            newComponent = new TransistorJFETN(xPixels, yPixels, uid, this);
        } else if (cirElm instanceof "TransistorJFETP")) {
            newComponent = new TransistorJFETP(xPixels, yPixels, uid, this);
        } else if (cirElm instanceof "LogicalPackage")) {
            newComponent = new LogicalPackage(xPixels, yPixels, uid, this);
        } */else if (cirElm instanceof DiodeElm) {
            newComponent = new Diode(cirElm.x2, cirElm.y2, uid, currentCircuit);
        } else {
            throw new Exception("Unknown component name: " + componentType);
        }
        
        newComponent.setCircuitElm(cirElm);
        
        cirElm.component = newComponent;
        
        return newComponent;
    }


    public void newTempComponent(String name, int headX, int headY) throws Exception {

        myTempComponent = currentCircuit.newComponent(name, headX, headY, -1, -1, null);

        //String componentKey = myTempComponent.toString();
        //Class cl = Class.forName("Model.Components."+mySelectedComponent.replaceAll(" ", ""));
        //Constructor cons = cl.getConstructor(Integer.TYPE,Integer.TYPE);
        //Point p = new Point(nearestX(),nearestY());
        //myTempComponent=(Component)cons.newInstance(nearestX(),nearestY());
        //myTempComponent=(Component)cons.newInstance(nearestX(),nearestY());
    /*} catch (InvocationTargetException e) { 
         //System.out.println("InvocationTargetException");
         } catch (NoSuchMethodException e) {  
         //System.out.println("NoSuchMethodException");
         } catch (ClassNotFoundException e) {
         //System.out.println("ClassNotFoundException");
         } catch (IllegalAccessException e) {
         //System.out.println("IllegalAccessException");
         } catch (InstantiationException e) {
         //System.out.println("InstantiationException");
         }*/
    }

    public void setSelectedComponent(final String theSelectedButton) {
        mySelectedComponent = theSelectedButton;
    }

    public int nearestGridPointXPixels() {
        long column = Math.round((getMouseX() - leftSideOffsetPixels) / terminalXSpacing);
        column = Math.max(column, 0);
        column = Math.min(column, xDistanceBetweenTerminalsPixels - 1);
        int xPixels = (int) Math.round(leftSideOffsetPixels + column * terminalXSpacing);
        return xPixels;
    }

    public int nearestGridPointYPixels() {
        long row = Math.round((getMouseY() - topSideYOffsetPixels) / terminalYSpacing);
        row = Math.max(row, 0);
        row = Math.min(row, yDistanceBetweenTerminalsPixels - 1);
        int yPixels = (int) Math.round(topSideYOffsetPixels + row * terminalYSpacing);
        return yPixels;
    }



    public boolean isNearTerminal() {
        boolean result = false;
        if (currentCircuit.terminals.containsKey(new Point(nearestGridPointXPixels(), nearestGridPointYPixels()))
                && sqr(getMouseX() - nearestGridPointXPixels()) + sqr(getMouseY() - nearestGridPointYPixels()) < 10 * 10) {
            
            result = true;
            myNearestTerminal = currentCircuit.terminals.get(new Point(nearestGridPointXPixels(), nearestGridPointYPixels()));
        }
        
        return result;
    }

    public double sqr(double x) {
        return x * x;
    }

    public boolean nearComponent() {
        int x3 = getMouseX();
        int y3 = getMouseY();
        double mindistance = Double.MAX_VALUE;
        boolean result = false;
        for (Component electricComponent : currentCircuit.components.values()) {
            int x1 = electricComponent.getHeadTerminal().getX();
            int y1 = electricComponent.getHeadTerminal().getY();
            int x2 = electricComponent.getTailTerminal().getX();
            int y2 = electricComponent.getTailTerminal().getY();
            double u = ((x3 - x1) * (x2 - x1) + (y3 - y1) * (y2 - y1)) / (sqr(x2 - x1) + sqr(y2 - y1));
            double x = x1 + u * (x2 - x1);
            double y = y1 + u * (y2 - y1);
            if (0 < u && u < 1 && sqr(x3 - x) + sqr(y3 - y) < 300) {
                if (sqr(x3 - x) + sqr(y3 - y) < mindistance) {
                    mindistance = sqr(x3 - x) + sqr(y3 - y);
                    myNearestComponent = electricComponent;
                    double d1 = sqr(electricComponent.getHeadTerminal().getX() - x3) + sqr(electricComponent.getHeadTerminal().getY() - y3);
                    double d2 = sqr(electricComponent.getTailTerminal().getX() - x3) + sqr(electricComponent.getTailTerminal().getY() - y3);
                    if (d1 < d2) {
                        myNearestTerminal = electricComponent.getHeadTerminal();
                    } else {
                        myNearestTerminal = electricComponent.getTailTerminal();
                    }
                }
                result = true;
            }
        }
        return result;
    }

    public boolean nearSwitch() {
        int x3 = getMouseX();
        int y3 = getMouseY();
        double mindistance = Double.MAX_VALUE;
        boolean result = false;
        for (Component electricComponent : currentCircuit.components.values()) {
            if (!(electricComponent instanceof Switch)) {
                continue;
            }
            int x1 = electricComponent.getHeadTerminal().getX();
            int y1 = electricComponent.getHeadTerminal().getY();
            int x2 = electricComponent.getTailTerminal().getX();
            int y2 = electricComponent.getTailTerminal().getY();
            double u = ((x3 - x1) * (x2 - x1) + (y3 - y1) * (y2 - y1)) / (sqr(x2 - x1) + sqr(y2 - y1));
            double x = x1 + u * (x2 - x1);
            double y = y1 + u * (y2 - y1);
            if (0 < u && u < 1 && sqr(x3 - 0.5 * (x1 + x2)) + sqr(y3 - 0.5 * (y1 + y2)) < 300) {
                if (sqr(x3 - x) + sqr(y3 - y) < mindistance) {
                    mindistance = sqr(x3 - x) + sqr(y3 - y);
                    myNearestSwitch = (Switch) electricComponent;
                }
                result = true;
            }
        }
        return result;
    }


    
    public void print()
    {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        boolean isNotCancel = printJob.printDialog();

        if(isNotCancel)
        {
            Paper paper = new Paper();

            //paper.setSize(2000, 2000);

            PageFormat pageFormat = new PageFormat();

            pageFormat.setOrientation(PageFormat.LANDSCAPE);
            pageFormat.setPaper(paper);

            printJob.setPrintable(drawingPanel, pageFormat);
            try {
                printJob.print();
            } catch (PrinterException p) {
                p.printStackTrace();
            }
        }
    }
    


}
