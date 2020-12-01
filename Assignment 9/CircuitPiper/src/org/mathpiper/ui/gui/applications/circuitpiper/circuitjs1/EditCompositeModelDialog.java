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

import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CustomCompositeChipElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.ChipElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.ChipElm.Pin;

import java.util.Collections;
import java.util.Comparator;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.stubs.*;

public class EditCompositeModelDialog /*///extends DialogBox implements MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler, MouseOverHandler*/ {

    ///VerticalPanel vp;
    boolean error;
    CustomCompositeChipElm chip;
    int postCount;
    Context2d context;
    CustomCompositeModel model;

    public void setModel(CustomCompositeModel m) {
        model = m;
    }

    boolean createModel() {
        model = CirSim.theSim.getCircuitAsComposite();
        if (model == null) {
            return false;
        }
        if (model.extList.size() == 0) {
            JOptionPane.showMessageDialog(null, CirSim.LS("Device has no external inputs/outputs!"));
            return false;
        }
        Collections.sort(model.extList, new Comparator<ExtListEntry>() {
            public int compare(ExtListEntry a, ExtListEntry b) {
                return a.name.toLowerCase().compareTo(b.name.toLowerCase());
            }
        });
        int i;
        int postCount = model.extList.size();

        model.sizeX = 2;
        model.sizeY = (postCount + 1) / 2;
        for (i = 0; i != postCount; i++) {
            boolean left = i < model.sizeY;
            int side = (left) ? ChipElm.SIDE_WEST : ChipElm.SIDE_EAST;
            ExtListEntry pin = model.extList.get(i);
            pin.pos = left ? i : i - model.sizeY;
            pin.side = side;
        }
        return true;
    }

    public EditCompositeModelDialog() {
        super();
    }

    JTextField modelNameJTextField = null;

    public void createDialog() {
        /*
		Button okButton;
		Anchor a;
		vp=new VerticalPanel();
		setWidget(vp);
		setText(CirSim.LS("Edit Subcircuit Model"));
		vp.add(new JLabel(CirSim.LS("Drag the pins to the desired position")));
		Date date = new Date();

		Canvas canvas = Canvas.createIfSupported();
		canvas.setWidth("400 px");
		canvas.setHeight("400 px");
		canvas.setCoordinateSpaceWidth(400);
		canvas.setCoordinateSpaceHeight(400);
		vp.add(canvas);
		context = canvas.getContext2d();
		
		chip = new CustomCompositeChipElm(50, 50);
		chip.x2 = 200;
		chip.y2 = 50;
		createPinsFromModel();
		
		if (model.name == null) {
		    vp.add(new JLabel(CirSim.LS("Model Name")));
		    modelNameJTextField = new JTextField();
		    vp.add(modelNameJTextField);
//		    modelNameJTextField.setText(model.name);
		}
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new JLabel(CirSim.LS("Width")));
		Button b;
		hp.add(b = new Button("+"));
		b.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                	adjustChipSize(1, 0);
                    }
                });
		hp.add(b = new Button("-"));
		b.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                	adjustChipSize(-1, 0);
                    }
                });
		hp.add(new JLabel(CirSim.LS("Height")));
		hp.add(b = new Button("+"));
		b.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                	adjustChipSize(0, 1);
                    }
                });
		hp.add(b = new Button("-"));
		b.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                	adjustChipSize(0, -1);
                    }
                });
		vp.add(hp);
	
                canvas.addMouseDownHandler(this);
                canvas.addMouseUpHandler(this);
                canvas.addMouseMoveHandler(this);
                canvas.addMouseOutHandler(this);
                canvas.addMouseOverHandler(this);

                hp = new HorizontalPanel();
                hp.setWidth("100%");
                hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
                hp.setStyleName("topSpace");
                vp.add(hp);
                hp.add(okButton = new Button(CirSim.LS("OK")));
                hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
                Button cancelButton;
		if (model.name == null) {
		    hp.add(cancelButton = new Button(CirSim.LS("Cancel")));
		    cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
			    closeDialog();
			}
		    });
		}
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
			    if (modelNameJTextField != null) {
				String name = modelNameJTextField.getText();
				if (name.length() == 0) {
				    JOptionPane.showMessageDialog(null, CirSim.LS("Please enter a model name."));
				    return;
				}
				model.setName(CustomCompositeElm.lastModelName = name);
			    }
			    CirSim.theSim.updateModels();
			    CirSim.theSim.needAnalyze(); // will get singular matrix if we don't do this
			    closeDialog();
			}
		});
		this.center();
         */
    }

