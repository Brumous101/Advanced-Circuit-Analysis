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


import javax.swing.JTextArea;

public class ImportFromTextDialog /*///extends DialogBox*/ {

///VerticalPanel vp;
///HorizontalPanel hp;
    CirSim sim;
// RichTextArea textBox;
    JTextArea textArea;

    public ImportFromTextDialog(CirSim asim) {
        /*
		super();
		sim=asim;
		Button okButton, cancelButton;
		vp=new VerticalPanel();
		setWidget(vp);
		setText(sim.LS("Import from Text"));
		vp.add(new JLabel(sim.LS("Paste the text file for your circuit here...")));
//		vp.add(textBox = new RichTextArea());
		vp.add(textArea = new TextArea());
		textArea.setWidth("300px");
		textArea.setHeight("200px");
		hp = new HorizontalPanel();
		vp.add(hp);
		hp.add(okButton = new Button(sim.LS("OK")));
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String s;
				sim.pushUndo();
				closeDialog();
//				s=textBox.getHTML();
//				s=s.replace("<br>", "\r");
				s=textArea.getText();
				if (s!=null)
					sim.readSetup(s, true);
			}
		});
		hp.add(cancelButton = new Button(sim.LS("Cancel")));
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				closeDialog();
			}
		});
		this.center();
		show();
         */
    }

    protected void closeDialog() {
        ///this.hide();
    }

}
