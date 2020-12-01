
package org.mathpiper.ui.gui.applications.circuitpiper.model.numericalanalysis;

import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ACCurrentSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.active.ACVoltageSource;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Capacitor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.Inductor;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.CurrentIntegrator;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters.VoltageIntegrator;
import org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra.SparseMatrix;
import org.mathpiper.ui.gui.applications.circuitpiper.view.CircuitPanel;

public class RungeKutta {
    
    private CircuitPanel circuitPanel;
    public SparseMatrix mainMatrix;
    private  double stepSize;
    
    public RungeKutta(CircuitPanel circuitPanel)
    {
        this.circuitPanel = circuitPanel;
    }
    
    public double rk(double h, double origh, double time) throws Exception {

        for (Component ec : circuitPanel.currentCircuit.components.values()) {
            if ((!(ec instanceof Capacitor) && !(ec instanceof Inductor)) || ec.getPrimaryValue() == null || ec.isIsConstant()) {
                if (!(ec instanceof CurrentIntegrator) && 
                    !(ec instanceof VoltageIntegrator) && 
                    !(ec instanceof ACVoltageSource) && 
                    !(ec instanceof ACCurrentSource)) {
                    continue;
                }
            }
            ec.originalValue = ec.getSecondaryValue();
        }
        
        rk2(h / 2, false, time);
        
        for (Component ec : circuitPanel.currentCircuit.components.values()) {
            if ((!(ec instanceof Capacitor) && !(ec instanceof Inductor)) || ec.getPrimaryValue() == null || ec.isIsConstant()) {
                if (!(ec instanceof CurrentIntegrator) && 
                    !(ec instanceof VoltageIntegrator) && 
                    !(ec instanceof ACVoltageSource) && 
                    !(ec instanceof ACCurrentSource)) {
                    continue;
                }
            }
            ec.originalCurrent = ec.I1;
        }
        
        rk2(h / 2, false, time + h / 2);
        
        for (Component ec : circuitPanel.currentCircuit.components.values()) {
            if ((!(ec instanceof Capacitor) && !(ec instanceof Inductor)) || ec.getPrimaryValue() == null || ec.isIsConstant()) {
                if (!(ec instanceof CurrentIntegrator) && 
                    !(ec instanceof VoltageIntegrator) && 
                    !(ec instanceof ACVoltageSource) && 
                    !(ec instanceof ACCurrentSource)) {
                    continue;
                }
            }
            ec.twoStepValue = ec.y2;
            ec.setSecondaryValue(ec.originalValue);
        }
        
        rk2(h, false, time);
        
        for (Component ec : circuitPanel.currentCircuit.components.values()) {
            if ((!(ec instanceof Capacitor) && !(ec instanceof Inductor)) || ec.getPrimaryValue() == null || ec.isIsConstant()) {
                if (!(ec instanceof CurrentIntegrator) && 
                    !(ec instanceof VoltageIntegrator) && 
                    !(ec instanceof ACVoltageSource) && 
                    !(ec instanceof ACCurrentSource)) {
                    continue;
                }
            }
            ec.oneStepValue = ec.y2;
            ec.setSecondaryValue(ec.originalValue);
        }
        
        double minh = h;
        for (Component ec : circuitPanel.currentCircuit.components.values()) {
            if ((!(ec instanceof Capacitor) && !(ec instanceof Inductor)) || ec.getPrimaryValue() == null || ec.isIsConstant()) {
                if (!(ec instanceof CurrentIntegrator) && 
                    !(ec instanceof VoltageIntegrator) && 
                    !(ec instanceof ACVoltageSource) && 
                    !(ec instanceof ACCurrentSource)) {
                    continue;
                }
            }
            if (ec.originalValue == ec.twoStepValue) {
                //ec.isConstant=true;  #commented out dec 21 08
                //System.out.println("=");
                //continue;  //##$$removed dec 21 08 testing
            }
            double delta0;
            
            if (ec instanceof Capacitor || 
                ec instanceof CurrentIntegrator || 
                ec instanceof ACCurrentSource) {
                delta0 = circuitPanel.capacitorError;
            } else if (ec instanceof Inductor || ec instanceof ACVoltageSource) {
                delta0 = circuitPanel.inductanceError;//error allowed in magnetic flux
            } else {//voltage integrator
                delta0 = circuitPanel.inductanceError;//error allowed in magnetic flux
            }
            //double delta0=Math.max(circuitEnginePanel.capacitorError*h*Math.abs(h*ec.originalCurrent),
            //                      circuitEnginePanel.inductanceError);
            // *((//Math.abs(ec.twoStepValue)+
            //   Math.abs(h*ec.originalCurrent)))
            // +circuitEnginePanel.minimumAbsoluteError
            //   ;//was 10^-30
            if (ec instanceof Capacitor || ec instanceof CurrentIntegrator) {
                //delta0=Math.min(delta0,0.000001);
            }
            
            ec.delta1 = ec.twoStepValue - ec.oneStepValue;
            
            if (ec.delta1 != 0) {//##added Dec 31 2008
                minh = Math.min(minh,
                        0.9 * h * Math.pow(//.9
                                delta0
                                / Math.abs(ec.delta1) //Math.abs(0.000000000000000001/(ec.oneStepValue-ec.twoStepValue))
                                , 0.25));
            }
            if (minh < h) {
                //todo:tk:recursive use of rk.
                time = rk(Math.min(0.5 * minh, h), h, time);
                //System.out.println("recursive rk");
                return time;
            }
            //System.out.println(minh);
        }
        
        for (Component ec : circuitPanel.currentCircuit.components.values()) {
            if ((!(ec instanceof Capacitor) && !(ec instanceof Inductor)) || ec.getPrimaryValue() == null || ec.isIsConstant()) {
                if (!(ec instanceof CurrentIntegrator) && !(ec instanceof VoltageIntegrator)) {
                    continue;
                }
            }
            
            ec.setSecondaryValue(ec.twoStepValue + ec.delta1 / 15);
            //System.out.println("delta1: "+ec.delta1);
            //System.out.println("twoStepValue: "+ec.twoStepValue);
            //System.out.println("oneStepValue: "+ec.oneStepValue);
        }
        //rk2(minh,false);
        time += h;
        
        stepSize = h;
        
        return time;
    }


