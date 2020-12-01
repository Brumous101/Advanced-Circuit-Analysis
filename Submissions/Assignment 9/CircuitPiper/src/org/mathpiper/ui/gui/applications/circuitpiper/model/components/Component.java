package org.mathpiper.ui.gui.applications.circuitpiper.model.components;

import java.awt.Color;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CircuitElm;
import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;

import org.mathpiper.ui.gui.applications.circuitpiper.model.Terminal;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ControlledSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Wire;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ScaledGraphics;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ScopePanel;
/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */

abstract public class Component implements DisplayLabel, Turtle {
    
    protected static Map<String, Double> siToValue;
    public CircuitElm circuitElm;
    public boolean isHideValue = false;
    public boolean isHighlight = false;

    static
    {
        setSiToValue(new HashMap<String, Double>());

        getSiToValue().put("Y", Math.pow(10, 24));
        getSiToValue().put("Z", Math.pow(10, 21));
        getSiToValue().put("E", Math.pow(10, 18));
        getSiToValue().put("P", Math.pow(10, 15));
        getSiToValue().put("T", Math.pow(10, 12));
        getSiToValue().put("G", Math.pow(10, 9));
        getSiToValue().put("M", Math.pow(10, 6));
        getSiToValue().put("k", Math.pow(10, 3));
        getSiToValue().put("h", Math.pow(10, 2));
        getSiToValue().put("da", Math.pow(10, 1));
        getSiToValue().put("", Math.pow(10, 0));
        getSiToValue().put("d", Math.pow(10, -1));
        getSiToValue().put("c", Math.pow(10, -2));
        getSiToValue().put("m", Math.pow(10, -3));
        getSiToValue().put("μ", Math.pow(10, -6));
        getSiToValue().put("n", Math.pow(10, -9));
        getSiToValue().put("p", Math.pow(10, -12));
        getSiToValue().put("f", Math.pow(10, -15));
        getSiToValue().put("a", Math.pow(10, -18));
        getSiToValue().put("z", Math.pow(10, -21));
        getSiToValue().put("y", Math.pow(10, -24));

    }

    protected String componentSubscript;
    protected Color color;
    protected Terminal headTerminal;
    protected Terminal tailTerminal;
    protected String siPrefix = "";
    protected static String micro = "\u03BC";
    protected static String[] siPrefixes = {"Y", "Z", "E", "P", "T", "G", "M", "k", "", "m", micro, "n", "p", "f", "a", "z", "y"};
    protected String primary;
    protected String secondary;
    protected String primaryUnit;
    protected String primaryUnitPlural;
    protected String primarySymbol;
    protected String primaryUnitSymbol;
    protected String secondaryUnitSymbol;
    protected String preselectedPrimaryPrefix;
    protected String preselectedFrequencyPrefix;
    protected String preselectedPhaseShiftPrefix;
    protected String enteredPrimaryValue;
    protected String selectedPrimaryPrefix;
    protected String selectedFrequencyPrefix;
    protected String selectedPhaseShiftPrefix;
    protected String frequencySymbol;
    protected Double primaryValue;//changed to double from Double on Dec 29 2008
    protected Double frequency;
    protected Double phaseShift;
    protected Double calculatedValue;
    protected String enteredFrequency;
    protected String enteredPhaseShift;
    protected double secondaryValue = 0.0;
    protected boolean isConstant;
    protected boolean isHeldConstant;
    protected String fullValue = "";
    protected List<ScopePanel> scopePanels = new ArrayList();
    protected Circuit circuit;
    protected String label;

    public double y1, yj, zj, k1, k2, k3, k4, k5, k6, I1, y2, y3, originalValue, twoStepValue, oneStepValue, originalCurrent, delta1;

    
    public Component(final int x, final int y, Circuit circuit) {
        this.circuit = circuit;
        headTerminal = new Terminal(x, y, circuit);
        tailTerminal = new Terminal(x+1, y+1, circuit);
        connectHeadAndTail();
    }
    
    public String getID()
    {
        return getPrimarySymbol() + getComponentSubscript();
    }
    
