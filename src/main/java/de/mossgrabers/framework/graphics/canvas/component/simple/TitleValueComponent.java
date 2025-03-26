// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component.simple;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsInfo;


/**
 * A graphics component with two labels, a volume/panning bar and a stereo VU.
 *
 * @author Jürgen Moßgraber
 */
public class TitleValueComponent extends AbstractBaseComponent
{
    private final String  label2;
    private final int     value;
    private final int     vuLeft;
    private final int     vuRight;
    private final boolean isPan;

    protected int         rowHeight = DEFAULT_ROW_HEIGHT;


    /**
     * Constructor.
     *
     * @param label1 The first row text
     * @param label2 The second row text
     * @param value The value, hides the value fader if set to -1
     * @param isPan True if display as panning bar
     */
    public TitleValueComponent (final String label1, final String label2, final int value, final boolean isPan)
    {
        this (label1, label2, value, -1, -1, isPan);
    }


    /**
     * Constructor.
     *
     * @param label1 The first row text
     * @param label2 The second row text
     * @param value The value, hides the value fader if set to -1
     * @param vuLeft The left VU value, not drawn if -1
     * @param vuRight The right VU value, not drawn if -1
     * @param isPan True if display as panning bar
     */
    public TitleValueComponent (final String label1, final String label2, final int value, final int vuLeft, final int vuRight, final boolean isPan)
    {
        super (label1);

        this.label2 = label2;
        this.value = value;
        this.vuLeft = vuLeft;
        this.vuRight = vuRight;
        this.isPan = isPan;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        final IGraphicsConfiguration configuration = info.getConfiguration ();
        final ColorEx colorText = configuration.getColorText ();
        final ColorEx colorFader = configuration.getColorFader ();

        final IGraphicsContext gc = info.getContext ();
        this.draw (gc, TOP, this.rowHeight, colorText, colorFader);
    }


    /**
     * Draw the element.
     *
     * @param gc The graphics context
     * @param faderTop The top of the fader
     * @param faderHeight The height of the fader
     * @param colorText The color of the text
     * @param colorFader The color of the fader
     */
    protected void draw (final IGraphicsContext gc, final double faderTop, final double faderHeight, final ColorEx colorText, final ColorEx colorFader)
    {
        this.drawLabels (gc, colorText);
        this.drawFader (gc, colorFader, faderTop, faderHeight);
        this.drawVU (gc, colorFader);
    }


    /**
     * Draw the parameter labels.
     *
     * @param gc The graphics context
     * @param colorText The color of the text
     */
    protected void drawLabels (final IGraphicsContext gc, final ColorEx colorText)
    {
        gc.drawTextInHeight (this.label, 0, 0, this.rowHeight, colorText, this.rowHeight);
        gc.drawTextInHeight (this.label2, 0, this.rowHeight, this.rowHeight, colorText, this.rowHeight);
    }


    /**
     * Draw the fader.
     *
     * @param gc The graphics context
     * @param colorFader The color of the fader
     * @param faderTop The y-top of the fader
     * @param faderHeight The height of the fader
     */
    protected void drawFader (final IGraphicsContext gc, final ColorEx colorFader, final double faderTop, final double faderHeight)
    {
        if (this.value < 0)
            return;

        final int width = this.value * 128 / RESOLUTION;
        gc.strokeRectangle (1, faderTop, 127, faderHeight, colorFader);
        if (this.isPan)
        {
            if (width == CENTER)
                gc.fillRectangle (CENTER + 1.0, faderTop, 1, faderHeight, colorFader);
            else if (width > CENTER)
                gc.fillRectangle (CENTER + 1.0, faderTop, width - (double) CENTER, faderHeight, colorFader);
            else
                gc.fillRectangle (width, faderTop, CENTER - (double) width, faderHeight, colorFader);
        }
        else
            gc.fillRectangle (1, faderTop, width, faderHeight, colorFader);
    }


    /**
     * Draw the stereo VU.
     *
     * @param gc The graphics context
     * @param color The color of the VU
     */
    protected void drawVU (final IGraphicsContext gc, final ColorEx color)
    {
        if (this.vuLeft <= 0 && this.vuRight <= 0)
            return;

        final double height = 2.0 * this.rowHeight;

        final double heightLeft = this.vuLeft * height / RESOLUTION;
        final double topLeft = Math.max (height - heightLeft - 1, 0);
        gc.strokeRectangle (118, topLeft, 5, heightLeft, ColorEx.BLACK);
        gc.fillRectangle (119, height - heightLeft, 3, heightLeft, color);

        final double heightRight = this.vuRight * height / RESOLUTION;
        final double topRight = Math.max (height - heightRight - 1, 0);
        gc.strokeRectangle (123, topRight, 4, height - topRight, ColorEx.BLACK);
        gc.fillRectangle (124, height - heightRight, 3, heightRight, color);
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = super.hashCode ();
        result = prime * result + (this.isPan ? 1231 : 1237);
        result = prime * result + (this.label2 == null ? 0 : this.label2.hashCode ());
        result = prime * result + this.value;
        result = prime * result + this.vuLeft;
        return prime * result + this.vuRight;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals (obj) || this.getClass () != obj.getClass ())
            return false;
        final TitleValueComponent other = (TitleValueComponent) obj;
        if (this.isPan != other.isPan)
            return false;
        if (this.label2 == null)
        {
            if (other.label2 != null)
                return false;
        }
        else if (!this.label2.equals (other.label2))
            return false;
        if (this.value != other.value || this.vuLeft != other.vuLeft)
            return false;
        return this.vuRight == other.vuRight;
    }
}
