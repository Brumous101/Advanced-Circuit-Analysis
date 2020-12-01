package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1;

public class ExprState {

    int n;
    public double values[];
    public double simulationTime;

    public ExprState(int xx) {
        n = xx;
        values = new double[9];
        values[4] = Math.E;
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("n: " + n);
        sb.append("\n");
        
        int valueIndex = 0;
        for(double value : values)
        {
            sb.append("Value " + valueIndex++ + ": " + value);
            sb.append("\n");
        }
        
        sb.append("Simulation Time: " + simulationTime);
        
        return sb.toString();
    }
}