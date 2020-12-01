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

public final class GraphFrame extends JFrame {

    public Plot plotBox;
    
    public String componentID;
    public String primary;
    public String unit;

    public GraphFrame(CircuitPanel parent, int x, int y, String componentID, String primary, String unit) {
        super();
        
        if(componentID == null)
        {
            componentID = "None";
        }

        if(primary == null)
        {
            primary = "None";
        }
        
        if(unit == null)
        {
            unit = "None";
        }
        
        this.componentID = componentID;
        this.primary = primary;
        this.unit = unit;
                
        plotBox = new Plot();
      
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
        String capitalized = primary.substring(0, 1).toUpperCase() + primary.substring(1);
        plotBox.setYLabel(capitalized + " (" + unit + ")");
        plotBox.setXLabel("Time (s)");
        //plotBox.setBackground(new Color(128,128,255));
        plotBox.setBackground(Color.white);
        plotBox.setTitle(capitalized + " Versus Time");
        this.setTitle(componentID + " " + capitalized);
        Color[] colors = new Color[1];
        colors[0] = new Color(12, 173, 59);
        plotBox.setColors(colors);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                plotBox = null;
                GraphFrame.this.dispose();
            }
        });

        //plotBox.
        this.getContentPane().add(plotBox);
        this.pack();
        this.setVisible(true);
    }
}
