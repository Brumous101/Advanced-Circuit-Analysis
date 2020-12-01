package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1;

// model for subcircuits

import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.ChipElm;

public class ExtListEntry {

    public String name;
    
    public int node, pos, side;
    
    public ExtListEntry(String s, int n) {
        name = s;
        node = n;
        side = ChipElm.SIDE_WEST;
    }

    public ExtListEntry(String s, int n, int p, int sd) {
        name = s;
        node = n;
        pos = p;
        side = sd;
    }

};
