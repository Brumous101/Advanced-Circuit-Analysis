package org.mathpiper.ui.gui.applications.circuitpiper.model;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.CirSim;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.ACVoltageElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.AmmeterElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CCCS2Elm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CCVSElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CapacitorElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CircuitElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CurrentElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.DiodeElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.InductorElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.JfetElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.NTransistorElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.OhmMeterElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.PTransistorElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.ResistorElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.SwitchElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.TransistorElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.VCCS2Elm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.VCVS2Elm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.VoltageElm;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.WireElm;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Block;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ACCurrentSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ACVoltageSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Capacitor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.Battery;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.CCCS;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.CCVS;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ControlledSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.CurrentSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.DCVoltageSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.VCCS;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.VCVS;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.VoltageSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.diodes.Diode;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.transistors.Transistor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.transistors.bipolar.TransistorNPN;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.transistors.bipolar.TransistorPNP;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.transistors.fet.TransistorJFETN;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.transistors.fet.TransistorJFETP;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.integratedcircuits.LogicalPackage;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.CurrentIntegrator;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Inductor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Meter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Resistor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Switch;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Wire;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Ammeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.CapacitanceMeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.InductanceMeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Ohmmeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.VoltageIntegrator;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.Voltmeter;
import org.mathpiper.ui.gui.applications.circuitpiper.model.exceptions.UnsupportedOrientationException;
import org.mathpiper.ui.gui.applications.circuitpiper.model.exceptions.UnsupportedPrefixException;
import org.mathpiper.ui.gui.applications.circuitpiper.model.exceptions.UnsupportedValueException;
import org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra.CapacitorEquationVector;
import org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra.ControlledSourceEquationVector;
import org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra.CurrentEquationVector;
import org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra.InductorEquationVector;
import org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra.MeterEquationVector;
import org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra.ResistorEquationVector;
import org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra.SparseMatrix;
import org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra.Vector;
import org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra.SourceEquationVector;
import org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra.WireEquationVector;
import org.mathpiper.ui.gui.applications.circuitpiper.model.numericalanalysis.RungeKutta;
import org.mathpiper.ui.gui.applications.circuitpiper.view.CircuitPanel;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ScreenCapture;
/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses//*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */

public final class Circuit {
    public static String turtleIndent = "\n        ";
    
    public boolean isTrace = false;

    public Map<String, Component> components;
    
    private Map<String, Integer> columnNumbers;
    
    public Map<Point, Terminal> terminals;
    
    public Map<String, String> componentNames;
    
    public CircuitPanel circuitPanel;
    
    public double time;

    public SparseMatrix mainMatrix;
    
    private RungeKutta numericalAnalysis;
    
    private StringBuilder traceData = new StringBuilder();
    
    public String id = "";
    public CirSim cirSim;
    public boolean isCirSim = true;
    

    public Circuit(String id, CircuitPanel circuitPanel) throws Exception {
        
        this.id = id;
        
        this.circuitPanel = circuitPanel;
        
        numericalAnalysis = new RungeKutta(circuitPanel);
                
        init();
    }
    
    private void init() throws Exception
    {
        columnNumbers = new HashMap<String, Integer>();
        
        components = new HashMap<String, Component>();
        
        componentNames = new HashMap<String, String>();
        
        terminals = new HashMap<Point, Terminal>();
        
        //todo:tk:find a better way to initialize these variables.
        Terminal.terminalCounter = 1;
        Transistor.componentCounter = 1;
        ACCurrentSource.componentCounter = 1;
        ACVoltageSource.componentCounter = 1;
        Battery.componentCounter = 1;
        CurrentSource.componentCounter = 1;
        VoltageSource.componentCounter = 1;
        LogicalPackage.componentCounter = 1;
        Meter.componentCounter = 1;
        Capacitor.componentCounter = 1;
        Inductor.componentCounter = 1;
        Resistor.componentCounter = 1;
        Switch.componentCounter = 1;
        Wire.componentCounter = 1;
        Block.componentCounter = 1;
        
        if(circuitPanel != null)
        {
            if(isCirSim && cirSim != null)
            {
                cirSim.init2();
            }

            circuitPanel.repaint();
        }
    }
    
    
    public void clear() throws Exception
    {
        this.init();
    }
    
    
    public void putNumbers(Object object, Integer value)
    {
       // myComponentNames.put(object.toString(), object.toString());
        columnNumbers.put(object.toString(), value);

    }
    
    public void putNumbers(Object object, String key, Integer value) throws Exception
    {
        
        if(object instanceof Component)
        {
            Component component = (Component) object;
            
            componentNames.put(component.getPrimarySymbol() + component.getComponentSubscript() + key, component.getPrimarySymbol() + component.getComponentSubscript() + key);
            columnNumbers.put(component.getPrimarySymbol() + component.getComponentSubscript() + key, value);
        }
        else if(object instanceof Point)
        {
            Point point = (Point) object;
            
            Terminal terminal = terminals.get(point);
            
            componentNames.put(point.toString() + key, "T" + terminal.terminalNumber + key);
            columnNumbers.put(object + key, value);
        }        
        else
        {
            throw new Exception("Unknown circuit element.");
        }
    }
    
    public Map<String, Integer> getColumnNumbers()
    {
        return columnNumbers;
    }
    
    public void setColumnNumbers(HashMap<String, Integer> hm)
    {
        columnNumbers = hm;
    }

    public void save(String filePath) throws IOException{

        FileOutputStream file = new FileOutputStream(filePath);

        file.write(export().getBytes());

        file.close();
    }

