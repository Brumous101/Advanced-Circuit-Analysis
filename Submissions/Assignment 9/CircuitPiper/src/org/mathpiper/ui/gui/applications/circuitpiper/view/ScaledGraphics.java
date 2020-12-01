/* {{{ License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ //}}}
// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:
// Copied from MathPiper (http://mathpiper.org)

package org.mathpiper.ui.gui.applications.circuitpiper.view;

import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;



public class ScaledGraphics {

    private double viewScale = 1;
    public Graphics iG = null;
    private Graphics2D iG2D = null;
    int prevGray = -1;
    float prevSetFontSize = -1;
    FontMetrics metrics = null;

    public ScaledGraphics(Graphics g) {
        iG = g;
        if (g instanceof Graphics2D) {
            iG2D = (Graphics2D) g;
        }
    }

    public void setLineWidth(double aThickness) {
        if (iG2D != null) {
            iG2D.setStroke(new BasicStroke((float) aThickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)); //BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }
    }
    
    public double getLineWidth() {
        if (iG2D != null) {
            BasicStroke stroke = (BasicStroke) iG2D.getStroke();
            return stroke.getLineWidth();
        }
        
        return 1;
    }

    public void drawLine(double x0, double y0, double x1, double y1) {
        iG.drawLine((int) (x0 * viewScale), (int) (y0 * viewScale), (int) (x1 * viewScale), (int) (y1 * viewScale));
    }


    public void drawArc(double x,double y,double width,double height,int startAngle,int arcAngle) {
        //iG.drawLine((int) (x0 * viewScale), (int) (y0 * viewScale), (int) (x1 * viewScale), (int) (y1 * viewScale));
        iG.drawArc((int) (x * viewScale), (int) (y * viewScale), (int) (width * viewScale), (int) (height * viewScale), startAngle, arcAngle);
    }

    public void drawRectangle(double x, double y, double width, double height) {
        iG.drawRect((int) (x * viewScale), (int) (y * viewScale), (int) (width * viewScale), (int) (height * viewScale));
    }
    
    public void drawOval(double x, double y, double width, double height) {
        iG.drawOval((int) (x * viewScale), (int) (y * viewScale), (int) (width * viewScale), (int) (height * viewScale));
    }

    public void fillRect(double x, double y, double width, double height) {
        iG.fillRect((int) (x * viewScale), (int) (y * viewScale), (int) (width * viewScale), (int) (height * viewScale));
    }    
    
    public void fillOval(double x, double y, double width, double height) {
        iG.fillOval((int) (x * viewScale), (int) (y * viewScale), (int) (width * viewScale), (int) (height * viewScale));
    }
    
    public void fillArc(double x,double y,double width,double height,int startAngle,int arcAngle) {
        //iG.drawLine((int) (x0 * viewScale), (int) (y0 * viewScale), (int) (x1 * viewScale), (int) (y1 * viewScale));
        iG.fillArc((int) (x * viewScale), (int) (y * viewScale), (int) (width * viewScale), (int) (height * viewScale), startAngle, arcAngle);
    }
    
    public void fillPolygon(int[] xPoints, int[] yPoints, int numberOfPoints)
    {
        int[] scaledXPoints = new int[numberOfPoints];
        int[] scaledYPoints = new int[numberOfPoints];
        
        for(int index = 0; index < numberOfPoints; index++)
        {
            scaledXPoints[index] = (int) Math.round(xPoints[index] * viewScale);
            scaledYPoints[index] = (int) Math.round(yPoints[index] * viewScale);
        }
        
        iG.fillPolygon(scaledXPoints, scaledYPoints, numberOfPoints);
    }
    
    public void setClip(double x, double y, double width, double height) {
        iG.setClip((int) (x * viewScale), (int) (y * viewScale), (int) (width * viewScale), (int) (height * viewScale));
    }

    public void clip(double x, double y, double width, double height) {
        Rectangle2D r = new Rectangle();
        r.setRect((int) (x * viewScale), (int) (y * viewScale), (int) (width * viewScale), (int) (height * viewScale));
        iG2D.clip(r);
    }
    
    public void setGray(int aGray) {
        if (prevGray != aGray) {
            prevGray = aGray;
            iG.setColor(new Color(aGray, aGray, aGray));
        }
    }

    public void drawString(String text, double x, double y) {
        double normalFontSize = getFontSize();

        double scaledFontSize = normalFontSize;

        setFontSize(scaledFontSize);
        
        iG.drawString(text, (int) (x * viewScale), (int) (y * viewScale));
        
        setFontSize(normalFontSize);
    }

    public void drawScaledString(String text, double x, double y, double scale) {
        double normalFontSize = getFontSize();

        double scaledFontSize = normalFontSize * scale;

        setFontSize(scaledFontSize);

        iG.drawString(text, (int) (x * viewScale), (int) (y * viewScale));

        setFontSize(normalFontSize);

    }
    

    public void setFontSize(double aSize) {
        this.prevSetFontSize = (int) (viewScale * aSize);
        
                        
        Font font = new Font("Monospaced", Font.PLAIN, 12);
        
        iG.setFont(font.deriveFont(this.prevSetFontSize));
        
        if(iG.getFontMetrics() != null)
        {
            metrics = iG.getFontMetrics();
        }
        else
        {
            Canvas canvas = new Canvas();
            metrics = canvas.getFontMetrics(font);
        }
        
    }

    public double getFontSize() {
        return  (prevSetFontSize / viewScale);
    }

    public double getScaledTextWidth(String text) {
        java.awt.geom.Rectangle2D textBoundingRectangle = metrics.getStringBounds(text, iG);
        return  (textBoundingRectangle.getWidth() / viewScale);
    }

    public double getScaledTextHeight(String text) {
        java.awt.geom.Rectangle2D textBoundingRectangle = metrics.getStringBounds(text, iG);
        return  (textBoundingRectangle.getHeight() / viewScale);
    }

    public double getTextWidth(String text) {
        java.awt.geom.Rectangle2D textBoundingRectangle = metrics.getStringBounds(text, iG);
        return  textBoundingRectangle.getWidth();
    }

    public double getTextHeight(String text) {
        java.awt.geom.Rectangle2D textBoundingRectangle = metrics.getStringBounds(text, iG);
        return  textBoundingRectangle.getHeight();
    }

    public double getAscent() {
        return (metrics.getAscent() / viewScale);
    }

    public double getDescent() {
        return  (metrics.getDescent() / viewScale);
    }

    public void setViewScale(double aViewScale) {
        viewScale = aViewScale;
    }

    public void setColor(Color color) {
        if (iG2D != null) {
            iG2D.setColor(color);
        } else if (iG != null) {
            iG.setColor(color);
        }

    }//end method.
    
    public static int fontForSize(int aSize) {

        if (aSize > 3) {
            aSize = 3;
        }

        if (aSize < 0) {
            aSize = 0;
        }

        switch (aSize) {

            case 0:
                return 6;

            case 1:
                return 8;

            case 2:
                return 12;

            case 3:
                return 16;

            default:
                return 16;
        }//end switch.
        
    }//end method.

    public void setRenderingHint(Key a, Object b)
    {
        if(iG2D != null)
        {
            this.iG2D.setRenderingHint(a, b);
        }
    }
    
    public void setPaint(Color color)
    {
        if(iG2D != null)
        {
            this.iG2D.setPaint(color);
        }
    }

    public void fill(Shape shape)
    {   
        if(iG2D != null)
        {
            Rectangle rect = shape.getBounds();
        
            iG2D.fill(new Arc2D.Double(rect.getX() * viewScale, rect.getY() * viewScale, rect.getWidth() * viewScale, rect.getHeight() * viewScale, 0, 360, Arc2D.CHORD));
        }
    }
    
    public void draw(Shape shape)
    {   
        if(iG2D != null)
        {
            Rectangle rect = shape.getBounds();
            iG2D.draw(new Arc2D.Double(rect.getX() * viewScale, rect.getY() * viewScale, rect.getWidth() * viewScale, rect.getHeight() * viewScale, 0, 360, Arc2D.CHORD));
        }
    }
    
    public void setStroke(Stroke stroke)
    {
        if(iG2D != null)
        {
            iG2D.setStroke(stroke);
        }
    }
    
    public void rotate(double angle)
    {
        if(iG2D != null)
        {
            iG2D.rotate(angle);
        }
    }
    
    public void rotate(double arg0, double arg1, double arg2)
    {
        if(iG2D != null)
        {
            iG2D.rotate(arg0, arg1, arg2);
        }
    }
    
    public void translate(double x, double y)
    {
        iG2D.translate(x, y);
    }
    
    public void scale(double x, double y)
    {
        if(iG2D != null)
        {
            iG2D.scale(x, y);
        }
    }
    
    public void setTransform(AffineTransform transform)
    {
        if(iG2D != null)
        {
            iG2D.setTransform(transform);
        }
    }
    
    public Font getFont()
    {
        if(iG2D != null)
        {
            return iG2D.getFont();
        }
        else
        {
            return null;
        }
    }
    
}
