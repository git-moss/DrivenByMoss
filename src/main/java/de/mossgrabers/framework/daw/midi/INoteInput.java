// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * Interface to a note input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface INoteInput
{
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
     * Get the interface to the note repeat control object.
     *
     * @return The note repeat object
     */
    INoteRepeat getNoteRepeat ();


    /**
     * Enable MPE (MIDI Polyphonic Expression) on the note input.
     *
     * @param enable True to enable MPE zone 1
     */
    void enableMPE (boolean enable);


    /**
     * Disable MPE on the note input.
     *
     * @param pitchBendRange The range of the pitch bend messages (1-96)
     */
    void setMPEPitchBendSensitivity (int pitchBendRange);
}