    public void open(String filePath) throws Exception {

        String deck = new String(Files.readAllBytes(Paths.get(filePath)));
            
        readCircuitPiperFormat(deck);
    }
    
    
    public void exampleCircuit(int select) throws Exception
    {
        String in = "";
        
        if(select == 1)
        {
            in =
            "Capacitor 256 67 256 162 9.999999999999999E-6\n" +
            "DCVoltageSource 162 67 162 162 5.2\n" +
            "Wire 256 162 382 162\n" +
            "Resistor 162 67 256 67 2000.0\n" +
            "Voltmeter 382 67 382 162\n" +
            "Wire 256 67 382 67\n" +
            "Ammeter 162 162 256 162\n";
            
        }
        else if(select == 2)
        {
            in =
            "DCVoltageSource_1 130 99 130 162 5.2\n" +
            "Resistor_1 256 36 256 130 2.0\n" +
            "Resistor_2 256 130 256 225 3.0\n" +
            "Wire_1 130 99 256 36\n" +
            "Wire_2 130 162 256 225";
        }
        else if(select == 3)
        {
            in =
            "Resistor_1 256 36 256 130 2.0\n" +
            "Resistor_2 256 130 256 225 3.0\n" +
            "Wire_1 130 99 256 36\n" +
            "Wire_2 130 162 256 225\n" +
            "CurrentSource_1 130 99 130 162 0.0345";
        }


        readCircuit(in);
    }
    
    
    public void readCircuit(String code) throws Exception
    {
        if(code.trim().startsWith("$"))
        {
            readCirSimFormat(code);
        }
        else
        {
            readCircuitPiperFormat(code);
        }
        
        circuitPanel.drawingPanel.isDrawGrid = false;
        circuitPanel.showGridCheckBox.setSelected(false);
    }
    
    
    public Component readCirSimElement(String line) throws Exception
    {
        if (line.startsWith("$"))
        {
            return null;
        }

        Stack<String> fields = new Stack<String>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
        Matcher regexMatcher = regex.matcher(line);
        while (regexMatcher.find())
        {
            fields.push(regexMatcher.group());
        }

        Collections.reverse(fields);

        String name = fields.pop();
        int headX = Integer.parseInt(fields.pop());
        int headY = Integer.parseInt(fields.pop());
        int tailX = Integer.parseInt(fields.pop());
        int tailY = Integer.parseInt(fields.pop());

        fields.pop();

        if (name.equals("v"))
        {
            name = "DCVoltageSource";
            fields.pop();
            fields.pop();
            String value = fields.pop();
            fields.clear();
            fields.push(value);

        }
        else if (name.equals("w"))
        {
            name = "Wire";
        }
        else if (name.equals("c"))
        {
            name = "Capacitor";
        }
        else if (name.equals("l"))
        {
            name = "Inductor";
        }
        else if (name.equals("r"))
        {
            name = "Resistor";
        }
        else if (name.equals("t"))
        {
            name = "TransistorPNP";
        }
        else if (name.equals("370"))
        {
            name = "Ammeter";
        }
        else
        {
            name = "Block";
        }

        Component component = newComponent(name, headX, headY, fields);

        component.moveTail(tailX, tailY);
            
        return component;
    }
    
    
    public void readCirSimFormat(String in) throws Exception
    {
        this.circuitPanel.isRunning = false;
        while(this.circuitPanel.isBusy)
        {
            Thread.sleep(100);
        }
        
        this.init();
        
        in = in.trim();
                
        String[] lines = in.split("\n");

        for(String line:lines)
        {

            Component component = readCirSimElement(line);
            
            if(component != null)
            {
                circuitPanel.currentCircuit.addComponent(component, component.getTailTerminal().getX(), component.getTailTerminal().getY());
            }
            
        }

        circuitPanel.drawingPanel.revalidate();
        circuitPanel.drawingPanel.repaint();
    }
    
    
    public void readCircuitPiperFormat(String in) throws Exception
    {
        boolean isCirSimFile = false;
        
        this.circuitPanel.isRunning = false;
        
        while(this.circuitPanel.isBusy)
        {
            Thread.sleep(100);
        }
        
        this.init();
        
        in = in.trim();
                
        String[] lines = in.split("\n");

        for(String line:lines)
        {
            StringTokenizer attributes = null;
            
            if(line.contains("|"))
            {
                isCirSimFile = true;
                
                String[] both = null;
                both = line.split("\\|");
                line = both[0].trim();
                attributes = new StringTokenizer(both[1]);
            }
            
            
            Stack<String> componentAttributeFields = new Stack<String>();
            Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
            Matcher regexMatcher = regex.matcher(line);
            while (regexMatcher.find()) {
                componentAttributeFields.push(regexMatcher.group());
            }

            Collections.reverse(componentAttributeFields);

            String name = componentAttributeFields.pop();
            int headX = Integer.parseInt(componentAttributeFields.pop());
            int headY = Integer.parseInt(componentAttributeFields.pop());
            int tailX = Integer.parseInt(componentAttributeFields.pop());
            int tailY = Integer.parseInt(componentAttributeFields.pop());


            Component component = newComponent(name, headX, headY, componentAttributeFields, attributes, circuitPanel);

            component.moveTail(tailX, tailY);

            circuitPanel.currentCircuit.addComponent(component, tailX, tailY);
            
            if(isCirSimFile)
            {
                cirSim.elmList.add(component.getCircuitElm());
            }
        }

        if(isCirSimFile)
        {         
            isCirSim = true;
            this.circuitPanel.circuitPiperMain.isCirSimCheckBox.setSelected(true);
            
            cirSim.needAnalyze();
        }
        else
        {
            isCirSim = false;
            cirSim = null;
            this.circuitPanel.circuitPiperMain.isCirSimCheckBox.setSelected(false);
        }
        
        circuitPanel.drawingPanel.revalidate();
        circuitPanel.drawingPanel.repaint();
        
    }
    
    
    
    private void processAscComponent(Stack componentAttributeFields, String componentTypeAndID, int headX, int tailX, int headY, int tailY) throws Exception
    {
        if (componentAttributeFields.empty())
        {
            // If the component did not have a value set its value to 0;

            componentAttributeFields.push("0.0");
        }

        Collections.reverse(componentAttributeFields);

        Component component = newComponent(componentTypeAndID, headX, headY, componentAttributeFields, null, circuitPanel);

        component.moveTail(tailX, tailY);

        addComponent(component, tailX, tailY);
    }
    
