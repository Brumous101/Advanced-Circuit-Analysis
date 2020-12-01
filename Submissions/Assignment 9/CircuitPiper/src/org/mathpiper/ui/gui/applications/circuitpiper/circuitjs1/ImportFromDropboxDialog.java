package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1;

import javax.swing.JOptionPane;

public class ImportFromDropboxDialog /*///extends DialogBox*/ {

    ///VerticalPanel vp;
    ///Button cancelButton;
    ///Button chooserButton;
    ///Button importButton;
    ///TextArea ta;
    ///JLabel la;
    ///HorizontalPanel hp;
    ImportFromDropbox importFromDropbox;
    public static CirSim sim;

    public static void setSim(CirSim csim) {
        sim = csim;
    }

    public static void doLoadCallback(String s) throws Exception {
        sim.pushUndo();
        sim.readSetup(s, true);
    }

    public static final native void doDropboxImport(String link) /*-{
		try {
			var xhr= new XMLHttpRequest();
		  	xhr.addEventListener("load", function reqListener() { 
	//			console.log(xhr.responseText);
				var text = xhr.responseText;
	       		@com.lushprojects.circuitjs1.client.ImportFromDropboxDialog::doLoadCallback(Ljava/lang/String;)(text);
			});
			xhr.open("GET", link, false);
			xhr.send();
		}
		catch(err) {

		}

 	}-*/;

    public static void doImportDropboxLink(String link, Boolean validateIsDropbox) {
        if (validateIsDropbox && link.indexOf("https://www.dropbox.com/") != 0) {
            JOptionPane.showMessageDialog(null, "Dropbox links must start https://www.dropbox.com/");
            return;
        }
        // Work-around to allow CORS access to dropbox links - see
        // https://www.dropboxforum.com/t5/API-support/CORS-issue-when-trying-to-download-shared-file/m-p/82466
        link = link.replace("www.dropbox.com", "dl.dropboxusercontent.com");
        doDropboxImport(link);

    }

    public ImportFromDropboxDialog(CirSim csim) {
        super();
        /*
		setSim(csim);

		vp=new VerticalPanel();
		setWidget(vp);
		setText(sim.LS("Import from Dropbox"));
		if (ImportFromDropbox.isSupported()) {
			vp.add(new JLabel(sim.LS("To open a file in your dropbox account using the chooser click below.")));
			chooserButton = new Button(sim.LS("Open Dropbox Chooser"));
			vp.add(chooserButton);
			chooserButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					closeDialog();
					importFromDropbox= new ImportFromDropbox(sim);
				}
			});
			la = new JLabel(sim.LS("To open a shared Dropbox file from a Dropbox link paste the link below..."));
		} else {
			vp.add(new JLabel("This site, or your browser doesn't support the Dropbox chooser so you can't pick a file from your dropbox account."));
			la = new JLabel("You can open a shared Dropbox file if you have a link. Paste the Dropbox link below...");
			la.setStyleName("topSpace");
		}
		
		vp.add(la);
		ta=new TextArea();
		ta.setWidth("300px");
		ta.setHeight("200px");
		vp.add(ta);
		hp = new HorizontalPanel();
		hp.setWidth("100%");
		vp.add(hp);
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		importButton= new Button(sim.LS("Import From Dropbox Link"));
		importButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				closeDialog();
				doImportDropboxLink(ta.getText(), true);
			}
		});
		hp.add(importButton);
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		cancelButton=new Button(sim.LS("Cancel"));
		hp.add(cancelButton);
		cancelButton.addClickHandler(new ClickHandler() {
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
