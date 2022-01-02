// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.resource;

/**
 * The different types of channels.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
    LAYER
}