    public void rk2(double h, boolean reset, double time) throws Exception {
        mainMatrix = new SparseMatrix(circuitPanel,
                circuitPanel.currentCircuit.terminals.values().size()
                + 3 * circuitPanel.currentCircuit.components.size()//max 3 equations
                ,
                2 * circuitPanel.currentCircuit.terminals.values().size() +//v and v'
                3 * circuitPanel.currentCircuit.components.size()
                + 1);
        
        // k1.
        mainMatrix.setUpMainMatrix(time);
        mainMatrix.rowEchelonFormMatrix(mainMatrix);
        mainMatrix.rowReducedEchelonFormMatrix(mainMatrix);
        for (Component ec : circuitPanel.currentCircuit.components.values()) {
            if ((!(ec instanceof Capacitor) && !(ec instanceof Inductor)) || ec.getPrimaryValue() == null || ec.isIsConstant()) {
                if (!(ec instanceof CurrentIntegrator) && 
                    !(ec instanceof VoltageIntegrator) && 
                    !(ec instanceof ACVoltageSource) && 
                    !(ec instanceof ACCurrentSource)) {
                    continue;
                }
            }
            
            if (ec instanceof Capacitor || ec instanceof CurrentIntegrator
                    || ec instanceof ACCurrentSource) {
                ec.k1 = mainMatrix.getCurrent(ec);
                //System.out.println("k1:"+ec.k2);
            } else if (ec instanceof Inductor) {
                ec.k1 = ec.getPrimaryValue() * mainMatrix.getCurrentPrime(ec);
            } else {//voltage integrator or ACVoltageSource
                ec.k1 = mainMatrix.getComponentDeltaV(ec);
            }
            
            ec.I1 = ec.k1;
            ec.y1 = ec.getSecondaryValue();
            ec.setSecondaryValue(ec.y1 + h / 2 * ec.k1);
        }
        
        // k2.
        mainMatrix.rows.clear();
        mainMatrix.columns.clear();
        mainMatrix.setUpMainMatrix(time + h / 2);//added dec 21 08
        mainMatrix.rowEchelonFormMatrix(mainMatrix);
        mainMatrix.rowReducedEchelonFormMatrix(mainMatrix);
        for (Component ec : circuitPanel.currentCircuit.components.values()) {
            if ((!(ec instanceof Capacitor) && !(ec instanceof Inductor)) || ec.getPrimaryValue() == null || ec.isIsConstant()) {
                if (!(ec instanceof CurrentIntegrator) && 
                    !(ec instanceof VoltageIntegrator) && 
                    !(ec instanceof ACVoltageSource) && 
                    !(ec instanceof ACCurrentSource)) {
                    continue;
                }
            }
            if (ec instanceof Capacitor || ec instanceof CurrentIntegrator
                    || ec instanceof ACCurrentSource) {
                ec.k2 = mainMatrix.getCurrent(ec);
                //System.out.println("k2:"+ec.k2);
            } else if (ec instanceof Inductor) {
                ec.k2 = ec.getPrimaryValue() * mainMatrix.getCurrentPrime(ec);
            } else {//voltage integrator or ACVoltageSource
                ec.k2 = mainMatrix.getComponentDeltaV(ec);
            }
            ec.setSecondaryValue(ec.y1 + h / 2 * ec.k2);
        }
        
        // k3. 
        mainMatrix.rows.clear();
        mainMatrix.columns.clear();
        mainMatrix.setUpMainMatrix(time + h / 2);//added dec 21 08
        mainMatrix.rowEchelonFormMatrix(mainMatrix);
        mainMatrix.rowReducedEchelonFormMatrix(mainMatrix);
        for (Component ec : circuitPanel.currentCircuit.components.values()) {
            if ((!(ec instanceof Capacitor) && !(ec instanceof Inductor)) || ec.getPrimaryValue() == null || ec.isIsConstant()) {
                if (!(ec instanceof CurrentIntegrator) && !(ec instanceof VoltageIntegrator)
                        && !(ec instanceof ACVoltageSource) && !(ec instanceof ACCurrentSource)) {
                    continue;
                }
            }
            
            if (ec instanceof Capacitor || ec instanceof CurrentIntegrator
                    || ec instanceof ACCurrentSource) {
                ec.k3 = mainMatrix.getCurrent(ec);
                //System.out.println("k3:"+ec.k3);
            } else if (ec instanceof Inductor) {
                ec.k3 = ec.getPrimaryValue() * mainMatrix.getCurrentPrime(ec);
            } else {//voltage integrator or ACVoltageSource
                ec.k3 = mainMatrix.getComponentDeltaV(ec);
            }
            ec.setSecondaryValue(ec.y1 + h * ec.k3);
        }
        
        // k4.
        mainMatrix.rows.clear();
        mainMatrix.columns.clear();
        mainMatrix.setUpMainMatrix(time + h);//added dec 21 08
        mainMatrix.rowEchelonFormMatrix(mainMatrix);
        mainMatrix.rowReducedEchelonFormMatrix(mainMatrix);
        for (Component ec : circuitPanel.currentCircuit.components.values()) {
            if ((!(ec instanceof Capacitor) && !(ec instanceof Inductor)) || ec.getPrimaryValue() == null || ec.isIsConstant()) {
                if (!(ec instanceof CurrentIntegrator) && 
                    !(ec instanceof VoltageIntegrator) && 
                    !(ec instanceof ACVoltageSource) && 
                    !(ec instanceof ACCurrentSource)) {
                    continue;
                }
            }
            
            if (ec instanceof Capacitor || ec instanceof CurrentIntegrator
                    || ec instanceof ACCurrentSource) {
                ec.k4 = mainMatrix.getCurrent(ec);
                //System.out.println("k4:"+ec.k4);
            } else if (ec instanceof Inductor) {
                ec.k4 = ec.getPrimaryValue() * mainMatrix.getCurrentPrime(ec);
            } else {//voltage integrator or ACVoltageSource
                ec.k4 = mainMatrix.getComponentDeltaV(ec);
            }
            
            ec.y2 = ec.y1 + h * (ec.k1 + 2 * ec.k2 + 2 * ec.k3 + ec.k4) / 6.0;
            //ec.secondaryValue=ec.y2;
            if (reset) {
                ec.setSecondaryValue(ec.y1);
            }
        }
    }
    
    public double getStepSize()
    {
        return stepSize;
    }

}
