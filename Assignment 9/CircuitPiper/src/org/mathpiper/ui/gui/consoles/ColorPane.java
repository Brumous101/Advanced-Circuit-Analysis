package org.mathpiper.ui.gui.consoles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

public class ColorPane extends JTextPane {
    
    private boolean isWordWrap = false;

    public ColorPane() {
        super();
        //this.getDocument().putProperty("i18n", Boolean.FALSE);
        //this.getDocument().putProperty("ZOOM_FACTOR", new Double(zoomScale));
        this.setDocument(new MathPiperDocument());
    }
    
    // Disable word wrapping https://stackoverflow.com/questions/7156038/jtextpane-line-wrapping
    @Override
    public boolean getScrollableTracksViewportWidth() {
        if(isWordWrap)
        {
            return super.getScrollableTracksViewportWidth();
        }
        else
        {
            
            // Only track viewport width when the viewport is wider than the preferred width
            return getUI().getPreferredSize(this).width 
                <= getParent().getSize().width;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if(isWordWrap)
        {
            return super.getPreferredSize();
        }
        else
        {
            // Avoid substituting the minimum width for the preferred width when the viewport is too narrow
            return getUI().getPreferredSize(this);
        }
    }
    
    public void append(String s)
    {
        append(Color.BLACK, s);
    }

    public void append(Color c, String s) { // better implementation--uses // StyleContext.
        MutableAttributeSet attrs = getInputAttributes();
        //attrs.removeAttribute("size");
        //SimpleAttributeSet attrs = new SimpleAttributeSet();
        //StyleConstants.setFontSize(attrs, fontSize);

        //StyleConstants.setFontSize(attrs, fontSize);
        StyleConstants.setForeground(attrs, c);

        int len = getDocument().getLength(); // same value as // getText().length();
        setCaretPosition(len); // place caret at the end (with no selection).
        setCharacterAttributes(attrs, false);


        /*try {
            this.getDocument().insertString(this.getCaretPosition(), s, attrs);
            } catch (BadLocationException e) {
            }*/
        replaceSelection(s); // there is no selection, so inserts at caret.
    }//end method.

    public void insert(Color c, String str, int pos) {

        MutableAttributeSet attrs = getInputAttributes();
        //attrs.removeAttribute(StyleConstants.FontSize);
        //SimpleAttributeSet attrs = new SimpleAttributeSet();

        //StyleConstants.setFontSize(attrs, fontSize);
        //StyleConstants.setFontFamily(attrs, font.getFamily());
        //StyleConstants.setFontSize(attrs, fontSize);
        StyleConstants.setForeground(attrs, c);

        //StyleContext sc = StyleContext.getDefaultStyleContext();
        //MutableAttributeSet aset = this.getInputAttributes();
        //AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        setCaretPosition(pos); // place caret at the end (with no selection)
        setCharacterAttributes(attrs, false);

        /*try {
            this.getDocument().insertString(this.getCaretPosition(), str, attrs);
            } catch (BadLocationException e) {
            }*/
        replaceSelection(str); // there is no selection, so inserts at caret.

    }

    /**
     * Translates an offset into the components text to a line number.
     *
     * @param offset the offset >= 0
     * @return the line number >= 0
     * @exception BadLocationException thrown if the offset is less than zero or
     * greater than the document length.
     */
    public int getLineOfOffset(int offset) throws BadLocationException {
        Document doc = getDocument();
        if (offset < 0) {
            throw new BadLocationException("Can't translate offset to line", -1);
        } else if (offset > doc.getLength()) {
            throw new BadLocationException("Can't translate offset to line", doc.getLength() + 1);
        } else {
            Element map = getDocument().getDefaultRootElement();
            return map.getElementIndex(offset);
        }
    }

    /**
     * Determines the number of lines contained in the area.
     *
     * @return the number of lines > 0
     */
    public int getLineCount() {
        Element map = getDocument().getDefaultRootElement();
        return map.getElementCount();
    }

    /**
     * Determines the offset of the start of the given line.
     *
     * @param line the line number to translate >= 0
     * @return the offset >= 0
     * @exception BadLocationException thrown if the line is less than zero or
     * greater or equal to the number of lines contained in the document (as
     * reported by getLineCount).
     */
    public int getLineStartOffset(int line) throws BadLocationException {
        int lineCount = getLineCount();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= lineCount) {
            throw new BadLocationException("No such line", getDocument().getLength() + 1);
        } else {
            Element map = getDocument().getDefaultRootElement();
            Element lineElem = map.getElement(line);
            return lineElem.getStartOffset();
        }
    }

    /**
     * Determines the offset of the end of the given line.
     *
     * @param line the line >= 0
     * @return the offset >= 0
     * @exception BadLocationException Thrown if the line is less than zero or
     * greater or equal to the number of lines contained in the document (as
     * reported by getLineCount).
     */
    public int getLineEndOffset(int line) throws BadLocationException {
        int lineCount = getLineCount();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= lineCount) {
            throw new BadLocationException("No such line", getDocument().getLength() + 1);
        } else {
            Element map = getDocument().getDefaultRootElement();
            Element lineElem = map.getElement(line);
            int endOffset = lineElem.getEndOffset();
            // hide the implicit break at the end of the document
            return ((line == lineCount - 1) ? (endOffset - 1) : endOffset);
        }
    }

    /**
     * Replaces text from the indicated start to end position with the new text
     * specified. Does nothing if the model is null. Simply does a delete if the
     * new string is null or empty.
     * <p>
     * This method is thread safe, although most Swing methods are not.
     *
     * @param str the text to use as the replacement
     * @param start the start position >= 0
     * @param end the end position >= start
     * @exception IllegalArgumentException if part of the range is an invalid
     * position in the model
     * @see #insert
     * @see #replaceRange
     */
    public void replaceRange(String str, int start, int end, int fontSize) {
        if (end < start) {
            throw new IllegalArgumentException("end before start");
        }

        Font font = getFont();

        MutableAttributeSet attrs = getInputAttributes();

        StyleConstants.setFontFamily(attrs, font.getFamily());
        StyleConstants.setFontSize(attrs, fontSize);

        setCharacterAttributes(attrs, false);
        this.select(start, end);
        replaceSelection(str);

    }

    public boolean isIsWordWrap() {
        return isWordWrap;
    }

    public void setIsWordWrap(boolean isWordWrap) {
        this.isWordWrap = isWordWrap;
        this.invalidate();
        this.revalidate();
        this.repaint();
    }
    
    

}
