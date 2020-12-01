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

public class LoadFile /*///extends FileUpload implements  ChangeHandler*/ {

    public static CirSim sim;

    public static final native boolean isSupported() /*-{
			return !!($wnd.File && $wnd.FileReader);
		 }-*/;

    public static void doLoadCallback(String s, String t) throws Exception {
        sim.pushUndo();
        sim.readSetup(s, true);
        sim.createNewLoadFile();
        sim.setCircuitTitle(t);
    }

    LoadFile(CirSim s) {
        super();
        sim = s;
        ///this.setName("Import");
        ///this.getElement().setId("LoadFileElement");
        ///this.addChangeHandler(this);
        ///this.addStyleName("offScreen");
    }

    public void onChange(/*///ChangeEvent*/Object e) {
        doLoad();
    }

    public final native void click() /*-{
		$doc.getElementById("LoadFileElement").click();
	 }-*/;

    public static final native void doLoad() /*-{
			var oFiles = $doc.getElementById("LoadFileElement").files,
    		nFiles = oFiles.length;
    		if (nFiles>=1 && oFiles[0].size<128000) {
        		var reader = new FileReader();
    			reader.onload = function(e) {
      				var text = reader.result;
      				@com.lushprojects.circuitjs1.client.LoadFile::doLoadCallback(Ljava/lang/String;Ljava/lang/String;)(text, oFiles[0].name);
        		};

    			reader.readAsText(oFiles[0]);
    		}
		 }-*/;

}
