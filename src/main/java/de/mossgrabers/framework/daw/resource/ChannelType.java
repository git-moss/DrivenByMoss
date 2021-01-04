// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
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
    /** Audio Track. */
    AUDIO,
    /** Instrument Track. */
    INSTRUMENT,
    /** Hybrid Track (Audio + Midi). */
    HYBRID,
    /** A group Track. */
    GROUP,
    /** An effect Track. */
    EFFECT,
    /** The master Track. */
    MASTER,
    /** A device layer. */
    LAYER
}
