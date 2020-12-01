package org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra;

import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ControlledSource;

public class ControlledSourceEquationVector extends EquationVector {
    
    private ControlledSource cs;
    
    ControlledSourceEquationVector(String description, ControlledSource cs)
    {
        super(description);
        this.cs = cs;
    }
    
    public ControlledSource getControlledSource()
    {
        return cs;
    }
}