package test.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class MutableTextAttribute extends TextAttribute {

    private Color fieldForeground;

    private Color fieldBackground;

    private int fieldStyle;

    private Font fieldFont;

    private int fHashCode;

    public MutableTextAttribute(final Color foreground) {
        this(foreground, null, 0, null);
    }

    public MutableTextAttribute(final Color foreground, final Color background, final int style) {
        this(foreground, background, style, null);
    }

    public MutableTextAttribute(final TextAttribute attrs) {
        this(attrs.getForeground(), attrs.getBackground(), attrs.getStyle(), attrs.getFont());
    }

    public MutableTextAttribute(final Color foreground, final Color background, final int style, final Font font) {
        super(null, null, 0, null);
        fieldForeground = foreground;
        fieldBackground = background;
        fieldStyle = style;
        fieldFont = font;
        update();
    }

    public void append(final TextAttribute attrs) {
        if (attrs.getBackground() != null && this.getBackground() == null) {
            this.setBackground(attrs.getBackground());
        }
        if (attrs.getFont() != null && this.getFont() == null) {
            this.setFont(attrs.getFont());
        }
        if (attrs.getForeground() != null && this.getForeground() == null) {
            this.setForeground(attrs.getForeground());
        }
        this.setStyle(this.getStyle() | attrs.getStyle());
    }

    public TextAttribute getAttributes() {
        return new TextAttribute(getForeground(), getBackground(), getStyle(), getFont());
    }

    /**
     * @return the foreground
     */
    @Override
    public Color getForeground() {
        return fieldForeground;
    }

    /**
     * @param foreground the foreground to set
     */
    public void setForeground(final Color foreground) {
        fieldForeground = foreground;
    }

    /**
     * @return the background
     */
    @Override
    public Color getBackground() {
        return fieldBackground;
    }

    /**
     * @param background the background to set
     */
    public void setBackground(final Color background) {
        fieldBackground = background;
    }

    /**
     * @return the style
     */
    @Override
    public int getStyle() {
        return fieldStyle;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(final int style) {
        fieldStyle = style;
        update();
    }

    /**
     * @return the font
     */
    @Override
    public Font getFont() {
        return fieldFont;
    }

    /**
     * @param font the font to set
     */
    public void setFont(final Font font) {
        fieldFont = font;
        update();
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof TextAttribute)) {
            return false;
        }
        final TextAttribute a = (TextAttribute) object;

        return (a.getStyle() == getStyle() && equals(a.getForeground(), getForeground()) && equals(a.getBackground(), getBackground()) && equals(a.getFont(),
                getFont()));
    }

    /**
     * Returns whether the two given objects are equal.
     * @param o1 the first object, can be <code>null</code>
     * @param o2 the second object, can be <code>null</code>
     * @return <code>true</code> if the given objects are equals
     * @since 2.0
     */
    private boolean equals(final Object o1, final Object o2) {
        if (o1 != null) {
            return o1.equals(o2);
        }
        return (o2 == null);
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (fHashCode == 0) {
            final int multiplier = 37; // some prime
            fHashCode = 13; // some random value
            fHashCode = multiplier * fHashCode + (fieldFont == null ? 0 : fieldFont.hashCode());
            fHashCode = multiplier * fHashCode + (fieldBackground == null ? 0 : fieldBackground.hashCode());
            fHashCode = multiplier * fHashCode + (fieldForeground == null ? 0 : fieldForeground.hashCode());
            fHashCode = multiplier * fHashCode + fieldStyle;
        }
        return fHashCode;
    }

    private void update() {
        final Font font = this.getFont();
        final int style = this.getStyle();
        if (style != 0 && font != null) {
            final FontData fontData = font.getFontData()[0];
            this.fieldFont = new Font(font.getDevice(), fontData.getName(), fontData.getHeight(), fontData.getStyle() | style);
        }
        fHashCode = 0;
    }
}
