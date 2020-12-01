package org.mathpiper.ui.gui.applications.circuitpiper.view;


import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PanelController extends JPanel implements ChangeListener, ItemListener {

    private JSlider scaleSlider;
    protected ViewPanel viewPanel;

    public PanelController(ViewPanel viewPanel, double primaryValue) {
        super();
        this.viewPanel = viewPanel;

        scaleSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int) (primaryValue*10));
        scaleSlider.addChangeListener(this);

        scaleSlider.setPaintLabels(true);

        this.add(new JLabel("Resize"));
        this.add(scaleSlider);
        //this.setBackground(Color.WHITE);
    }

    public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        //if (!source.getValueIsAdjusting()) {
        int intValue = (int) source.getValue();
        double doubleValue = intValue / 10.0;
        //System.out.println("XXX: " + doubleValue);
        viewPanel.setViewScale(doubleValue);
        viewPanel.repaint();

        //}
        }//end method.

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            viewPanel.repaint();

        } else {
            viewPanel.repaint();
        }

    }//end method.
    }
