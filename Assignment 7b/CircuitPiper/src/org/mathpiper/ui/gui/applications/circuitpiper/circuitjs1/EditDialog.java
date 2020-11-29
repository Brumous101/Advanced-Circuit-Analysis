/*    
    Copyright (C) Paul Falstad and Iain Sharp
    
    This file is part of CircuitJS1.

    CircuitJS1 is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    CircuitJS1 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CircuitJS1.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CircuitElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.VoltageElm;

/*///
interface Editable {
    EditInfo getEditInfo(int n);
    void setEditValue(int n, EditInfo ei);
}
 */
// class EditDialog extends Dialog implements AdjustmentListener, ActionListener, ItemListener {
public class EditDialog extends JDialog
/*///extends DialogBox*/ {

    Editable elm;
    CirSim cframe;
    JButton applyButton, okButton, cancelButton;
    EditInfo einfos[];
    int einfocount;
    final int barmax = 1000;
    ///VerticalPanel vp;
    Box vp;

    ///HorizontalPanel hp;
    Box hp;

    public static NumberFormat noCommaFormat; /// = NumberFormat.getFormat("####.##########");

    public EditDialog(Editable ce, CirSim f)
    {

//		super(f, "Edit Component", false);
        super(); // Do we need this?
        setLocationRelativeTo(null);
        setTitle(CirSim.LS("Edit Component"));
        this.cframe = f;
        elm = ce;
//		setLayout(new EditDialogLayout());
        ///	vp=new VerticalPanel();
        vp = Box.createVerticalBox();

        ///	setWidget(vp);
        this.add(vp);

        einfos = new EditInfo[10];
        noCommaFormat = DecimalFormat.getInstance();
        noCommaFormat.setMaximumFractionDigits(10);
        noCommaFormat.setGroupingUsed(false);
        
        
        buildDialog();
        
        ///	hp=new HorizontalPanel();
        hp = Box.createHorizontalBox();

        ///	hp.setWidth("100%");
        ///	hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        ///	hp.setStyleName("topSpace");
        vp.add(hp);

        hp.add(applyButton = new JButton(CirSim.LS("Apply")));
        
        applyButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                try
                {
                    apply();
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                }
            }
        });

        hp.add(okButton = new JButton(CirSim.LS("OK")));
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                try
                {
                    apply();
                    closeDialog();
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                }
            }
        });
        //hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

        hp.add(cancelButton = new JButton(CirSim.LS("Cancel")));

        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                try
                {
                    closeDialog();
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                }
            }
        });
        
        //this.center();

        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        this.setResizable(false);

        vp.add(Box.createVerticalGlue());

        this.pack();
    }

    
    
    void buildDialog()
    {
        int i;
        ///int idx;
        for (i = 0;; i++)
        {
            JLabel label = null;
            einfos[i] = elm.getEditInfo(i);
            if (einfos[i] == null)
            {
                break;
            }
            EditInfo ei = einfos[i];
            ///	idx = vp.getWidgetIndex(hp);
            String name = CirSim.LS(ei.name);
            ///	if (ei.name.startsWith("<"))
            ///	    vp.insert(l = new HTML(name),idx);
            ///	else
            ///		    vp.insert(l = new JLabel(name),idx);
            
            label = new JLabel(name);
            //label.setHorizontalAlignment(SwingConstants.LEFT);
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            labelPanel.add(label);
            vp.add(labelPanel);
            ///		if (i!=0 && l != null)
            ///			l.setStyleName("topSpace");

            ///idx = vp.getWidgetIndex(hp);
            if (ei.comboBox != null)
            {
                ///vp.insert(ei.choice,idx);
                vp.add(ei.comboBox);
                /*
                ei.comboBox.addActionListener(new ActionListener()
                {
                    
                    public void actionPerformed(ActionEvent e)
                    {
                        try
                        {
                            itemStateChanged(e);
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }

                });
                */
            }
            else if (ei.checkbox != null)
            {
                ///vp.insert(ei.checkbox,idx);
                JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                checkboxPanel.add(ei.checkbox);
                vp.add(checkboxPanel);
                
                /*
                ei.checkbox.addItemListener(new ItemListener()
                {
                    public void itemStateChanged(ItemEvent e)
                    {
                        ActionEvent ae = new ActionEvent(e.getSource(), -1, "None");
                        try
                        {
                            EditDialog.this.itemStateChanged(ae);
                        }
                        catch(Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                });
                */
            }
            else if (ei.button != null)
            {
                ///vp.insert(ei.button, idx);
                vp.add(ei.button);
                ei.button.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        try
                        {
                            itemStateChanged(event);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else if (ei.textArea != null)
            {
                ///vp.insert(ei.textArea, idx);
                vp.add(ei.textArea);
                
                ///} else if (ei.widget != null) {
                ///    ///vp.insert(ei.widget, idx);
                ///    vp.add(ei.widget);
            }
            else
            {
                ///vp.insert(ei.textf = new JTextField(), idx);
                vp.add(ei.textf = new JTextField());
                if (ei.text != null)
                {
                    ei.textf.setText(ei.text);
                }
                if (ei.text == null)
                {
                    ei.textf.setText(unitString(ei));
                }
            }
        }
        einfocount = i;

    }


    public void itemStateChanged(ActionEvent e) throws Exception
    {
        Object src = e.getSource();

        int i;
        boolean changed = false;
        boolean applied = false;
        for (i = 0; i != einfocount; i++)
        {
            EditInfo ei = einfos[i];
            
            if (ei.comboBox == src || ei.checkbox == src || ei.button == src)
            {

                // if we're pressing a button, make sure to apply changes first
                if (ei.button == src && !ei.newDialog)
                {
                    apply();
                    applied = true;
                }

                elm.setEditValue(i, ei);
                
                if (ei.newDialog)
                {
                    changed = true;
                }
                
                if (elm instanceof CircuitElm)
                {
                    ((CircuitElm) elm).sim.drawingPanel.repaint();
                }
                
                cframe.needAnalyze();
            }
        }
        if (changed)
        {
            // apply changes before we reset everything
            // (need to check if we already applied changes; otherwise Diode create simple model button doesn't work)
            if (!applied)
            {
                apply();
            }

            clearDialog();
            buildDialog();
        }
    }
        

    void apply() throws Exception
    {
        int i;
        for (i = 0; i != einfocount; i++)
        {
            EditInfo ei = einfos[i];
            
            if (ei.textf != null && ei.text == null)
            {
                try
                {
                    double d = parseUnits(ei);
                    ei.value = d;
                }
                catch (Exception ex)
                {
                    /* ignored */ 
                }
            }
            if (ei.button != null)
            {
                continue;
            }
            
            elm.setEditValue(i, ei);

            
            if (elm instanceof CircuitElm)
            {
                // update slider if any
                Adjustable adj = cframe.findAdjustable((CircuitElm) elm, i);
                if (adj != null)
                {
                    adj.setSliderValue(ei.value);
                }
                
                ((CircuitElm) elm).sim.drawingPanel.repaint();
            }
        }
        cframe.needAnalyze();
    }



    public void clearDialog()
    {
        /*
        /// todo:tk
		while (vp.getWidget(0)!=hp)
			vp.remove(0);
         */
    }

    protected void closeDialog()
    {
        ///EditDialog.this.hide();
        this.dispose();
        cframe.editDialog = null;
    }
    
    public static final double ROOT2 = 1.41421356237309504880;

    double diffFromInteger(double x)
    {
        return Math.abs(x - Math.round(x));
    }

    String unitString(EditInfo ei)
    {
        // for voltage elements, express values in rms if that would be shorter
        if (elm != null && elm instanceof VoltageElm
                && Math.abs(ei.value) > 1e-4
                && diffFromInteger(ei.value * 1e4) > diffFromInteger(ei.value * 1e4 / ROOT2))
        {
            return unitString(ei, ei.value / ROOT2) + "rms";
        }
        return unitString(ei, ei.value);
    }

    public static String unitString(EditInfo ei, double v)
    {

        double va  = Math.abs(v);
        if (ei.dimensionless)
        {
            return noCommaFormat.format(v);
        }
        if (v == 0)
        {
            return "0";
        }
        if (va  < 1e-9)
        {
            return noCommaFormat.format(v * 1e12) + "p";
        }
        if (va  < 1e-6)
        {
            return noCommaFormat.format(v * 1e9) + "n";
        }
        if (va  < 1e-3)
        {
            return noCommaFormat.format(v * 1e6) + "u";
        }
        if (va  < 1 /*&& !ei.forceLargeM*/)
        {
            return noCommaFormat.format(v * 1e3) + "m";
        }
        if (va  < 1e3)
        {
            return noCommaFormat.format(v);
        }
        if (va  < 1e6)
        {
            return noCommaFormat.format(v * 1e-3) + "k";
        }
        if (va  < 1e9)
        {
            return noCommaFormat.format(v * 1e-6) + "M";
        }
        return noCommaFormat.format(v * 1e-9) + "G";

        ///return null;
    }

    double parseUnits(EditInfo ei) throws java.text.ParseException
    {
        String s = ei.textf.getText();
        return parseUnits(ei, s);
    }

    public static double parseUnits(EditInfo ei, String s) throws java.text.ParseException
    {
        s = s.trim();
        double rmsMult = 1;
        if (s.endsWith("rms"))
        {
            s = s.substring(0, s.length() - 3).trim();
            rmsMult = ROOT2;
        }
        // rewrite shorthand (eg "2k2") in to normal format (eg 2.2k) using regex
        s = s.replaceAll("([0-9]+)([pPnNuUmMkKgG])([0-9]+)", "$1.$3$2");
        int len = s.length();
        char uc = s.charAt(len - 1);
        double mult = 1;
        switch (uc)
        {
            case 'p':
            case 'P':
                mult = 1e-12;
                break;
            case 'n':
            case 'N':
                mult = 1e-9;
                break;
            case 'u':
            case 'U':
                mult = 1e-6;
                break;

            // for ohm values, we used to assume mega for lowercase m, otherwise milli
            case 'm':
                mult = /*(ei.forceLargeM) ? 1e6 : */ 1e-3;
                break;

            case 'k':
            case 'K':
                mult = 1e3;
                break;
            case 'M':
                mult = 1e6;
                break;
            case 'G':
            case 'g':
                mult = 1e9;
                break;
        }
        if (mult != 1)
        {
            s = s.substring(0, len - 1).trim();
        }

        return noCommaFormat.parse(s).doubleValue() * mult * rmsMult;
    }
}
