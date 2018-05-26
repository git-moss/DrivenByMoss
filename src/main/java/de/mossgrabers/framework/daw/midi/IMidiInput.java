// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * Interface to a MIDI input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
     * Set a callback for midi system exclusive messages coming from this input.
     *
     * @param callback The callback
     */
    void setSysexCallback (MidiSysExCallback callback);


    /**
     * Specifies a translation table which defines the actual key value (0-127) of notes arriving in
     * the DAW for each note key potentially received from the hardware. This is used for
     * note-on/off and polyphonic aftertouch events. Specifying a value of -1 for a key means that
     * notes with the key value will be filtered out.
     *
     * Typically this method is used to implement transposition or scale features in controller
     * scripts. By default an identity transform table is configured, which means that all incoming
     * MIDI notes keep their original key value when being sent into the DAW.
     *
     * @param table An array which should contain 128 entries. Each entry should be a note value in
     *            the range [0..127] or -1 in case of filtering.
     */
    void setKeyTranslationTable (Integer [] table);


    /**
     * Specifies a translation table which defines the actual velocity value (0-127) of notes
     * arriving in the DAW for each note velocity potentially received from the hardware. This is
     * used for note-on events only.
     *
     * Typically this method is used to implement velocity curves or fixed velocity mappings in
     * controller scripts. By default an identity transform table is configured, which means that
     * all incoming MIDI notes keep their original velocity when being sent into the DAW.
     *
     * @param table An array which should contain 128 entries. Each entry should be a note value in
     *            the range [0..127] or -1 in case of filtering.
     */
    void setVelocityTranslationTable (Integer [] table);


    /**
     * Create a note input.
     *
     * @param name the name of the note input as it appears in the track input choosers in the DAW
     *
     * @param filters a filter string formatted as hexadecimal value with `?` as wildcard. For
     *            example `80????` would match note-off on channel 1 (0). When this parameter is
     *            {@null}, a standard filter will be used to forward note-related messages on
     *            channel 1 (0).
     */
    void createNoteInput (final String name, final String... filters);


    /**
     * Sends a midi short message to the DAW.
     *
     * @param status The MIDI status byte
     * @param data1 The MIDI data byte 1
     * @param data2 The MIDI data byte 2
     */
    void sendRawMidiEvent (int status, int data1, int data2);


    /**
     * Toggle note repeat on/off.
     */
    void toggleRepeat ();
}