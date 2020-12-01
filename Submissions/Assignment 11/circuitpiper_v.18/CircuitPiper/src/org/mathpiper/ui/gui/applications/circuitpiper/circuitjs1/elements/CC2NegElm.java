package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements;

public class CC2NegElm extends CC2Elm {

    public CC2NegElm(int xx, int yy) {
        super(xx, yy, -1);
    }

    public Class getDumpClass() {
        return CC2Elm.class;
    }
}
