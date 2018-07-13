// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.controller.display.grid;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsContext;

import java.util.ArrayList;
import java.util.List;


/**
 * An element in the grid which contains several text items. Each item can be selected.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BoxListGridElement extends AbstractGridElement
{
    private String []     items;
    private List<ColorEx> colors = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param items The list items
     * @param colors The colors for the background boxes
     */
    public BoxListGridElement (final String [] items, final List<double []> colors)
    {
        super (null, false, null, null, null, false);

        this.items = items;
        for (final double [] color: colors)
            this.colors.add (new ColorEx (color[0], color[1], color[2]));
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final double left, final double width, final double height, final PushConfiguration configuration)
    {
        final int size = this.items.length;
        final double itemHeight = DISPLAY_HEIGHT / (double) size;

        final ColorEx textColor = configuration.getColorText ();
        final ColorEx borderColor = configuration.getColorBackgroundLighter ();

        for (int i = 0; i < size; i++)
        {
            final double itemLeft = left + SEPARATOR_SIZE;
            final double itemTop = i * itemHeight;
            final double itemWidth = width - SEPARATOR_SIZE;

            gc.fillRectangle (itemLeft, itemTop + SEPARATOR_SIZE, itemWidth, itemHeight - 2 * SEPARATOR_SIZE, this.colors.get (i));
            gc.strokeRectangle (itemLeft, itemTop + SEPARATOR_SIZE, itemWidth, itemHeight - 2 * SEPARATOR_SIZE, borderColor);
            gc.drawTextInBounds (this.items[i], itemLeft + INSET, itemTop - 1, itemWidth - 2 * INSET, itemHeight, Align.LEFT, textColor, itemHeight / 2);
        }
    }
}
