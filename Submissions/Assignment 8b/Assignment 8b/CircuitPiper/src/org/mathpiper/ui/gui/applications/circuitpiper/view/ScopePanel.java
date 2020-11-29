/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mathpiper.ui.gui.applications.circuitpiper.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.mathpiper.ui.gui.applications.circuitpiper.view.pt.Plot;


public class ScopePanel extends JPanel
{
     public Plot plotBox;
    
    public String componentID;
    public String primary;
    public String unit;

    public ScopePanel(CircuitPanel parent, int x, int y, String componentID, String primary, String unit) {
        
        this.setLayout(new BorderLayout());
        
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
        
        this.add(menuBar, BorderLayout.NORTH);
      //this.setJMenuBar(menuBar);
        String capitalized = primary.substring(0, 1).toUpperCase() + primary.substring(1);
        plotBox.setYLabel(capitalized + " (" + unit + ")");
        plotBox.setXLabel("Time (s)");
        //plotBox.setBackground(new Color(128,128,255));
        plotBox.setBackground(Color.white);
        plotBox.setTitle(capitalized + " Versus Time");

        
        Border blackline = BorderFactory.createLineBorder(Color.black);
        
        TitledBorder titledBorder = BorderFactory.createTitledBorder(blackline, componentID + " " + capitalized);
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        this.setBorder(titledBorder);
      
        Color[] colors = new Color[1];
        colors[0] = new Color(12, 173, 59);
        plotBox.setColors(colors);
 

        add(plotBox);

    }   
}