    void createPinsFromModel() {
        postCount = model.extList.size();
        chip.allocPins(postCount);
        chip.sizeX = model.sizeX;
        chip.sizeY = model.sizeY;
        for (int i = 0; i != postCount; i++) {
            ExtListEntry pin = model.extList.get(i);
            chip.setPin(i, pin.pos, pin.side, pin.name);
            chip.nodeVoltages[i] = 0;
            if (i == selectedPin) {
                chip.pins[i].selected = true;
            }
        }
        chip.setPoints();
    }

    double scale;

    void drawChip() {
        Graphics g = new Graphics(context);
        double scalew = context.getCanvas().getWidth() / (double) (chip.boundingBox.width + chip.boundingBox.x * 2);
        double scaleh = context.getCanvas().getHeight() / (double) (chip.boundingBox.height + chip.boundingBox.y * 2);
        scale = 1 / Math.min(scalew, scaleh);
        context.setFillStyle("#000000");
        context.fillRect(0, 0, context.getCanvas().getWidth(), context.getCanvas().getHeight());
        context.setTransform(1 / scale, 0, 0, 1 / scale, 0, 0);
        chip.draw(g);
    }

    void adjustChipSize(int dx, int dy) {
        if (dx < 0 || dy < 0) {
            for (int i = 0; i != postCount; i++) {
                Pin p = chip.pins[i];
                if (dx < 0 && (p.side == chip.SIDE_NORTH || p.side == chip.SIDE_SOUTH) && p.pos >= chip.sizeX + dx) {
                    return;
                }
                if (dy < 0 && (p.side == chip.SIDE_EAST || p.side == chip.SIDE_WEST) && p.pos >= chip.sizeY + dy) {
                    return;
                }
            }
        }
        if (chip.sizeX + dx < 1 || chip.sizeY + dy < 1) {
            return;
        }
        model.sizeX += dx;
        model.sizeY += dy;
        createPinsFromModel();
        drawChip();
    }

    protected void closeDialog() {
        //this.hide();
    }

    boolean dragging;

    public void onMouseOver(/*///MouseOverEvent*/Object event) {
        // TODO Auto-generated method stub

    }

    public void onMouseOut(/*///MouseOutEvent*/Object event) {
        // TODO Auto-generated method stub

    }

    public void onMouseUp(/*///MouseUpEvent*/Object event) {
        // TODO Auto-generated method stub
        dragging = false;
    }

    int selectedPin;

    public void onMouseMove(/*///MouseMoveEvent*/Object event) {
        /*
	    if (dragging) {
		if (selectedPin < 0)
		    return;
		int pos[] = new int[2];
		if (chip.getPinPos((int)(event.getX()*scale), (int)(event.getY()*scale), selectedPin, pos)) {
		    ExtListEntry p = model.extList.get(selectedPin);
		    p.pos  = pos[0];
		    p.side = pos[1];
		    createPinsFromModel();
		    drawChip();
		}
	    } else {
		int i;
		double bestdist = 20;
		selectedPin = -1;
		for (i = 0; i != postCount; i++) {
		    Pin p = chip.pins[i];
		    int dx = (int)(event.getX()*scale) - p.textloc.x;
		    int dy = (int)(event.getY()*scale) - p.textloc.y;
		    double dist = Math.hypot(dx, dy);
		    if (dist < bestdist) {
			bestdist = dist;
			selectedPin = i;
		    }
		    p.selected = false;
		}
		if (selectedPin >= 0)
		    chip.pins[selectedPin].selected = true;
		drawChip();
	    }
         */
    }

    public void onMouseDown(/*///MouseDownEvent*/Object event) {
        // TODO Auto-generated method stub
        dragging = true;
    }

}
