// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.resource.ResourceHandler;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IBounds;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.graphics.IGraphicsInfo;
import de.mossgrabers.framework.graphics.IImage;


/**
 * A component which draws a label. A label is a text and an optional icon.
 *
 * @author Jürgen Moßgraber
 */
public class LabelComponent implements IComponent
{
    /** Several layouts for the label component. */
    public enum LabelLayout
    {
        /** Simple box. */
        PLAIN,
        /** A header label with a line below the text. */
        SMALL_HEADER,
        /** A label with a colored background. */
        COLORED
    }


    private final String      text;
    private final String      icon;
    private final boolean     isSelected;
    private final boolean     isActive;
    private final LabelLayout layout;

    private ColorEx           backgroundColor;


    /**
     * Constructor.
     *
     * @param text The text of the label
     * @param icon The optional icon
     * @param color The background color
     * @param isSelected True if the component should be drawn in selected state
     * @param isActive True if the component should be displayed as active
     * @param layout The layout to draw the component
     */
    public LabelComponent (final String text, final String icon, final ColorEx color, final boolean isSelected, final boolean isActive, final LabelLayout layout)
    {
        this.text = text;
        this.icon = icon;
        this.backgroundColor = color;
        this.isSelected = isSelected;

        this.isActive = isActive;
        this.layout = layout;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        if (this.layout == LabelLayout.COLORED)
        {
            this.drawColoredLayout (info);
            return;
        }

        final IGraphicsConfiguration configuration = info.getConfiguration ();
        final IBounds bounds = info.getBounds ();

        final IGraphicsDimensions dimensions = info.getDimensions ();
        final double separatorSize = dimensions.getSeparatorSize ();
        final double menuHeight = dimensions.getMenuHeight ();

        final ColorEx bgColor = this.getBackgroundColor (configuration);
        final ColorEx textColor = ColorEx.calcContrastColor (bgColor);
        final IGraphicsContext gc = info.getContext ();

        if (this.text == null || this.text.length () == 0)
        {
            if (this.layout == LabelLayout.SMALL_HEADER)
            {
                // Remove the 2 pixels of the previous menus border line
                gc.fillRectangle (bounds.left () - separatorSize, menuHeight - 2, separatorSize, 1, configuration.getColorBorder ());
            }
            return;
        }

        final double left = bounds.left ();
        final double top = bounds.top ();
        final double width = bounds.width ();
        final double height = bounds.height ();

        if (this.layout == LabelLayout.SMALL_HEADER)
        {
            gc.fillRectangle (left, top, width, menuHeight - 1.0, this.isSelected ? textColor : bgColor);
            gc.fillRectangle (left, menuHeight - 2.0, width + separatorSize, 1, textColor);
        }
        else
            gc.fillRectangle (left, top, width, height, this.isSelected ? textColor : bgColor);

        final double unit = dimensions.getUnit ();

        if (this.layout == LabelLayout.SMALL_HEADER)
            gc.drawTextInBounds (this.text, left, 1, width, unit + separatorSize, Align.CENTER, this.isSelected ? bgColor : textColor, unit);
        else
            gc.drawTextInBounds (this.text, left, top, width, height, Align.CENTER, this.isSelected ? bgColor : textColor, height / 2);
    }


    /**
     * Draws the label in the SEPARATE_COLOR layout.
     *
     * @param info All necessary information to draw the component
     */
    protected void drawColoredLayout (final IGraphicsInfo info)
    {
        final IGraphicsContext gc = info.getContext ();
        final IGraphicsDimensions dimensions = info.getDimensions ();
        final IGraphicsConfiguration configuration = info.getConfiguration ();
        final IBounds bounds = info.getBounds ();
        final double left = bounds.left ();
        final double width = bounds.width ();
        final double top = bounds.top ();
        final double height = bounds.height ();
        final double inset = dimensions.getInset ();
        final double unit = dimensions.getUnit ();

        // The tracks icon and name
        final String iconName = this.getIcon ();

        final double textHeight = 1.2 * unit + 1;
        final double textTop = top + height - textHeight;

        // The track color section
        final ColorEx infoColor = this.backgroundColor;
        gc.fillRectangle (left, textTop, width, textHeight, this.isActive ? infoColor : ColorEx.evenDarker (infoColor));

        double offsetX = 0;
        if (iconName != null)
        {
            final double leftColumn = left + inset - 1;
            final IImage image = ResourceHandler.getSVGImage (iconName);
            offsetX = image.getWidth () + 1.4 * inset;
            final double y = textTop + (textHeight - image.getHeight ()) / 2.0;
            ColorEx maskColor = this.getMaskColor (configuration);
            if (maskColor == null)
                gc.drawImage (image, leftColumn, y);
            else
            {
                if (this.isSelected)
                    maskColor = ColorEx.calcContrastColor (maskColor);
                gc.maskImage (image, leftColumn, y, this.modifyIfOff (maskColor));
            }
        }

        ColorEx colorText = configuration.getColorText ();
        if (this.isSelected)
            colorText = ColorEx.calcContrastColor (colorText);
        gc.drawTextInBounds (this.text, left + offsetX, textTop, width - offsetX - 1.4 * inset, textHeight, Align.LEFT, this.modifyIfOff (colorText), unit);
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
     * Get the text of the label.
     *
     * @return The name
     */
    public String getText ()
    {
        return this.text;
    }


    /**
     * Get an alternative background color.
     *
     * @param backgroundColor The color
     */
    public void setColor (final ColorEx backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }


    /**
     * Is the component selected?
     *
     * @return True if selected
     */
    public boolean isSelected ()
    {
        return this.isSelected;
    }


    private ColorEx modifyIfOff (final ColorEx color)
    {
        return this.isActive ? color : ColorEx.dimToGray (color);
    }


    protected ColorEx getMaskColor (final IGraphicsConfiguration configuration)
    {
        return configuration.getColorText ();
    }


    private ColorEx getBackgroundColor (final IGraphicsConfiguration configuration)
    {
        if (this.isSelected)
            return configuration.getColorText ();

        if (this.backgroundColor != null)
            return this.backgroundColor;

        if (this.layout == LabelLayout.COLORED)
            return configuration.getColorBackground ();

        if (this.layout == LabelLayout.PLAIN)
            return configuration.getColorBackgroundDarker ();

        // SMALL_HEADER
        return configuration.getColorBorder ();
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.backgroundColor == null ? 0 : this.backgroundColor.hashCode ());
        result = prime * result + (this.icon == null ? 0 : this.icon.hashCode ());
        result = prime * result + (this.isActive ? 1231 : 1237);
        result = prime * result + (this.isSelected ? 1231 : 1237);
        result = prime * result + (this.layout == null ? 0 : this.layout.hashCode ());
        return prime * result + (this.text == null ? 0 : this.text.hashCode ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || this.getClass () != obj.getClass ())
            return false;
        final LabelComponent other = (LabelComponent) obj;
        if (this.backgroundColor == null)
        {
            if (other.backgroundColor != null)
                return false;
        }
        else if (!this.backgroundColor.equals (other.backgroundColor))
            return false;
        if (this.icon == null)
        {
            if (other.icon != null)
                return false;
        }
        else if (!this.icon.equals (other.icon))
            return false;
        if (this.isActive != other.isActive || this.isSelected != other.isSelected || this.layout != other.layout)
            return false;
        if (this.text == null)
        {
            if (other.text != null)
                return false;
        }
        else if (!this.text.equals (other.text))
            return false;
        return true;
    }
}
