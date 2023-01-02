// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.graphics.canvas.component;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsInfo;

import java.util.Arrays;


/**
 * A graphics component with a label and 16 channels (volume/vu/panorama).
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TitleChannelsComponent extends AbstractBaseComponent
{
    private final boolean [] selected;
    private final int []     values;
    private final boolean    isPan;


    /**
     * Constructor.
     *
     * @param label The first row text
     * @param selected The selected states of the channel
     * @param values The values to display for the channel
     * @param isPan Draw values as panorama if true
     */
    public TitleChannelsComponent (final String label, final boolean [] selected, final int [] values, final boolean isPan)
    {
        super (label);

        this.selected = selected;
        this.values = values;
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

        final int channelWidth = WIDTH / this.values.length;
        final int halfChannelWidth = channelWidth / 2;
        final int lowerHeight = HEIGHT - ROW_HEIGHT - 1;

        for (int i = 0; i < this.values.length; i++)
        {
            final int left = i * channelWidth;

            final int faderHeight = this.values[i] * lowerHeight / RESOLUTION;

            if (this.isPan)
            {
                final int center = lowerHeight / 2;

                if (this.selected[i])
                {
                    if (faderHeight == center)
                        gc.fillRectangle (left, HEIGHT - center + 1.0, halfChannelWidth + 1.0, 1, colorFader);
                    else if (faderHeight > center)
                        gc.fillRectangle (left, HEIGHT - (double) center, halfChannelWidth + 1.0, faderHeight - (double) center, colorFader);
                    else
                        gc.fillRectangle (left, HEIGHT - lowerHeight + (double) faderHeight, halfChannelWidth + 1.0, center - faderHeight + 2.0, colorFader);
                }
                else
                {
                    if (faderHeight == center)
                        gc.strokeRectangle (left + 1.0, HEIGHT - center + 1.0, halfChannelWidth, 1, colorFader);
                    else if (faderHeight > center)
                        gc.strokeRectangle (left + 1.0, HEIGHT - (double) center, halfChannelWidth, faderHeight - (double) center, colorFader);
                    else
                        gc.strokeRectangle (left + 1.0, HEIGHT - lowerHeight + (double) faderHeight, halfChannelWidth, center - faderHeight + 2.0, colorFader);
                }
            }
            else
            {
                if (this.selected[i])
                    gc.fillRectangle (left, HEIGHT - (double) faderHeight, halfChannelWidth + 1.0, faderHeight, colorFader);
                else
                    gc.strokeRectangle (left + 1.0, HEIGHT - (double) faderHeight, halfChannelWidth, faderHeight, colorFader);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = super.hashCode ();
        result = prime * result + (this.isPan ? 1231 : 1237);
        result = prime * result + Arrays.hashCode (this.selected);
        return prime * result + Arrays.hashCode (this.values);
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals (obj) || this.getClass () != obj.getClass ())
            return false;
        final TitleChannelsComponent other = (TitleChannelsComponent) obj;
        if (this.isPan != other.isPan || !Arrays.equals (this.selected, other.selected))
            return false;
        return Arrays.equals (this.values, other.values);
    }
}
