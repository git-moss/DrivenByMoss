// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller.display.model.grid;

import de.mossgrabers.framework.ColorEx;
import de.mossgrabers.push.PushConfiguration;

import com.bitwig.extension.api.graphics.GraphicsOutput;
import com.bitwig.extension.api.graphics.GraphicsOutput.AntialiasMode;

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
    private String []       items;
    private List<double []> colors = new ArrayList<> ();


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
        this.colors.addAll (colors);
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final GraphicsOutput gc, final double left, final double width, final double height, final PushConfiguration configuration)
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

            // Element background
            final double [] ds = this.colors.get (i);
            gc.setColor (ds[0], ds[1], ds[2]);
            gc.rectangle (itemLeft, itemTop + SEPARATOR_SIZE, itemWidth, itemHeight - 2 * SEPARATOR_SIZE);
            gc.fill ();

            // Element border
            gc.setAntialias (AntialiasMode.OFF);
            setColor (gc, borderColor);
            gc.setLineWidth (1);
            gc.rectangle (itemLeft, itemTop + SEPARATOR_SIZE, itemWidth, itemHeight - 2 * SEPARATOR_SIZE);
            gc.stroke ();
            gc.setAntialias (AntialiasMode.BEST);

            // Text
            gc.setFontSize (itemHeight / 2);
            drawTextInBounds (gc, this.items[i], itemLeft + INSET, itemTop - 1, itemWidth - 2 * INSET, itemHeight, Align.LEFT, textColor);
        }
    }
}
