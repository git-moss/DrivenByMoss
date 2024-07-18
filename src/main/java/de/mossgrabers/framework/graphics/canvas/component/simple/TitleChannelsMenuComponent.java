// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component.simple;

import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsInfo;


/**
 * Like TitleChannelsComponent but with a menu strip at the bottom.
 *
 * @author Jürgen Moßgraber
 */
public class TitleChannelsMenuComponent extends TitleChannelsComponent
{
    /**
     * Constructor.
     *
     * @param label The first row text
     * @param selected The selected states of the channel
     * @param values The values to display for the channel
     * @param isPan Draw values as panorama if true
     */
    public TitleChannelsMenuComponent (final String label, final boolean [] selected, final int [] values, final boolean isPan)
    {
        super (label, selected, values, isPan);

        this.rowHeight = DEFAULT_HEIGHT / 4 - 2;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        super.draw (info);

        final IGraphicsContext gc = info.getContext ();

        // final IGraphicsConfiguration configuration = info.getConfiguration ();
        // final ColorEx colorText = configuration.getColorText ();
        // final ColorEx colorFader = configuration.getColorFader ();
        //
        // gc.drawTextInHeight (this.label, 0, 0, DEFAULT_ROW_HEIGHT, colorText,
        // DEFAULT_ROW_HEIGHT);
        //
        // final int channelWidth = DEFAULT_WIDTH / this.values.length;
        // final int halfChannelWidth = channelWidth / 2;
        // final int lowerHeight = DEFAULT_HEIGHT - DEFAULT_ROW_HEIGHT - 1;
        //
        // for (int i = 0; i < this.values.length; i++)
        // {
        // final int left = i * channelWidth;
        //
        // final int faderHeight = this.values[i] * lowerHeight / RESOLUTION;
        //
        // if (this.isPan)
        // {
        // final int center = lowerHeight / 2;
        //
        // if (this.selected[i])
        // {
        // if (faderHeight == center)
        // gc.fillRectangle (left, DEFAULT_HEIGHT - center + 1.0, halfChannelWidth + 1.0, 1,
        // colorFader);
        // else if (faderHeight > center)
        // gc.fillRectangle (left, DEFAULT_HEIGHT - (double) center, halfChannelWidth + 1.0,
        // faderHeight - (double) center, colorFader);
        // else
        // gc.fillRectangle (left, DEFAULT_HEIGHT - lowerHeight + (double) faderHeight,
        // halfChannelWidth + 1.0, center - faderHeight + 2.0, colorFader);
        // }
        // else
        // {
        // if (faderHeight == center)
        // gc.strokeRectangle (left + 1.0, DEFAULT_HEIGHT - center + 1.0, halfChannelWidth, 1,
        // colorFader);
        // else if (faderHeight > center)
        // gc.strokeRectangle (left + 1.0, DEFAULT_HEIGHT - (double) center, halfChannelWidth,
        // faderHeight - (double) center, colorFader);
        // else
        // gc.strokeRectangle (left + 1.0, DEFAULT_HEIGHT - lowerHeight + (double) faderHeight,
        // halfChannelWidth, center - faderHeight + 2.0, colorFader);
        // }
        // }
        // else
        // {
        // if (this.selected[i])
        // gc.fillRectangle (left, DEFAULT_HEIGHT - (double) faderHeight, halfChannelWidth + 1.0,
        // faderHeight, colorFader);
        // else
        // gc.strokeRectangle (left + 1.0, DEFAULT_HEIGHT - (double) faderHeight, halfChannelWidth,
        // faderHeight, colorFader);
        // }
        // }
    }
}
