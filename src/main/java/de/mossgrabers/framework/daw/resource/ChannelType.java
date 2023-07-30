// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.resource;

import java.util.EnumMap;
import java.util.Map;


/**
 * The different types of channels.
 *
 * @author Jürgen Moßgraber
 */
public enum ChannelType
{
    /** A Track of unknown type. */
    UNKNOWN,
    /** Audio track. */
    AUDIO,
    /** Instrument track. */
    INSTRUMENT,
    /** Hybrid track (Audio + MIDI). */
    HYBRID,
    /** A group track. */
    GROUP,
    /** An open group track. */
    GROUP_OPEN,
    /** An effect track. */
    EFFECT,
    /** The master track. */
    MASTER,
    /** A device layer. */
    LAYER,
    /** A cue channel. */
    CUE;


    private static final Map<ChannelType, String> LABELS = new EnumMap<> (ChannelType.class);
    static
    {
        LABELS.put (UNKNOWN, "Unknown");
        LABELS.put (AUDIO, "Audio");
        LABELS.put (INSTRUMENT, "Instrument");
        LABELS.put (HYBRID, "Hybrid");
        LABELS.put (GROUP, "Group");
        LABELS.put (GROUP_OPEN, "Group");
        LABELS.put (EFFECT, "Effect");
        LABELS.put (MASTER, "Master");
        LABELS.put (LAYER, "Layer");
        LABELS.put (CUE, "Cue");
    }


    /**
     * Get the label of the given channel type.
     *
     * @param channelType THe channel type for which to get a label
     * @return The label
     */
    public static String getLabel (final ChannelType channelType)
    {
        return LABELS.get (channelType);
    }
}
