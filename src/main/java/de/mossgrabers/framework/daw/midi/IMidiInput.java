// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteControl;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.controller.hardware.IHwContinuousControl;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;


/**
 * Interface to a MIDI input.
 *
 * @author Jürgen Moßgraber
 */
public interface IMidiInput
{
    /**
     * Registers a callback for receiving short (normal) MIDI messages on this MIDI input port.
     *
     * @param callback A callback function that receives three MIDI message parameters
     */
    void setMidiCallback (MidiShortCallback callback);


    /**
     * Set a callback for MIDI system exclusive messages coming from this input.
     *
     * @param callback The callback
     */
    void setSysexCallback (MidiSysExCallback callback);


    /**
     * Create a note input.
     *
     * @param name the name of the note input as it appears in the track input choosers in the DAW
     * @param filters a filter string formatted as hexadecimal value with `?` as wildcard. For
     *            example `80????` would match note-off on channel 1 (0). When this parameter is
     *            {@null}, a standard filter will be used to forward note-related messages on
     *            channel 1 (0).
     * @return The note input
     */
    INoteInput createNoteInput (final String name, final String... filters);


    /**
     * Get the default note input.
     *
     * @return The input or null if none exists
     */
    INoteInput getDefaultNoteInput ();


    /**
     * Sends a MIDI short message to the DAW.
     *
     * @param status The MIDI status byte
     * @param data1 The MIDI data byte 1
     * @param data2 The MIDI data byte 2
     */
    void sendRawMidiEvent (int status, int data1, int data2);


    /**
     * Bind the given button to a MIDI command received on this MIDI input.
     *
     * @param button The button to bind
     * @param type The MIDI binding type
     * @param channel The MIDI channel
     * @param control The MIDI command (CC, Note, ...)
     */
    void bind (IHwButton button, BindType type, int channel, int control);


    /**
     * Bind a MIDI command coming from a MIDI input to the button.
     *
     * @param button The button to bind
     * @param type How to bind
     * @param channel The MIDI channel
     * @param control The MIDI CC or note to bind
     * @param value The specific value of the control to bind to
     */
    void bind (IHwButton button, BindType type, int channel, int control, int value);


    /**
     * Unbind the button from its MIDI command.
     *
     * @param button The button to unbind
     */
    void unbind (IHwButton button);


    /**
     * Bind the given fader to a MIDI command received on this MIDI input.
     *
     * @param fader The fader to bind
     * @param type The MIDI binding type
     * @param channel The MIDI channel
     * @param control The MIDI command (CC, Note, ...)
     */
    void bind (IHwFader fader, BindType type, int channel, int control);


    /**
     * Bind the given absolute knob to a MIDI command received on this MIDI input.
     *
     * @param absoluteControl The absolute control to bind
     * @param type The MIDI binding type
     * @param channel The MIDI channel
     * @param control The MIDI command (CC, Note, ...)
     */
    void bind (IHwAbsoluteControl absoluteControl, BindType type, int channel, int control);


    /**
     * Bind the given absolute control to a MIDI CC 14-bit command received on this MIDI input.
     *
     * @param absoluteControl The absolute control to bind
     * @param channel The MIDI channel
     * @param control The first MIDI CC command in the range of [0..31]. The second MIDI CC is the
     *            first plus 32
     */
    void bindHiRes (IHwAbsoluteControl absoluteControl, int channel, int control);


    /**
     * Unbind the given absolute control from a MIDI command.
     *
     * @param absoluteControl The absolute control to un-bind
     */
    void unbind (IHwAbsoluteControl absoluteControl);


    /**
     * Bind the given relative knob to a MIDI command received on this MIDI input.
     *
     * @param relativeKnob The relative knob to bind
     * @param type The MIDI binding type
     * @param channel The MIDI channel
     * @param control The MIDI command (CC, Note, ...)
     * @param encoding The encoding of the relative value
     */
    void bind (IHwRelativeKnob relativeKnob, BindType type, int channel, int control, RelativeEncoding encoding);


    /**
     * Un-bind the given relative knob from a MIDI command.
     *
     * @param relativeKnob The relative knob to un-bind
     */
    void unbind (IHwRelativeKnob relativeKnob);


    /**
     * Bind the given fader to a MIDI command received on this MIDI input as a touch action.
     *
     * @param continuousControl The continuous control to bind
     * @param type The MIDI binding type
     * @param channel The MIDI channel
     * @param control The MIDI command (CC, Note, ...)
     */
    void bindTouch (IHwContinuousControl continuousControl, BindType type, int channel, int control);
}