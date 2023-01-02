// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.IBounds;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.graphics.IGraphicsInfo;
import de.mossgrabers.framework.graphics.canvas.component.LabelComponent.LabelLayout;


/**
 * An element in the grid which can display on option on top and on the bottom of the element. In
 * the middle two texts can be displayed. The texts are not clipped horizontally and can reach into
 * the next elements.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OptionsComponent implements IComponent
{
    private final LabelComponent header;
    private final LabelComponent footer;

    private final String         headerBottom;
    private final String         headerTop;
    private final boolean        isBottomHeaderSelected;


    /**
     * Constructor.
     *
     * @param headerTop A header for the top menu options (may span multiple grids), may be null
     * @param menuTopName A name for the to menu, may be null
     * @param isMenuTopSelected Is the top menu selected?
     * @param menuTopColor The color to use for the background top menu, may be null
     * @param headerBottom A header for the bottom menu options (may span multiple grids), may be
     *            null
     * @param menuBottomName A name for the bottom menu, may be null
     * @param isMenuBottomSelected Is the bottom menu selected?
     * @param menuBottomColor The color to use for the background bottom menu, may be null
     * @param useSmallTopMenu Draw the small version of the top menu if true
     * @param isBottomHeaderSelected True to draw the lower header selected
     */
    public OptionsComponent (final String headerTop, final String menuTopName, final boolean isMenuTopSelected, final ColorEx menuTopColor, final String headerBottom, final String menuBottomName, final boolean isMenuBottomSelected, final ColorEx menuBottomColor, final boolean useSmallTopMenu, final boolean isBottomHeaderSelected)
    {
        this.header = new LabelComponent (menuTopName, null, menuTopColor, isMenuTopSelected, true, useSmallTopMenu ? LabelLayout.SMALL_HEADER : LabelLayout.PLAIN);
        this.footer = new LabelComponent (menuBottomName, null, menuBottomColor, isMenuBottomSelected, true, LabelLayout.PLAIN);

        this.headerTop = headerTop;
        this.headerBottom = headerBottom;
        this.isBottomHeaderSelected = isBottomHeaderSelected;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        final IGraphicsContext gc = info.getContext ();

        final IGraphicsDimensions dimensions = info.getDimensions ();

        final double menuHeight = 2 * dimensions.getMenuHeight ();
        final IGraphicsConfiguration configuration = info.getConfiguration ();

        this.header.draw (info.withBounds (0, menuHeight));

        final IBounds bounds = info.getBounds ();
        final double left = bounds.left ();
        final double height = bounds.height ();

        this.footer.draw (info.withBounds (height - menuHeight, menuHeight));

        final boolean hasTopHeader = this.headerTop != null && !this.headerTop.isEmpty ();
        final boolean hasBottomHeader = this.headerBottom != null && !this.headerBottom.isEmpty ();
        if (!hasTopHeader && !hasBottomHeader)
            return;

        final double headerHeight = (height - 2 * menuHeight) / 2;
        final ColorEx textColor = configuration.getColorText ();
        if (hasTopHeader)
            gc.drawTextInHeight (this.headerTop, left, menuHeight, headerHeight, textColor, headerHeight / 2.0);
        if (hasBottomHeader)
        {
            if (this.isBottomHeaderSelected)
                gc.drawTextInHeight (this.headerBottom, left, menuHeight + headerHeight, headerHeight, ColorEx.calcContrastColor (textColor), textColor, headerHeight / 2.0);
            else
                gc.drawTextInHeight (this.headerBottom, left, menuHeight + headerHeight, headerHeight, textColor, headerHeight / 2.0);
        }
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.footer == null ? 0 : this.footer.hashCode ());
        result = prime * result + (this.header == null ? 0 : this.header.hashCode ());
        result = prime * result + (this.headerBottom == null ? 0 : this.headerBottom.hashCode ());
        result = prime * result + (this.headerTop == null ? 0 : this.headerTop.hashCode ());
        result = prime * result + (this.isBottomHeaderSelected ? 1231 : 1237);
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || this.getClass () != obj.getClass ())
            return false;
        final OptionsComponent other = (OptionsComponent) obj;
        if (this.footer == null)
        {
            if (other.footer != null)
                return false;
        }
        else if (!this.footer.equals (other.footer))
            return false;
        if (this.header == null)
        {
            if (other.header != null)
                return false;
        }
        else if (!this.header.equals (other.header))
            return false;
        if (this.headerBottom == null)
        {
            if (other.headerBottom != null)
                return false;
        }
        else if (!this.headerBottom.equals (other.headerBottom))
            return false;
        if (this.headerTop == null)
        {
            if (other.headerTop != null)
                return false;
        }
        else if (!this.headerTop.equals (other.headerTop))
            return false;
        return this.isBottomHeaderSelected == other.isBottomHeaderSelected;
    }
}