    //import java.text.DecimalFormat;
    public static String formatValue(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return "Error ";
        }
        String sign = "";
        if (d == 0.0) {
            return "0.00 ";
        }
        if (d < 0) {
            sign = "-";
        }
        double absd = Math.abs(d);
        int p = (int) Math.floor(Math.log(absd) / Math.log(1000));
        double mantissa = absd / Math.pow(1000, p);
        //System.out.println(mantissa+" "+p*3);
        //System.out.println((-3.5));
        DecimalFormat format;
        if (mantissa < 10) {
            format = new DecimalFormat("0.00");
            //mantissa=((mantissa*100))/100; 
        } else if (mantissa < 100) {
            format = new DecimalFormat("00.0");
            //mantissa=((mantissa*10))/10;
        } else {
            format = new DecimalFormat("000");
        }
        if (absd < 10e-24 || absd >= 10e27 || 8 - p < 0 || 8 - p >= getSiPrefixes().length) {
            return sign + format.format(mantissa) + "E" + 3 * p + "";
        }
        //int i=8-p;

        return sign + format.format(mantissa) + getSiPrefixes()[8 - p] + "";
    }


    public void moveTail(final int x, final int y) {
        getTailTerminal().setX(x);
        getTailTerminal().setY(y);
        connectHeadAndTail();
    }

    public void setTail(Terminal theTerminal) {
        disconnectHeadAndTail();
        setTailTerminal(theTerminal);
        connectHeadAndTail();
        
        if(getCircuitElm() != null)
        {
            if(this instanceof ControlledSource)
            {
                this.circuitElm.x1 = theTerminal.getX();
                this.circuitElm.y1 = theTerminal.getY();                
            }
            else
            {
                this.circuitElm.x2 = theTerminal.getX();
                this.circuitElm.y2 = theTerminal.getY();
            }
            
            this.getCircuitElm().setPoints();
        }
    }

    public void setHead(Terminal theTerminal) {
        disconnectHeadAndTail();
        setHeadTerminal(theTerminal);
        connectHeadAndTail();
        
        if(getCircuitElm() != null)
        {
            if(this instanceof ControlledSource)
            {
                this.circuitElm.x2 = theTerminal.getX();
                this.circuitElm.y2 = theTerminal.getY();                
            }
            else
            {
                this.circuitElm.x1 = theTerminal.getX();
                this.circuitElm.y1 = theTerminal.getY();
            }
            
            this.getCircuitElm().setPoints();
        }
    }

    public void connectHeadAndTail() {
        getHeadTerminal().myConnectedTo.add(getTailTerminal());
        getTailTerminal().myConnectedTo.add(getHeadTerminal());
        getHeadTerminal().in.add(this);
        getTailTerminal().out.add(this);
    }

    public void disconnectHeadAndTail() {
        getHeadTerminal().myConnectedTo.remove(getTailTerminal());
        getTailTerminal().myConnectedTo.remove(getHeadTerminal());
        getHeadTerminal().in.remove(this);
        getTailTerminal().out.remove(this);
    }

    public boolean badSize() {
        return getHeadTerminal().getX() == getTailTerminal().getX() && getHeadTerminal().getY() == getTailTerminal().getY();
    }

    public void draw(ScaledGraphics sg) throws Exception {
        
        sg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

        if(((this instanceof Wire) && getCircuit().circuitPanel.drawingPanel.isDrawWireLabels) || !(this instanceof Wire))
        {
            drawLabel(sg);
        }

    }

    public double sqr(double x) {
        return x * x;
    }
    
    public String getDisplayLabel() throws Exception
    {
        String text = "";
        
        if(this.circuitElm != null)
        {
            text = this.circuitElm.getDisplayLabel();
        }
        else
        {
            if (this.getPrimaryValue() != null) {
                String unit;

                if (this.getPrimaryValue() == 1) {
                    unit = this.getPrimaryUnitSymbol();//primaryUnit;
                } else {
                    unit = this.getPrimaryUnitSymbol();//Plural;
                }

                text = formatValue(this.getPrimaryValue()) + unit;

            }
            else if(this.getLabel() != null)
            {
                text = getLabel();
            }
            else
            {
                throw new Exception("Empty component label.");
            }
        }
        
        return text;
    }
    
    public int getLabelDistance()
    {
        return 25;
    }

    public void drawLabel(ScaledGraphics sg) throws Exception {

        String text = "";
        
        int distanceFromSymbol = getLabelDistance();

        int x1 =  this.getHeadTerminal().getX();
        int y1 =  this.getHeadTerminal().getY();
        int x2 =  this.getTailTerminal().getX();
        int y2 =  this.getTailTerminal().getY();
        int run = x2 - x1;
        int rise = y2 - y1;
        double d = Math.sqrt(sqr(run / 2.0) + sqr(rise / 2.0));
        
        double textYOffset = sg.getScaledTextHeight(getID());
        //textYOffset = 10; //todo:tk:font metrics don't seem to be working duirng printing.
        
        if (rise * run > 0) {
            sg.drawString(getID(),
                     (x1 + run / 2.0 + Math.abs(rise) / (2.0 * d) * distanceFromSymbol),
                     (y1 + rise / 2.0 - Math.abs(run) / (2.0 * d) * distanceFromSymbol)
            );
        } else {
            sg.drawString(getID(),  
                    (x1 + run / 2.0 + Math.abs(rise) / 2.0 / d * distanceFromSymbol),
                    (y1 + rise / 2.0 + Math.abs(run) / 2.0 / d * distanceFromSymbol));
        }

        if(getCircuit().circuitPanel.drawingPanel.isDrawComponentValues && isHideValue == false)
        {
            text = getDisplayLabel();

            if (rise * run > 0) {
                sg.drawString(text,
                         (x1 + run / 2.0 + Math.abs(rise) / (2.0 * d) * distanceFromSymbol),
                         (y1 + textYOffset + rise / 2.0 - Math.abs(run) / (2.0 * d) * distanceFromSymbol)
                );
            } else {
                sg.drawString(text,  
                        (x1 + run / 2.0 + Math.abs(rise) / 2.0 / d * distanceFromSymbol),
                        (y1 + textYOffset + rise / 2.0 + Math.abs(run) / 2.0 / d * distanceFromSymbol));
            }
        }
    }

    public void reverse() {
        disconnectHeadAndTail();
        Terminal oldHead = getHeadTerminal();
        this.setHeadTerminal(this.getTailTerminal());
        this.setTailTerminal(oldHead);
        connectHeadAndTail();
    }
    
    
    /*
     g the graphics component.
     x1 x-position of first point.
     y1 y-position of first point.
     x2 x-position of second point.
     y2 y-position of second point.
     d  the width of the arrow.
     h  the height of the arrow.

        Obatined from https://stackoverflow.com/questions/2027613/how-to-draw-a-directed-arrow-line-in-java
     */
    public void drawArrowLine(ScaledGraphics sg, double x1, double y1, double x2, double y2, double d, double h) {

        double dx = x2 - x1;
        double dy = y2 - y1;

        double D = Math.sqrt(dx*dx + dy*dy);
        double xm = D - d;
        double xn = xm;
        double ym = h;
        double yn = -h;
        double x;

        double sin = dy / D, cos = dx / D;

        x = xm*cos - ym*sin + x1;
        ym = xm*sin + ym*cos + y1;
        xm = x;

        x = xn*cos - yn*sin + x1;
        yn = xn*sin + yn*cos + y1;
        xn = x;

        int[] xpoints = {(int) Math.round(x2), (int) Math.round(xm), (int) Math.round(xn)};
        int[] ypoints = {(int) Math.round(y2), (int) Math.round(ym), (int) Math.round(yn)};

        sg.drawLine(x1, y1, x2, y2);
        sg.fillPolygon(xpoints, ypoints, 3);
    }

    
    protected void handleAttribute(Stack<String> attributes) throws Exception
    {
        if(attributes != null && attributes.size() == 1)
        {
            String attribute = attributes.pop();

            double value = Double.parseDouble(attribute);

            setPrimaryValue((Double) value);
            setEnteredPrimaryValue("" + 1 / getSiToValue().get(getSiPrefix()) * getPrimaryValue());
        }
        else
        {
            throw new Exception("Wrong number of attibutes.");
        }
    }
    
    protected void handleACSourceAttributes(Stack<String> attributes) throws Exception // todo:tk:move into new ACSource class.
    {
        if(attributes != null && attributes.size() == 3)
        {
            try {
                String attribute = attributes.pop();
                double value = Double.parseDouble(attribute);
                setPrimaryValue((Double) value);
                setEnteredPrimaryValue("" + 1 / getSiToValue().get(getSiPrefix()) * getPrimaryValue());
                
                attribute = attributes.pop();
                value = Double.parseDouble(attribute);
                setFrequency((Double) value);
                setEnteredFrequency("" + getFrequency());
                
                attribute = attributes.pop();
                value = Double.parseDouble(attribute);
                setPhaseShift((Double) value);
                setEnteredPhaseShift("" + getPhaseShift());
            }
            catch (NumberFormatException nfe)
            {
                setPrimaryValue(null);
                setFrequency(null);
                setPhaseShift(null);
                setEnteredPrimaryValue("");
                setEnteredFrequency("");
                setEnteredPhaseShift("");
                
                throw nfe;
            }
        }
        else
        {
            throw new Exception("Wrong number of attibutes.");
        }
    }

    public static Map<String, Double> getSiToValue() {
        return siToValue;
    }

    public static void setSiToValue(Map<String, Double> aSiToValue) {
        siToValue = aSiToValue;
    }

    public CircuitElm getCircuitElm() {
        return circuitElm;
    }

    public void setCircuitElm(CircuitElm circuitElm) {
        this.circuitElm = circuitElm;
    }

    public String getComponentSubscript() {
        return componentSubscript;
    }

    public void setComponentSubscript(String subscript)
    {
        this.componentSubscript = subscript;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Terminal getHeadTerminal() {
        return headTerminal;
    }

    public void setHeadTerminal(Terminal headTerminal) {
        this.headTerminal = headTerminal;
    }

    public Terminal getTailTerminal() {
        return tailTerminal;
    }

    public void setTailTerminal(Terminal tailTerminal) {
        this.tailTerminal = tailTerminal;
    }

    public String getSiPrefix() {
        return siPrefix;
    }

    public void setSiPrefix(String siPrefix) {
        this.siPrefix = siPrefix;
    }

    public static String getMicro() {
        return micro;
    }

    public static void setMicro(String aMicro) {
        micro = aMicro;
    }

    public static String[] getSiPrefixes() {
        return siPrefixes;
    }

    public static void setSiPrefixes(String[] aSiPrefixes) {
        siPrefixes = aSiPrefixes;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getSecondary() {
        return secondary;
    }

    public void setSecondary(String secondary) {
        this.secondary = secondary;
    }

    public String getPrimaryUnit() {
        return primaryUnit;
    }

    public void setPrimaryUnit(String primaryUnit) {
        this.primaryUnit = primaryUnit;
    }

    public String getPrimaryUnitPlural() {
        return primaryUnitPlural;
    }

    public void setPrimaryUnitPlural(String primaryUnitPlural) {
        this.primaryUnitPlural = primaryUnitPlural;
    }

    public String getPrimarySymbol() {
        return primarySymbol;
    }

    public void setPrimarySymbol(String primarySymbol) {
        this.primarySymbol = primarySymbol;
    }

    public String getPrimaryUnitSymbol() {
        return primaryUnitSymbol;
    }

    public void setPrimaryUnitSymbol(String primaryUnitSymbol) {
        this.primaryUnitSymbol = primaryUnitSymbol;
    }

    public String getSecondaryUnitSymbol() {
        return secondaryUnitSymbol;
    }

    public void setSecondaryUnitSymbol(String secondaryUnitSymbol) {
        this.secondaryUnitSymbol = secondaryUnitSymbol;
    }

    public String getPreselectedPrimaryPrefix() {
        return preselectedPrimaryPrefix;
    }

    public void setPreselectedPrimaryPrefix(String preselectedPrimaryPrefix) {
        this.preselectedPrimaryPrefix = preselectedPrimaryPrefix;
    }

    public String getPreselectedFrequencyPrefix() {
        return preselectedFrequencyPrefix;
    }

    public void setPreselectedFrequencyPrefix(String preselectedFrequencyPrefix) {
        this.preselectedFrequencyPrefix = preselectedFrequencyPrefix;
    }

    public String getPreselectedPhaseShiftPrefix() {
        return preselectedPhaseShiftPrefix;
    }

    public void setPreselectedPhaseShiftPrefix(String preselectedPhaseShiftPrefix) {
        this.preselectedPhaseShiftPrefix = preselectedPhaseShiftPrefix;
    }

    public String getEnteredPrimaryValue() {
        return enteredPrimaryValue;
    }

    public void setEnteredPrimaryValue(String enteredPrimaryValue) {
        this.enteredPrimaryValue = enteredPrimaryValue;
    }

    public String getSelectedPrimaryPrefix() {
        return selectedPrimaryPrefix;
    }

    public void setSelectedPrimaryPrefix(String selectedPrimaryPrefix) {
        this.selectedPrimaryPrefix = selectedPrimaryPrefix;
    }

    public String getSelectedFrequencyPrefix() {
        return selectedFrequencyPrefix;
    }

    public void setSelectedFrequencyPrefix(String selectedFrequencyPrefix) {
        this.selectedFrequencyPrefix = selectedFrequencyPrefix;
    }

    public String getSelectedPhaseShiftPrefix() {
        return selectedPhaseShiftPrefix;
    }

    public void setSelectedPhaseShiftPrefix(String selectedPhaseShiftPrefix) {
        this.selectedPhaseShiftPrefix = selectedPhaseShiftPrefix;
    }

    public String getFrequencySymbol() {
        return frequencySymbol;
    }

    public void setFrequencySymbol(String frequencySymbol) {
        this.frequencySymbol = frequencySymbol;
    }

    public Double getPrimaryValue() {
           return primaryValue; 
        }

    public void setPrimaryValue(Double primaryValue) {
        this.primaryValue = primaryValue;
    }

    public Double getFrequency() {
        return frequency;
    }

    public void setFrequency(Double frequency) {
        this.frequency = frequency;
    }

    public Double getPhaseShift() {
        return phaseShift;
    }

    public void setPhaseShift(Double phaseShift) {
        this.phaseShift = phaseShift;
    }

    public Double getCalculatedValue() {
        return calculatedValue;
    }

    public void setCalculatedValue(Double calculatedValue) {
        this.calculatedValue = calculatedValue;
    }

    public String getEnteredFrequency() {
        return enteredFrequency;
    }

    public void setEnteredFrequency(String enteredFrequency) {
        this.enteredFrequency = enteredFrequency;
    }

    public String getEnteredPhaseShift() {
        return enteredPhaseShift;
    }

    public void setEnteredPhaseShift(String enteredPhaseShift) {
        this.enteredPhaseShift = enteredPhaseShift;
    }

    public double getSecondaryValue() {
        return secondaryValue;
    }

    public void setSecondaryValue(double secondaryValue) {
        this.secondaryValue = secondaryValue;
    }

    public boolean isIsConstant() {
        return isConstant;
    }

    public void setIsConstant(boolean isConstant) {
        this.isConstant = isConstant;
    }

    public boolean isIsHeldConstant() {
        return isHeldConstant;
    }

    public void setIsHeldConstant(boolean isHeldConstant) {
        this.isHeldConstant = isHeldConstant;
    }

    public String getFullValue() {
        return fullValue;
    }

    public void setFullValue(String fullValue) {
        this.fullValue = fullValue;
    }

    public List<ScopePanel> getScopePanels() {
        return scopePanels;
    }

    public void addScopePanel(ScopePanel scopePanel) {
        this.scopePanels.add(scopePanel);
        
        circuit.circuitPanel.scopesPanel.add(scopePanel);
        circuit.circuitPanel.scopesPanel.invalidate();
        circuit.circuitPanel.scopesPanel.revalidate();
        circuit.circuitPanel.scopesPanel.repaint();
    }
    
    public void deleteScopePanel(ScopePanel scopePanel)
    {
        scopePanels.remove(scopePanel);
        
        circuit.circuitPanel.scopesPanel.remove(scopePanel);
        circuit.circuitPanel.scopesPanel.invalidate();
        circuit.circuitPanel.scopesPanel.revalidate();
        circuit.circuitPanel.scopesPanel.repaint();
    }
    
    public void deleteScopePanels()
    {
        scopePanels.clear();
        circuit.circuitPanel.scopesPanel.removeAll();
        circuit.circuitPanel.scopesPanel.invalidate();
        circuit.circuitPanel.scopesPanel.revalidate();
        circuit.circuitPanel.scopesPanel.repaint();
    }

    public Circuit getCircuit() {
        return circuit;
    }

    public void setCircuit(Circuit circuit) {
        this.circuit = circuit;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getInfo()
    {
        StringBuilder sb = new StringBuilder();
                    
        if(this.getCircuitElm() != null)
        { 
            CircuitElm cirElm = this.getCircuitElm();
            
            String info[] = new String[10];
            
            cirElm.getInfo(info);
            
            for (int index = 0; info[index] != null; index++)
            {                
                sb.append(info[index]);
                sb.append("\n");
            }
        }
        else
        {
            sb.append("");
        }
        
        return sb.toString().trim();
    }
    
    
    public String getTurtle()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(Circuit.turtleIndent + "cp:type \"" + getClass().getSimpleName() + "\" ;");
        sb.append(Circuit.turtleIndent + "cp:subscript \"" + getComponentSubscript() + "\" ;");
        sb.append(Circuit.turtleIndent + "cp:terminal \"" + headTerminal.getID() + "\" ;");
        sb.append(Circuit.turtleIndent + "cp:terminal \"" + tailTerminal.getID() + "\" ;");

        return sb.toString();
    }

    public String toString()
    {
        return this.getClass().getSimpleName() + "_" + getComponentSubscript() + " " + this.getHeadTerminal().getX() + " " + this.getHeadTerminal().getY()+ " " + this.getTailTerminal().getX() + " " + this.getTailTerminal().getY();
    }
}
