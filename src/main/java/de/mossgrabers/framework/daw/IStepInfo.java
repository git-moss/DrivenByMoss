// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

/**
 * Interface to the data about a note in a sequencer step.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IStepInfo
{
    /** Constant for getStep result for note off. */
    int NOTE_OFF      = 0;
    /** Constant for getStep result for note continue. */
    int NOTE_CONTINUE = 1;
    /** Constant for getStep result for note start. */
    int NOTE_START    = 2;


    /**
     * Get state.
     *
     * @return 0: not set, 1: note continues playing, 2: start of note, see the defined constants
     */
    int getState ();


    /**
     * Get the duration of the note.
     *
     * @return The length of the note
     */
    double getDuration ();


    /**
     * Get the velocity of the note.
     *
     * @return The velocity of the note
     */
    double getVelocity ();


    /**
     * Get the release velocity of the note.
     *
     * @return The release velocity of the note
     */
    double getReleaseVelocity ();


    /**
     * Get the pressure of the note.
     *
     * @return The pressure of the note from 0 to +1
     */
    double getPressure ();


    /**
     * Get the timbre of the note.
     *
     * @return The timbre of the note from -1 to +1
     */
    double getTimbre ();


    /**
     * Get the panorama of the note.
     *
     * @return The panorama of the note, -1 for left, +1 for right
     */
    double getPan ();


    /**
     * Get the transposition of the note.
     *
     * @return The transposition of the note in semitones, from -24 to +24
     */
    double getTranspose ();


    /**
     * Get the gain of the note.
     *
     * @return The gain of the note
     */
    double getGain ();
}
