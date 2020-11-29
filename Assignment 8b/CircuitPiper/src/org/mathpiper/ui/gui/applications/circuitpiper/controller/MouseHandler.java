package org.mathpiper.ui.gui.applications.circuitpiper.controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.event.MouseInputListener;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.EditDialog;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.SwitchElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.VCCSElm;

import org.mathpiper.ui.gui.applications.circuitpiper.model.Terminal;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ACCurrentSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ACVoltageSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Ammeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Block;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.CapacitanceMeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Capacitor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.CurrentIntegrator;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.CurrentSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ControlledSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.DCVoltageSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.InductanceMeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Inductor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Ohmmeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Resistor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.VoltageIntegrator;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.transistors.Transistor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Voltmeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Wire;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ChangeACParametersFrame;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ChangePrimaryValueFrame;
import org.mathpiper.ui.gui.applications.circuitpiper.view.CircuitPanel;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ControlledSourceDialog;
import org.mathpiper.ui.gui.applications.circuitpiper.view.PhasePlane;
import org.mathpiper.ui.gui.applications.circuitpiper.view.PopupMenu;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ScopePanel;

/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */
public final class MouseHandler implements MouseMotionListener, MouseInputListener {

    CircuitPanel circuitPanel;
    
    private boolean isJustDoneDragging = false;

    public MouseHandler(final CircuitPanel theParentCircuitEnginePanel) {
        circuitPanel = theParentCircuitEnginePanel;
    }

    //@Override
    public void mouseDragged(MouseEvent theMouseEvent) {
        //System.out.println("dragged");
        circuitPanel.waiting = true;
        
        synchronized (circuitPanel.drawingPanel) {
            //System.out.println("begin drag");
            // TODO make more elegant
            circuitPanel.setMouseX(theMouseEvent.getX());
            circuitPanel.setMouseY(theMouseEvent.getY());
            if (!circuitPanel.isDrawing()
                    && circuitPanel.isMovingPoint()) {
                
                handleDragTerminal();
                
            } else if (!circuitPanel.isDrawing()
                    && circuitPanel.isMovingComponent()) {
                
                handleDragComponent();
                
            }
            //System.out.println("end dragged");
        }
        
        this.isJustDoneDragging = true;
        
        circuitPanel.waiting = false;
    }

    public void determineHint() {
        if (!circuitPanel.isDrawing()
                && !circuitPanel.isMovingPoint()) {
            if (circuitPanel.isNearTerminal()) {
                circuitPanel.hintNearTerminal();
                circuitPanel.myNearestComponent = null;
                // myParentCircuitEnginePanel.myNearestSwitch = null;
            } else if (circuitPanel.nearSwitch()) {
                circuitPanel.hintNearSwitch();
                circuitPanel.myNearestComponent = null;
            } else if (circuitPanel.nearComponent()) {
                circuitPanel.hintNearComponent();
                // myParentCircuitEnginePanel.myNearestTerminal=null;
            } else {
                circuitPanel.myNearestComponent = null;
                circuitPanel.myNearestTerminal = null;
                circuitPanel.setHintStarting();
            }
        }
    }

    //@Override
    public void mouseMoved(MouseEvent theMouseEvent) {
        //System.out.println("moved");
    
        //System.out.println("XXX " + theMouseEvent);
    
        //System.out.println("waiting");
        circuitPanel.waiting = true;

        synchronized (circuitPanel.drawingPanel) {
            // System.out.println("moved");
            circuitPanel.setMouseX(theMouseEvent.getX());
            circuitPanel.setMouseY(theMouseEvent.getY());
            if (circuitPanel.isDrawing()) {
                circuitPanel.myTempComponent
                        .moveTail(circuitPanel.nearestGridPointXPixels(), circuitPanel
                                .nearestGridPointYPixels());
            } else {
                determineHint();
            }
            circuitPanel.repaint();
        }
        circuitPanel.waiting = false;
    }

