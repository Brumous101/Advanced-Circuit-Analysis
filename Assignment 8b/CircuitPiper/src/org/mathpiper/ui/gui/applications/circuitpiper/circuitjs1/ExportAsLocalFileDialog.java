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

import java.util.Date;

public class ExportAsLocalFileDialog /*///extends DialogBox*/ {

    ///VerticalPanel vp;
    public static final native boolean downloadIsSupported() /*-{
		return !!(("download" in $doc.createElement("a")));
	 }-*/;

    public static final native String getBlobUrl(String data) /*-{
		var datain=[""];
		datain[0]=data;
		var oldblob = $doc.exportBlob;
		if (oldblob)
		    URL.revokeObjectURL(oldblob);
		var blob=new Blob(datain, {type: 'text/plain' } );
		var url = URL.createObjectURL(blob);
		$doc.exportBlob = url;
		return url;
	}-*/;

    public ExportAsLocalFileDialog(String data) {
        super();
        /*
		Button okButton;
		Anchor a;
		String url;
		vp=new VerticalPanel();
		setWidget(vp);
		setText(CirSim.LS("Export as Local File"));
		vp.add(new JLabel(CirSim.LS("Click on the link below to save your circuit")));
		url=getBlobUrl(data);
		Date date = new Date();
		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMdd-HHmm");
		String fname = "circuit-"+ dtf.format(date) + ".circuitjs.txt";
		a=new Anchor(fname, url);
		a.getElement().setAttribute("Download", fname);
		vp.add(a);
		vp.add(okButton = new Button(CirSim.LS("OK")));
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				closeDialog();
			}
		});
		this.center();
         */
    }

    protected void closeDialog() {
        ///this.hide();
    }

}
