package org.mathpiper.ui.gui.applications.circuitpiper.model.components.passive.meters;

import org.mathpiper.ui.gui.applications.circuitpiper.model.Circuit;
import org.mathpiper.ui.gui.applications.circuitpiper.model.components.Component;
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

public abstract class Meter extends Component {

    public static int METER_SIZE = 27;
    
    public static int componentCounter = 1;

    public Meter(int x, int y, Circuit circuit) {
        super(x, y, circuit);
        componentSubscript = componentCounter++ + "";
        primarySymbol = "M";
    }
    
    public void reset()
    {
        setSecondaryValue(0);
        setFullValue("");
        setCalculatedValue((Double) 0.0);
    }

    public void draw(ScaledGraphics sg) throws Exception {
        super.draw(sg);
        int x1 = getHeadTerminal().getX();
        int x2 = getTailTerminal().getX();
        int y1 = getHeadTerminal().getY();
        int y2 = getTailTerminal().getY();
        double xm = (x1 + x2) / 2;
        double ym = (y1 + y2) / 2;
        sg.drawString("Meter",  xm - 15,  ym + 15);
    }
    
    public int getLabelDistance()
    {
        return 30;
    }
}
