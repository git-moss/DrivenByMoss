// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component.simple;

import java.util.Arrays;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsInfo;


/**
 * Like TitleValueComponent but with a menu strip at the bottom.
 *
 * @author Jürgen Moßgraber
 */
public class TitleValueMenuComponent extends TitleValueComponent
{
    private final String [] menu;


    /**
     * Constructor.
     *
     * @param label1 The first row text
     * @param label2 The second row text
     * @param menu The 4 menu entries
     * @param value The value, hides the value fader if set to -1
     * @param isPan True if display as panning bar
     */
    public TitleValueMenuComponent (final String label1, final String label2, final String [] menu, final int value, final boolean isPan)
    {
        this (label1, label2, menu, value, -1, -1, isPan);
    }


    /**
     * Constructor.
     *
     * @param label1 The first row text
     * @param label2 The second row text
     * @param menu The 4 menu entries
     * @param value The value, hides the value fader if set to -1
     * @param vuLeft The left VU value, not drawn if -1
     * @param vuRight The right VU value, not drawn if -1
     * @param isPan True if display as panning bar
     */
    public TitleValueMenuComponent (final String label1, final String label2, final String [] menu, final int value, final int vuLeft, final int vuRight, final boolean isPan)
    {
        super (label1, label2, value, vuLeft, vuRight, isPan);

        this.menu = menu;
        this.rowHeight = DEFAULT_HEIGHT / 4 - 2;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        final IGraphicsConfiguration configuration = info.getConfiguration ();
        final ColorEx colorText = configuration.getColorText ();
        final ColorEx colorFader = configuration.getColorFader ();

        final IGraphicsContext gc = info.getContext ();
        this.draw (gc, 2 * this.rowHeight + 4, this.rowHeight - 4, colorText, colorFader);
        this.drawMenu (gc);
    }


    private void drawMenu (final IGraphicsContext gc)
    {
        final int menuHeight = this.rowHeight - 4;
        final int menuTop = DEFAULT_HEIGHT - menuHeight;
        final int itemWidth = DEFAULT_WIDTH / 4;

        gc.fillRectangle (0, menuTop, DEFAULT_WIDTH, menuHeight, ColorEx.WHITE);

        gc.drawTextInBounds (this.menu[0], 0, menuTop, itemWidth, menuHeight, Align.CENTER, ColorEx.BLACK, menuHeight + 2);
        gc.drawTextInBounds (this.menu[1], itemWidth, menuTop, itemWidth, menuHeight, Align.CENTER, ColorEx.BLACK, menuHeight + 2);
        gc.drawTextInBounds (this.menu[2], 2 * itemWidth, menuTop, itemWidth, menuHeight, Align.CENTER, ColorEx.BLACK, menuHeight + 2);
        gc.drawTextInBounds (this.menu[3], 3 * itemWidth, menuTop, itemWidth, menuHeight, Align.CENTER, ColorEx.BLACK, menuHeight + 2);
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = super.hashCode ();
        result = prime * result + Arrays.hashCode (this.menu);
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals (obj) || this.getClass () != obj.getClass ())
            return false;
        final TitleValueMenuComponent other = (TitleValueMenuComponent) obj;
        if (!Arrays.equals (this.menu, other.menu))
            return false;
        return true;
    }
}
