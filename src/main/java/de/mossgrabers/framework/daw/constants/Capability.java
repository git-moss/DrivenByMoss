// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.constants;

/**
 * Capabilities of the hosts API.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum Capability
{
    /** The API provides support for adding and deleting markers. */
    MARKERS,

    /** The API provides support for editing note repeat note lengths. */
    NOTE_REPEAT_LENGTH,
    /** The API provides support for editing note repeat swing factor. */
    NOTE_REPEAT_SWING,
    /** The API provides support for editing note repeat arpeggiator mode. */
    NOTE_REPEAT_MODE,
    /** The API provides support for editing note repeat arpeggiator octaves range. */
    NOTE_REPEAT_OCTAVES,
    /** The API provides support for editing note repeat arpeggiator free running. */
    NOTE_REPEAT_IS_FREE_RUNNING,
    /** The API provides support for editing note repeat use pressure. */
    NOTE_REPEAT_USE_PRESSURE_TO_VELOCITY,

    /** The API provides support for editing note release velocity. */
    NOTE_EDIT_RELEASE_VELOCITY,
    /** The API provides support for editing note pressure. */
    NOTE_EDIT_PRESSURE,
    /** The API provides support for editing note timbre. */
    NOTE_EDIT_TIMBRE,
    /** The API provides support for editing note panorama. */
    NOTE_EDIT_PANORAMA,
    /** The API provides support for editing note transpose. */
    NOTE_EDIT_TRANSPOSE,
    /** The API provides support for editing note gain. */
    NOTE_EDIT_GAIN,

    /** The API provides support quantizing the note lengths of MIDI input. */
    QUANTIZE_INPUT_NOTE_LENGTH,
    /** The API provides support quantizing notes by an amount percentage. */
    QUANTIZE_AMOUNT,

    /** The API provides support chaning the cue/preview volume. */
    CUE_VOLUME,

    /** Devices have additional slot chains. */
    HAS_SLOT_CHAINS,
    /** There is a specific drum device. */
    HAS_DRUM_DEVICE,
    /** Support for a crossfader. */
    HAS_CROSSFADER,
    /** If the DAW supports track/device pinning. */
    HAS_PINNING,
    /** The host has a dedicated send bank. */
    HAS_EFFECT_BANK
}
