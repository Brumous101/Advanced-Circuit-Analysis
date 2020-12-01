package org.mathpiper.ui.gui.applications.circuitpiper.model.components.active;

import java.util.Stack;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.CirSim;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Point;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CircuitElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.VCCSElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.WireElm;
import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Wire;

public class ControlledSource extends Component {
    
    protected String controllingComponentLabel;
    protected double multiplier = 1;
    protected String controllerUnitSymbol;
    
    public ControlledSource(int x, int y, Circuit circuit)
    {
        super(x, y, circuit);
    }
        
    protected void handleControlledSourceAttributes(Stack<String> attributes) throws Exception
    {
        if(attributes != null && attributes.size() == 2)
        {

            String attribute = attributes.pop();
            controllingComponentLabel = attribute;

            attribute = attributes.pop();
            double value = Double.parseDouble(attribute);
            multiplier = value;
            setLabel(multiplier + "*" + controllingComponentLabel + controllerUnitSymbol);

        }
        else
        {
            throw new Exception("Wrong number of attibutes.");
        }
    }

    public String getControllingComponentLabel() {
        return controllingComponentLabel;
    }

    public void setControllingComponent(String controllingComponentLabel) {
        
        this.controllingComponentLabel = controllingComponentLabel;
        
        setLabel(multiplier + "*" + controllingComponentLabel + controllerUnitSymbol);
        
        if(circuit.isCirSim) 
        {
            if(! controllingComponentLabel.equals("None"))
            {
                Component controllingComponent = circuit.components.get(controllingComponentLabel);

                CircuitElm source = controllingComponent.getCircuitElm();
                VCCSElm dest = (VCCSElm) this.circuitElm;
                CirSim cirSim = CircuitElm.sim;

                cirSim.elmList.remove(dest.wireControl0);
                cirSim.elmList.remove(dest.wireControl1);

                WireElm wire0 = new WireElm(source.x1, source.y1);
                Point post = dest.getPost(0);
                wire0.x1 = post.x;
                wire0.y1 = post.y;
                wire0.point1 = new Point(source.x1, source.y1);
                wire0.point2 = new Point(post.x, post.y);
                //wire0.setPoints();
                Component component = new Wire(-1, -1, "Hidden0", null);
                wire0.component = component;
                dest.wireControl0 = wire0;
                cirSim.elmList.add(wire0);

                WireElm wire1 = new WireElm(source.x2, source.y2);
                post = dest.getPost(1);
                wire1.x1 = post.x;
                wire1.y1 = post.y;
                wire1.point1 = new Point(source.x2, source.y2);
                wire1.point2 = new Point(post.x, post.y);
                //wire1.setPoints();
                component = new Wire(-1, -1, "Hidden1", null);
                wire1.component = component;
                dest.wireControl1 = wire1;
                cirSim.elmList.add(wire1);


                cirSim.needAnalyze();
            }
        }
    }
    

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
        setLabel(multiplier + "*" + controllingComponentLabel + controllerUnitSymbol);
    }
    
    
    
    public String toString()
    {
        return super.toString() + " " + this.controllingComponentLabel + " " + this.multiplier;
    }
}
