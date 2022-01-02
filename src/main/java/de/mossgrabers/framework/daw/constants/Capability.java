// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.constants;

/**
 * Capabilities of the hosts API.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum Capability
{
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

    /** The API provides support for editing a notes' mute state. */
    NOTE_EDIT_MUTE,
    /** The API provides support for editing note velocity spread. */
    NOTE_EDIT_VELOCITY_SPREAD,
    /** The API provides support for editing note release velocity. */
    NOTE_EDIT_RELEASE_VELOCITY,
    /** Supports editing of note expressions like gain, panorama, pitch, timbre and pressure. */
    NOTE_EDIT_EXPRESSIONS,
    /** Supports editing of note repeat options like count, curve, etc. */
    NOTE_EDIT_REPEAT,
    /** Supports editing of note chance. */
    NOTE_EDIT_CHANCE,
    /** Supports editing of note occurrence. */
    NOTE_EDIT_OCCURRENCE,
    /** Supports editing of note recurrence. */
    NOTE_EDIT_RECCURRENCE,

    /** The API provides support quantizing the note lengths of MIDI input. */
    QUANTIZE_INPUT_NOTE_LENGTH,
    /** The API provides support quantizing notes by an amount percentage. */
    QUANTIZE_AMOUNT,

    /** The API provides support changing the cue/preview volume. */
    CUE_VOLUME,

    /** Devices have additional slot chains. */
    HAS_SLOT_CHAINS,
    /** There is a specific drum device. */
    HAS_DRUM_DEVICE,
    /** Support for a cross-fader. */
    HAS_CROSSFADER,
    /** If the DAW supports track/device pinning. */
    HAS_PINNING,
    /** The host has a dedicated send bank. */
    HAS_EFFECT_BANK,
    /** The host supports browser preview. */
    HAS_BROWSER_PREVIEW
}
