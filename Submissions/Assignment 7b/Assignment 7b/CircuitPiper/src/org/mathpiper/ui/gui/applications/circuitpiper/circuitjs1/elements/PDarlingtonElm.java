package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements;

public class PDarlingtonElm extends DarlingtonElm {

    public PDarlingtonElm(int xx, int yy) throws Exception {
        super(xx, yy, true);
    }

    public Class getDumpClass() {
        return DarlingtonElm.class;
    }
}
