package org.mathpiper.ui.gui.applications.circuitpiper.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.view.pt.Plot;

/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */
public final class PhasePlane extends JFrame {

    public Plot pb;
    public Component xComponent = null;
    public Component yComponent = null;
    private CircuitPanel myParent;

    public PhasePlane(CircuitPanel parent, int x, int y) {
        super();
        myParent = parent;
        final Plot plotBox = new Plot();
        pb = plotBox;
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Graph Options");
        JMenuItem clearMenuItem = new JMenuItem("Clear Graph");
        clearMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                plotBox.clear(0);
            }
        });
        JMenuItem resizeMenuItem = new JMenuItem("Resize Graph to Fit Data");
        resizeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                plotBox.fillPlot();
            }
        });
        menu.add(clearMenuItem);
        menu.add(resizeMenuItem);
        menuBar.add(menu);
        this.setJMenuBar(menuBar);
        //String capitalized=primary.substring(0,1).toUpperCase()+primary.substring(1);
        //plotBox.setYLabel(capitalized+" ("+unit+")");
        //plotBox.setXLabel("Time (s)");
        //plotBox.setBackground(new Color(128,128,255));
        plotBox.setBackground(Color.white);
        //plotBox.setTitle(capitalized+" Versus Time");
        this.setTitle("Phase Plane Graph");
        Color[] colors = new Color[1];
        colors[0] = new Color(12, 173, 59);
        plotBox.setColors(colors);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                synchronized (myParent.drawingPanel) {
                    pb = null;
                    myParent.phasePlanes.remove(PhasePlane.this);
                    PhasePlane.this.dispose();
                }
            }
        });

        //plotBox.
        this.getContentPane().add(plotBox);
        this.pack();
        this.setVisible(true);
    }

    public void setX(Component ec) {
        String capitalized = ec.getPrimary().substring(0, 1).toUpperCase() + ec.getPrimary().substring(1);
        this.pb.setXLabel(capitalized + " (" + ec.getPrimaryUnitSymbol() + ")");
        this.xComponent = ec;
        if (yComponent != null) {
            this.pb.setTitle(yComponent.getPrimary().substring(0, 1).toUpperCase()
                    + yComponent.getPrimary().substring(1) + " Versus " + capitalized);
            ///this.repaint();
            this.pack();
            this.repaint();
        }
    }

    public void setY(Component ec) {
        String capitalized = ec.getPrimary().substring(0, 1).toUpperCase() + ec.getPrimary().substring(1);
        this.pb.setYLabel(capitalized + " (" + ec.getPrimaryUnitSymbol() + ")");
        this.yComponent = ec;
        if (xComponent != null) {
            this.pb.setTitle(capitalized + " Versus " + xComponent.getPrimary().substring(0, 1).toUpperCase()
                    + xComponent.getPrimary().substring(1));
            this.pack();
            this.repaint();
        }
    }
}
