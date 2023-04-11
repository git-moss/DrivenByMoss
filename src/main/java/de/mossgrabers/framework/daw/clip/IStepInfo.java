// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.clip;

/**
 * Interface to the data about a note in a sequencer step.
 *
 * @author Jürgen Moßgraber
 */
public interface IStepInfo
{
    /**
     * Get state.
     *
     * @return The state of the step
     */
    StepState getState ();


    /**
     * Is the note muted?
     *
     * @return If true the note is muted
     */
    boolean isMuted ();


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
     * Get the velocity spread of the note.
     *
     * @return The velocity spread of the note
     */
    double getVelocitySpread ();


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


    /**
     * Is chance (a random value to play the note) enabled?
     *
     * @return True if enabled
     */
    boolean isChanceEnabled ();


    /**
     * Get the chance (random value) that the note is played.
     *
     * @return The chance in the range of [0..1]
     */
    double getChance ();


    /**
     * Is occurrence (condition if the note is played) enabled?
     *
     * @return True if enabled
     */
    boolean isOccurrenceEnabled ();


    /**
     * Get the occurrence.
     *
     * @return The occurrence
     */
    NoteOccurrenceType getOccurrence ();


    /**
     * Is recurrence enabled?
     *
     * @return True if enabled
     */
    boolean isRecurrenceEnabled ();


    /**
     * Get the length of the recurrence.
     *
     * @return The length, [1..8], 1=Off
     */
    int getRecurrenceLength ();


    /**
     * Get the mask for the recurrence.
     *
     * @return Field of bits, cycle N -> bit N; max 8 cycles
     */
    int getRecurrenceMask ();


    /**
     * Are repeats enabled.
     *
     * @return True if enabled
     */
    boolean isRepeatEnabled ();


    /**
     * Get the number of repeats.
     *
     * @return The number of repeats in the range of [-127..127]
     */
    int getRepeatCount ();


    /**
     * Get the number of repeats formatted as a text.
     *
     * @return The text
     */
    String getFormattedRepeatCount ();


    /**
     * Get the repeat curve.
     *
     * @return The curve
     */
    double getRepeatCurve ();


    /**
     * Get the repeat velocity curve between the start and end velocity.
     *
     * @return The repeat velocity curve
     */
    double getRepeatVelocityCurve ();


    /**
     * Get the repeat velocity end.
     *
     * @return The repeat velocity end
     */
    double getRepeatVelocityEnd ();


    /**
     * Creates a copy of this object.
     *
     * @return The cloned object
     */
    IStepInfo createCopy ();
}
