package org.mathpiper.ui.gui.applications.circuitpiper.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.CirSim;
import org.mathpiper.ui.gui.applications.circuitpiper.model.exceptions.UnsupportedOrientationException;
import org.mathpiper.ui.gui.applications.circuitpiper.model.exceptions.UnsupportedPrefixException;
import org.mathpiper.ui.gui.applications.circuitpiper.model.exceptions.UnsupportedValueException;
import org.mathpiper.ui.gui.applications.circuitpiper.view.CircuitPanel;

public class Circuits
{
    private Map<String, Circuit> circuits = new HashMap();
    
    
    public void add(Circuit circuit)
    {
        circuits.put(circuit.id, circuit);
    }
    
    public Map<String, Circuit> get()
    {
        return circuits;
    }
    
    
    public void loadCircuitsJS() throws Exception
    {
        boolean isVerbose = false;
        
        List<Path> list = new ArrayList();
                
        Files.find(Paths.get("/home/tkosan/git2/circuitjs1/src/com/lushprojects/circuitjs1/public/circuits"), 9999, (p, bfa) -> bfa.isRegularFile()
                && p.getFileName().toString().matches(".*\\.txt")).forEach(list::add);
        
        int readCount = 0;
        
        CircuitPanel circuitPanel = new CircuitPanel();
        circuitPanel.drawingPanel.isDrawGrid = false;
        circuitPanel.showGridCheckBox.setSelected(false);
        circuitPanel.drawingPanel.viewScale = 1;
        
        for(Path circuitPath : list)
        {
            if(isVerbose)
            {
                System.out.println(circuitPath.toString());
            }
            
            Circuit circuit = new Circuit(circuitPath.toString(), null);
            circuit.cirSim = new CirSim(circuitPanel.drawingPanel);
            circuit.cirSim.init2();
            
            try {

                circuit.circuitPanel = circuitPanel;
                circuit.isCirSim = true;
                circuitPanel.currentCircuit = circuit;
                
        
                circuitPanel.currentCircuit.cirSim.setSimRunning(false);
                circuitPanel.currentCircuit.cirSim.readSetup(new String(Files.readAllBytes(Paths.get(circuitPath.toString()))).getBytes(), false, false, false);
                        
                add(circuit);
                
                readCount++;
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                
                System.exit(-1);
            }
            
        }
        
        //System.out.println("Read: " + readCount + "/" + list.size());        
    }
    
    
    
    
    public void loadLTspice() throws Exception
    {
        boolean isVerbose = false;
        
        List<Path> list = new ArrayList();
                
        try(Stream<Path> path = Files.find(Paths.get("/home/tkosan/Documents/LTspice/LTspiceXVII/examples/"), 9999, (p, bfa) -> bfa.isRegularFile() && p.getFileName().toString().matches(".*\\.asc")))
        {
            path.forEach(list::add);
        }
        
        
        int readCount = 0;
        
        for(Path ascPath : list)
        {
            //System.out.println(ascPath.toString());
            
            Circuit circuit = new Circuit(ascPath.toString(), null);
            
            try {
                circuit.readAscFormat(ascPath.toString());

                add(circuit);
                
                readCount++;
            }
            catch(UnsupportedOrientationException e)
            {
                if(isVerbose) System.out.println(e.getMessage());
            }
            catch(UnsupportedValueException e)
            {
                if(isVerbose) System.out.println(e.getMessage());
            }
            catch(UnsupportedPrefixException e)
            {
                if(isVerbose) System.out.println(e.getMessage());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                
                System.exit(-1);
            }
        }
        
        System.out.println("Read: " + readCount + "/" + list.size());
    }
    
    
    public static void main(String[] args) throws Exception
    {
        
        Circuits circuits = new Circuits();
        
        
        // ============================ CircuitsJS circuits ==============
        
        // circuits.loadCircuitsJS();
        
        
        
 /*       
        // Search for the largest resistor value.
        double largestResistance = 0.0d;
        Circuit hasLargestResistor = null;
            
            
        for(Circuit circuit : circuits.circuits.values())
        {
            List<CircuitElm> elmList = circuit.cirSim.elmList;

            for(CircuitElm elm : elmList)
            {
                if(elm instanceof ResistorElm)
                {
                    ResistorElm relm = (ResistorElm) elm;
                    
                    if(relm.resistance > largestResistance)
                    {
                        largestResistance = relm.resistance;
                        hasLargestResistor = circuit;
                    }
                }
            }
            //circuit.cirSim.nodeList;
        }
        
        System.out.println(largestResistance + ", " + hasLargestResistor.id);
     */   
        
        // ============================ LTspice circuits ==============
        
        //circuits.loadLTspice();
        
        /*
        
        // Display circuits as they are searched
        
        JFrame jFrame = new JFrame();

        CircuitPanel circuitPanel = new CircuitPanel();
        circuitPanel.drawingPanel.isDrawGrid = false;
        circuitPanel.showGridCheckBox.setSelected(false);
        circuitPanel.drawingPanel.viewScale = 1;
        
        jFrame.getContentPane().add(circuitPanel);
        
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        
        JOptionPane.showInputDialog("");
        
        int count = 0;
        
        for(Circuit circuit : circuits.circuits.values())
        {
            int lowestX = 5000;
            int lowestY = 5000;
            
            for(Terminal terminal : circuit.terminals.values())
            {
                if(terminal.getX() < lowestX)
                {
                    lowestX = terminal.getX();
                }
                
                if(terminal.getY() < lowestY)
                {
                    lowestY = terminal.getY();
                }
            }
            
            for(Terminal terminal : circuit.terminals.values())
            {
                terminal.setX(terminal.getX() - lowestX);
                terminal.setY(terminal.getY() - lowestY);
            }
            
            
            
            
            
            circuit.circuitPanel = circuitPanel;
            circuit.isCirSim = false;
            circuitPanel.currentCircuit = circuit;
            circuitPanel.drawingPanel.repaint();
            
            
            Thread.sleep(1000);
            
            if(count++ == 20)
            {
                break;
            }
        }
        */
        

        
        /*
        // Find the circuit with the greatest number of resistors.
        int totalResistors = 0;
        String circuitPath = "";
        
        for(Circuit circuit : circuits.circuits.values())
        {
            int resistorCount = 0;
        
            for(Component component : circuit.components.values())
            {
                if(component instanceof Resistor)
                {
                    resistorCount++;
                }
            }
        
            if(resistorCount > totalResistors)
            {
                totalResistors = resistorCount;
                circuitPath = circuit.id;
            }
        }
        System.out.println(totalResistors + ", " + circuitPath);
        */
        
        
        
        /*
        // Find the circuit with the greatest resistor value.
        double highestResistorValue = 0;

        String circuitPath = "";
        
        for(Circuit circuit : circuits.circuits.values())
        {

        
            for(Component component : circuit.components.values())
            {
                if(component instanceof Resistor)
                {
                    Resistor resistor = (Resistor) component;
                
                    if(resistor.getPrimaryValue() > highestResistorValue)
                    {
                        highestResistorValue = resistor.getPrimaryValue();
                        circuitPath = circuit.id;
                    }
                }
            }
        

        }
        System.out.println(highestResistorValue + ", " + circuitPath);
        */
        
        System.exit(0);
    }
}
