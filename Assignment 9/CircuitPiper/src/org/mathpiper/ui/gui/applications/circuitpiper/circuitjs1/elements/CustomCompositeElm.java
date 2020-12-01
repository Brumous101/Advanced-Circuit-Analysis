package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.CustomCompositeModel;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.CustomLogicModel;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.EditCompositeModelDialog;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.EditInfo;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.ExtListEntry;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Graphics;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;


// instances of subcircuits
public class CustomCompositeElm extends CompositeElm {

    String modelName;
    CustomCompositeChipElm chip;
    int postCount;
    int inputCount, outputCount;
    CustomCompositeModel model;
    public static String lastModelName = "default";

    public CustomCompositeElm(int xx, int yy) throws Exception {
        super(xx, yy);

        // use last model as default when creating new element in UI.
        // use default otherwise, to avoid infinite recursion when creating nested subcircuits.
        modelName = (xx == 0 && yy == 0) ? "default" : lastModelName;

        flags |= FLAG_ESCAPE;
        updateModels();
    }

    public CustomCompositeElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) throws Exception {
        super(xa, ya, xb, yb, f);
        modelName = CustomLogicModel.unescape(st.nextToken());
        updateModels(st);
    }

    public String dump() {
        // insert model name before the elements
        String s = super.dumpWithMask(0);
        s += " " + CustomLogicModel.escape(modelName);
        s += dumpElements();
        return s;
    }

    public String dumpModel() {
        String modelStr = "";

        // dump models of all children
        for (int i = 0; i < compElmList.size(); i++) {
            CircuitElm ce = compElmList.get(i);
            String m = ce.dumpModel();
            if (m != null && !m.isEmpty()) {
                if (!modelStr.isEmpty()) {
                    modelStr += "\n";
                }
                modelStr += m;
            }
        }
        if (model.dumped) {
            return modelStr;
        }

        // dump our model
        if (!modelStr.isEmpty()) {
            modelStr += "\n";
        }
        modelStr += model.dump();

        return modelStr;
    }

    public void draw(Graphics g) {
        int i;
        for (i = 0; i != postCount; i++) {
            chip.nodeVoltages[i] = nodeVoltages[i];
            chip.pins[i].current = getCurrentIntoNode(i);
        }
        chip.setSelected(needsHighlight());
        chip.draw(g);
        boundingBox = chip.boundingBox;
    }

    public void setPoints() {
        chip = new CustomCompositeChipElm(x1, y1);
        chip.x2 = x2;
        chip.y2 = y2;

        chip.sizeX = model.sizeX;
        chip.sizeY = model.sizeY;
        chip.allocPins(postCount);
        int i;
        for (i = 0; i != postCount; i++) {
            ExtListEntry pin = model.extList.get(i);
            chip.setPin(i, pin.pos, pin.side, pin.name);
        }

        chip.setPoints();
        for (i = 0; i != getPostCount(); i++) {
            setPost(i, chip.getPost(i));
        }
    }

    public void updateModels() throws Exception {
        updateModels(null);
    }

    public void updateModels(StringTokenizer st) throws Exception {
        model = CustomCompositeModel.getModelWithName(modelName);
        if (model == null) {
            return;
        }
        postCount = model.extList.size();
        int externalNodes[] = new int[postCount];
        int i;
        for (i = 0; i != postCount; i++) {
            externalNodes[i] = model.extList.get(i).node;
        }
        if (st == null) {
            st = new StringTokenizer(model.elmDump, " ");
        }
        loadComposite(st, model.nodeList, externalNodes);
        allocNodes();
        setPoints();
    }

    public int getPostCount() {
        return postCount;
    }

    Vector<CustomCompositeModel> models;

    public EditInfo getEditInfo(int n) {
        if (n == 0) {
            EditInfo ei = new EditInfo("<a href=\"subcircuits.html\" target=\"_blank\">Model Name</a>", 0, -1, -1);
            models = CustomCompositeModel.getModelList();
            ei.comboBox = new JComboBox();
            int i;
            for (i = 0; i != models.size(); i++) {
                CustomCompositeModel ccm = models.get(i);
                ei.comboBox.addItem(ccm.name);
                if (ccm == model) {
                    ei.comboBox.setSelectedItem(i);
                }
            }
            return ei;
        }
        if (n == 1) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.button = new JButton(sim.LS("Edit Model"));
            return ei;
        }
        return null;
    }

    public void setEditValue(int n, EditInfo ei) throws Exception {
        if (n == 0) {
            model = models.get(ei.comboBox.getSelectedIndex());
            lastModelName = modelName = model.name;
            updateModels();
            setPoints();
            return;
        }
        if (n == 1) {
            if (model.name.equals("default")) {
                ///JOptionPane.showMessageDialog(null, CirSim.LS("Can't edit this model."));
                return;
            }
            EditCompositeModelDialog dlg = new EditCompositeModelDialog();
            dlg.setModel(model);
            dlg.createDialog();
            ///CirSim.dialogShowing = dlg;
            ///dlg.show();
            return;
        }
    }

    int getDumpType() {
        return 410;
    }

    public void getInfo(String arr[]) {
        super.getInfo(arr);
        arr[0] = "subcircuit (" + model.name + ")";
        int i;
        for (i = 0; i != postCount; i++) {
            if (i + 1 >= arr.length) {
                break;
            }
            arr[i + 1] = model.extList.get(i).name + " = " + getVoltageText(nodeVoltages[i]);
        }
    }
}
