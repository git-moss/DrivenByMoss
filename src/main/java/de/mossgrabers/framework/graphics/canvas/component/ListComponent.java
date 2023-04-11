// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.graphics.IGraphicsInfo;
import de.mossgrabers.framework.utils.Pair;

import java.util.ArrayList;
import java.util.List;


/**
 * A component which contains several text items. Each item can be selected.
 *
 * @author Jürgen Moßgraber
 */
public class ListComponent implements IComponent
{
    private final List<Pair<String, Boolean>> items = new ArrayList<> (6);


    /**
     * Constructor.
     *
     * @param items The list items
     */
    public ListComponent (final List<Pair<String, Boolean>> items)
    {
        this.items.addAll (items);
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        final IGraphicsContext gc = info.getContext ();
        final IGraphicsDimensions dimensions = info.getDimensions ();
        final IGraphicsConfiguration configuration = info.getConfiguration ();
        final double left = info.getBounds ().left ();
        final double width = info.getBounds ().width ();
        final double height = info.getBounds ().height ();

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
            gc.drawTextInBounds (item.getKey (), itemLeft + inset, itemTop, itemWidth - 2 * inset, itemHeight, Align.LEFT, isSelected ? borderColor : textColor, itemHeight * 0.6);
        }
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        final int result = 1;
        return prime * result + this.items.hashCode ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || this.getClass () != obj.getClass ())
            return false;
        final ListComponent other = (ListComponent) obj;
        return this.items.equals (other.items);
    }
}