    //@Override
    public void mouseClicked(MouseEvent theMouseEvent) {

        /*
        if (theMouseEvent.getButton() == MouseEvent.BUTTON3) {
            circuitPanel.setIsDrawing(false);
            circuitPanel.setHintStarting();
            circuitPanel.repaint();
        }
        */


        if(this.isJustDoneDragging)
        {
            //Terminal.terminalCounter++;
            
            isJustDoneDragging = false;
        }
        
        circuitPanel.waiting = true;

        synchronized (circuitPanel.drawingPanel) {
            //System.out.println("clicked");
            // TODO reduce redundancy
            if (theMouseEvent.getClickCount() == 1 && theMouseEvent.getButton() == MouseEvent.BUTTON1) {
                if (circuitPanel.isMovingPoint()) {
                    // todo:tk:Ignore clicks if the mouse is moving.
                    //myParentCircuitEnginePanel.setIsMovingPoint(false);
                }

                if (!circuitPanel.isDrawing()
                        && !circuitPanel.isMovingPoint()
                        && !circuitPanel.isMovingComponent()) {
                    // todo:tk:legitimate first click in drawing mode.

                    if (!circuitPanel.isNearTerminal() && circuitPanel.nearSwitch()) {
                        // todo:tk:if the intent is to flip a switch, then leave.

                        //myParentCircuitEnginePanel.myNearestSwitch.flip();
                        //myParentCircuitEnginePanel.repaint();
                        return;
                    }
                    
                    if (circuitPanel.drawingPanel.isComponentHighlightingMode && circuitPanel.isNearTerminal()) {
                        final Terminal terminal = circuitPanel.myNearestTerminal ;
                        
                        if(terminal.isHighlight)
                        {
                            terminal.isHighlight = false;
                        }
                        else
                        {
                            terminal.isHighlight = true;
                        }
                        
                        return;
                    }
                    
                    circuitPanel.myNearestComponent = null;
                    circuitPanel.myNearestTerminal = null;

                    // clicked
                    
                    if (circuitPanel.drawingPanel.isComponentHighlightingMode && circuitPanel.nearComponent()) {
                        final Component ec = circuitPanel.myNearestComponent;
                        if(ec.isHighlight)
                        {
                            ec.isHighlight = false;
                        }
                        else
                        {
                            ec.isHighlight = true;
                        }
                    }
                    else
                    {
                        circuitPanel.setIsDrawing(true);

                        circuitPanel.setHintDrawing();

                        try {
                            circuitPanel.newTempComponent(circuitPanel.mySelectedComponent.replaceAll(" ", ""),
                                    circuitPanel.nearestGridPointXPixels(),
                                    circuitPanel.nearestGridPointYPixels());

                            circuitPanel.repaint();
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                        }
                    }

                    // Click another point on the canvas to create a component, or click
                    // cancel
                    // g.fillOval((int)(x1+alpha*deltax-4),(int)(y1+beta*deltay-4), 8, 8);
                } else if (circuitPanel.isDrawing()) {
                    // todo:tk:second click, the tail of a component is being placed.

                    if (circuitPanel.myTempComponent.badSize()) {
                        return;
                    }

                    Point headPoint = circuitPanel.myTempComponent.getHeadTerminal().getPosition();

                    Point nearestPoint = new Point(circuitPanel.nearestGridPointXPixels(),
                            circuitPanel.nearestGridPointYPixels());

                    if (circuitPanel.currentCircuit.terminals.containsKey(nearestPoint)
                            && circuitPanel.currentCircuit.terminals.containsKey(headPoint)
                            && circuitPanel.currentCircuit.terminals.get(headPoint)
                            .isConnectedTo(circuitPanel.currentCircuit.terminals.get(nearestPoint))) {
                        return;

                    }
                    /*myParentCircuitEnginePanel.nearTerminal();//required-side effects
                     if (myParentCircuitEnginePanel.nearTerminal()&&//##removed dec 23 08
            
                     myParentCircuitEnginePanel.myGridPoints.containsKey(p)&&
                     myParentCircuitEnginePanel.myGridPoints.get(p)
                     .isConnectedTo(myParentCircuitEnginePanel.myNearestTerminal)
                     ){
                     return;
                     }*/
                    /*if (myParentCircuitEnginePanel.nearTerminal()
                     &&!(myParentCircuitEnginePanel.myGridPoints.containsKey(p)&&
                     myParentCircuitEnginePanel.myGridPoints.get(p)
                     .isConnectedTo(myParentCircuitEnginePanel.myNearestTerminal))){
                     myParentCircuitEnginePanel.setIsDrawing(false);
                     myParentCircuitEnginePanel.setHintStarting();
                     myParentCircuitEnginePanel.addElectricComponent();
                     }*/
                    circuitPanel.setIsDrawing(false);
                    //myParentCircuitEnginePanel.setHintStarting();
                    
                    try
                    {
                        circuitPanel.currentCircuit.addComponent(circuitPanel.myTempComponent, circuitPanel.nearestGridPointXPixels(), circuitPanel.nearestGridPointYPixels());

                        if(circuitPanel.currentCircuit.isCirSim)
                        {
                            circuitPanel.currentCircuit.cirSim.elmList.add(circuitPanel.myTempComponent.getCircuitElm());
                            
                            circuitPanel.currentCircuit.cirSim.needAnalyze();
                        }

                    }
                    catch(Throwable e)
                    {
                        e.printStackTrace(); // todo:tk:maybe show an error dialog here.
                    }
                    
                    determineHint();
                    circuitPanel.repaint();
                    
                }
            } else if (theMouseEvent.getClickCount() == 1 && theMouseEvent.getButton() == MouseEvent.BUTTON3) {
                //System.out.println("hello");
                if (!circuitPanel.isDrawing()
                        && !circuitPanel.isMovingPoint()
                        && !circuitPanel.isMovingComponent()) {
                    
                    if (circuitPanel.isNearTerminal()) {
                        
                        final Point point = circuitPanel.myNearestTerminal.getPosition();
                        final Terminal nearestTerminal = circuitPanel.myNearestTerminal;
                        
                        
                        JPopupMenu jPopupMenu = new JPopupMenu();
                        
                        JMenuItem deleteMenuItem = new JMenuItem("Delete Terminal");
                        deleteMenuItem.addActionListener(new ActionListener() {
                            public void actionPerformed(final ActionEvent e) {
                                //System.out.println("delete");
                                Iterator<Component> i = circuitPanel.currentCircuit.components.values().iterator();
                                while (i.hasNext()) {
                                    Component ec = i.next();
                                    if (ec.getHeadTerminal().getPosition().equals(point)) {
                                        ec.disconnectHeadAndTail();
                                        i.remove();//myParentCircuitEnginePanel.myCircuit.components.remove(ec);
                                        if (ec.getTailTerminal().myConnectedTo.size() == 0) {
                                            circuitPanel.currentCircuit.terminals.remove(ec.getTailTerminal().getPosition());
                                        }
                                    } else if (ec.getTailTerminal().getPosition().equals(point)) {
                                        ec.disconnectHeadAndTail();
                                        i.remove();//myParentCircuitEnginePanel.myCircuit.components.remove(ec);
                                        if (ec.getHeadTerminal().myConnectedTo.size() == 0) {
                                            circuitPanel.currentCircuit.terminals.remove(ec.getHeadTerminal().getPosition());
                                        }
                                    }
                                }
                                circuitPanel.currentCircuit.terminals.remove(point);
                                // jPopupMenu.setVisible(false);
                            }
                        });
                        jPopupMenu.add(deleteMenuItem);
                        
                        JMenuItem nameMenuItem = new JMenuItem("Name Terminal");
                        nameMenuItem.addActionListener(new ActionListener() {
                            public void actionPerformed(final ActionEvent e) {
                                String name = JOptionPane.showInputDialog(null, "New name");
                                nearestTerminal.name = name;
                                circuitPanel.drawingPanel.repaint();
                            }
                        });
                        jPopupMenu.add(nameMenuItem);
                        
                        
                        jPopupMenu.show(circuitPanel, circuitPanel.getMouseX(),
                                circuitPanel.getMouseY());
                        
                    } else if (circuitPanel.nearComponent()) {
                        final Component ec = circuitPanel.myNearestComponent;
                        
                        JPopupMenu jPopupMenu = new JPopupMenu();
                        
                        if(circuitPanel.currentCircuit.isCirSim)
                        {
                            JMenuItem change = new JMenuItem("Edit Parameters");
                            change.addActionListener(new ActionListener() {
                                public void actionPerformed(final ActionEvent e) {

                                    EditDialog editDialog = new EditDialog(ec.getCircuitElm(), circuitPanel.currentCircuit.cirSim);
                                    editDialog.setModal(true);
                                    editDialog.setVisible(true);
                                }
                            });
                            jPopupMenu.add(change);
                        }
                        else
                        {
                            if (ec instanceof Voltmeter || ec instanceof Ammeter
                                    || ec instanceof CurrentIntegrator || ec instanceof VoltageIntegrator || ec instanceof Ohmmeter
                                    || ec instanceof CapacitanceMeter || ec instanceof InductanceMeter) {

                                JMenuItem graphItem = new JMenuItem("Graph this " + ec.getPrimary().substring(0, 1).toUpperCase() + ec.getPrimary().substring(1));
                                graphItem.addActionListener(new ActionListener() {
                                    public void actionPerformed(final ActionEvent e) {

                                        ScopePanel scopePanel = new ScopePanel(circuitPanel, 0, 0, ec.getID(), ec.getPrimary(), ec.getPrimaryUnitSymbol());
                                        ec.addScopePanel(scopePanel);
                                        //JOptionPane.showMessageDialog(null, "Not available in demo");
                                    }
                                });
                                jPopupMenu.add(graphItem);

                                JMenuItem phasePlaneYItem = new JMenuItem("Make this " + ec.getPrimary().substring(0, 1).toUpperCase()
                                        + ec.getPrimary().substring(1) + " the Y-axis of a phase plane");
                                phasePlaneYItem.addActionListener(new ActionListener() {
                                    public void actionPerformed(final ActionEvent e) {
                                        // /*demo  
                                        if (circuitPanel.phasePlanes.size() == 0
                                                || (circuitPanel.phasePlanes.getLast().yComponent != null
                                                && circuitPanel.phasePlanes.getLast().xComponent != null)) {
                                            PhasePlane phasePlane = new PhasePlane(circuitPanel, 0, 0);
                                            phasePlane.setY(ec);
                                            circuitPanel.phasePlanes.add(phasePlane);
                                        } else {//if (myParentCircuitEnginePanel.phasePlanes.getLast().xComponent==null){
                                            circuitPanel.phasePlanes.getLast().setY(ec);
                                        }
                                        // */
                                        //      JOptionPane.showMessageDialog(null, "Not available in demo");
                                    }
                                });
                                jPopupMenu.add(phasePlaneYItem);

                                JMenuItem phasePlaneXItem = new JMenuItem("Make this " + ec.getPrimary().substring(0, 1).toUpperCase()
                                        + ec.getPrimary().substring(1) + " the X-axis of a phase plane");
                                phasePlaneXItem.addActionListener(new ActionListener() {
                                    public void actionPerformed(final ActionEvent e) {
                                        // /*demo   
                                        if (circuitPanel.phasePlanes.size() == 0
                                                || (circuitPanel.phasePlanes.getLast().yComponent != null
                                                && circuitPanel.phasePlanes.getLast().xComponent != null)) {
                                            PhasePlane phasePlane = new PhasePlane(circuitPanel, 0, 0);
                                            phasePlane.setX(ec);
                                            circuitPanel.phasePlanes.add(phasePlane);
                                        } else {//if (myParentCircuitEnginePanel.phasePlanes.getLast().yComponent==null){
                                            circuitPanel.phasePlanes.getLast().setX(ec);
                                        }
                                        //  */
                                        //   JOptionPane.showMessageDialog(null, "Not available in demo");
                                    }
                                });
                                jPopupMenu.add(phasePlaneXItem);
                            }
                            else if (ec instanceof DCVoltageSource || ec instanceof Capacitor || ec instanceof Resistor
                                    || ec instanceof CurrentSource || ec instanceof Inductor) {

                                JMenuItem change = new JMenuItem("Change " + ec.getPrimary());
                                change.addActionListener(new ActionListener() {
                                    public void actionPerformed(final ActionEvent e) {

                                        if(circuitPanel.currentCircuit.isCirSim)
                                        {
                                            EditDialog editDialog = new EditDialog(ec.getCircuitElm(), circuitPanel.currentCircuit.cirSim);
                                            editDialog.setModal(true);
                                            editDialog.setVisible(true);
                                        }
                                        else
                                        {
                                            new ChangePrimaryValueFrame(circuitPanel, 0, 0, ec);
                                        }
                                    }
                                });
                                jPopupMenu.add(change);
                            }
                            else if (ec instanceof ACVoltageSource || ec instanceof ACCurrentSource) {
                                JMenuItem change = new JMenuItem("Adjust Paramaters");
                                change.addActionListener(new ActionListener() {
                                    public void actionPerformed(final ActionEvent e) {

                                        new ChangeACParametersFrame(circuitPanel, 0, 0, ec);
                                        //JOptionPane.showMessageDialog(null, "Not available in demo");
                                    }
                                });
                                jPopupMenu.add(change);
                            }
                            else if (ec instanceof ControlledSource) {
                                JMenuItem multipler = new JMenuItem("Expression");
                                multipler.addActionListener(new ActionListener() {
                                    public void actionPerformed(final ActionEvent e) {

                                        // todo:tk:support for CircuitPiper has been disabled for now.

                                        //ControlledSource controlledSource = (ControlledSource) ec;

                                        VCCSElm controlledElm = (VCCSElm) ec.getCircuitElm();

                                        String valueString = JOptionPane.showInputDialog(null, "Expression", controlledElm.exprString);

                                        if(valueString != null)
                                        {
                                            try
                                            {
                                                //double value = Double.parseDouble(valueString);

                                                controlledElm.setExpr(valueString);
                                                //controlledSource.setMultiplier(value);
                                            }
                                            catch(Throwable nfe)
                                            {
                                                JOptionPane.showMessageDialog(null, "The expression was not formatted correctly.", "", JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                    }
                                });
                                jPopupMenu.add(multipler);

                                JMenuItem change = new JMenuItem("Controlling Component");
                                change.addActionListener(new ActionListener() {
                                    public void actionPerformed(final ActionEvent e) {
                                        new ControlledSourceDialog((ControlledSource) ec, circuitPanel.currentCircuit.components);
                                    }
                                });
                                jPopupMenu.add(change);
                            }
                            else if (ec instanceof CurrentIntegrator || ec instanceof Capacitor
                                    || ec instanceof Inductor || ec instanceof VoltageIntegrator) {
                                JMenuItem resetMenuItem = new JMenuItem("Reset " + ec.getSecondary() + " to 0 " + ec.getSecondaryUnitSymbol());
                                resetMenuItem.addActionListener(new ActionListener() {
                                    public void actionPerformed(final ActionEvent e) {
                                        ec.setSecondaryValue(0);
                                        if (ec instanceof CurrentIntegrator) {
                                            CurrentIntegrator ci = (CurrentIntegrator) ec;
                                            ci.setChargeString("0.00 C");
                                            circuitPanel.repaint();
                                        }
                                        if (ec instanceof VoltageIntegrator) {
                                            VoltageIntegrator ci = (VoltageIntegrator) ec;
                                            ci.setMagneticFluxString("0.00 Wb");
                                            circuitPanel.repaint();
                                        }
                                    }
                                });
                                jPopupMenu.add(resetMenuItem);
                            }
                            else if (!(ec instanceof Wire) && ! (ec instanceof Resistor)) {
                                JMenuItem reverseOrientationMenuItem = new JMenuItem("Reverse Orientation");
                                reverseOrientationMenuItem.addActionListener(new ActionListener() {
                                    public void actionPerformed(final ActionEvent e) {
                                        ec.reverse();
                                    }
                                });
                                jPopupMenu.add(reverseOrientationMenuItem);
                            }
                            else if (ec instanceof Transistor) {
                                JMenuItem reverseOrientationMenuItem = new JMenuItem("Mirror");
                                reverseOrientationMenuItem.addActionListener(new ActionListener() {
                                    public void actionPerformed(final ActionEvent e) {
                                        Transistor transistor = (Transistor) ec;
                                        transistor.mirror();
                                    }
                                });
                                jPopupMenu.add(reverseOrientationMenuItem);
                            }
                            else if (ec instanceof Block) {
                                JMenuItem enterTextMenuItem = new JMenuItem("Text");
                                enterTextMenuItem.addActionListener(new ActionListener() {
                                    public void actionPerformed(final ActionEvent e) {
                                        final JTextArea textArea = new JTextArea(4, 10);

                                        JOptionPane pane = new JOptionPane(textArea, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
                                            @Override
                                            public void selectInitialValue() {
                                                textArea.requestFocusInWindow();
                                            }
                                        };
                                        JDialog dialog = pane.createDialog(null, "Enter text");
                                        dialog.setVisible(true);
                                        dialog.dispose();

                                        Object object = pane.getValue();
                                        if(object instanceof Integer)
                                        {
                                            if(((Integer) object) == JOptionPane.OK_OPTION)
                                            {

                                                Block block = (Block) ec;
                                                block.setText(textArea.getText());
                                            }
                                        }
                                    }
                                });
                                jPopupMenu.add(enterTextMenuItem);
                            }
                        }

                        
                        JMenuItem deleteMenuItem = new JMenuItem("Delete Component");
                        deleteMenuItem.addActionListener(new ActionListener() {
                            public void actionPerformed(final ActionEvent e) {
                                //System.out.println("delete");
                                
                                
                                ec.disconnectHeadAndTail();
                                
                                if (ec.getHeadTerminal().myConnectedTo.size() == 0) {
                                    circuitPanel.currentCircuit.terminals.remove(ec.getHeadTerminal().getPosition());
                                }
                                
                                if (ec.getTailTerminal().myConnectedTo.size() == 0) {
                                    circuitPanel.currentCircuit.terminals.remove(ec.getTailTerminal().getPosition());
                                }
                                
                                
                                if(ec.getCircuitElm() != null)
                                {
                                    if(ec.getCircuitElm() instanceof VCCSElm)
                                    {
                                        VCCSElm cirElm = (VCCSElm) ec.getCircuitElm();
                                        
                                        if(cirElm.wireControl0 != null)
                                        {
                                            circuitPanel.currentCircuit.cirSim.elmList.remove(cirElm.wireControl0);
                                        }
                                        
                                        if(cirElm.wireControl1 != null)
                                        {
                                            circuitPanel.currentCircuit.cirSim.elmList.remove(cirElm.wireControl1);
                                        }
                                    }
                                    
                                    circuitPanel.currentCircuit.cirSim.elmList.remove(ec.getCircuitElm());
                                    
                                    circuitPanel.currentCircuit.cirSim.needAnalyze();
                                }
                                
                                ec.deleteScopePanels();
                                
                                circuitPanel.currentCircuit.components.remove(ec.getID());
                                // jPopupMenu.setVisible(false);
                            }
                        });
                        jPopupMenu.add(deleteMenuItem);
                        
                        
                        JMenuItem changeNumberMenuItem = new JMenuItem("Change Subscript");
                        changeNumberMenuItem.addActionListener(new ActionListener() {
                            public void actionPerformed(final ActionEvent e) {
                                String subscript = JOptionPane.showInputDialog(null, "New Subscript");
                                
                                if(subscript.equals(""))
                                {
                                    JOptionPane.showMessageDialog(null, "Subscripts cannot be blank.");
                                    return;
                                }
                                
                                if(subscript.contains(" ") || subscript.contains("\t") || subscript.contains("\n"))
                                {
                                    JOptionPane.showMessageDialog(null, "Subscripts cannot contain whitespace characters.");
                                    return;
                                }
                                
                                String newID = ec.getPrimarySymbol() + subscript;

                                if(circuitPanel.currentCircuit.components.keySet().contains(newID))
                                {
                                    JOptionPane.showMessageDialog(null, "The ID " + newID + " already exists.");
                                }
                                else
                                {
                                    circuitPanel.currentCircuit.components.remove(ec.getID(), ec);
                                    ec.setComponentSubscript(subscript);
                                    circuitPanel.currentCircuit.components.put(newID, ec);
                                    circuitPanel.drawingPanel.repaint();
                                }
                            }
                        });
                        jPopupMenu.add(changeNumberMenuItem);
                        
                        JMenuItem isHideValueCheckBox = new JCheckBoxMenuItem("Hide Value");
                        //probeCheckBox.setMnemonic(KeyEvent.VK_C); 
                        isHideValueCheckBox.setSelected(ec.isHideValue);
                        isHideValueCheckBox.addItemListener(new ItemListener() {
                            public void itemStateChanged(ItemEvent e) {
                                if(e.getStateChange() == ItemEvent.SELECTED)
                                {
                                    ec.isHideValue = true;
                                }
                                else
                                {
                                    ec.isHideValue = false;
                                }

                                circuitPanel.drawingPanel.repaint();;
                            }
                        });
                        jPopupMenu.add(isHideValueCheckBox);
                        
                        JMenuItem showInScopeMenuItem = new JMenuItem("Show Voltage In Scope");
                        showInScopeMenuItem.addActionListener(new ActionListener() {
                            public void actionPerformed(final ActionEvent e) {
                                
                                ScopePanel scopePanel = new ScopePanel(circuitPanel, 0, 0, ec.getID(), "Voltage", "V");
                                ec.addScopePanel(scopePanel);
                            }
                        });
                        jPopupMenu.add(showInScopeMenuItem);
                        
                        showInScopeMenuItem = new JMenuItem("Show Current In Scope");
                        showInScopeMenuItem.addActionListener(new ActionListener() {
                            public void actionPerformed(final ActionEvent e) {
                                
                                ScopePanel scopePanel = new ScopePanel(circuitPanel, 0, 0, ec.getID(), "Current", "A");
                                ec.addScopePanel(scopePanel);
                            }
                        });
                        jPopupMenu.add(showInScopeMenuItem);
                        
                        
                        // ===============================
                        jPopupMenu.show(circuitPanel, circuitPanel.getMouseX(),
                                circuitPanel.getMouseY());
                    }
                }
                else
                {
                    PopupMenu popupMenu = new PopupMenu(circuitPanel);

                    popupMenu.show(circuitPanel, theMouseEvent.getX(), theMouseEvent.getY());
                }
            }
        }

        circuitPanel.waiting = false;

    }

    //@Override
    public void mouseEntered(MouseEvent theMouseEvent) {
        // System.out.println("entered");
        circuitPanel.setMouseEntered(true);
        circuitPanel.repaint();
    }

    //@Override
    public void mouseExited(MouseEvent arg0) {
        circuitPanel.setMouseEntered(false);
        if (!circuitPanel.isDrawing()
                && !circuitPanel.isMovingPoint()
                && !circuitPanel.isMovingComponent()) {
            circuitPanel.myNearestComponent = null;
            circuitPanel.myNearestTerminal = null;
            circuitPanel.setHintStarting();
        }
        circuitPanel.repaint();
        // System.out.println("exited");
    }

    //@Override
    public void mousePressed(MouseEvent theMouseEvent) {

        circuitPanel.waiting = true;

        synchronized (circuitPanel.drawingPanel) {
            //System.out.println("pressed");
            if (theMouseEvent.getButton() == MouseEvent.BUTTON1) {
                circuitPanel.setButtonState(true);
                if (!circuitPanel.isDrawing()
                        && !circuitPanel.isMovingPoint()
                        && !circuitPanel.isMovingComponent()) {
                    if (!circuitPanel.isNearTerminal() && circuitPanel.nearSwitch()) {
                        circuitPanel.myNearestSwitch.flip();
                        if(circuitPanel.currentCircuit.isCirSim)
                        {
                            ((SwitchElm) circuitPanel.myNearestSwitch.getCircuitElm()).toggle();
                            circuitPanel.currentCircuit.cirSim.needAnalyze();
                        }
                        circuitPanel.repaint();
                    } else if (circuitPanel.isNearTerminal()) {
                        circuitPanel.setIsMovingPoint(true);
                        circuitPanel.draggedTerminal
                                = circuitPanel.myNearestTerminal;
                    } else if (circuitPanel.nearComponent()) {
                        //myParentCircuitEnginePanel.nearTerminal();//to set Nearest
                        circuitPanel.setIsMovingComponent(true);
                        circuitPanel.myDraggedComponent
                                = circuitPanel.myNearestComponent;
                        circuitPanel.draggedTerminal
                                = circuitPanel.myNearestTerminal;
                    }
                }
            }
        }
        circuitPanel.waiting = false;

    }

    //@Override
    public void mouseReleased(MouseEvent theMouseEvent) {

        circuitPanel.waiting = true;

        synchronized (circuitPanel.drawingPanel) {
            if (theMouseEvent.getButton() == MouseEvent.BUTTON1) {
                circuitPanel.setButtonState(false);
                if (circuitPanel.isMovingPoint()) {
                    //System.out.println("a"+myParentCircuitEnginePanel.myDraggedTerminal.myConnectedTo.size());
                    Point point
                            = //new Point(myParentCircuitEnginePanel.nearestX(), myParentCircuitEnginePanel.nearestY());
                            new Point(circuitPanel.draggedTerminal.getX(),
                                    circuitPanel.draggedTerminal.getY());
                    //System.out.println("aa"+myParentCircuitEnginePanel.myGridPoints.get(point).myConnectedTo.size());
                    for (Component e : circuitPanel.currentCircuit.components.values()) {
                        if (e.getHeadTerminal() == circuitPanel.draggedTerminal) {
                            e.setHead(circuitPanel.currentCircuit.terminals.get(point));
                            //System.out.println("head");
                        } else if (e.getTailTerminal() == circuitPanel.draggedTerminal) {
                            e.setTail(circuitPanel.currentCircuit.terminals.get(point));
                            //System.out.println("tail");
                        }
                    }
                    //System.out.println(myParentCircuitEnginePanel.myDraggedTerminal.myConnectedTo.size());
                    circuitPanel.draggedTerminal = null;
                    circuitPanel.myDraggedComponent = null;
                    circuitPanel.setIsMovingPoint(false);
                    circuitPanel.setIsMovingComponent(false);
                    if(circuitPanel.currentCircuit.isCirSim)
                    {
                        circuitPanel.currentCircuit.cirSim.needAnalyze();
                    }
                } else if (circuitPanel.isMovingComponent()) {
                    circuitPanel.draggedTerminal = null;
                    circuitPanel.myDraggedComponent = null;
                    circuitPanel.setIsMovingPoint(false);
                    circuitPanel.setIsMovingComponent(false);
                    if(circuitPanel.currentCircuit.isCirSim)
                    {
                        circuitPanel.currentCircuit.cirSim.needAnalyze();
                    }
                }
                determineHint();
                circuitPanel.repaint();
            }
            //System.out.println("released");
        }

        circuitPanel.waiting = false;

    }

    public void handleDragTerminal() {
        if (circuitPanel.nearestGridPointXPixels() != circuitPanel.draggedTerminal.getX()
                || circuitPanel.nearestGridPointYPixels() != circuitPanel.draggedTerminal.getY()) {
            Point point = new Point(circuitPanel.nearestGridPointXPixels(),
                    circuitPanel.nearestGridPointYPixels());
            Point oldPoint = new Point(circuitPanel.draggedTerminal.getX(),
                    circuitPanel.draggedTerminal.getY());
            //System.out.println(point+" "+oldPoint);
            if (!circuitPanel.currentCircuit.terminals.containsKey(point)) {//that is if we are dragging to an unoccupied location
                if (circuitPanel.currentCircuit.terminals.get(oldPoint)
                        == circuitPanel.draggedTerminal) {//that is, if there is no point left behind
                    //System.out.println("123");
                    circuitPanel.currentCircuit.terminals.remove(oldPoint);
                }
                //System.out.println("567");
                circuitPanel.currentCircuit.terminals
                        .put(point, circuitPanel.draggedTerminal);
                circuitPanel.draggedTerminal.setX(circuitPanel
                        .nearestGridPointXPixels());
                circuitPanel.draggedTerminal.setY(circuitPanel
                        .nearestGridPointYPixels());
            } else {//if there is already a point here
                if (!circuitPanel.draggedTerminal
                        .isConnectedTo(circuitPanel.currentCircuit.terminals.get(point))
                        && !circuitPanel.draggedTerminal
                        .isConnectedToOrder2(circuitPanel.currentCircuit.terminals.get(point))) {//that is, if the new point at the new location isn't connectod to the old point
                    // System.out.println("not connected");
                    if (circuitPanel.currentCircuit.terminals.get(oldPoint)
                            == circuitPanel.draggedTerminal) {//that is, if there is no point left behind after drag
                        //myParentCircuitEnginePanel.myDraggedTerminal=
                        //System.out.println("removed old");
                        circuitPanel.currentCircuit.terminals.remove(oldPoint);
                    }
                    circuitPanel.draggedTerminal.setX(circuitPanel
                            .nearestGridPointXPixels());
                    circuitPanel.draggedTerminal.setY(circuitPanel
                            .nearestGridPointYPixels());
                }
            }
            circuitPanel.repaint();
        }
    }

    public void handleDragComponent() {
        if (circuitPanel.nearestGridPointXPixels() != circuitPanel.draggedTerminal.getX()
                || circuitPanel.nearestGridPointYPixels() != circuitPanel.draggedTerminal.getY()) 
        {
            
            //System.out.println("dragged comp");
            Point point = new Point(circuitPanel.nearestGridPointXPixels(),
                    circuitPanel.nearestGridPointYPixels());
            
            Point oldTerminalPoint = new Point(circuitPanel.draggedTerminal.getX(),
                    circuitPanel.draggedTerminal.getY());
            
            if (circuitPanel.isNearTerminal()) {//must be called because this method has side effects
                //System.out.println("connected");
            }
            
            boolean isAttachedToAnothercomponent = false;
            
            if (circuitPanel.draggedTerminal.myConnectedTo.size() == 1
                    && !(circuitPanel.currentCircuit.terminals.containsKey(point)
                    && ((circuitPanel.myDraggedComponent.getHeadTerminal() == circuitPanel.draggedTerminal
                    && (circuitPanel.myDraggedComponent.getTailTerminal() == circuitPanel.myNearestTerminal
                    || circuitPanel.myDraggedComponent.getTailTerminal().isConnectedTo(circuitPanel.myNearestTerminal)))
                    || (circuitPanel.myDraggedComponent.getTailTerminal() == circuitPanel.draggedTerminal
                    && (circuitPanel.myDraggedComponent.getHeadTerminal() == circuitPanel.myNearestTerminal
                    || circuitPanel.myDraggedComponent.getHeadTerminal().isConnectedTo(circuitPanel.myNearestTerminal))))) //!(myParentCircuitEnginePanel.myGridPoints.containsKey(point)&&
                    //  (myParentCircuitEnginePanel.myDraggedTerminal
                    // .isConnectedTo(myParentCircuitEnginePanel.myGridPoints.get(point))||
                    // myParentCircuitEnginePanel.myDraggedTerminal
                    //.isConnectedToOrder2(myParentCircuitEnginePanel.myGridPoints.get(point))))
                    ) {
                //System.out.println("r2");
                //System.out.prntln();
                circuitPanel.currentCircuit.terminals.remove(oldTerminalPoint);
                isAttachedToAnothercomponent = true;
            }
            
            if (!circuitPanel.currentCircuit.terminals.containsKey(point)) {//that is if there is no point at this location yet
            
                try
                {
                    Terminal newTerminal = new Terminal(circuitPanel.nearestGridPointXPixels(),
                        circuitPanel.nearestGridPointYPixels(), circuitPanel.currentCircuit);

                    if(this.isJustDoneDragging && isAttachedToAnothercomponent)
                    {
                        Terminal.terminalCounter--;
                    }

                    //System.out.println();

                    circuitPanel.currentCircuit.terminals.put(point, newTerminal);//then put a point there

                    if (circuitPanel.myDraggedComponent.getHeadTerminal()
                            == circuitPanel.draggedTerminal) {//and set the location of the head
                        circuitPanel.myDraggedComponent
                                .setHead(circuitPanel.currentCircuit.terminals.get(point));
                        circuitPanel.draggedTerminal = circuitPanel.myDraggedComponent.getHeadTerminal();
                    } else {//or tail of the dragged component accordingly
                        circuitPanel.myDraggedComponent
                                .setTail(circuitPanel.currentCircuit.terminals.get(point));
                        //myParentCircuitEnginePanel.myDraggedComponent.setTail(newTerminal);
                        circuitPanel.draggedTerminal = circuitPanel.myDraggedComponent.getTailTerminal();
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            } else {//if there is a point here already
                //myParentCircuitEnginePanel.myGridPoints.put(point, newTerminal);
                // System.out.println("4");
                if ((circuitPanel.myDraggedComponent.getHeadTerminal() == circuitPanel.draggedTerminal
                        && (circuitPanel.myDraggedComponent.getTailTerminal() == circuitPanel.myNearestTerminal
                        || circuitPanel.myDraggedComponent.getTailTerminal().isConnectedTo(circuitPanel.myNearestTerminal)))//if this is the head and is connectedorder2 to nearesttiepoint 
                        || (circuitPanel.myDraggedComponent.getTailTerminal() == circuitPanel.draggedTerminal
                        && (circuitPanel.myDraggedComponent.getHeadTerminal() == circuitPanel.myNearestTerminal
                        || circuitPanel.myDraggedComponent.getHeadTerminal().isConnectedTo(circuitPanel.myNearestTerminal))) //myParentCircuitEnginePanel.myDraggedTerminal
                        //.isConnectedTo(myParentCircuitEnginePanel.myGridPoints.get(point))||
                        //myParentCircuitEnginePanel.myDraggedTerminal
                        //.isConnectedToOrder2(myParentCircuitEnginePanel.myGridPoints.get(point))
                        // myParentCircuitEnginePanel.myNearestTerminal.isConnectedTo(
                        // myParentCircuitEnginePanel.myDraggedTerminal)||
                        //myParentCircuitEnginePanel.myNearestTerminal.isConnectedToOrder2(
                        //myParentCircuitEnginePanel.myDraggedTerminal)
                        ) {
                    //System.out.println("connected");
                    //System.out.println(myParentCircuitEnginePanel.myNearestTerminal.getY());
                    //System.out.println(myParentCircuitEnginePanel.myDraggedTerminal.getY());
                    return;//then don't do anything, we can't move here
                }
                
                //if we reach this point, it means that we are moving the component to a location that already has a point
                if (circuitPanel.myDraggedComponent.getHeadTerminal()
                        == circuitPanel.draggedTerminal) {//if we're moving the head of draggedcomp
                    circuitPanel.myDraggedComponent
                            .setHead(circuitPanel.currentCircuit.terminals.get(point));//then move the head
                    circuitPanel.draggedTerminal = circuitPanel.myDraggedComponent.getHeadTerminal();
                } else {
                    circuitPanel.myDraggedComponent
                            .setTail(circuitPanel.currentCircuit.terminals.get(point));
                    //myParentCircuitEnginePanel.myDraggedComponent.setTail(newTerminal);
                    circuitPanel.draggedTerminal = circuitPanel.myDraggedComponent.getTailTerminal();
                }
            }
            circuitPanel.myNearestTerminal = circuitPanel.draggedTerminal;
            circuitPanel.repaint();
        }

    }
}
