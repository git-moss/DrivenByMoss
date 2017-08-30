// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller.display.model.grid;

import de.mossgrabers.framework.Pair;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.display.model.LayoutSettings;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.api.GraphicsOutput;

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
    public void draw (final GraphicsOutput gc, final double left, final double width, final double height, final LayoutSettings layoutSettings, PushConfiguration configuration)
    {
        final int size = this.items.size ();
        final double itemHeight = DISPLAY_HEIGHT / size;

        final Color textColor = layoutSettings.getTextColor ();
        final Color borderColor = layoutSettings.getBorderColor ();

        for (int i = 0; i < size; i++)
        {
            final Pair<String, Boolean> item = this.items.get (i);
            final boolean isSelected = item.getValue ().booleanValue ();
            final double itemLeft = left + SEPARATOR_SIZE;
            final double itemTop = i * itemHeight;
            final double itemWidth = width - SEPARATOR_SIZE;

            gc.setColor (isSelected ? textColor : borderColor);
            gc.rectangle (itemLeft, itemTop + SEPARATOR_SIZE, itemWidth, itemHeight - 2 * SEPARATOR_SIZE);
            gc.fill ();

            gc.setFontSize (itemHeight / 2);
            drawTextInBounds (gc, item.getKey (), itemLeft + INSET, itemTop, itemWidth - 2 * INSET, itemHeight, Align.LEFT, isSelected ? borderColor : textColor);
        }
    }
}
