// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.graphics.canvas.component;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsInfo;


/**
 * A graphics component with two labels and a volume/panorama bar.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TitleValueComponent extends AbstractBaseComponent
{
    private final String  label2;
    private final int     value;
    private final int     vuLeft;
    private final int     vuRight;
    private final boolean isPan;


    /**
     * Constructor.
     *
     * @param label1 The first row text
     * @param label2 The second row text
     * @param value The value, hides the value fader if set to -1
     * @param isPan True if display as panorama bar
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
     * @param isPan True if display as panorama bar
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
        final IGraphicsContext gc = info.getContext ();

        final IGraphicsConfiguration configuration = info.getConfiguration ();
        final ColorEx colorText = configuration.getColorText ();
        final ColorEx colorFader = configuration.getColorFader ();

        gc.drawTextInHeight (this.label, 0, 0, ROW_HEIGHT, colorText, ROW_HEIGHT);
        gc.drawTextInHeight (this.label2, 0, ROW_HEIGHT, ROW_HEIGHT, colorText, ROW_HEIGHT);

        if (this.value >= 0)
        {
            final int width = this.value * 128 / RESOLUTION;
            gc.strokeRectangle (1, TOP, 127, ROW_HEIGHT, colorFader);
            if (this.isPan)
            {
                if (width == CENTER)
                    gc.fillRectangle (CENTER + 1.0, TOP, 1, ROW_HEIGHT, colorFader);
                else if (width > CENTER)
                    gc.fillRectangle (CENTER + 1.0, TOP, width - (double) CENTER, ROW_HEIGHT, colorFader);
                else
                    gc.fillRectangle (width, TOP, CENTER - (double) width, ROW_HEIGHT, colorFader);
            }
            else
                gc.fillRectangle (1, TOP, width, ROW_HEIGHT, colorFader);
        }

        if (this.vuLeft > 0 || this.vuRight > 0)
        {
            final double height = 2.0 * ROW_HEIGHT;

            final double heightLeft = this.vuLeft * height / RESOLUTION;
            final double topLeft = Math.max (height - heightLeft - 1, 0);
            gc.strokeRectangle (118, topLeft, 5, heightLeft, ColorEx.BLACK);
            gc.fillRectangle (119, height - heightLeft, 3, heightLeft, colorFader);

            final double heightRight = this.vuRight * height / RESOLUTION;
            final double topRight = Math.max (height - heightRight - 1, 0);
            gc.strokeRectangle (123, topRight, 4, height - topRight, ColorEx.BLACK);
            gc.fillRectangle (124, height - heightRight, 3, heightRight, colorFader);
        }
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
