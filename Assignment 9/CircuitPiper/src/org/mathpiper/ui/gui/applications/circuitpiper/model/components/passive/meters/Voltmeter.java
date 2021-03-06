package org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters;

import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import org.mathpiper.ui.gui.applications.circuitpiper.view.ScaledGraphics;

/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */

public final class Voltmeter extends Meter {

    private static String voltageStringInitial = "??? V";
    protected String voltageString = voltageStringInitial;

    public Voltmeter(int x, int y, String uid, Circuit circuit) {
        
        super(x, y, circuit);
        
        if(uid == null)
        {
            componentSubscript = componentCounter++ + "";
        }
        else
        {
            componentSubscript = uid;
            
            try{
                int number = Integer.parseInt(uid);
        
                if(number >= componentCounter)
                {
                    componentCounter = number + 1;
                }
            }
            catch (NumberFormatException nfe)
            {
            }
        }
        primary = "voltage";
        primaryUnitSymbol = "V";
    }
    
    public void reset() {
        super.reset();
        setVoltageString(Voltmeter.voltageStringInitial);
    }

    public void draw(ScaledGraphics sg) throws Exception {
        super.draw(sg);
        int x1 = getHeadTerminal().getX();
        int x2 = getTailTerminal().getX();
        int y1 = getHeadTerminal().getY();
        int y2 = getTailTerminal().getY();
        int rise = y2 - y1;
        int run = x2 - x1;
        double distance = Math.sqrt(rise * rise + run * run);
        double xm = (x1 + x2) / 2;
        double ym = (y1 + y2) / 2;
        if (distance >= 2 * METER_SIZE) {
            sg.drawLine(x1, y1,  (x1 + run / 2.0 - METER_SIZE * run / distance),  (y1 + rise / 2 - METER_SIZE * rise / distance));
            sg.drawLine( (x2 - run / 2.0 + METER_SIZE * run / distance),  (y2 - rise / 2.0 + METER_SIZE * rise / distance), x2, y2);
            sg.drawOval( (xm - METER_SIZE),  (ym - METER_SIZE), 2 * METER_SIZE, 2 * METER_SIZE);
            sg.drawString("+",  (x1 + run / 2.0 - (METER_SIZE + 10) * run / distance - 10 * rise / distance),
                     (y1 + rise / 2 - (METER_SIZE + 10) * rise / distance + 10 * run / distance));
        } else {
            sg.drawOval( (xm - distance / 2),  (ym - distance / 2),  distance,  distance);
        }
    }

    public String getVoltageString() {
        return voltageString;
    }

    public void setVoltageString(String voltageString) {
        this.voltageString = voltageString;
    }
}
