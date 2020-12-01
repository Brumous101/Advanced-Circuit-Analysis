package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements;

import java.util.Date;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.EditInfo;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Font;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Graphics;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.Point;
import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.StringTokenizer;

public class DataRecorderElm extends CircuitElm {

    int dataCount, dataPtr;
    double data[];
    boolean dataFull;

    public DataRecorderElm(int xx, int yy) {
        super(xx, yy);
        setDataCount(10240);
    }

    public DataRecorderElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        setDataCount(Integer.parseInt(st.nextToken()));
    }

    public String dump() {
        return super.dump() + " " + dataCount;
    }

    int getDumpType() {
        return 210;
    }

    public int getPostCount() {
        return 1;
    }

    public void reset() {
        dataPtr = 0;
        dataFull = false;
    }

    public void setPoints() {
        super.setPoints();
        lead1 = new Point();
    }

    public void draw(Graphics g) {
        boolean selected = (needsHighlight());
        Font f = new Font("SansSerif", selected ? Font.BOLD : 0, 14);
        g.setFont(f);
        g.setColor(selected ? selectColor : whiteColor);
        String s = "export";
        interpPoint(point1, point2, lead1, 1 - ((int) g.context.measureText(s).getWidth() / 2 + 8) / dn);
        setBbox(point1, lead1, 0);
        drawCenteredText(g, s, x2, y2, true);
        setVoltageColor(g, nodeVoltages[0]);
        if (selected) {
            g.setColor(selectColor);
        }
        drawThickLine(g, point1, lead1);
        drawPosts(g);
    }

    public double getVoltageDiff() {
        return nodeVoltages[0];
    }

    public void getInfo(String arr[]) {
        arr[0] = "data export";
        arr[1] = "V = " + getVoltageText(nodeVoltages[0]);
        arr[2] = (dataFull ? dataCount : dataPtr) + "/" + dataCount;
    }

    public void stepFinished() {
        data[dataPtr++] = nodeVoltages[0];
        if (dataPtr >= dataCount) {
            dataPtr = 0;
            dataFull = true;
        }
    }

    void setDataCount(int ct) {
        dataCount = ct;
        data = new double[dataCount];
        dataPtr = 0;
        dataFull = false;
    }

    public static final native String getBlobUrl(String data) /*-{
                var datain=[""];
                datain[0]=data;
		var oldblob = $doc.recorderBlob;
		// remove old blob if any.  We should do this when dialog is dismissed, but this is easier
		if (oldblob)
		    URL.revokeObjectURL(oldblob);
                var blob=new Blob(datain, {type: 'text/plain' } );
                var url = URL.createObjectURL(blob);
                $doc.recorderBlob = url;
                return url;
        }-*/;

    public EditInfo getEditInfo(int n) {
        if (n == 0) {
            EditInfo ei = new EditInfo("# of Data Points", dataCount, -1, -1).setDimensionless();
            return ei;
        }
        if (n == 1) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            String dataStr = "# time step = " + sim.timeStep + " sec\n";
            int i;
            if (dataFull) {
                for (i = 0; i != dataCount; i++) {
                    dataStr += data[(i + dataPtr) % dataCount] + "\n";
                }
            } else {
                for (i = 0; i != dataPtr; i++) {
                    dataStr += data[i] + "\n";
                }
            }
            String url = getBlobUrl(dataStr);
            Date date = new Date();
            ///DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMdd-HHmm");
            ///String fname = "data-"+ dtf.format(date) + ".circuitjs.txt";
            ///Anchor a=new Anchor(fname, url);
            ///a.getElement().setAttribute("Download", fname);
            ///ei.widget = a;
            return ei;
        }
        return null;
    }

    public void setEditValue(int n, EditInfo ei) {
        if (n == 0 && ei.value > 0) {
            setDataCount((int) ei.value);
        }
        if (n == 1) {
            return;
        }
    }
}