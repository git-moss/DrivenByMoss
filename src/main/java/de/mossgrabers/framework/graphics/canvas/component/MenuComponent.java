// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.IGraphicsInfo;
import de.mossgrabers.framework.graphics.canvas.component.LabelComponent.LabelLayout;


/**
 * A component which contains a menu and a channels' icon, name and color.
 *
 * @author Jürgen Moßgraber
 */
public class MenuComponent implements IComponent
{
    protected final LabelComponent header;
    protected final LabelComponent footer;


    /**
     * Constructor.
     *
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param icon The icon to use
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param isActive True if channel is activated
     */
    public MenuComponent (final String menuName, final boolean isMenuSelected, final String name, final String icon, final ColorEx color, final boolean isSelected, final boolean isActive)
    {
        this (menuName, isMenuSelected, name, icon, color, isSelected, isActive, LabelLayout.SEPARATE_COLOR);
    }


    /**
     * Constructor.
     *
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param icon The icon to use
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param isActive True if channel is activated
     * @param lowerLayout The layout for the lower label
     */
    public MenuComponent (final String menuName, final boolean isMenuSelected, final String name, final String icon, final ColorEx color, final boolean isSelected, final boolean isActive, final LabelLayout lowerLayout)
    {
        this.header = new LabelComponent (menuName, null, null, isMenuSelected, true, LabelLayout.SMALL_HEADER);
        this.footer = new LabelComponent (name, icon, color, isSelected, isActive, lowerLayout);
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        this.header.draw (info);

        final String name = this.footer.getText ();
        // Element is off if the name is empty
        if (name == null || name.length () == 0)
            return;

        final double menuHeight = 2 * info.getDimensions ().getMenuHeight ();
        this.footer.draw (info.withBounds (info.getBounds ().height () - menuHeight, menuHeight));
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.footer == null ? 0 : this.footer.hashCode ());
        result = prime * result + (this.header == null ? 0 : this.header.hashCode ());
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
        final MenuComponent other = (MenuComponent) obj;
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
        return true;
    }
}
