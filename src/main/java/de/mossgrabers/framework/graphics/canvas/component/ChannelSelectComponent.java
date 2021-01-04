// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.resource.ChannelType;

import java.util.EnumMap;


/**
 * An element in the grid which contains a channel name and color but no settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ChannelSelectComponent extends MenuComponent
{
    private static final EnumMap<ChannelType, String> ICONS = new EnumMap<> (ChannelType.class);

    static
    {
        ICONS.put (ChannelType.AUDIO, "track/audio_track.svg");
        ICONS.put (ChannelType.INSTRUMENT, "track/instrument_track.svg");
        ICONS.put (ChannelType.GROUP, "track/group_track.svg");
        ICONS.put (ChannelType.EFFECT, "track/return_track.svg");
        ICONS.put (ChannelType.HYBRID, "track/hybrid_track.svg");
        ICONS.put (ChannelType.MASTER, "track/master_track.svg");
        ICONS.put (ChannelType.LAYER, "track/multi_layer.svg");
    }


    /**
     * Constructor.
     *
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param type The type of the track
     * @param isActive True if channel is activated
     */
    public ChannelSelectComponent (final ChannelType type, final String menuName, final boolean isMenuSelected, final String name, final ColorEx color, final boolean isSelected, final boolean isActive)
    {
        super (menuName, isMenuSelected, name, getIcon (type), color, isSelected, isActive);
    }


    /**
     * Get the icon for the channel type.
     *
     * @param type The for which to get the icon
     * @return The icon or null if the channel type is null
     */
    public static String getIcon (final ChannelType type)
    {
        return type == null ? null : ICONS.get (type);
    }
}
