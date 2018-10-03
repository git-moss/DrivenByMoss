// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * Interface to a MIDI input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IMidiInput extends INoteInput
{
    /**
     * Registers a callback for receiving short (normal) MIDI messages on this MIDI input port.
     *
     * @param callback A callback function that receives three MIDI message parameters
     */
    void setMidiCallback (MidiShortCallback callback);


    /**
     * Set a callback for midi system exclusive messages coming from this input.
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
     * Sends a midi short message to the DAW.
     *
     * @param status The MIDI status byte
     * @param data1 The MIDI data byte 1
     * @param data2 The MIDI data byte 2
     */
    void sendRawMidiEvent (int status, int data1, int data2);
}