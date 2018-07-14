// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.controller.display.grid;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.daw.resource.DeviceTypes;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsContext;


/**
 * An element in the grid which contains a fader and text for a value.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParamGridElement extends SelectionGridElement
{
    private final String  paramName;
    private final String  paramValueText;
    private final int     paramValue;
    private int           modulatedParamValue;
    private final boolean isTouched;
    private final String  deviceName;


    /**
     * Constructor.
     *
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param type The channel type if any
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param paramName The name of the parameter
     * @param paramValue The value of the fader
     * @param modulatedParamValue The modulated value of the fader, -1 if not modulated
     * @param paramValueText The textual form of the faders value
     * @param isTouched True if touched
     */
    public ParamGridElement (final String menuName, final boolean isMenuSelected, final String name, final ChannelType type, final ColorEx color, final boolean isSelected, final String paramName, final int paramValue, final int modulatedParamValue, final String paramValueText, final boolean isTouched)
    {
        this (menuName, isMenuSelected, name, null, type, color, isSelected, paramName, paramValue, modulatedParamValue, paramValueText, isTouched);
    }


    /**
     * Constructor.
     *
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The name of the grid element (track name, parameter name, etc.)
     * @param deviceName The name of the device
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param paramName The name of the parameter
     * @param paramValue The value of the fader
     * @param modulatedParamValue The modulated value of the fader, -1 if not modulated
     * @param paramValueText The textual form of the faders value
     * @param isTouched True if touched
     */
    public ParamGridElement (final String menuName, final boolean isMenuSelected, final String name, final String deviceName, final ColorEx color, final boolean isSelected, final String paramName, final int paramValue, final int modulatedParamValue, final String paramValueText, final boolean isTouched)
    {
        this (menuName, isMenuSelected, name, deviceName, null, color, isSelected, paramName, paramValue, modulatedParamValue, paramValueText, isTouched);
    }


    /**
     * Constructor.
     *
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param deviceName The name of the device
     * @param type The channel type if any
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param paramName The name of the parameter
     * @param paramValue The value of the fader
     * @param modulatedParamValue The modulated value of the fader, -1 if not modulated
     * @param paramValueText The textual form of the faders value
     * @param isTouched True if touched
     */
    private ParamGridElement (final String menuName, final boolean isMenuSelected, final String name, final String deviceName, final ChannelType type, final ColorEx color, final boolean isSelected, final String paramName, final int paramValue, final int modulatedParamValue, final String paramValueText, final boolean isTouched)
    {
        super (menuName, isMenuSelected, name, color, isSelected, type);

        this.deviceName = deviceName;
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.modulatedParamValue = modulatedParamValue;
        this.paramValueText = paramValueText;
        this.isTouched = isTouched;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final double left, final double width, final double height, final PushConfiguration configuration)
    {
        this.drawMenu (gc, left, width, configuration);

        final boolean isValueMissing = this.paramValue == -1;
        final boolean isModulated = this.modulatedParamValue != -1;

        final double trackRowTop = height - TRACK_ROW_HEIGHT - UNIT - SEPARATOR_SIZE;
        final String name = this.getName ();
        if (name != null && name.length () > 0)
            this.drawTrackInfo (gc, left, width, height, trackRowTop, name, configuration);

        // Element is off if the name is empty
        if (this.paramName == null || this.paramName.length () == 0)
            return;

        final double elementWidth = width - 2 * INSET;
        final double elementHeight = (trackRowTop - CONTROLS_TOP - INSET) / 3;

        // Draw the background
        final ColorEx backgroundColor = configuration.getColorBackground ();
        gc.fillRectangle (left, MENU_HEIGHT + 1, width, trackRowTop - (isValueMissing ? CONTROLS_TOP + elementHeight : MENU_HEIGHT + 1), this.isTouched ? configuration.getColorBackgroundLighter () : backgroundColor);

        // Draw the name and value texts
        final ColorEx textColor = configuration.getColorText ();
        final double fontSize = elementHeight * 2 / 3;
        gc.drawTextInBounds (this.paramName, left + INSET - 1, CONTROLS_TOP - INSET, elementWidth, elementHeight, Align.CENTER, textColor, fontSize);
        gc.drawTextInBounds (this.paramValueText, left + INSET - 1, CONTROLS_TOP - INSET + elementHeight, elementWidth, elementHeight, Align.CENTER, textColor, fontSize);

        // Value slider
        if (isValueMissing)
            return;
        final double elementInnerWidth = elementWidth - 2;
        final double maxValue = getMaxValue ();
        final double value = isModulated ? this.modulatedParamValue : this.paramValue;
        final double valueSliderWidth = value >= maxValue - 1 ? elementInnerWidth : elementInnerWidth * value / maxValue;
        final double innerTop = CONTROLS_TOP + 2 * elementHeight + 1;
        final ColorEx borderColor = configuration.getColorBorder ();
        gc.fillRectangle (left + INSET - 1, CONTROLS_TOP + 2 * elementHeight, elementWidth, elementHeight, borderColor);
        gc.fillRectangle (left + INSET, innerTop, valueSliderWidth, elementHeight - 2, configuration.getColorFader ());

        final double w = this.isTouched ? 3 : 1;
        final double valueWidth = this.paramValue >= maxValue - 1 ? elementInnerWidth : elementInnerWidth * this.paramValue / maxValue;
        gc.fillRectangle (left + INSET + Math.max (0, valueWidth - w), innerTop, w, elementHeight - 2, configuration.getColorEdit ());
    }


    /** {@inheritDoc} */
    @Override
    public String getIcon ()
    {
        if (this.deviceName != null)
            return DeviceTypes.getIconId (this.deviceName);
        return super.getIcon ();
    }
}
