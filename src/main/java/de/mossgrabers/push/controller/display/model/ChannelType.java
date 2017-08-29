// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller.display.model;

/**
 * The different types of channels.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum ChannelType
{
    /** Audio Track */
    AUDIO,
    /** Instrument Track */
    INSTRUMENT,
    /** Hybrid Track (Audio + Midi) */
    HYBRID,
    /** Group Track */
    GROUP, // isGroup
    /** Effect Track */
    EFFECT,
    /** Master Track */
    MASTER,
    /** A device layer */
    LAYER
}
