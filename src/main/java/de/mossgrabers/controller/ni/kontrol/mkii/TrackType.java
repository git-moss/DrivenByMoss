// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii;

import de.mossgrabers.framework.daw.resource.ChannelType;


/**
 * Track types for the NI Host protocol.
 *
 * @author Jürgen Moßgraber
 */
public class TrackType
{
    /** A non existing track. */
    public static final int EMPTY      = 0;
    /** A MIDI, Audio or Group track. */
    public static final int GENERIC    = 1;
    /** A MIDI track. */
    public static final int MIDI       = 2;
    /** An Audio track. */
    public static final int AUDIO      = 3;
    /** A Group track. */
    public static final int GROUP      = 4;
    /** A Send track. */
    public static final int RETURN_BUS = 5;
    /** A master track. */
    public static final int MASTER     = 6;


    /**
     * Constructor.
     */
    private TrackType ()
    {
        // Intentionally empty
    }


    /**
     * Converts the given track type to a channel type.
     *
     * @param trackType The track type
     * @return The ChannelType
     */
    public static ChannelType toChannelType (final int trackType)
    {
        switch (trackType)
        {
            case GENERIC:
                return ChannelType.HYBRID;
            case AUDIO:
                return ChannelType.AUDIO;
            case MIDI:
                return ChannelType.INSTRUMENT;
            case GROUP:
                return ChannelType.GROUP;
            case RETURN_BUS:
                return ChannelType.EFFECT;
            case MASTER:
                return ChannelType.MASTER;
            case EMPTY:
            default:
                return ChannelType.UNKNOWN;
        }
    }


    /**
     * Converts the given channel type to a track type.
     *
     * @param channelType The channel type
     * @return The track type
     */
    public static int toTrackType (final ChannelType channelType)
    {
        switch (channelType)
        {
            case HYBRID:
                // GENERIC would be more accurate here but it seems that Mute and Solo are not
                // working on generic tracks
                return AUDIO;
            case AUDIO:
                return AUDIO;
            case INSTRUMENT:
                return MIDI;
            case GROUP, GROUP_OPEN:
                return GROUP;
            case EFFECT:
                return RETURN_BUS;
            case MASTER:
                return MASTER;
            case UNKNOWN:
            default:
                return EMPTY;
        }
    }
}
