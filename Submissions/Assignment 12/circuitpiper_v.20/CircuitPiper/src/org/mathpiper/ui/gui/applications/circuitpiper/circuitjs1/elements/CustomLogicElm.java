package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements;

import javax.swing.JButton;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.ChipElm.Pin;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.CirSim;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.CustomLogicModel;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.EditDialog;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.EditInfo;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;

public class CustomLogicElm extends ChipElm {

    String modelName;
    int postCount;
    int inputCount, outputCount;
    CustomLogicModel model;
    boolean lastValues[];
    boolean patternValues[];
    boolean highImpedance[];
    public static String lastModelName = "default";

    public CustomLogicElm(int xx, int yy) {
        super(xx, yy);
        modelName = lastModelName;
        setupPins();
    }

    public CustomLogicElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) throws Exception {
        super(xa, ya, xb, yb, f, st);
        modelName = CustomLogicModel.unescape(st.nextToken());
        updateModels();
        int i;
        for (i = 0; i != getPostCount(); i++) {
            if (pins[i].output) {
                nodeVoltages[i] = Double.parseDouble(st.nextToken());
                pins[i].value = nodeVoltages[i] > 2.5;
            }
        }
    }

    public String dump() {
        String s = super.dump();
        s += " " + CustomLogicModel.escape(modelName);

        // the code to do this in ChipElm doesn't work here because we don't know
        // how many pins to read until we read the model name!  So we have to
        // duplicate it here.
        int i;
        for (i = 0; i != getPostCount(); i++) {
            if (pins[i].output) {
                s += " " + nodeVoltages[i];
            }
        }
        return s;
    }

    public String dumpModel() {
        if (model.dumped) {
            return "";
        }
        return model.dump();
    }

    public void updateModels() throws Exception {
        model = CustomLogicModel.getModelWithNameOrCopy(modelName, model);
        setupPins();
        allocNodes();
        setPoints();
    }

    @Override
    public void setupPins() {
        if (modelName == null) {
            postCount = bits;
            allocNodes();
            return;
        }

        model = CustomLogicModel.getModelWithName(modelName);
        inputCount = model.inputs.length;
        outputCount = model.outputs.length;
        sizeY = inputCount > outputCount ? inputCount : outputCount;
        if (sizeY == 0) {
            sizeY = 1;
        }
        sizeX = 2;
        postCount = inputCount + outputCount;
        pins = new Pin[postCount];
        int i;
        for (i = 0; i != inputCount; i++) {
            pins[i] = new Pin(i, SIDE_WEST, model.inputs[i]);
            fixPinName(pins[i]);
        }
        for (i = 0; i != outputCount; i++) {
            pins[i + inputCount] = new Pin(i, SIDE_EAST, model.outputs[i]);
            pins[i + inputCount].output = true;
            fixPinName(pins[i + inputCount]);
        }
        lastValues = new boolean[postCount];
        patternValues = new boolean[26];
        highImpedance = new boolean[postCount];
    }

    void fixPinName(Pin p) {
        if (p.text.startsWith("/")) {
            p.text = p.text.substring(1);
            p.lineOver = true;
        }
        if (p.text.compareToIgnoreCase("clk") == 0) {
            p.text = "";
            p.clock = true;
        }
    }

    public int getPostCount() {
        return postCount;
    }

    @Override
    public int getVoltageSourceCount() {
        return outputCount;
    }

    // keep track of whether we have any tri-state outputs.  if not, then we can simplify things quite a bit, making the simulation faster
    boolean hasTriState() {
        return model == null ? false : model.triState;
    }

    public boolean nonLinear() {
        return hasTriState();
    }

    public int getInternalNodeCount() {
        // for tri-state outputs, we need an internal node to connect a voltage source to, and then connect a resistor from there to the output.
        // we do this for all outputs if any of them are tri-state
        return (hasTriState()) ? outputCount : 0;
    }

    public void stamp() {
        int i;
        int add = (hasTriState()) ? outputCount : 0;
        for (i = 0; i != getPostCount(); i++) {
            Pin p = pins[i];
            if (p.output) {
                sim.stampVoltageSource(0, nodes[i + add], p.voltSource);
                if (hasTriState()) {
                    sim.stampNonLinear(nodes[i + add]);
                    sim.stampNonLinear(nodes[i]);
                }
            }
        }
    }

    public void doStep() {
        int i;
        for (i = 0; i != getPostCount(); i++) {
            Pin p = pins[i];
            if (!p.output) {
                p.value = nodeVoltages[i] > 2.5;
            }
        }
        execute();
        int add = (hasTriState()) ? outputCount : 0;
        for (i = 0; i != getPostCount(); i++) {
            Pin p = pins[i];
            if (p.output) {
                // connect output voltage source (to internal node if tri-state, otherwise connect directly to output)
                sim.updateVoltageSource(0, nodes[i + add], p.voltSource, p.value ? 5 : 0);

                // add resistor for tri-state if necessary
                if (hasTriState()) {
                    sim.stampResistor(nodes[i + add], nodes[i], highImpedance[i] ? 1e8 : 1e-3);
                }
            }
        }
    }

    void execute() {
        int i;
        for (i = 0; i != model.rulesLeft.size(); i++) {
            // check for a match
            String rl = model.rulesLeft.get(i);
            int j;
            for (j = 0; j != rl.length(); j++) {
                char x = rl.charAt(j);
                if (x == '0' || x == '1') {
                    if (pins[j].value == (x == '1')) {
                        continue;
                    }
                    break;
                }

                // don't care
                if (x == '?') {
                    continue;
                }

                // up transition
                if (x == '+') {
                    if (pins[j].value && !lastValues[j]) {
                        continue;
                    }
                    break;
                }

                // down transition
                if (x == '-') {
                    if (!pins[j].value && lastValues[j]) {
                        continue;
                    }
                    break;
                }

                // save pattern values
                if (x >= 'a' && x <= 'z') {
                    patternValues[x - 'a'] = pins[j].value;
                    continue;
                }

                // compare pattern values
                if (x >= 'A' && x <= 'z') {
                    if (patternValues[x - 'A'] != pins[j].value) {
                        break;
                    }
                    continue;
                }
            }
            if (j != rl.length()) {
                continue;
            }

            // success
            String rr = model.rulesRight.get(i);
            for (j = 0; j != rr.length(); j++) {
                char x = rr.charAt(j);
                highImpedance[j + inputCount] = false;
                if (x >= 'a' && x <= 'z') {
                    pins[j + inputCount].value = patternValues[x - 'a'];
                } else if (x == '_') {
                    highImpedance[j + inputCount] = true;
                } else {
                    pins[j + inputCount].value = (x == '1');
                }
            }

            // save values for transition checking
            for (j = 0; j != postCount; j++) {
                lastValues[j] = pins[j].value;
            }
            break;
        }
    }

    public EditInfo getEditInfo(int n) {
        if (n == 2) {
            EditInfo ei = new EditInfo("Model Name", 0, -1, -1);
            ei.text = modelName;
            return ei;
        }
        if (n == 3) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.button = new JButton(sim.LS("Edit Model"));
            return ei;
        }
        return super.getEditInfo(n);
    }

    public void setEditValue(int n, EditInfo ei) throws Exception {
        if (n == 2) {
            ///modelName = lastModelName = ei.textf.getText();
            model = CustomLogicModel.getModelWithNameOrCopy(modelName, model);
            setupPins();
            allocNodes();
            setPoints();
            return;
        }
        if (n == 3) {
            EditDialog editDialog = new EditDialog(model, sim);
            CirSim.customLogicEditDialog = editDialog;
            ///editDialog.show();
            return;
        }

        super.setEditValue(n, ei);
    }

    int getDumpType() {
        return 208;
    }

    public void getInfo(String arr[]) {
        super.getInfo(arr);
        arr[0] = model.infoText;
    }
}
