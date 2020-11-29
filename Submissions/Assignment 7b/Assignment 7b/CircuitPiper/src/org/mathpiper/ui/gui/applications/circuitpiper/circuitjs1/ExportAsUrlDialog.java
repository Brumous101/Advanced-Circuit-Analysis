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

public class ExportAsUrlDialog /*extends DialogBox*/ {

    ///VerticalPanel vp;
    ///Button shortButton;
    ///public static RichTextArea tb;
    String requrl;

    public boolean shortIsSupported() {
        ///return circuitjs1.shortRelaySupported;
        return true;
    }

//	public static public final native boolean bitlyIsSupported() 
//	/*-{
//		return !!($wnd.bitlytoken !==undefined && $wnd.bitlytoken !==null);
//	}-*/;
//	
    public static void createShort(String urlin) {
        /*///
    	String url;
    	url = "shortrelay.php"+"?v="+urlin; 
    	tb.setText("Waiting for short URL for web service...");
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Exception exception) {
					GWT.log("File Error Response", exception);
				}

				public void onResponseReceived(Request request, Response response) {
					// processing goes here
					if (response.getStatusCode()==Response.SC_OK) {
					String text = response.getText();
					tb.setText(text);
					// end or processing
					}
					else  {
						String text="Shortner error:"+response.getStatusText();
						tb.setText(text);
						GWT.log(text );
					}
				}
			});
		} catch (RequestException e) {
			GWT.log("failed file reading", e);
		}
         */
    }

    public ExportAsUrlDialog(String dump) {
        super();
        /*///
    	String start[] = Location.getHref().split("\\?");
    	dump=dump.replace(' ', '+');
    	String query="?cct=" + URL.encode(dump);
	    dump = start[0] + query;
		requrl = URL.encodeQueryString(query);
		Button okButton;
	
		JLabel la1, la2;
		vp=new VerticalPanel();
		setWidget(vp);
		setText(CirSim.LS("Export as URL"));
		vp.add(new JLabel(CirSim.LS("URL for this circuit is...")));
		if (dump.length()>2000) {
			vp.add( la1= new JLabel(CirSim.LS("Warning: this URL is longer than 2000 characters and may not work in some browsers."), true));
			la1.setWidth("300px");
		}
		vp.add(tb = new RichTextArea());
		tb.setText(dump);
//		tb.setMaxLength(s.length());
//		tb.setVisibleLength(s.length());
		vp.add(la2 = new JLabel(CirSim.LS("To save this URL select it all (eg click in text and type control-A) and copy to your clipboard (eg control-C) before pasting to a suitable place."), true));
		la2.setWidth("300px");
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");
		hp.setStyleName("topSpace");
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		hp.add(okButton = new Button(CirSim.LS("OK")));
		vp.add(hp);
		if (shortIsSupported()) {
			hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	
			hp.add(shortButton = new Button(CirSim.LS("Create short URL")));
			shortButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					shortButton.setVisible(false);
					createShort(requrl);
				}
			});
		}
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