    public void addComponent(Component component, int tailXPixels, int tailYPixels) throws Exception {
        Point headPoint = new Point(component.getHeadTerminal().getX(), component.getHeadTerminal().getY());
        if (!this.terminals.containsKey(headPoint)) {
            this.terminals.put(headPoint, component.getHeadTerminal());
        }
        component.setHead(this.terminals.get(headPoint));

        Point tailPoint = new Point(tailXPixels, tailYPixels);
        if (!this.terminals.containsKey(tailPoint)) {
            this.terminals.put(tailPoint, component.getTailTerminal());
        }
        component.setTail(this.terminals.get(tailPoint));
        //component.connectHeadAndTail();
        this.components.put(component.getID(), component);    
    }
    
    
    public void readAscFormat(String path) throws Exception
    {
        if(circuitPanel != null)
        {
            this.circuitPanel.isRunning = false;
            
            while (this.circuitPanel.isBusy)
            {
                Thread.sleep(100);
            }
        }

        this.init();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), "ISO-8859-1"));
        
        boolean isEOF = false;


        int xRef = 0;
        int yRef = 0;
        int xOff = 0;
        int yOff = 0;
        int lowestX = 0;
        int lowestY = 0;

        int headX = 0;
        int headY = 0;
        int tailX = 0;
        int tailY = 0;
        
        int wireNumber = 1;

        int refToTail = 0;

        String componentType = "None";
        String componentTypeAndID = "None";

        Stack<String> componentAttributeFields = new Stack<String>();

        String lastField1 = "";
        try
        {
            while (in.ready() || isEOF)
            {
                String line = in.readLine();
                
                if(isEOF)
                {
                    line = "EOF ";
                }
                
                //System.out.println(line);
                
                if(line.trim().equals(""))
                {
                    continue;
                }
                
                
                String field1 = line.substring(0, line.indexOf(" "));

                if ((field1.equalsIgnoreCase("SYMBOL") || isEOF) && (lastField1.equals("SYMBOL") || lastField1.equals("SYMATTR")))
                {
                    if (!componentType.startsWith("Unsupported"))
                    {
          //            System.out.println("XXX " + componentAttributeFields.firstElement());
                        processAscComponent(componentAttributeFields, componentTypeAndID, headX, tailX, headY, tailY);
                    }
                        
                    if (isEOF)
                    {
                        break;
                    }

                    componentAttributeFields = new Stack<String>();

                    headX = 0;
                    headY = 0;
                    tailX = 0;
                    tailY = 0;

                    refToTail = 0;
                }
                
                if(! in.ready())
                {
                    isEOF = true;
                }

                StringTokenizer lineFields = new StringTokenizer(line, " ");

                lineFields.nextToken(); // Disguard field 1;

                if (field1.equalsIgnoreCase("symbol"))
                {
                    lastField1 = field1;

                    componentType = lineFields.nextToken();

                    xRef = Integer.parseInt(lineFields.nextToken());
                    yRef = Integer.parseInt(lineFields.nextToken());

                    String orientation = lineFields.nextToken();

                    if (componentType.equalsIgnoreCase("res"))
                    {
                        componentType = "Resistor";
                        xOff = 16;
                        yOff = 16;

                        refToTail = 96;
                    }
                    else if (componentType.equalsIgnoreCase("cap"))
                    {
                        componentType = "Capacitor";
                        xOff = 16;
                        yOff = 0;
                        refToTail = 64;
                    }
                    else
                    {
                        componentType = "Unsupported";
                        continue;
                    }

                    if (orientation.equalsIgnoreCase("R0"))
                    {
                        headX = xRef + xOff;
                        headY = yRef + yOff;
                        tailX = headX;
                        tailY = yRef + refToTail;
                    }
                    else if (orientation.equalsIgnoreCase("R90"))
                    {
                        headX = xRef - yOff;
                        headY = yRef + xOff;
                        tailX = xRef - refToTail;
                        tailY = headY;
                    }
                    else if (orientation.equalsIgnoreCase("R180"))
                    {
                        headX = xRef - xOff;
                        headY = yRef - yOff;
                        tailX = headX;
                        tailY = yRef - refToTail;
                    }
                    else if (orientation.equalsIgnoreCase("R270"))
                    {
                        headX = xRef + yOff;
                        headY = yRef - xOff;
                        tailX = xRef + refToTail;
                        tailY = headY;
                    }
                    else
                    {
                        throw new UnsupportedOrientationException("Invalid orientation: " + orientation);
                    }

                }
                else if (!componentType.startsWith("Unsupported") && field1.equals("SYMATTR"))
                {
                    lastField1 = "SYMATTR";

                    String attributeName = lineFields.nextToken();

                    if (attributeName.equalsIgnoreCase("InstName"))
                    {
                        String componentName = lineFields.nextToken();

                        componentTypeAndID = componentType + "_" + componentName.substring(1);
                    }
                    else if (attributeName.equalsIgnoreCase("Value"))
                    {
                        String value = lineFields.nextToken();
                        
                        if(value.contains("{"))
                        {
                            throw new UnsupportedValueException("Unsupported value: " + value);
                        }

                        if(value.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?"))
                        {
                            componentAttributeFields.push(value);
                        }
                        else
                        {
                            String[] numberAndPrefix = value.split("(?<=\\d)(?=[a-zA-Zµ])");

                            String numberString = numberAndPrefix[0];

                            if (numberAndPrefix.length == 2)
                            {
                                try {

                                    Double.parseDouble(numberString); // Just to trigger a parsing exception.

                                    String prefix = numberAndPrefix[1];

                                    String ee = "";

                                    if (prefix.equalsIgnoreCase("k"))
                                    {
                                        ee = "E3";
                                    }
                                    else if (prefix.equalsIgnoreCase("meg"))
                                    {
                                        ee = "E6";
                                    }
                                    else if (prefix.equalsIgnoreCase("G"))
                                    {
                                        ee = "E9";
                                    }
                                    else if (prefix.equalsIgnoreCase("t"))
                                    {
                                        ee = "E12";
                                    }
                                    else if (prefix.equalsIgnoreCase("m"))
                                    {
                                        ee = "E-3";
                                    }
                                    else if (prefix.equalsIgnoreCase("u") || prefix.equals("µ"))
                                    {
                                        ee = "E-6";
                                    }
                                    else if (prefix.equalsIgnoreCase("N"))
                                    {
                                        ee = "E-9";
                                    }
                                    else if (prefix.equalsIgnoreCase("p"))
                                    {
                                        ee = "E-12";
                                    }
                                    else if (prefix.equalsIgnoreCase("f"))
                                    {
                                        ee = "E-15";
                                    }
                                    else
                                    {
                                        throw new UnsupportedPrefixException("Invalid prefix: " + prefix);
                                    }
                                    
                                    componentAttributeFields.push(numberString + ee); // Don't use "number.toString()" because it can output in exponential format.
                                } 
                                catch (NumberFormatException nfe)
                                {
                                    throw new UnsupportedValueException("Unsupported value: " + value);
                                }
                            }
                            else
                            {
                                throw new UnsupportedValueException("Unsupported value: " + value);
                            }
                        }
                    }
                }
                else if (field1.equalsIgnoreCase("wire"))
                {
                    componentType = "Wire";
                    
                    componentTypeAndID = componentType + "_" + wireNumber++;
                    
                    headX = Integer.parseInt(lineFields.nextToken());
                    headY = Integer.parseInt(lineFields.nextToken());
                    tailX = Integer.parseInt(lineFields.nextToken());
                    tailY = Integer.parseInt(lineFields.nextToken());
                    
                    
                    processAscComponent(componentAttributeFields, componentTypeAndID, headX, tailX, headY, tailY);
                    
                    componentAttributeFields = new Stack<String>();

                    headX = 0;
                    headY = 0;
                    tailX = 0;
                    tailY = 0;

                    refToTail = 0;
                }
                else
                {
                    // Unsupported field 1;
                }
            }

        }
        finally
        {
            in.close();
        }
        
        
        for(Terminal terminal : terminals.values())
        {   
            if(terminal.getX() < lowestX)
            {
                lowestX = terminal.getX();
            }
            else if(terminal.getY() < lowestY)
            {
                lowestY = terminal.getY();
            }
        }
        

        for(Terminal terminal : terminals.values())
        {
            terminal.setX(terminal.getX() + Math.abs(lowestX) + 20);
            terminal.setY(terminal.getY() + Math.abs(lowestY) + 20);
        }
        
        if(circuitPanel != null)
        {
            circuitPanel.drawingPanel.revalidate();
            circuitPanel.drawingPanel.repaint();
        }
    }
    
    
    public void viewAutoLayout()
    {
        Graph graph = new SingleGraph("Circuit");
        
        for(Terminal terminal:terminals.values())
        {
            String nodeName = "T" + terminal.terminalNumber;
            graph.addNode(nodeName);

        }

        for(Component component: this.components.values())
        {
            graph.addEdge(component.getID(), "T" + component.getHeadTerminal().terminalNumber, "T" + component.getTailTerminal().terminalNumber);

            Edge edge = graph.getEdge(component.getID());
            
            if(! (component instanceof Wire))
            {
                edge.addAttribute("ui.label", component.getID());
                edge.addAttribute("ui.style", "text-size: 20;");
            }
        }


        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
    }
    

    
    public String export()
    {
        StringBuilder sb = new StringBuilder();
        Iterator<Component> itr = components.values().iterator();
        while (itr.hasNext()) {
            Component component = itr.next();

            sb.append(component.toString());
            
            if(isCirSim)
            {
                CircuitElm circuitElement = component.getCircuitElm();
                
                circuitElement.reset(); //todo:tk:saving a circuit resets all of its elements.
                    
                sb.append(" | " + circuitElement.dump());
            }
            
            sb.append("\n");
        }
        
        return sb.toString().trim();
    }
    
    public String exportTurtle()
    {
        StringBuilder sb = new StringBuilder();
        
        List<String> lineList = new ArrayList();
        
        sb.append("@prefix cp: <http://mathpiper.org/ns/circuitpiper#> .\n");
        

        
        int blankNodeID = 1;
        
        sb.append("\n\ncp:circuit1");

        for (Component component : components.values()) {
            
            sb.append(turtleIndent + "cp:component _:c" + blankNodeID + " ;");
            
            String componentString = component.getTurtle();
            componentString = componentString.substring(0, componentString.length() -1) + ".";
            
            lineList.add("\n_:c" + blankNodeID++ + componentString);
            //sb.append("");
            //sb.append("");
            //sb.append("");

            //sb.append(component.toString());
            
            /*
            if(isCirSim)
            {
                CircuitElm circuitElement = component.getCircuitElm();
                    
                sb.append(" | " + circuitElement.dump());
            }
            */
            
        }

        blankNodeID = 1;
        
        for (Terminal terminal : terminals.values())
        {
            sb.append(turtleIndent + "cp:terminal _:t" + blankNodeID + " ;");
            lineList.add("\n_:t" + blankNodeID++ + terminal.getTurtle());
        }
        
        sb.deleteCharAt(sb.length()-1);
        sb.append(".");
        
        for(String line : lineList)
        {
            sb.append(line);
        }
        
        return sb.toString().trim();
    }
    
    public void run(boolean isRun)
    {
        if(isRun)
        {
            this.circuitPanel.isRunning = true;
        }
        else
        {
            this.circuitPanel.isRunning = false;
        }
    }
    
    
 

    
    public synchronized StringBuilder updateCircuit() throws Exception {
        
        circuitPanel.isBusy = true;

        if (circuitPanel.currentCircuit.terminals.size() > 0) {

// ========== Instantaneous Charge Movement Matrix for Capacitors ===========
            int rowsSize = circuitPanel.currentCircuit.terminals.size() + 3 * circuitPanel.currentCircuit.components.size(); //max 3 equations 27
            int columnsSize = 2 * circuitPanel.currentCircuit.terminals.size() + 3 * circuitPanel.currentCircuit.components.size() + 1; //v and v' 34        

            SparseMatrix instantaneousChargeMovementMatrix = new SparseMatrix(circuitPanel, rowsSize, columnsSize);
            
            instantaneousChargeMovementMatrix.setUpInstantaneousChargeMovementMatrix(time);
            instantaneousChargeMovementMatrix.rowEchelonFormMatrix(instantaneousChargeMovementMatrix);
            instantaneousChargeMovementMatrix.rowReducedEchelonFormMatrix(instantaneousChargeMovementMatrix);
         
            if(isTrace)
            {
                this.traceData.delete(0, traceData.length());
                traceData.append("== Instantaneous Charge Movement Matrix\n");
                traceData.append(SparseMatrix.dumpMatrix(instantaneousChargeMovementMatrix) + "\n");
            }
           
            for (Component ec : circuitPanel.currentCircuit.components.values()) {
                if (ec instanceof Capacitor) {
                    if (ec.getDisplayLabel() != null) {
                        ec.setSecondaryValue(ec.getSecondaryValue() + instantaneousChargeMovementMatrix.getDeltaQ(ec));
                    }
                }
                
                if (ec instanceof CurrentIntegrator) {
                    ec.setSecondaryValue(ec.getSecondaryValue() + instantaneousChargeMovementMatrix.getDeltaQ(ec));
                }
            }

// ========== Instantaneous Current or Magnetic Flux Matrix for Inductors ===========
            
            SparseMatrix instantaneousCurrentOrMagneticFluxMatrix = new SparseMatrix(circuitPanel, rowsSize, columnsSize);

            instantaneousCurrentOrMagneticFluxMatrix.setUpInstantaneousCurrentOrMagneticFluxMatrix(time);
            instantaneousCurrentOrMagneticFluxMatrix.rowEchelonFormMatrix(instantaneousCurrentOrMagneticFluxMatrix);
            instantaneousCurrentOrMagneticFluxMatrix.rowReducedEchelonFormMatrix(instantaneousCurrentOrMagneticFluxMatrix);      

            if(isTrace)
            {
                traceData.append("== Instantaneous Current Or Magnetic Flux Matrix\n");
                traceData.append(SparseMatrix.dumpMatrix(instantaneousCurrentOrMagneticFluxMatrix) + "\n");
            }
         
            for (Component ec : circuitPanel.currentCircuit.components.values()) {
                if (ec instanceof Inductor) {
                    if (ec.getPrimaryValue() != null) {
                        ec.setSecondaryValue(ec.getSecondaryValue() + ec.getPrimaryValue() * instantaneousCurrentOrMagneticFluxMatrix.getDeltaCurrent(ec));
                    }
                } else if (ec instanceof VoltageIntegrator) {
                    ec.setSecondaryValue(ec.getSecondaryValue() + instantaneousCurrentOrMagneticFluxMatrix.getDeltaFlux(ec));
                }
            }

            
// ========== Main Matrix ===========

            mainMatrix = new SparseMatrix(circuitPanel, rowsSize, columnsSize);
            
            mainMatrix.setUpMainMatrix(time);
            
            if(isTrace)
            {
                traceData.append("== Main Matrix 1 (after setup)\n");
                traceData.append(SparseMatrix.dumpMatrix(mainMatrix, true));
                
                
                // KCL equations.
                
                int index = 1;
                
                for (Vector row: mainMatrix.rows ) {
                    
                    StringBuilder equation = new StringBuilder();
                    
                    equation.append(index + (index < 10 ? "  " : " "));
                    index++;
                    
                    if(row instanceof CurrentEquationVector)
                    {
                        equation.append(row.getComponentName() + ": ");
                        
                        CurrentEquationVector cev = (CurrentEquationVector) row;
                        
                        for (int columnIndex = 0; columnIndex < mainMatrix.columns.size(); columnIndex++) {
                            
                            double value = cev.get(columnIndex);
                            
                            if(value != 0.0)
                            {
                                Vector vector = mainMatrix.columns.get(columnIndex);
                                equation.append(value < 0 ? "-" : "");
                                equation.append(vector.description);

                                equation.append(" + ");
                            }
                        }
                        
                        equation.delete(equation.length() - 3, equation.length());
                        
                        equation.append(" = 0\n");
                        
                        traceData.append(equation.toString());
                    }
                    else if(row instanceof SourceEquationVector)
                    {
                        SourceEquationVector sev = (SourceEquationVector) row;
                        double value = sev.get(mainMatrix.columns.size() - 1); 
                        equation.append(sev.getComponentName() + " = " + value + "\n");
                        traceData.append(equation.toString());
                    }
                    else if(row instanceof ControlledSourceEquationVector)
                    {
                        ControlledSourceEquationVector csev = (ControlledSourceEquationVector) row;
                        ControlledSource cs = csev.getControlledSource();

                        equation.append(csev.getComponentName() + " = " + cs.getMultiplier() + " * " + cs.getControllingComponentLabel() + "\n");
                        traceData.append(equation.toString());
                    }
                    else if(row instanceof WireEquationVector)
                    {
                        WireEquationVector wev = (WireEquationVector) row;
                        double value = wev.get(mainMatrix.columns.size() - 1);
                        equation.append(wev.getComponentName() + " = " + value + "\n");
                        traceData.append(equation.toString());
                    }
                   /* todo:tk:see p.28 of "Circuit Simulation" by Najm. */
                    else if(row instanceof ResistorEquationVector)
                    {
                        equation.append(row.getComponentName() + ": ");
                        ResistorEquationVector rev = (ResistorEquationVector) row;
                        
                        for (int columnIndex = 0; columnIndex < mainMatrix.columns.size(); columnIndex++) {
                            
                            double value = rev.get(columnIndex);
                            
                            if(value != 0.0)
                            {
                                if(columnIndex < mainMatrix.firstTerminalVoltageIndex)
                                {
                                    equation.append(value + " * ");
                                }
                                Vector vector = mainMatrix.columns.get(columnIndex);
                                equation.append(value < 0 ? "-" : "");
                                equation.append(vector.description);

                                equation.append(" + ");
                            }
                        }
                        
                        equation.delete(equation.length() - 3, equation.length());
                        equation.append(" = 0\n");
                        traceData.append(equation.toString());
                    }
                    else if(row instanceof MeterEquationVector)
                    {
                        MeterEquationVector mev = (MeterEquationVector) row;
                        double value = mev.get(mainMatrix.columns.size() - 1);
                        equation.append(mev.getComponentName() + " = " + value + "\n");
                        traceData.append(equation.toString());
                    }
                    else if(row instanceof CapacitorEquationVector)
                    {
                        CapacitorEquationVector cev = (CapacitorEquationVector) row;
                        double value = cev.get(mainMatrix.columns.size() - 1);
                        equation.append(cev.getComponentName() + " = " + value + "\n");
                        traceData.append(equation.toString());
                    }
                    else if(row instanceof InductorEquationVector)
                    {
                        InductorEquationVector iev = (InductorEquationVector) row;
                        double value = iev.get(mainMatrix.columns.size() - 1);
                        equation.append(iev.getComponentName() + " = " + value + "\n");
                        traceData.append(equation.toString());
                    }
                    else
                    {
                        throw new Exception(row.toString() + " is not supported.");
                    }
                }
                
                traceData.append("\n\n");
            }
            
            mainMatrix.rowEchelonFormMatrix(mainMatrix);
            
            if(isTrace)
            {
                traceData.append("== Main Matrix 2 (row echelon form)\n");
                traceData.append(SparseMatrix.dumpMatrix(mainMatrix) + "\n");
            }
            
            mainMatrix.rowReducedEchelonFormMatrix(mainMatrix);

/*            
            for(Component ec: components.values())
            {
                if (ec instanceof ControlledSource) {
                    ControlledSource controlledSource = (ControlledSource) ec;
                    
                    Component controllingComponent = components.get(controlledSource.controllingComponent);
                    
                    double value = mainMatrix.getComponentDeltaV(controllingComponent);
                    
                    controlledSource.secondaryValue = value * controlledSource.multiplier;
                }
            }
*/        
            if(isTrace)
            {
                traceData.append("== Main Matrix 3 (reduced row echelon form)\n");
                traceData.append(SparseMatrix.dumpMatrix(mainMatrix) + "\n");
            }
            

            circuitPanel.drawingPanel.updateGraphsAndMeters(mainMatrix, time);

            
            double timeStep = 0.05;
            double finalTime = time + timeStep;
            long start = System.currentTimeMillis();

            if (circuitPanel.timeOption) {
                do {
                    time = numericalAnalysis.rk(timeStep, timeStep, time);
                } while (time < finalTime && System.currentTimeMillis() < start + 0.03);
                
                mainMatrix = numericalAnalysis.mainMatrix;
                
            if(isTrace)
            {
                traceData.append("== Main Matrix 4 (after numerical analysis)\n");
                traceData.append(SparseMatrix.dumpMatrix(mainMatrix) + "\n");
            }
                
                circuitPanel.drawingPanel.updateGraphsAndMeters(mainMatrix, time);
            }
        }

        circuitPanel.isBusy = false;
        
        return traceData;
    }
    

    
    public String dumpMatrix() throws Exception {
        return SparseMatrix.dumpMatrix(mainMatrix) + toString();
    }
    
    
    public String toMathPiper()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        Iterator<Component> ecs = components.values().iterator();
        while (ecs.hasNext()) {
            Component component = ecs.next();
            String componentName = component.getID();
            sb.append("[\"" + componentName + "\",[");
            sb.append("[\"name\", \"" + componentName + "\"]");

            sb.append(", [\"Terminals\", ");
            Terminal headTerminal = component.getHeadTerminal();
            Terminal tailTerminal = component.getTailTerminal();
            sb.append("[\"T" + headTerminal.terminalNumber +  "\", \"T" + tailTerminal.terminalNumber + "\"]]");
            
            Double componentCurrent = mainMatrix.getCurrent(component);
            sb.append(", [\"Current\", " + componentCurrent + "]");
            
            if(component instanceof Resistor) //todo:tk:handle more components than just resistors.
            {
                Double voltage = component.getPrimaryValue() * componentCurrent;
                sb.append(", [\"Voltage\", " + voltage + "]");
            }
            sb.append("]],\n");
        }
        
        sb.append("];\n");
        
        return sb.toString();
    }
        
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Terminals.
        sb.append("Terminals\n");
        ArrayList<Terminal> terminalsList = new ArrayList<Terminal>(terminals.values());
        
        Collections.sort(terminalsList, new Comparator<Terminal>() {
            public int compare(Terminal lhs, Terminal rhs) {
                return lhs.terminalNumber < rhs.terminalNumber ? -1 : (lhs.terminalNumber > rhs.terminalNumber) ? 1 : 0;
            }
        });

        for (Terminal terminal : terminalsList){
            sb.append(terminal.getID() + ", " + "(" + terminal.getX() + ", " + terminal.getY() + ")\n");
        }
        
        sb.append("\n\n");
        
        
        // Components.
        sb.append("Components\n");
        /*
        Collections.sort(components, new Comparator<Component>() {
            public int compare(Component lhs, Component rhs) {
                return lhs.getID().compareTo(rhs.getID());
            }
        });
        */
        
        Iterator<Component> itr = components.values().iterator();
        while (itr.hasNext()) {
            Component component = itr.next();
            
            if(component instanceof Meter)
            {
                continue;
            }

            String componentName = component.getID();
            Terminal headTerminal = component.getHeadTerminal();
            Terminal tailTerminal = component.getTailTerminal();
            
            
            sb.append(componentName);
            
            
            /*
            Double componentCurrent = mainMatrix.getCurrent(component);

            
            sb.append(componentName +
                      ", T" + headTerminal.terminalNumber +
                      ", T" + tailTerminal.terminalNumber +
                      ", I: " + String.format("%.8f",componentCurrent)
                      // + ", I': " + String.format("%.8f",currentPrime)
            );
            
            if(component instanceof Resistor)
            {
                Double voltage = component.getPrimaryValue() * componentCurrent;
                sb.append(", V: " + String.format("%.8f", voltage));
            }
*/
            
            sb.append("\n");
        }

        return sb.toString().trim();// + "\n\n" + toMathPiper();
    }
    
    
    public double getStepSize()
    {
        return this.numericalAnalysis.getStepSize();
    }
    
    
    
    // API.
    
    public void highlight(String id, boolean isHighlight) throws Exception
    {
        Component component = components.get(id);
        
        if(component != null)
        {
            component.isHighlight = isHighlight;
        }
        else
        {
            Terminal terminal = terminals.get(id);
            
            if(terminal != null)
            {
                terminal.isHighlight = isHighlight;
            }
            else
            {
                throw new Exception("Unknown ID.");
            }
        }
        
        this.circuitPanel.drawingPanel.repaint();
    }
    
    public void highlightClear()
    {
        for(Component component : components.values())
        {
            component.isHighlight = false;
        }
        
        for(Terminal terminal : terminals.values())
        {
            terminal.isHighlight = false;
        }

        this.circuitPanel.drawingPanel.repaint();
    }
    
    
    public BufferedImage circuitImageSave(String path) throws Exception
    {
        BufferedImage circuitImage = circuitPanel.drawingPanel.getImageOfCircuit();
                        
        ScreenCapture.writeImage(circuitImage, path);
        
        return circuitImage;
    }

    public Component newComponent(String typeAndUID, int headX, int headY, Stack attributes) throws Exception
    {
        return newComponent(typeAndUID, headX, headY, attributes, null, null);
    }

    public Component newComponent(String typeAndUID, int headX, int headY, Stack componentAttributes, StringTokenizer elmAttributes, CircuitPanel circuitPanel) throws Exception
    {
        Component newComponent = null;
        Integer x1 = null;
        Integer y1 = null;
        Integer x2 = null;
        Integer y2 = null;
        Integer flags = null;
        if (isCirSim && elmAttributes != null)
        {
            String ElmType = elmAttributes.nextToken();
            x1 = Integer.parseInt(elmAttributes.nextToken());
            y1 = Integer.parseInt(elmAttributes.nextToken());
            x2 = Integer.parseInt(elmAttributes.nextToken());
            y2 = Integer.parseInt(elmAttributes.nextToken());
            flags = Integer.parseInt(elmAttributes.nextToken());
        }
        CircuitElm cirElm = null;
        String componentType = "";
        String uid = null;
        if (typeAndUID.contains("_"))
        {
            String[] typeAndUIDArray = typeAndUID.split("_");
            componentType = typeAndUIDArray[0];
            uid = typeAndUIDArray[1];
        }
        else
        {
            componentType = typeAndUID;
        }
        
        // Note:tk:the head and the tail have been reversed in the CirSim components because
        // CircuitPiper draws components head to tail while CirSim draws components tail to head.
        if (componentType.startsWith("Ammeter"))
        {
            newComponent = new Ammeter(headX, headY, uid, this);
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new AmmeterElm(headX, headY);
                }
                else
                {
                    cirElm = new AmmeterElm(x1, y1, x2, y2, flags, elmAttributes);
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("DCVoltageSource"))
        {
            if (componentAttributes == null)
            {
                newComponent = new DCVoltageSource(headX, headY, uid, this);
            }
            else
            {
                newComponent = new DCVoltageSource(headX, headY, uid, componentAttributes, this);
            }
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new VoltageElm(headX, headY, VoltageElm.WF_DC);
                }
                else
                {
                    cirElm = new VoltageElm(x1, y1, x2, y2, flags, elmAttributes);
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("CapacitanceMeter"))
        {
            newComponent = new CapacitanceMeter(headX, headY, uid, this);
        }
        else if (componentType.startsWith("Capacitor"))
        {
            if (componentAttributes == null)
            {
                newComponent = new Capacitor(headX, headY, uid, this);
            }
            else
            {
                newComponent = new Capacitor(headX, headY, uid, componentAttributes, this);
            }
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new CapacitorElm(headX, headY);
                }
                else
                {
                    cirElm = new CapacitorElm(x1, y1, x2, y2, flags, elmAttributes);
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("CurrentSource"))
        {
            if (componentAttributes == null)
            {
                newComponent = new CurrentSource(headX, headY, uid, this);
            }
            else
            {
                newComponent = new CurrentSource(headX, headY, uid, componentAttributes, this);
            }
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new CurrentElm(headX, headY);
                }
                else
                {
                    cirElm = new CurrentElm(x1, y1, x2, y2, flags, elmAttributes);
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("CurrentIntegrator"))
        {
            newComponent = new CurrentIntegrator(headX, headY, uid, this);
        }
        else if (componentType.startsWith("VoltageIntegrator"))
        {
            newComponent = new VoltageIntegrator(headX, headY, uid, this);
        }
        else if (componentType.startsWith("InductanceMeter"))
        {
            newComponent = new InductanceMeter(headX, headY, uid, this);
        }
        else if (componentType.startsWith("Inductor"))
        {
            if (componentAttributes == null)
            {
                newComponent = new Inductor(headX, headY, uid, this);
            }
            else
            {
                newComponent = new Inductor(headX, headY, uid, componentAttributes, this);
            }
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new InductorElm(headX, headY);
                }
                else
                {
                    cirElm = new InductorElm(x1, y1, x2, y2, flags, elmAttributes);
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("Ohmmeter"))
        {
            newComponent = new Ohmmeter(headX, headY, uid, this);
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new OhmMeterElm(headX, headY);
                }
                else
                {
                    cirElm = new OhmMeterElm(x1, y1, x2, y2, flags, elmAttributes);
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("Resistor"))
        {
            if (componentAttributes == null)
            {
                newComponent = new Resistor(headX, headY, uid, this);
            }
            else
            {
                newComponent = new Resistor(headX, headY, uid, componentAttributes, this);
            }
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new ResistorElm(headX, headY);
                }
                else
                {
                    cirElm = new ResistorElm(x1, y1, x2, y2, flags, elmAttributes);
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("Switch"))
        {
            newComponent = new Switch(headX, headY, uid, this);
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    SwitchElm switchElm = new SwitchElm(headX, headY);
                    switchElm.toggle();
                    cirElm = switchElm;
                }
                else
                {
                    SwitchElm switchElm = new SwitchElm(x1, y1, x2, y2, flags, elmAttributes);
                    switchElm.toggle();
                    cirElm = switchElm;
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("Voltmeter"))
        {
            newComponent = new Voltmeter(headX, headY, uid, this);
        }
        else if (componentType.startsWith("Wire"))
        {
            newComponent = new Wire(headX, headY, uid, this);
            
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new WireElm(headX, headY);
                }
                else
                {
                    cirElm = new WireElm(x1, y1, x2, y2, flags, elmAttributes);
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("ACCurrentSource"))
        {
            if (componentAttributes == null)
            {
                newComponent = new ACCurrentSource(headX, headY, uid, this);
            }
            else
            {
                newComponent = new ACCurrentSource(headX, headY, uid, componentAttributes, this);
            }
        }
        else if (componentType.startsWith("ACVoltageSource"))
        {
            if (componentAttributes == null)
            {
                newComponent = new ACVoltageSource(headX, headY, uid, this);
            }
            else
            {
                newComponent = new ACVoltageSource(headX, headY, uid, componentAttributes, this);
            }
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new ACVoltageElm(headX, headY);
                }
                else
                {
                    cirElm = new VoltageElm(x1, y1, x2, y2, flags, elmAttributes);
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("Block"))
        {
            if (componentAttributes == null)
            {
                newComponent = new Block(headX, headY, uid, this);
            }
            else
            {
                newComponent = new Block(headX, headY, uid, componentAttributes, this);
            }
        }
        else if (componentType.startsWith("VCVS"))
        {
            if (componentAttributes == null)
            {
                newComponent = new VCVS(headX, headY, uid, this);
            }
            else
            {
                newComponent = new VCVS(headX, headY, uid, componentAttributes, this);
            }
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new VCVS2Elm(headX, headY);
                }
                else
                {
                    VCVS2Elm vcvs2Elm = new VCVS2Elm(x1, y1, x2, y2, flags, elmAttributes);
                    cirElm = vcvs2Elm;
                    if (vcvs2Elm.wireControl0 != null)
                    {
                        cirSim.elmList.add(vcvs2Elm.wireControl0);
                    }
                    if (vcvs2Elm.wireControl1 != null)
                    {
                        cirSim.elmList.add(vcvs2Elm.wireControl1);
                    }
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("VCCS"))
        {
            if (componentAttributes == null)
            {
                newComponent = new VCCS(headX, headY, uid, this);
            }
            else
            {
                newComponent = new VCCS(headX, headY, uid, componentAttributes, this);
            }
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new VCVS2Elm(headX, headY);
                }
                else
                {
                    VCCS2Elm vccs2Elm = new VCCS2Elm(x1, y1, x2, y2, flags, elmAttributes);
                    cirElm = vccs2Elm;
                    if (vccs2Elm.wireControl0 != null)
                    {
                        cirSim.elmList.add(vccs2Elm.wireControl0);
                    }
                    if (vccs2Elm.wireControl1 != null)
                    {
                        cirSim.elmList.add(vccs2Elm.wireControl1);
                    }
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("CCVS"))
        {
            if (componentAttributes == null)
            {
                newComponent = new CCVS(headX, headY, uid, this);
            }
            else
            {
                //newComponent = new CCVS(xPixels, yPixels, uid, attributes, this);
            }
            if (isCirSim)
            {
                cirElm = new CCVSElm(headX, headY);
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("CCCS"))
        {
            if (componentAttributes == null)
            {
                newComponent = new CCCS(headX, headY, uid, this);
            }
            else
            {
                newComponent = new CCCS(headX, headY, uid, componentAttributes, this);
            }
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new CCCS2Elm(headX, headY);
                }
                else
                {
                    CCCS2Elm cccs2Elm = new CCCS2Elm(x1, y1, x2, y2, flags, elmAttributes);
                    cirElm = cccs2Elm;
                    if (cccs2Elm.wireControl0 != null)
                    {
                        cirSim.elmList.add(cccs2Elm.wireControl0);
                    }
                    if (cccs2Elm.wireControl1 != null)
                    {
                        cirSim.elmList.add(cccs2Elm.wireControl1);
                    }
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("TransistorNPN"))
        {
            newComponent = new TransistorNPN(headX, headY, uid, this);
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new NTransistorElm(headX, headY);
                }
                else
                {
                    cirElm = new TransistorElm(x1, y1, x2, y2, flags, elmAttributes);
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("TransistorPNP"))
        {
            newComponent = new TransistorPNP(headX, headY, uid, this);
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new PTransistorElm(headX, headY);
                }
                else
                {
                    cirElm = new TransistorElm(x1, y1, x2, y2, flags, elmAttributes);
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("TransistorJFETN"))
        {
            newComponent = new TransistorJFETN(headX, headY, uid, this);
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new JfetElm(headX, headY, false);
                }
                else
                {
                    cirElm = new JfetElm(x1, y1, x2, y2, flags, elmAttributes);
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("TransistorJFETP"))
        {
            newComponent = new TransistorJFETP(headX, headY, uid, this);
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new JfetElm(headX, headY, true);
                }
                else
                {
                    cirElm = new JfetElm(x1, y1, x2, y2, flags, elmAttributes);
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else if (componentType.startsWith("LogicalPackage"))
        {
            newComponent = new LogicalPackage(headX, headY, uid, this);
        }
        else if (componentType.startsWith("Diode"))
        {
            newComponent = new Diode(headX, headY, uid, this);
            if (isCirSim)
            {
                if (elmAttributes == null)
                {
                    cirElm = new DiodeElm(headX, headY);
                }
                else
                {
                    cirElm = new DiodeElm(x1, y1, x2, y2, flags, elmAttributes);
                }
                newComponent.setCircuitElm(cirElm);
            }
        }
        else
        {
            throw new Exception("Unknown component name: " + componentType);
        }
        
        if (cirElm != null)
        {
            cirElm.component = newComponent;
        }
        
        return newComponent;
    }
}
