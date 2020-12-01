package org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra;

import java.math.BigInteger;
import java.util.HashMap;
/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */

public abstract class Vector {

    public HashMap<Integer, Double> contents;
    public String description;
    private String componentName = "None"; // todo:tk:is "component" the correct name for this variable?

    public Vector(String description) {
        this.description = description;
        contents = new HashMap<Integer, Double>();
    }

    public void put(Integer index, Double value) {
        if (value.equals(0.0)) {
            contents.remove(index); //todo:tk:for making a sparse vector?
        } else {
            contents.put(index, value);
        }
    }

    public double get(int index) {
        if (contents.containsKey(index)) {
            return contents.get(index);
        } else {
            return 0.0;
        }
    }

    public void remove(int index) {
        contents.remove(index);
    }
    
    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }
    
    public String getComponentName()
    {
        return componentName;
    }
}
