package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1;

// values with sliders

import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CircuitElm;

public class Adjustable /*///implements Command*/ {

    CircuitElm elm;
    double minValue, maxValue;
    String sliderText;

    // index of value in getEditInfo() list that this slider controls
    int editItem;

    ///JLabel label;
    Scrollbar slider;
    boolean settingValue;

    Adjustable(CircuitElm ce, int item) {
        minValue = 1;
        maxValue = 1000;
        elm = ce;
        editItem = item;
    }

    // undump
    Adjustable(StringTokenizer st, CirSim sim) {
        int e = Integer.parseInt(st.nextToken());
        if (e == -1) {
            return;
        }
        elm = sim.getElm(e);
        editItem = Integer.parseInt(st.nextToken());
        minValue = Double.parseDouble(st.nextToken());
        maxValue = Double.parseDouble(st.nextToken());
        sliderText = CustomLogicModel.unescape(st.nextToken());
    }

    void createSlider(CirSim sim) {
        double value = elm.getEditInfo(editItem).value;
        createSlider(sim, value);
    }

    void createSlider(CirSim sim, double value) {
        /*///
        sim.addWidgetToVerticalPanel(label = new JLabel(sim.LS(sliderText)));
        label.addStyleName("topSpace");
        int intValue = (int) ((value-minValue)*100/(maxValue-minValue));
        sim.addWidgetToVerticalPanel(slider = new Scrollbar(Scrollbar.HORIZONTAL, intValue, 1, 0, 101, this, elm));
         */
    }

    void setSliderValue(double value) {
        int intValue = (int) ((value - minValue) * 100 / (maxValue - minValue));
        settingValue = true; // don't recursively set value again in execute()
        ///slider.setValue(intValue);
        settingValue = false;
    }

    public void execute() throws Exception {
        elm.sim.analyzeFlag = true;
        if (settingValue) {
            return;
        }
        EditInfo ei = elm.getEditInfo(editItem);
        ei.value = getSliderValue();
        elm.setEditValue(editItem, ei);
        elm.sim.repaint();
    }

    double getSliderValue() {
        ///return minValue + (maxValue-minValue)*slider.getValue()/100;
        return 0.0;
    }

    void deleteSlider(CirSim sim) {
        ///sim.removeWidgetFromVerticalPanel(label);
        ///sim.removeWidgetFromVerticalPanel(slider);
    }

    String dump() {
        return elm.sim.locateElm(elm) + " " + editItem + " " + minValue + " " + maxValue + " " + CustomLogicModel.escape(sliderText);
    }
}
