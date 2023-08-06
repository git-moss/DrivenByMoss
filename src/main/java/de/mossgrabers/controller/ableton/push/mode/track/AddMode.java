// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.track;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.resource.ChannelType;


/**
 * The sub-mode of this add-mode.
 *
 * @author Jürgen Moßgraber
 */
public enum AddMode
{
    /** Add an instrument track. */
    INSTRUMENT("Instrument", ColorEx.YELLOW, ChannelType.INSTRUMENT),
    /** Add an audio track. */
    AUDIO("Audio", ColorEx.GREEN, ChannelType.AUDIO),
    /** Add an effect track. */
    EFFECT("Effect", ColorEx.BLUE, ChannelType.EFFECT),
    /** Add a device. */
    DEVICE("Device", ColorEx.ORANGE, ChannelType.UNKNOWN);


    private final String      label;
    private final ColorEx     color;
    private final ChannelType channelType;


    /**
     * Constructor.
     *
     * @param label The label
     * @param color The color
     * @param channelType The type of the channel
     */
    private AddMode (final String label, final ColorEx color, final ChannelType channelType)
    {
        this.label = label;
        this.color = color;
        this.channelType = channelType;
    }


    /**
     * Get the label.
     *
     * @return The label
     */
    public String getLabel ()
    {
        return this.label;
    }


    /**
     * Get the color.
     *
     * @return The color
     */
    public ColorEx getColor ()
    {
        return this.color;
    }


    /**
     * Get the type of the channel to add.
     *
     * @return The channel type
     */
    public ChannelType getChannelType ()
    {
        return this.channelType;
    }
}
