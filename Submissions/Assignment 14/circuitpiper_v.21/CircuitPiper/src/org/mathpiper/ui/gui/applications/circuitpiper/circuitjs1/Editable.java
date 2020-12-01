package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1;

public interface Editable {

    EditInfo getEditInfo(int n);

    void setEditValue(int n, EditInfo ei) throws Exception;
}
