// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.grid;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;


/**
 * Abstract base class for an element in the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractGridElement implements IGridElement
{
    /** The maximum possible value for a parameter. */
    private static double   maxValue = 1024;

    private final String    name;
    private final String    icon;
    private final ColorEx   color;
    private final boolean   isSelected;

    protected final boolean isMenuSelected;
    protected final String  menuName;


    /**
     * Constructor.
     *
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param icon The icon to use in the header, may be null
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     */
    public AbstractGridElement (final String menuName, final boolean isMenuSelected, final String icon, final String name, final ColorEx color, final boolean isSelected)
    {
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.isSelected = isSelected;
        this.menuName = menuName;
        this.isMenuSelected = isMenuSelected;
    }


    /**
     * Get the icon
     *
     * @return The icon or null if not set
     */
    public String getIcon ()
    {
        return this.icon;
    }


    /**
     * Get the name of the grid element.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Get the color to use in the header.
     *
     * @return The color or null if not set
     */
    public ColorEx getColor ()
    {
        return this.color;
    }


    /**
     * Is the grid element selected?
     *
     * @return True if selected
     */
    public boolean isSelected ()
    {
        return this.isSelected;
    }


    /**
     * Draws a menu at the top of the element.
     *
     * @param gc The graphics context
     * @param configuration The layout settings to use
     * @param dimensions Pre-calculated grid dimensions
     * @param left The left bound of the menus drawing area
     * @param width The width of the menu
     */
    protected void drawMenu (final IGraphicsContext gc, final IGraphicsConfiguration configuration, IGraphicsDimensions dimensions, final double left, final double width)
    {
        final double separatorSize = dimensions.getSeparatorSize ();
        final double menuHeight = dimensions.getMenuHeight ();
        final ColorEx borderColor = configuration.getColorBorder ();
        if (this.menuName == null || this.menuName.length () == 0)
        {
            // Remove the 2 pixels of the previous menus border line
            gc.fillRectangle (left - separatorSize, menuHeight - 2, separatorSize, 1, borderColor);
            return;
        }

        final ColorEx textColor = configuration.getColorText ();
        gc.fillRectangle (left, 0, width, menuHeight - 1.0, this.isMenuSelected ? textColor : borderColor);
        gc.fillRectangle (left, menuHeight - 2.0, width + separatorSize, 1, textColor);

        final double unit = dimensions.getUnit ();
        gc.drawTextInBounds (this.menuName, left, 1, width, unit + separatorSize, Align.CENTER, this.isMenuSelected ? borderColor : textColor, unit);
    }


    /**
     * Get the maximum value range.
     *
     * @return The maximum value
     */
    public static double getMaxValue ()
    {
        return maxValue;
    }


    /**
     * Set the maximum value range. The default is 1024.0.
     *
     * @param maxValue The new maximum value
     */
    public static void setMaxValue (final double maxValue)
    {
        AbstractGridElement.maxValue = maxValue;
    }
}
