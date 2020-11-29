package org.mathpiper.ui.gui.applications.circuitpiper.view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;


public class ScaledGraphicsBounds extends ScaledGraphics {
    private double viewScale = 1;
    public Graphics iG = null;
    private Graphics2D iG2D = null;
    int prevGray = -1;
    float prevSetFontSize = -1;
    FontMetrics metrics = null;
    
    private double leftMostX = Double.POSITIVE_INFINITY;
    private double rightMostX = 0;
    private double topMostY = Double.POSITIVE_INFINITY;
    private double bottomMostY = 0;
    private double border = 4.0; //3.0;

    public ScaledGraphicsBounds(Graphics g) {
        super(g);
        iG = g;
        if (g instanceof Graphics2D) {
            iG2D = (Graphics2D) g;
        }
    }
    
    public Rectangle2D getBounds()
    {
        return new Rectangle2D.Double(leftMostX - border, topMostY - border, (rightMostX - leftMostX) + border, (bottomMostY - topMostY) + border);
    }
    
    public Rectangle2D getClip()
    {
        return new Rectangle2D.Double(leftMostX - border, topMostY - border, rightMostX + border, bottomMostY + border);
    }

    public void setLineWidth(double aThickness) {
    }
    
    public double getLineWidth() {
        return 1;
    }

    private void adjustBounds(double x0, double y0, double x1, double y1)
    {   
        if(x0 < leftMostX)
        {
            leftMostX = x0;
        }
        
        if(x0 > rightMostX)
        {
            rightMostX = x0;
        }
        
        if(x1 < leftMostX)
        {
            leftMostX = x1;
        }
        
        if(x1 > rightMostX)
        {
            rightMostX = x1;
        }
        
        //
        
        if(y0 < topMostY)
        {
            topMostY = y0;
        }
        
        if(y0 > bottomMostY)
        {
            bottomMostY = y0;
        }
        
        if(y1 < topMostY)
        {
            topMostY = y1;
        }
        
        if(y1 > bottomMostY)
        {
            bottomMostY = y1;
        }
        
        
        //System.out.println("XXX lmx: " + Math.round(leftMostX) + ", rmx:" + Math.round(rightMostX) + ", tmy: " + Math.round(topMostY) + ", bmy: " + Math.round(bottomMostY) + ", x0: " + Math.round(x0) + ", x1: " + Math.round(x1) + ", y0: " + Math.round(y0) + ", y1: " + Math.round(y1));
    }
    
    public void drawLine(double x0, double y0, double x1, double y1) {
        adjustBounds(x0, y0, x1, y1);
    }

    public void drawArc(double x,double y,double width,double height,int startAngle,int arcAngle) {
        adjustBounds(x, y, x + width, y + height);
    }

    public void drawRectangle(double x, double y, double width, double height) {
        adjustBounds(x, y, x + width, y + height);
    }
    
    public void drawOval(double x, double y, double width, double height) {
        adjustBounds(x, y, x + width, y + height);
    }

    public void fillRect(double x, double y, double width, double height) {
        adjustBounds(x, y, x + width, y + height);
    }    
    
    public void fillOval(double x, double y, double width, double height) {
        adjustBounds(x, y, x + width, y + height);
    }
    
    public void fillArc(double x,double y,double width,double height,int startAngle,int arcAngle) {
        adjustBounds(x, y, x + width, y + height);
    }
    
    public void fillPolygon(int[] xPoints, int[] yPoints, int numberOfPoints)
    {
        for(int index = 0; index < numberOfPoints; index++)
        {
            adjustBounds(xPoints[index], yPoints[index], xPoints[index], yPoints[index]);
        }
    }
    
    public void setClip(double x, double y, double width, double height) {
    }

    public void clip(double x, double y, double width, double height) {

    }
    
    public void setGray(int aGray) {

    }

    public void drawString(String text, double x, double y) {

        if(! text.equals(""))
        {
            double textWidth = stringWidth(text);
            double maxAscent = getMaxAscent();
            double maxDescent = getMaxDescent();

            adjustBounds(x, y - maxAscent, x + textWidth, y + maxDescent);
        }
    }

    public void drawScaledString(String text, double x, double y, double scale) {
        drawString(text, x, y);
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
        return  getTextWidth(text);
    }

    public double getScaledTextHeight(String text) {
        return  getTextHeight(text);
    }

    public double getTextWidth(String text) {
        java.awt.geom.Rectangle2D textBoundingRectangle = metrics.getStringBounds(text, iG);
        return  textBoundingRectangle.getWidth();
    }

    public double getTextHeight(String text) {
        java.awt.geom.Rectangle2D textBoundingRectangle = metrics.getStringBounds(text, iG);
        return  textBoundingRectangle.getHeight();
    }
    
    public int stringWidth(String text)
    {
        int adjustment = 4; //todo:tk:compensate for clipping of the righmost character in the string.
        return metrics.stringWidth(text) + adjustment;
    }

    public double getAscent() {
        return (metrics.getAscent());
    }

    public double getDescent() {
        return  (metrics.getDescent());
    }
    
    public double getMaxAscent() {
        return (metrics.getMaxAscent());
    }

    public double getMaxDescent() {
        return  (metrics.getMaxDescent());
    }

    public void setViewScale(double aViewScale) {

    }

    public void setColor(Color color) {

    }
    
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
    }
    
    public void setPaint(Color color)
    {
    }

    public void fill(Shape shape)
    {   
        Rectangle rect = shape.getBounds();
        
        adjustBounds(rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());
    }
    
    public void draw(Shape shape)
    {   
        Rectangle rect = shape.getBounds();
        
        adjustBounds(rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());
    }
    
    public void setStroke(Stroke stroke)
    {
    }
    
    public void rotate(double angle)
    {
    }
    
    public void rotate(double arg0, double arg1, double arg2)
    {
    }
    
    public void translate(double x, double y)
    {
    }
    
    public void scale(double x, double y)
    {
    }
    
    public void setTransform(AffineTransform transform)
    {
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

    public double getLeftMostX()
    {
        return leftMostX;
    }

    public double getTopMostY()
    {
        return topMostY;
    }
    
    public double getBorder()
    {
        return border;
    }
}
