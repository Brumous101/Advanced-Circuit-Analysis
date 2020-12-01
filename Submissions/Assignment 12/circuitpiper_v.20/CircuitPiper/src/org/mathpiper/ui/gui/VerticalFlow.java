package org.mathpiper.ui.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;


// Obtained from https://stackoverflow.com/questions/8196530/java-vertical-flowlayout-with-horizontal-scrolling/8202614#8202614

public class VerticalFlow extends JPanel implements LayoutManager{
    
    private List<Integer> columnLeftSides = new ArrayList<>();
    
    private int width = 0;
    private int height = 1000;

    public static final int TOP = 0;
    public static final int CENTER = 1;
    public static final int BOTTOM = 2;

    private List<JComponent> componentList = new ArrayList<>();
    private int align = TOP;
    private int hgap = 3;
    private int vgap = 0;
    
    public VerticalFlow()
    {
        this(1000);
    }
    
    public VerticalFlow(double height)
    {
        this.height = (int) Math.round(height);
        this.setLayout(this);
        this.setBackground(Color.white);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Rectangle bounds = g.getClipBounds();
        
        for(Integer x : columnLeftSides)
        {
            g.drawLine(x, bounds.y, x, bounds.height);
        }
    }

    /**
     * Adds the specified component to the layout.
     * Not used by this class.
     * @param name the name of the component
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Removes the specified component from the layout.
     * Not used by this class.
     * @param comp the component to remove
     * @see    java.awt.Container#removeAll
     */
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Returns the preferred dimensions for this layout given the
     * <i>visible</i> components in the specified target container.
     *
     * @param target the container that needs to be laid out
     * @return  the preferred dimensions to lay out the
     *          subcomponents of the specified container
     * @see Container
     * @see #minimumLayoutSize
     * @see    java.awt.Container#getPreferredSize
     */
    public Dimension preferredLayoutSize(Container target)
    {
        synchronized (target.getTreeLock())
        {
            Dimension preferredDimension = new Dimension(0, 0);
            
            int componentsCount = target.getComponentCount();
            
            boolean firstVisibleComponent = true;

            for (int i = 0 ; i < componentsCount ; i++)
            {
                Component component = target.getComponent(i);

                if (component.isVisible())
                {
                    Dimension componentDimension = component.getPreferredSize();
                    preferredDimension.width = Math.max(preferredDimension.width, componentDimension.width);

                    if (firstVisibleComponent)
                    {
                        firstVisibleComponent = false;
                    }
                    else
                    {
                        preferredDimension.height += vgap;
                    }

                    preferredDimension.height += componentDimension.height;
                }
            }

            Insets insets = target.getInsets();
            preferredDimension.width += insets.left + insets.right + hgap*2;
            preferredDimension.height += insets.top + insets.bottom + vgap*2;

            return preferredDimension;
        }
    }
    


    /**
     * Returns the minimum dimensions needed to layout the <i>visible</i>
     * components contained in the specified target container.
     * @param target the container that needs to be laid out
     * @return  the minimum dimensions to lay out the
     *          subcomponents of the specified container
     * @see #preferredLayoutSize
     * @see    java.awt.Container
     * @see    java.awt.Container#doLayout
     */
    public Dimension minimumLayoutSize(Container target)
    {
        synchronized (target.getTreeLock())
        {
            Dimension minimumDimension = new Dimension(0, 0);
            int nmembers = target.getComponentCount();
            boolean firstVisibleComponent = true;

            for (int i = 0 ; i < nmembers ; i++)
            {
                Component component = target.getComponent(i);
                
                if (component.isVisible())
                {
                    Dimension componentDimension = component.getMinimumSize();
                    
                    minimumDimension.width = Math.max(minimumDimension.width, componentDimension.width);

                    if (firstVisibleComponent)
                    {
                        firstVisibleComponent = false;
                    }
                    else
                    {
                        minimumDimension.height += vgap;
                    }

                    minimumDimension.height += componentDimension.height;
                }
            }


            Insets insets = target.getInsets();
            minimumDimension.width += insets.left + insets.right + hgap*2;
            minimumDimension.height += insets.top + insets.bottom + vgap*2;

            return minimumDimension;
        }
    }
    

    
    public Dimension getPreferredSize() {
        //arg1 sets the horizontal scrollbar trigger position.
        //arg2 sets the vertical scrollbar's trigger position and the .png's height.
        return new Dimension(width, height);
    }

