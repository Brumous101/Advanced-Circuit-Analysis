package org.mathpiper.ui.gui.applications.circuitpiper.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CircuitElm;

import org.mathpiper.ui.gui.applications.circuitpiper.controller.MouseHandler;
import org.mathpiper.ui.gui.applications.circuitpiper.model.Terminal;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Ammeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.CapacitanceMeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Capacitor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.CurrentIntegrator;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.InductanceMeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Meter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Ohmmeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.VoltageIntegrator;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Voltmeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra.SparseMatrix;

/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */
public final class DrawingPanel extends JPanel implements ViewPanel, Printable {

    public CircuitPanel circuitPanel;
    MouseHandler eventHandler;

    public boolean updated;

    public double viewScale = 1.5;
    public int fontSize = 11;
    public boolean isShowBoardOutline = false;
    public double boardXPixels = 0;
    public double boardYPixels = 0;
    public boolean drawPlus = true;
    public boolean isDrawWireLabels = false;
    public boolean isDrawTerminalLabels = false;
    public boolean isDrawComponentValues = true;
    public boolean isDrawGrid = true;
    public boolean isReadout = true;
    private long readoutUpdatePeriod = 100;
    private long oldUpdateTime = 0;
    public boolean isComponentHighlightingMode = false;

    public DrawingPanel(final CircuitPanel parentCircuitEnginePanel) {
        super();
        circuitPanel = parentCircuitEnginePanel;
        eventHandler
                = new MouseHandler(parentCircuitEnginePanel);
        this.addMouseMotionListener(eventHandler);
        this.addMouseListener(eventHandler);
        this.setBackground(Color.WHITE);
        
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), "Cancel");
        this.getActionMap().put("Cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                circuitPanel.setIsDrawing(false);
                //circuitPanel.setHintStarting();
                circuitPanel.repaint();
            }
        });
        
    }


       

    public void updateGraphsAndMeters(SparseMatrix mainMatrix, double time) {
        /* todo:tk:commented out for not to delay needing to address a component having multiple graph frames.
        for (Component ec : circuitPanel.currentCircuit.components.values()) {
            if (ec instanceof Voltmeter) {
                Voltmeter voltmeter = (Voltmeter) ec;
                voltmeter.setVoltageString(mainMatrix.getVoltageDeltaVString(ec, true));
                voltmeter.setFullValue(mainMatrix.getVoltageDeltaVString(ec, false));
                voltmeter.setSecondaryValue(mainMatrix.getComponentDeltaV(ec));
                if (ec.getGraphFrames() != null && ec.getGraphFrames().plotBox != null) {
                    ec.getGraphFrames().plotBox.addPoint(0, time, mainMatrix.getComponentDeltaV(ec), true);
                    ec.getGraphFrames().plotBox.fillPlot();
                }
            }
            if (ec instanceof Ammeter) {
                Ammeter ammeter = (Ammeter) ec;
                ammeter.setAmpString(mainMatrix.getCurrentString(ec, true));
                ammeter.setFullValue(mainMatrix.getCurrentString(ec, false));
                ammeter.setSecondaryValue((double) mainMatrix.getCurrent(ec));

                if (ec.getGraphFrames() != null && ec.getGraphFrames().plotBox != null) {
                    ec.getGraphFrames().plotBox.addPoint(0, time, mainMatrix.getCurrent(ec), true);
                    ec.getGraphFrames().plotBox.fillPlot();
                }
            }
            if (ec instanceof CurrentIntegrator) {
                CurrentIntegrator currentIntegrator = (CurrentIntegrator) ec;
                currentIntegrator.setChargeString(Component.formatValue(currentIntegrator.getSecondaryValue()) + "C");
                currentIntegrator.setFullValue(currentIntegrator.getSecondaryValue() + "C");
                currentIntegrator.setSecondaryValue(currentIntegrator.getSecondaryValue());
                if (ec.getGraphFrames() != null && ec.getGraphFrames().plotBox != null) {
                    ec.getGraphFrames().plotBox.addPoint(0, time, ec.getSecondaryValue(), true);
                    ec.getGraphFrames().plotBox.fillPlot();
                }
            }
            if (ec instanceof VoltageIntegrator) {
                VoltageIntegrator voltageIntegrator = (VoltageIntegrator) ec;
                voltageIntegrator.setMagneticFluxString(Component.formatValue(voltageIntegrator.getSecondaryValue()) + "Wb");
                voltageIntegrator.setFullValue(voltageIntegrator.getSecondaryValue() + "Wb");
                voltageIntegrator.setSecondaryValue(voltageIntegrator.getSecondaryValue());
                if (ec.getGraphFrames() != null && ec.getGraphFrames().plotBox != null) {
                    ec.getGraphFrames().plotBox.addPoint(0, time, ec.getSecondaryValue(), true);
                    ec.getGraphFrames().plotBox.fillPlot();
                }
            }
            if (ec instanceof Ohmmeter) {
                Ohmmeter ohmmeter = (Ohmmeter) ec;
                ohmmeter.setOhmString(mainMatrix.getResistanceDeltaVString(ec, true));
                ohmmeter.setFullValue(mainMatrix.getResistanceDeltaVString(ec, false));
                ohmmeter.setSecondaryValue(mainMatrix.getComponentDeltaV(ec));
                if (ec.getGraphFrames() != null && ec.getGraphFrames().plotBox != null) {
                    ec.getGraphFrames().plotBox.addPoint(0, time, mainMatrix.getComponentDeltaV(ec), true);
                    ec.getGraphFrames().plotBox.fillPlot();
                }
            }
            if (ec instanceof CapacitanceMeter) {
                CapacitanceMeter capacitanceMeter = (CapacitanceMeter) ec;
                capacitanceMeter.setCapacitanceString(mainMatrix.getCapacitanceString(ec, true));
                capacitanceMeter.setFullValue(mainMatrix.getCapacitanceString(ec, false));
                capacitanceMeter.setSecondaryValue(mainMatrix.getCapacitance(ec));
                if (ec.getGraphFrames() != null && ec.getGraphFrames().plotBox != null) {
                    ec.getGraphFrames().plotBox.addPoint(0, time, mainMatrix.getCapacitance(ec), true);
                    ec.getGraphFrames().plotBox.fillPlot();
                }
            }
            if (ec instanceof InductanceMeter) {
                InductanceMeter inductanceMeter = (InductanceMeter) ec;
                inductanceMeter.setInductanceString(mainMatrix.getInductanceDeltaVString(ec, true));
                inductanceMeter.setFullValue(mainMatrix.getInductanceDeltaVString(ec, false));
                inductanceMeter.setSecondaryValue(mainMatrix.getInductanceDeltaV(ec));
                if (ec.getGraphFrames() != null && ec.getGraphFrames().plotBox != null) {
                    ec.getGraphFrames().plotBox.addPoint(0, time, mainMatrix.getInductanceDeltaV(ec), true);
                    ec.getGraphFrames().plotBox.fillPlot();
                }
            }
        
        }
        */
        
        for (PhasePlane phasePlane : circuitPanel.phasePlanes) {
            if (phasePlane.xComponent == null || phasePlane.yComponent == null
                    || !circuitPanel.currentCircuit.components.containsValue(phasePlane.xComponent)
                    || !circuitPanel.currentCircuit.components.containsValue(phasePlane.yComponent)) {
                continue;
            }
            phasePlane.pb.addPoint(0, phasePlane.xComponent.getSecondaryValue(), phasePlane.yComponent.getSecondaryValue(), true);
            phasePlane.pb.fillPlot();
        }
    }






    /**
     * Draws the panel showing all the components. Draws grid points,
     * components, labels. Grid points are only shown near mouse cursor.
     * Components are sometimes drawn in a special color.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        ScaledGraphics sg = new ScaledGraphics(g);
        sg.setViewScale(viewScale);
        sg.setFontSize(fontSize);
                
        drawGridPoints(sg);
        
    //updateCircuit();

        try
        {
            // now we know I, ie k1
            drawComponents(sg);

            for (Component ec : circuitPanel.currentCircuit.components.values()) {
                if (ec instanceof Capacitor && ec.getPrimaryValue() != null) {
                    //System.out.println(ec.secondaryValue);
                }
            }
            if (circuitPanel.isDrawing()) {
                drawTempComponent(sg);
            }

            if(isShowBoardOutline)
            {
                drawBoard(sg);
            }
            
            drawTerminals(sg);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        
        //Iterator i;
        //i = myParentCircuitEnginePanel.myCircuit.components.iterator();
        // System.out.println(terminals.size());
        // boolean
        // isClose=movingState!=MOVING&drawingState!=DRAWING&isCloseToComponent(mouseLocation)&!isClose(new
        // Terminal(new Point(mouseLocation)), INNER_RADIUS);
    }
    
    private void drawBoard(ScaledGraphics sg)
    {
        double oldLineWidth = sg.getLineWidth();
        sg.setColor(Color.BLACK);
        sg.setLineWidth(2);
        sg.drawRectangle(0, 0, boardXPixels, boardYPixels); // 432 = 6*72
        sg.setLineWidth(oldLineWidth);
    }
    
    
    public void drawTerminals(ScaledGraphics sg)
    {
        /*
        // todo:tk:only show terminals that have 3 or more components attched to them.
        for (Terminal t : myParentCircuitEnginePanel.myCircuit.myTerminals.values()) {
            if (t.myConnectedTo.size() >= 3) {
                sg.setColor(Color.black);
                sg.fillOval(t.getX() - 3, t.getY() - 3, 6, 6);
            }
        }
        */
        
        
        // todo:tk:show all active terminals.
        for (Terminal terminal : circuitPanel.currentCircuit.terminals.values()) {
            
            if(terminal.isInactive)
            {
                continue;
            }
            
            if(terminal.myConnectedTo.size() == 1)
            {
                sg.setColor(Color.red);
            }
            else
            {
                if(terminal.isHighlight)
                {
                    sg.setColor(Color.MAGENTA);
                }
                else
                {
                    sg.setColor(Color.black);
                }
            }
            
            
            sg.fillOval(terminal.getX() - 3, terminal.getY() - 3, 6, 6);
            
            if(this.isDrawTerminalLabels)
            {
                sg.drawString(terminal.getID(), terminal.getX() + 4, terminal.getY() - 4);
            }
            
            if(! terminal.name.equals(""))
            {
                sg.drawString(terminal.name, terminal.getX() - 10, terminal.getY() - 4);
            }
            
            sg.setColor(Color.black);
        }
    }

    public void drawGridPoints(final ScaledGraphics sg) {
         
        sg.setColor(Color.black);
        
        if(isDrawGrid)
        {
            int x1 = circuitPanel.leftSideOffsetPixels;
            double deltax = circuitPanel.terminalXSpacing;
            int y1 = circuitPanel.topSideYOffsetPixels;
            double deltay = circuitPanel.terminalYSpacing;
            int xDistanceBetweenTerminalsPixels = circuitPanel.xDistanceBetweenTerminalsPixels;
            int yDistanceBetweenTerminalsPixels = circuitPanel.yDistanceBetweenTerminalsPixels;
            for (double x = x1; x <= x1 + deltax * (xDistanceBetweenTerminalsPixels - 1.0); x += deltax) {
                for (double y = y1; y <= y1 + deltay * (yDistanceBetweenTerminalsPixels - 1.0); y += deltay) {
                    sg.fillOval(x - 1, y - 1, 3, 3);
                }
            }
        }
        
        sg.setColor(Color.LIGHT_GRAY);
        
        if (circuitPanel.getMouseEntered()) {
            
            /*
            for (Terminal t : myParentCircuitEnginePanel.myCircuit.myTerminals.values()) {
                sg.setColor(Color.black);
                sg.fillOval(t.getX() - 3, t.getY() - 3, 6, 6);
            }
            */
            
            
            // Draw large gray circle around nearest terminal.
            sg.setColor(Color.LIGHT_GRAY);
            
            if (!circuitPanel.isDrawing()
                    && !circuitPanel.isMovingPoint()
                    && !circuitPanel.isMovingComponent()) {
                
                Point nearestPoint = new Point(circuitPanel.nearestGridPointXPixels(), circuitPanel
                        .nearestGridPointYPixels());
                
                if (circuitPanel.currentCircuit.terminals.containsKey(nearestPoint)
                        && circuitPanel.myNearestTerminal == circuitPanel.currentCircuit.terminals
                        .get(nearestPoint)) {
                    sg.setColor(Color.blue);

                    Terminal nearTerminal = circuitPanel.currentCircuit.terminals.get(nearestPoint);
                    
                    String postVoltageString = "None";
                    
                    Set<Component> allAttahcedComponents = new HashSet();
                    
                    allAttahcedComponents.addAll(nearTerminal.in);
                    allAttahcedComponents.addAll(nearTerminal.out);
                    
                    foundTerminal:
                    for(Component component : allAttahcedComponents)
                    {
                        CircuitElm cirElm = component.circuitElm;
                        int postCount = cirElm.getPostCount();
                        for(int index = 0; index < postCount; index++)
                        {
                            org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Point postPoint = cirElm.getPost(index);
                            
                            if(nearTerminal.getX() == postPoint.x && nearTerminal.getY() == postPoint.y)
                            {
                                postVoltageString = "" + cirElm.getPostVoltage(index);
                                break foundTerminal;
                            }
                        }
                        
                    }

                    this.circuitPanel.hintPanel.message("T" + nearTerminal.terminalNumber + ": " + nearTerminal.getX() + ", " + nearTerminal.getY()  + ", " + postVoltageString);
                    
                    circuitPanel.readoutPanel.setText("T" + nearTerminal.terminalNumber + ": " + postVoltageString + " V");
                }
                
                sg.fillOval(circuitPanel.nearestGridPointXPixels() - 5, circuitPanel
                        .nearestGridPointYPixels() - 5, 10, 10);
            }
        }
    }
    
    private Component lastReadoutComponent = null;

    public void drawComponents(final ScaledGraphics sg) throws Exception {
        // System.out.println();
        // System.out.println(myParentCircuitEnginePanel.myGridPoints.values().size());
        sg.setColor(Color.black);
        
        
        if(isReadout)
        {
            if(circuitPanel.currentCircuit.isCirSim)
            {
                sg.drawString("CirSim Time: " + CircuitElm.getTimeText(circuitPanel.currentCircuit.cirSim.simulationTime), 10, 10);
                sg.drawString("CirSim Step size: " + CircuitElm.getTimeText(circuitPanel.currentCircuit.cirSim.timeStep), 10, 25);
                
            }
            else
            {
                sg.drawString("Time: " + circuitPanel.currentCircuit.time /*String.format("%.2f", myParentCircuitEnginePanel.myCircuit.time)*/ + "s", 10, 10);
                sg.drawString("Step size: " + circuitPanel.currentCircuit.getStepSize() + "s", 10, 25);
            }
        }
        
        for (Component ec : circuitPanel.currentCircuit.components.values()) {
            // System.out.println(e.head);
            // System.out.println(e.tail);
            if (!circuitPanel.isDrawing() && //!myParentCircuitEnginePanel.getIsMovingComponent()&&
                    !circuitPanel.isMovingPoint()
                    && !circuitPanel.isNearTerminal()
                    && circuitPanel.nearSwitch()
                    && ec == circuitPanel.myNearestSwitch) {
                sg.setColor(Color.MAGENTA);
                ec.draw(sg);
                
            } else if (ec == circuitPanel.myNearestComponent) {
                sg.setColor(Color.blue);
                
                double oldLineWidth = sg.getLineWidth();
                sg.setLineWidth(2.0);
                ec.draw(sg);
                sg.setLineWidth(oldLineWidth);
                
                
                sg.fillOval(circuitPanel.myNearestTerminal.getX() - 4,
                        circuitPanel.myNearestTerminal.getY() - 4, 8, 8);
                
                if (ec instanceof Ammeter || ec instanceof CurrentIntegrator
                        || ec instanceof VoltageIntegrator || ec instanceof Voltmeter
                        || ec instanceof Ohmmeter || ec instanceof CapacitanceMeter
                        || ec instanceof InductanceMeter) {
                    sg.drawString(ec.getFullValue(), 10, 40);
                }

                /* todo:tk:experimental.
                if (myParentCircuitEnginePanel.isProbe) {
                    this.myParentCircuitEnginePanel.myHintPanel.message(myParentCircuitEnginePanel.myCircuit.myComponentNames.get(ec.getID()) + ", " + myParentCircuitEnginePanel.myCircuit.mainMatrix.getCurrent(ec));
                }
                */
                if(System.currentTimeMillis() - this.oldUpdateTime > this.readoutUpdatePeriod)
                {
                    circuitPanel.readoutPanel.setText(ec.getInfo());
                    
                    oldUpdateTime = System.currentTimeMillis();
                    
                    lastReadoutComponent = ec;
                }
                
            } else {
                

                if(ec == lastReadoutComponent)
                {   
                    lastReadoutComponent = null;
                    circuitPanel.readoutPanel.clear();
                }

                
                if(ec.isHighlight)
                {
                    sg.setColor(Color.MAGENTA);
                
                    double oldLineWidth = sg.getLineWidth();
                    sg.setLineWidth(2.0);
                    ec.draw(sg);
                    sg.setLineWidth(oldLineWidth);
                }
                else
                {
                    sg.setColor(Color.black);
                    ec.draw(sg);
                }
            }

            int x1 = (int) ec.getHeadTerminal().getX();
            int y1 = (int) ec.getHeadTerminal().getY();
            int x2 = (int) ec.getTailTerminal().getX();
            int y2 = (int) ec.getTailTerminal().getY();

            if (ec instanceof Ammeter) {
                //System.out.println(((Ammeter)e).ampString);
                sg.drawString(((Ammeter) ec).getAmpString(), (int) ((x1 + x2) * 0.5 + 5)
                        - Meter.METER_SIZE,
                        (int) (0.5 * (y1 + y2)));
            } else if (ec instanceof CurrentIntegrator) {
                sg.drawString(((CurrentIntegrator) ec).getChargeString(), (int) ((x1 + x2) * 0.5 + 5)
                        - Meter.METER_SIZE,
                        (int) (0.5 * (y1 + y2)));
            } else if (ec instanceof VoltageIntegrator) {
                sg.drawString(((VoltageIntegrator) ec).getMagneticFluxString(), (int) ((x1 + x2) * 0.5 + 4) -//changed 5 to 4 on Dec 25 2008
                        Meter.METER_SIZE,
                        (int) (0.5 * (y1 + y2)));
            } else if (ec instanceof Voltmeter) {
                sg.drawString(((Voltmeter) ec).getVoltageString(), (int) ((x1 + x2) * 0.5 + 5)
                        - Meter.METER_SIZE, (int) (0.5 * (y1 + y2)));
            } else if (ec instanceof Ohmmeter) {
                sg.drawString(((Ohmmeter) ec).getOhmString(), (int) ((x1 + x2) * 0.5 + 5)
                        - Meter.METER_SIZE, (int) (0.5 * (y1 + y2)));
            } else if (ec instanceof CapacitanceMeter) {
                sg.drawString(((CapacitanceMeter) ec).getCapacitanceString(), (int) ((x1 + x2) * 0.5 + 5)
                        - Meter.METER_SIZE, (int) (0.5 * (y1 + y2)));
            } else if (ec instanceof InductanceMeter) {
                sg.drawString(((InductanceMeter) ec).getInductanceString(), (int) ((x1 + x2) * 0.5 + 5)
                        - Meter.METER_SIZE, (int) (0.5 * (y1 + y2)));
            }
        }
    }

    public void drawTempComponent(final ScaledGraphics sg) throws Exception {
        sg.setColor(Color.GRAY);
        circuitPanel.myTempComponent.draw(sg);
    }
    
    
    public void setViewScale(double viewScale) {
        this.viewScale = viewScale;

        this.revalidate();
        this.repaint();
    }
    
    public Dimension getPreferredSize() {
        
        int largestX = circuitPanel.getSize().width;
        int largestY = circuitPanel.getSize().height;
                for (Terminal terminal : circuitPanel.currentCircuit.terminals.values()){
                    
                    if(terminal.getX() * viewScale > largestX)
                    {
                        largestX = (int) (terminal.getX() * viewScale) + 150;
                    }
                    
                    if(terminal.getY() * viewScale > largestY)
                    {
                        largestY = (int) (terminal.getY() * viewScale) + 150;
                    }
                }
        return new Dimension(largestX, largestY);
    }
    
    
    public Dimension getMaximumSize() {
        return this.getPreferredSize();
    }

    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }
    
    public BufferedImage getImageOfCircuit() throws Exception
    {
        // todo:tk:this code needs to be fine-tuned.
        double oldScale = viewScale;
        viewScale = 1.0;
        isReadout = false;

        // This section calculates the bounds.
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        ScaledGraphicsBounds sgb = new ScaledGraphicsBounds(g2d);
        sgb.setFontSize(12);
        drawTerminals(sgb);
        drawComponents(sgb);
        g2d.dispose();
        Rectangle region = sgb.getBounds().getBounds();

        BufferedImage image2 = new BufferedImage(region.width, region.height + 3, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d2 = image2.createGraphics();
        g2d2.translate(-sgb.getLeftMostX() + sgb.getBorder(), -(sgb.getTopMostY() - sgb.getBorder()));
        this.paint(g2d2);
        g2d2.dispose();
        this.setOpaque(true);
        
        viewScale = oldScale;
            
        return image2;
    }
    
    
    public void toEPS() throws Exception
    {
        this.viewScale = 1.0;
        isReadout = false;
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
	Graphics2D g2d = image.createGraphics();

        ScaledGraphicsBounds sgb = new ScaledGraphicsBounds(g2d);
        sgb.setFontSize(12);

        //drawGridPoints(sgb);
        
        drawComponents(sgb);
        drawTerminals(sgb);    
        
        Rectangle bounds = sgb.getBounds().getBounds();
        
        //System.out.println("XXX " + bounds);
        

        FileOutputStream file = new java.io.FileOutputStream("/home/tkosan/atmp2/foo2.eps");
        EPSGraphics2 eps = new EPSGraphics2(file, (int)bounds.getX(), (int)bounds.getY(), (int)bounds.getWidth(), (int)bounds.getHeight());
        ScaledGraphics sg = new ScaledGraphics(eps);
        sg.setViewScale(1.0);
        this.viewScale = 1.0;
        sg.setFontSize(12);
        drawTerminals(sg);    
        drawGridPoints(sg);
        drawComponents(sg);
        
        eps.showpage();
        file.close();
    }
    
    
    
    
    public int print(Graphics g, PageFormat pageFormat, int page) throws PrinterException {
        // Original code obtained from https://stackoverflow.com/questions/17904518/fit-scale-jcomponent-to-page-being-printed
        Graphics2D g2d = (Graphics2D) g;

        // Move origin to printer area corner.
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        //FontMetrics titleFontMetrics = g2d.getFontMetrics(titleFont);
        //FontMetrics fontMetrics = g2d.getFontMetrics(new Font("helvetica", Font.PLAIN, 10));

        //System.out.println(page);
        if (page > 0) {
            return Printable.NO_SUCH_PAGE;
        }
        
        //System.out.println(pageFormat.getImageableWidth()  + ", " + pageFormat.getImageableHeight());
        
/*
        // Get the preferred size of the component...
        Dimension componentSize = this.getPreferredSize();
        
        // Make sure we size to the preferred size
        this.setSize(componentSize);
        // Get the the print size
        Dimension printSize = new Dimension();
        printSize.setSize(pageFormat.getImageableWidth(), pageFormat.getImageableHeight());

        // Calculate the scale factor
        double scaleFactor = getScaleFactorToFit(componentSize, printSize);
        // Don't want to scale up, only want to scale down
        if (scaleFactor > 1d) {
            scaleFactor = 1d;
        }

        // Calcaulte the scaled size...
        double scaleWidth = componentSize.width * scaleFactor;
        double scaleHeight = componentSize.height * scaleFactor;

        // Create a clone of the graphics context.  This allows us to manipulate
        // the graphics context without begin worried about what effects
        // it might have once we're finished
        Graphics2D g2 = (Graphics2D) g.create();
        // Calculate the x/y position of the component, this will center
        // the result on the page if it can
        double x = ((pageFormat.getImageableWidth() - scaleWidth) / 2d) + pageFormat.getImageableX();
        double y = ((pageFormat.getImageableHeight() - scaleHeight) / 2d) + pageFormat.getImageableY();
        // Create a new AffineTransformation
        AffineTransform at = new AffineTransform();
        // Translate the offset to out "center" of page
        at.translate(x, y);
        // Set the scaling
        at.scale(scaleFactor, scaleFactor);
        // Apply the transformation
        g2.transform(at);
        // Print the component
        this.printAll(g2);
        // Dispose of the graphics context, freeing up memory and discarding
        // our changes
        g2.dispose();

        this.revalidate();
*/        
        
        
        //g2d.drawLine(0, 50, 432, 50);
        
        this.viewScale = 1.0;
        
        this.paintComponent(g);
        

        /*
        ScaledGraphics sg = new ScaledGraphics(g);
        sg.setViewScale(viewScale);
        sg.setFontSize(fontSize);

        drawGridPoints(sg);
    //updateCircuit();

        // now we know I, ie k1
        drawComponents(sg);

        for (Component ec : myParentCircuitEnginePanel.myCircuit.components) {
            if (ec instanceof Capacitor && ec.primaryValue != null) {
                //System.out.println(ec.secondaryValue);
            }
        }
        if (myParentCircuitEnginePanel.isDrawing()) {
            drawTempComponent(sg);
        } //*/
        

        return Printable.PAGE_EXISTS;
    }
    
    
    public static double getScaleFactorToFit(Dimension original, Dimension toFit) {

        double dScale = 1d;

        if (original != null && toFit != null) {

            double dScaleWidth = getScaleFactor(original.width, toFit.width);
            double dScaleHeight = getScaleFactor(original.height, toFit.height);

            dScale = Math.min(dScaleHeight, dScaleWidth);

        }

        return dScale;

    }

    public static double getScaleFactor(int iMasterSize, int iTargetSize) {

        double dScale = 1;
        if (iMasterSize > iTargetSize) {

            dScale = (double) iTargetSize / (double) iMasterSize;

        } else {

            dScale = (double) iTargetSize / (double) iMasterSize;

        }

        return dScale;

    }
    
    
/*
    // todo:tk:I am not sure what this code is used for.
    
    public HashSet<Component> union(HashSet<Component> a, HashSet<Component> b) {
        HashSet<Component> u = new HashSet<Component>(a);
        u.addAll(b);
        return u;
    }

    public void findConstantCaps() {
        for (Component ec : myParentCircuitEnginePanel.myCircuit.components) {
            if (ec instanceof Capacitor) {
                ec.isConstant = false;
                ec.isHeldConstant = false;
                HashSet<Component> seen = new HashSet<Component>();
                for (Component e : union(ec.headTerminal.in, ec.headTerminal.out)) {
                    if ((e instanceof Wire || e instanceof DCVoltageSource || e instanceof Ammeter
                            || ec instanceof CurrentIntegrator)) {
                        seen.add(e);
                        expand(ec, e, seen);
                    }
                }
                //(ec,ec,seen);
            } else {
                ec.isConstant = false;
                ec.isHeldConstant = false;
            }
        }
    }
    
        public void expand(Component e1, Component e2, HashSet<Component> seen) {
        if (e1.isConstant) {
            return;
        }
        for (Component ec : union(union(e2.headTerminal.in, e2.headTerminal.out), union(e2.tailTerminal.in, e2.tailTerminal.out))) {
            if ((ec instanceof Wire || ec instanceof DCVoltageSource || ec instanceof Ammeter
                    || ec instanceof CurrentIntegrator)
                    && !seen.contains(ec)) {
                if (ec.headTerminal == e1.tailTerminal || ec.tailTerminal == e1.tailTerminal) {
                    e1.isConstant = true;
                    e1.isHeldConstant = true;
                    System.out.println("is constant");
                    return;
                }
                seen.add(ec);
                expand(e1, ec, seen);
            }
        }
    }
*/

}
