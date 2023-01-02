// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.core.command.trigger;

/**
 * The mode of the encoder knob.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum EncoderMode
{
    /** No mode is active. */
    OFF,
    /** The encoder controls the volume of the master channel. */
    MASTER_VOLUME,
    /** The encoder controls the panorama of the master channel. */
    MASTER_PANORAMA,
    /** The encoder controls the volume of the metronome. */
    METRONOME_VOLUME,
    /** The encoder controls the volume of the selected track. */
    SELECTED_TRACK_VOLUME,
    /** The encoder controls the panorama of the selected track. */
    SELECTED_TRACK_PANORAMA,
    /** The encoder controls the volume of the cue output. */
    CUE_VOLUME,
    /** The encoder controls the mix of the cue output. */
    CUE_MIX,
    /** The encoder controls the tempo - temporary mode. */
    TEMPORARY_TEMPO,
    /** The encoder controls the swing - temporary mode. */
    TEMPORARY_SWING,
    /** The encoder controls the play position. */
    PLAY_POSITION,
    /** The encoder controls the view parameter assigned to perform - temporary mode. */
    TEMPORARY_PERFORM,
    /** The encoder controls the view parameter assigned to notes - temporary mode. */
    TEMPORARY_NOTES,
    /** The encoder controls the view parameter assigned to lock - temporary mode. */
    TEMPORARY_LOCK,
    /** The encoder controls the view parameter assigned to tune - temporary mode. */
    TEMPORARY_TUNE,
    /** The encoder controls the result list of the browser - temporary mode. */
    TEMPORARY_BROWSER;
}