    /**
     * Lays out the container. This method lets each
     * <i>visible</i> component take
     * its preferred size by reshaping the components in the
     * target container in order to satisfy the alignment of
     * this <code>VerticalFlowLayout</code> object.
     *
     * @param target the specified component being laid out
     * @see Container
     * @see    java.awt.Container#doLayout
     */
    public void layoutContainer(Container target)
    {
    synchronized (target.getTreeLock())
    {
        Insets insets = target.getInsets();
        
        //int maxHeight = target.getSize().height - (insets.top + insets.bottom + vgap*2);
        int maxHeight = height;
        
        int componentCount = target.getComponentCount();
        int columnLeftSide = insets.left + hgap;
        int y = 0;
        int columnWidth = 0;
        int start = 0;
        
        columnLeftSides.clear();
        columnLeftSides.add(columnLeftSide - hgap);

        boolean isTargetLeftToRightComponentOrientation = target.getComponentOrientation().isLeftToRight();
        
        List<Component> linesList = new ArrayList<>();

        for (int i = 0 ; i < componentCount ; i++)
        {
            Component component = target.getComponent(i);

            if (component.isVisible())
            {
                Dimension componentSize = component.getPreferredSize();
                
                if (component.getName() != null && component.getName().equals("LINE"))
                {
                    linesList.add(component);
                }
                                
                component.setSize(componentSize.width, componentSize.height);

                if ((y == 0) || ((y + componentSize.height) <= maxHeight))
                {
                    if (y > 0)
                    {
                        y += vgap;
                    }

                    y += componentSize.height;
                    columnWidth = Math.max(columnWidth, componentSize.width);
                }
                else
                {
                    for(Component lineComponent: linesList)
                    {
                        lineComponent.setSize(columnWidth, lineComponent.getSize().height);
                    }
                    linesList.clear();
                    
                    moveComponents(target, columnLeftSide, insets.top + vgap, columnWidth, maxHeight - y, start, i, isTargetLeftToRightComponentOrientation);
                    y = componentSize.height;
                    columnLeftSide += hgap + columnWidth;
                    columnLeftSides.add(columnLeftSide - hgap + ((i == 0) ? 0 : 1));
                    columnWidth = componentSize.width;
                    start = i;
                }
            }

        }
        
        width = columnLeftSide + columnWidth + hgap;
        columnLeftSides.add(width - hgap);
        
        moveComponents(target, columnLeftSide, insets.top + vgap, columnWidth, maxHeight - y, start, componentCount, isTargetLeftToRightComponentOrientation);
    }
    }

    /**
     * Centers the elements in the specified row, if there is any slack.
     * @param target the component which needs to be moved
     * @param x the x coordinate
     * @param y the y coordinate
     * @param columnWidth the width dimensions
     * @param height the height dimensions
     * @param rowStart the beginning of the column
     * @param rowEnd the the ending of the column
     */
    private void moveComponents(
        Container target, int x, int y, int columnWidth, int height, int rowStart, int rowEnd, boolean isTargetLeftToRightComponentOrientation)
    {
        switch (align)
        {
            case TOP:
                y += isTargetLeftToRightComponentOrientation ? 0 : height;
                break;
            case CENTER:
                y += height / 2;
                break;
            case BOTTOM:
                y += isTargetLeftToRightComponentOrientation ? height : 0;
                break;
        }

        for (int i = rowStart ; i < rowEnd ; i++)
        {
            Component component = target.getComponent(i);

            if (component.isVisible())
            {
                int cx;
                cx = x + (columnWidth - component.getSize().width) / 2;

                if (isTargetLeftToRightComponentOrientation)
                {
                    component.setLocation(cx, y);
                }
                else
                {
                    component.setLocation(cx, target.getSize().height - y - component.getSize().height);
                }

                y += component.getSize().height + vgap;
            }
        }
    }

}
