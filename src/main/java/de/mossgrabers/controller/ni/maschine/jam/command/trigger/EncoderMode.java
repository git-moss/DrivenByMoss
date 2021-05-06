// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.command.trigger;

/**
 * The mode of the encoder knob.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum EncoderMode
{
    /** The encoder controls the volume of the master channel. */
    MASTER_VOLUME,
    /** The encoder controls the volume of the selected track. */
    SELECTED_TRACK_VOLUME,
    /** The encoder controls the volume of the metronome. */
    METRONOME_VOLUME,
    /** The encoder controls the volume of the cue output. */
    CUE_VOLUME
}
