// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.graphics.canvas.component;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsInfo;
import de.mossgrabers.framework.graphics.canvas.component.IComponent;


/**
 * A graphics component with two labels and a volume/panorama bar.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TitleValueComponent implements IComponent
{
    private final String  label1;
    private final String  label2;
    private final int     value;
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
        this.label1 = label1;
        this.label2 = label2;
        this.value = value;
        this.isPan = isPan;
    }


    /** {@inheritDoc}} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        final IGraphicsContext gc = info.getContext ();

        final IGraphicsConfiguration configuration = info.getConfiguration ();
        final ColorEx colorText = configuration.getColorText ();
        final ColorEx colorFader = configuration.getColorFader ();

        gc.drawTextInHeight (this.label1, 0, 0, 20, colorText, 20);
        gc.drawTextInHeight (this.label2, 0, 20, 20, colorText, 20);

        if (this.value >= 0)
        {
            final int width = this.value * 128 / 1024;
            final int barHeight = 20;
            gc.strokeRectangle (1, 44, 127, barHeight, colorFader);
            if (this.isPan)
            {
                if (width == 64)
                    gc.fillRectangle (65, 44, 1, barHeight, colorFader);
                else if (width > 64)
                    gc.fillRectangle (65, 44, width - 64.0, barHeight, colorFader);
                else
                    gc.fillRectangle (width, 44, 64.0 - width, barHeight, colorFader);
            }
            else
                gc.fillRectangle (1, 44, width, barHeight, colorFader);
        }
    }
}
