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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class EditInfo {
    // mn/mx were used in the java version to create sliders in the edit dialog but we don't do that in the javascript version, so this
    // constructor is deprecated

    public EditInfo(String n, double val, double mn, double mx) {
        name = n;
        value = val;
        dimensionless = false;
    }

    public EditInfo(String n, double val) {
        name = n;
        value = val;
        dimensionless = false;
    }

    public EditInfo setDimensionless() {
        dimensionless = true;
        return this;
    }

    public int changeFlag(int flags, int bit) {
        if (checkbox.isSelected()) {
            return flags | bit;
        }
        return flags & ~bit;
    }

    public String name, text;
    public double value;
    public JTextField textf;
    public JComboBox<String> comboBox;
    public JCheckBox checkbox;
    public JButton button;
    JTextArea textArea;
    ///Widget widget;
    public boolean newDialog;
    boolean dimensionless;

    // for slider dialog
    JTextField minBox, maxBox, labelBox;

    boolean canCreateAdjustable() {
        return comboBox == null && checkbox == null && button == null && textArea == null /* /// && widget == null*/;
    }
}
