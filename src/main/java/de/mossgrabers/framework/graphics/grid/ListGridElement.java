// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.grid;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.utils.Pair;

import java.util.ArrayList;
import java.util.List;


/**
 * An element in the grid which contains several text items. Each item can be selected.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ListGridElement extends AbstractGridElement
{
    private final List<Pair<String, Boolean>> items = new ArrayList<> (6);


    /**
     * Constructor.
     *
     * @param items The list items
     */
    public ListGridElement (final List<Pair<String, Boolean>> items)
    {
        super (null, false, null, null, null, false);
        this.items.addAll (items);
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final IGraphicsConfiguration configuration, IGraphicsDimensions dimensions, final double left, final double width, final double height)
    {
        final double separatorSize = dimensions.getSeparatorSize ();
        final double inset = dimensions.getInset ();

        final int size = this.items.size ();
        final double itemLeft = left + separatorSize;
        final double itemWidth = width - separatorSize;
        final double itemHeight = height / size;

        final ColorEx textColor = configuration.getColorText ();
        final ColorEx borderColor = configuration.getColorBorder ();

        for (int i = 0; i < size; i++)
        {
            final Pair<String, Boolean> item = this.items.get (i);
            final boolean isSelected = item.getValue ().booleanValue ();
            final double itemTop = i * itemHeight;
            gc.fillRectangle (itemLeft, itemTop + separatorSize, itemWidth, itemHeight - 2 * separatorSize, isSelected ? textColor : borderColor);
            gc.drawTextInBounds (item.getKey (), itemLeft + inset, itemTop, itemWidth - 2 * inset, itemHeight, Align.LEFT, isSelected ? borderColor : textColor, itemHeight / 2);
        }
    }
}
