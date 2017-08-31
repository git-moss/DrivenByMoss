// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller.display.model.grid;

import de.mossgrabers.push.PushConfiguration;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.api.GraphicsOutput;
import com.bitwig.extension.api.TextExtents;


/**
 * Abstract base class for an element in the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractGridElement implements GridElement
{
    /** The maximum possible value for a parameter. */
    private static double   maxValue = 1024;

    private final String    name;
    private final String    icon;
    private final Color     color;
    private final boolean   isSelected;

    protected final boolean isMenuSelected;
    protected final String  menuName;

    protected enum Align
    {
        LEFT,
        RIGHT,
        CENTER
    }


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
    public AbstractGridElement (final String menuName, final boolean isMenuSelected, final String icon, final String name, final Color color, final boolean isSelected)
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
    public Color getColor ()
    {
        return this.color;
    }


    /**
     * Is the grid element seleted?
     *
     * @return True if selected
     */
    public boolean isSelected ()
    {
        return this.isSelected;
    }


    /**
     * Draws a text into a boundary. The text is clipped on the right border of the bounds.
     * Calculates the text descent.
     *
     * @param g The graphics context in which to draw
     * @param text The text to draw
     * @param x The x position of the boundary
     * @param y The y position of the boundary
     * @param width The width position of the boundary
     * @param height The height position of the boundary
     * @param alignment The alignment of the text: Label.LEFT or Label.CENTER
     * @param color The color of the text
     */
    public static void drawTextInBounds (final GraphicsOutput g, final String text, final double x, final double y, final double width, final double height, final Align alignment, final Color color)
    {
        if (text == null || text.length () == 0)
            return;

        final TextExtents textExtents = g.getTextExtents (text);
        // We need to calculate the text height from a character which has no ascent, since showText
        // always draws the text on the baseline of the font!
        final double h = g.getTextExtents ("T").getHeight ();
        final double pos = alignment == Align.CENTER ? x + (width - textExtents.getWidth ()) / 2.0 : x;

        g.save ();
        g.rectangle (x, y, width, height);
        g.clip ();
        g.setColor (color);
        g.moveTo (pos, y + (height + h) / 2);
        g.showText (text);
        g.resetClip ();
        g.restore ();
    }


    /**
     * Draws a text centered into a height (horizontally). The text is not clipped.
     *
     * @param g The graphics context in which to draw
     * @param text The text to draw
     * @param x The x position of the boundary
     * @param y The y position of the boundary
     * @param height The height position of the boundary
     * @param color The color of the text
     */
    public static void drawTextInHeight (final GraphicsOutput g, final String text, final double x, final double y, final double height, final Color color)
    {
        if (text == null || text.length () == 0)
            return;

        // We need to calculate the text height from a character which has no ascent, since showText
        // always draws the text on the baseline of the font!
        final double h = g.getTextExtents ("T").getHeight ();

        g.save ();
        g.setColor (color);
        g.moveTo (x, y + (height + h) / 2);
        g.showText (text);
        g.restore ();
    }


    /**
     * Draws a menu at the top of the element.
     *
     * @param gc The graphics context
     * @param left The left bound of the menus drawing area
     * @param width The width of the menu
     * @param configuration The layout settings to use
     */
    protected void drawMenu (final GraphicsOutput gc, final double left, final double width, final PushConfiguration configuration)
    {
        final Color borderColor = configuration.getColorBorder ();
        if (this.menuName == null || this.menuName.length () == 0)
        {
            // Remove the 2 pixels of the previous menus border line
            gc.setColor (borderColor);
            gc.rectangle (left - SEPARATOR_SIZE, MENU_HEIGHT - 2, SEPARATOR_SIZE, 1);
            gc.fill ();
            return;
        }

        final Color textColor = configuration.getColorText ();
        gc.setColor (this.isMenuSelected ? textColor : borderColor);
        gc.rectangle (left, 0, width, MENU_HEIGHT - 1.0);
        gc.fill ();

        gc.setColor (textColor);
        gc.rectangle (left, MENU_HEIGHT - 2.0, width + SEPARATOR_SIZE, 1);
        gc.fill ();

        gc.setColor (this.isMenuSelected ? borderColor : textColor);
        gc.setFontSize (UNIT);
        drawTextInBounds (gc, this.menuName, left, 1, width, UNIT + SEPARATOR_SIZE, Align.CENTER, this.isMenuSelected ? borderColor : textColor);
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
