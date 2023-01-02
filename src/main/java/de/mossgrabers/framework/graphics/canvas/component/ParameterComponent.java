// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.daw.resource.DeviceTypes;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.graphics.IGraphicsInfo;
import de.mossgrabers.framework.graphics.canvas.component.LabelComponent.LabelLayout;


/**
 * An element in the grid which contains a fader and text for a value.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterComponent extends MenuComponent
{
    private final String  paramName;
    private final String  paramValueText;
    private final int     paramValue;
    private final int     modulatedParamValue;
    private final boolean isTouched;


    /**
     * Constructor. A generic parameter.
     *
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param paramName The name of the parameter
     * @param paramValue The value of the fader
     * @param modulatedParamValue The modulated value of the fader, -1 if not modulated
     * @param paramValueText The textual form of the faders value
     * @param isTouched True if touched
     */
    public ParameterComponent (final String menuName, final boolean isMenuSelected, final String name, final ColorEx color, final boolean isSelected, final String paramName, final int paramValue, final int modulatedParamValue, final String paramValueText, final boolean isTouched)
    {
        this (menuName, isMenuSelected, name, (String) null, color, isSelected, paramName, paramValue, modulatedParamValue, paramValueText, isTouched);
    }


    /**
     * Constructor. A parameter with a device footer.
     *
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param deviceName The name of the device
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param paramName The name of the parameter
     * @param paramValue The value of the fader
     * @param modulatedParamValue The modulated value of the fader, -1 if not modulated
     * @param paramValueText The textual form of the faders value
     * @param isTouched True if touched
     */
    public ParameterComponent (final String menuName, final boolean isMenuSelected, final String name, final String deviceName, final ColorEx color, final boolean isSelected, final String paramName, final int paramValue, final int modulatedParamValue, final String paramValueText, final boolean isTouched)
    {
        this (menuName, isMenuSelected, name, deviceName, color, isSelected, paramName, paramValue, modulatedParamValue, paramValueText, isTouched, LabelLayout.SEPARATE_COLOR);
    }


    /**
     * Constructor. A parameter with a device footer.
     *
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param deviceName The name of the device
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param paramName The name of the parameter
     * @param paramValue The value of the fader
     * @param modulatedParamValue The modulated value of the fader, -1 if not modulated
     * @param paramValueText The textual form of the faders value
     * @param isTouched True if touched
     * @param lowerLayout The layout for the lower label
     */
    public ParameterComponent (final String menuName, final boolean isMenuSelected, final String name, final String deviceName, final ColorEx color, final boolean isSelected, final String paramName, final int paramValue, final int modulatedParamValue, final String paramValueText, final boolean isTouched, final LabelLayout lowerLayout)
    {
        super (menuName, isMenuSelected, name, deviceName == null ? null : DeviceTypes.getIconId (deviceName), color, isSelected, true, lowerLayout);

        this.paramName = paramName;
        this.paramValue = paramValue;
        this.modulatedParamValue = modulatedParamValue;
        this.paramValueText = paramValueText;
        this.isTouched = isTouched;
    }


    /**
     * Constructor. A parameter with a channel footer.
     *
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param type The type of the channel
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param paramName The name of the parameter
     * @param paramValue The value of the fader
     * @param modulatedParamValue The modulated value of the fader, -1 if not modulated
     * @param paramValueText The textual form of the faders value
     * @param isTouched True if touched
     */
    public ParameterComponent (final String menuName, final boolean isMenuSelected, final String name, final ChannelType type, final ColorEx color, final boolean isSelected, final String paramName, final int paramValue, final int modulatedParamValue, final String paramValueText, final boolean isTouched)
    {
        super (menuName, isMenuSelected, name, ChannelSelectComponent.getIcon (type, false), color, isSelected, true);

        this.paramName = paramName;
        this.paramValue = paramValue;
        this.modulatedParamValue = modulatedParamValue;
        this.paramValueText = paramValueText;
        this.isTouched = isTouched;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        super.draw (info);

        final IGraphicsContext gc = info.getContext ();
        final IGraphicsDimensions dimensions = info.getDimensions ();
        final IGraphicsConfiguration configuration = info.getConfiguration ();
        final double left = info.getBounds ().left ();
        final double width = info.getBounds ().width ();
        final double height = info.getBounds ().height ();

        final double separatorSize = dimensions.getSeparatorSize ();
        final double menuHeight = dimensions.getMenuHeight ();
        final double unit = dimensions.getUnit ();
        final double controlsTop = dimensions.getControlsTop ();
        final double inset = dimensions.getInset ();

        final boolean isValueMissing = this.paramValue == -1;
        final boolean isModulated = this.modulatedParamValue != -1;

        final int trackRowHeight = (int) (1.6 * unit);
        final double trackRowTop = height - trackRowHeight - unit - separatorSize;

        // Component is off if the name is empty
        if (this.paramName == null || this.paramName.length () == 0)
            return;

        final double elementWidth = width - 2 * inset;
        final double elementHeight = (trackRowTop - controlsTop - inset) / 3;

        // Draw the background
        final ColorEx backgroundColor = configuration.getColorBackground ();
        gc.fillRectangle (left, menuHeight + 1, width, trackRowTop - (isValueMissing ? controlsTop + elementHeight : menuHeight + 1), this.isTouched ? configuration.getColorBackgroundLighter () : backgroundColor);

        // Draw the name and value texts
        final ColorEx textColor = configuration.getColorText ();
        final double fontSize = elementHeight * 2 / 3;
        gc.drawTextInBounds (this.paramName, left + inset - 1, controlsTop - inset, elementWidth, elementHeight, Align.CENTER, textColor, fontSize);
        gc.drawTextInBounds (this.paramValueText, left + inset - 1, controlsTop - inset + elementHeight, elementWidth, elementHeight, Align.CENTER, textColor, fontSize);

        // Value slider
        if (isValueMissing)
            return;
        final double elementInnerWidth = elementWidth - 2;
        final double maxValue = dimensions.getParameterUpperBound ();
        final double value = isModulated ? this.modulatedParamValue : this.paramValue;
        final double valueSliderWidth = value >= maxValue - 1 ? elementInnerWidth : elementInnerWidth * value / maxValue;
        final double innerTop = controlsTop + 2 * elementHeight + 1;
        final ColorEx borderColor = configuration.getColorBorder ();
        gc.fillRectangle (left + inset - 1, controlsTop + 2 * elementHeight, elementWidth, elementHeight, borderColor);
        gc.fillRectangle (left + inset, innerTop, valueSliderWidth, elementHeight - 2, configuration.getColorFader ());

        final double w = this.isTouched ? 3 : 1;
        final double valueWidth = this.paramValue >= maxValue - 1 ? elementInnerWidth : elementInnerWidth * this.paramValue / maxValue;
        gc.fillRectangle (left + inset + Math.max (0, valueWidth - w), innerTop, w, elementHeight - 2, configuration.getColorEdit ());
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = super.hashCode ();
        result = prime * result + (this.isTouched ? 1231 : 1237);
        result = prime * result + this.modulatedParamValue;
        result = prime * result + (this.paramName == null ? 0 : this.paramName.hashCode ());
        result = prime * result + this.paramValue;
        result = prime * result + (this.paramValueText == null ? 0 : this.paramValueText.hashCode ());
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
        final ParameterComponent other = (ParameterComponent) obj;
        if (this.isTouched != other.isTouched || this.modulatedParamValue != other.modulatedParamValue)
            return false;
        if (this.paramName == null)
        {
            if (other.paramName != null)
                return false;
        }
        else if (!this.paramName.equals (other.paramName))
            return false;
        if (this.paramValue != other.paramValue)
            return false;
        if (this.paramValueText == null)
        {
            if (other.paramValueText != null)
                return false;
        }
        else if (!this.paramValueText.equals (other.paramValueText))
            return false;
        return true;
    }
}
