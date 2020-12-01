package org.mathpiper.ui.gui.applications.circuitpiper.view;

import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class ReadoutPanel extends JPanel {
    
    private JTextArea readoutTextArea;
    
    public ReadoutPanel()
    {
        readoutTextArea = new JTextArea();

        readoutTextArea.setFont(new Font("Monospaced", 0, 18));
        
        this.add(readoutTextArea);
    }
    
    public void setText(String readout)
    {
        this.readoutTextArea.setText(readout);
    }
    
    public void clear()
    {
        readoutTextArea.setText("");
    }
}
