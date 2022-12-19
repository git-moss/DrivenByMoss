// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.clip;

import de.mossgrabers.framework.daw.data.IPinnable;

import java.util.List;


/**
 * Interface to a clip, which contains note data.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface INoteClip extends IClip, IPinnable
{
    /**
     * Get the row of notes.
     *
     * @return The row of notes
     */
    int getNumRows ();


    /**
     * Get the index of the current step
     *
     * @return The index of the current step
     */
    int getCurrentStep ();


    /**
     * Set the length of a step.
     *
     * @param length The length
     */
    void setStepLength (double length);


    /**
     * Get the length of a step.
     *
     * @return The length
     */
    double getStepLength ();


    /**
     * Get the state of a step.
     *
     * @param position The position of the note
     * @return The step info
     */
    IStepInfo getStep (NotePosition position);


    /**
     * Toggle a note at a step.
     *
     * @param position The position of the note
     * @param velocity The velocity of the note
     */
    void toggleStep (NotePosition position, int velocity);


    /**
     * Set a note at a step.
     *
     * @param position The position of the note
     * @param velocity The velocity of the note
     * @param duration The length of the note
     */
    void setStep (NotePosition position, int velocity, double duration);


    /**
     * Set a step and copy all data from the given note step.
     *
     * @param position The position of the note
     * @param noteStep The note step
     */
    void setStep (NotePosition position, IStepInfo noteStep);


    /**
     * Clear a note at a step.
     *
     * @param position The position of the note
     */
    void clearStep (NotePosition position);


    /**
     * Moves the step to a different row.
     *
     * @param position The position of the note
     * @param newRow The new note row
     */
    void moveStepY (NotePosition position, int newRow);


    /**
     * If there is a note started at this position, it will update the mute state of the note.
     *
     * @param position The position of the note
     * @param isMuted Is the note muted?
     */
    void updateStepMuteState (NotePosition position, boolean isMuted);


    /**
     * If there is a note started at this position, it will change the mute state of the note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepMuteState (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the duration of the note.
     *
     * @param position The position of the note
     * @param duration The new length of the note
     */
    void updateStepDuration (NotePosition position, double duration);


    /**
     * If there is a note started at this position, it will change the duration of the note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepDuration (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the velocity of the note.
     *
     * @param position The position of the note
     * @param velocity The velocity of the note
     */
    void updateStepVelocity (NotePosition position, double velocity);


    /**
     * If there is a note started at this position, it will change the velocity of the note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepVelocity (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the velocity spread of the note.
     *
     * @param position The position of the note
     * @param velocitySpread The velocity spread of the note
     */
    void updateStepVelocitySpread (NotePosition position, double velocitySpread);


    /**
     * If there is a note started at this position, it will change the velocity spread of the note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepVelocitySpread (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the release velocity of the note.
     *
     * @param position The position of the note
     * @param releaseVelocity The release velocity of the note
     */
    void updateStepReleaseVelocity (NotePosition position, double releaseVelocity);


    /**
     * If there is a note started at this position, it will change the release velocity of the note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepReleaseVelocity (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the pressure of the note.
     *
     * @param position The position of the note
     * @param pressure The pressure of the note from 0 to +1
     */
    void updateStepPressure (NotePosition position, double pressure);


    /**
     * If there is a note started at this position, it will change the pressure of the note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepPressure (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the timbre of the note.
     *
     * @param position The position of the note
     * @param timbre The timbre of the note from -1 to +1
     */
    void updateStepTimbre (NotePosition position, double timbre);


    /**
     * If there is a note started at this position, it will change the timbre of the note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepTimbre (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the panorama of the note.
     *
     * @param position The position of the note
     * @param panorama The panorama of the note, -1 for left, +1 for right
     */
    void updateStepPan (NotePosition position, double panorama);


    /**
     * If there is a note started at this position, it will change the panorama of the note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepPan (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the transposition of the note.
     *
     * @param position The position of the note
     * @param semitones The transposition of the note in semitones, from -24 to +24
     */
    void updateStepTranspose (NotePosition position, double semitones);


    /**
     * If there is a note started at this position, it will change the transposition of the note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepTranspose (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the gain of the note.
     *
     * @param position The position of the note
     * @param gain The gain to set
     */
    void updateStepGain (NotePosition position, double gain);


    /**
     * If there is a note started at this position, it will change the gain of the note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepGain (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the on/off state of the chance of
     * the note.
     *
     * @param position The position of the note
     * @param isEnabled True to enabled
     */
    void updateStepIsChanceEnabled (NotePosition position, boolean isEnabled);


    /**
     * If there is a note started at this position, it will update the chance of the note.
     *
     * @param position The position of the note
     * @param chance The chance to set (0..1)
     */
    void updateStepChance (NotePosition position, double chance);


    /**
     * If there is a note started at this position, it will change the chance of the note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepChance (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the on/off state of occurrence of
     * the note.
     *
     * @param position The position of the note
     * @param isEnabled True to enabled
     */
    void updateStepIsOccurrenceEnabled (NotePosition position, boolean isEnabled);


    /**
     * If there is a note started at this position, it will change the occurrence of the note to the
     * next or previous.
     *
     * @param position The position of the note
     * @param increase True to increase otherwise decrease
     */
    void setStepPrevNextOccurrence (NotePosition position, boolean increase);


    /**
     * If there is a note started at this position, it will change the occurrence of the note to the
     * next or previous.
     *
     * @param position The position of the note
     * @param occurrence The occurrence to set
     */
    void setStepOccurrence (NotePosition position, NoteOccurrenceType occurrence);


    /**
     * If there is a note started at this position, it will update the on/off state of the
     * recurrence of the note.
     *
     * @param position The position of the note
     * @param isEnabled True to enabled
     */
    void updateStepIsRecurrenceEnabled (NotePosition position, boolean isEnabled);


    /**
     * If there is a note started at this position, it will update the recurrence length of the
     * note.
     *
     * @param position The position of the note
     * @param recurrenceLength The recurrence length to set
     */
    void updateStepRecurrenceLength (NotePosition position, int recurrenceLength);


    /**
     * If there is a note started at this position, it will update the recurrence mask of the note.
     *
     * @param position The position of the note
     * @param mask The recurrence mask to set
     */
    void updateStepRecurrenceMask (NotePosition position, int mask);


    /**
     * If there is a note started at this position, it will update the given step of recurrence mask
     * of the note by toggling it on/off.
     *
     * @param position The position of the note
     * @param step The step to set (0-7)
     */
    default void updateStepRecurrenceMaskToggleBit (final NotePosition position, final int step)
    {
        final IStepInfo stepInfo = this.getStep (position);
        if (stepInfo.getState () == StepState.OFF)
            return;
        int mask = stepInfo.getRecurrenceMask ();
        final int bitVal = 1 << step;
        if ((mask & bitVal) > 0)
            mask &= ~bitVal;
        else
            mask |= bitVal;
        this.updateStepRecurrenceMask (position, mask);
    }


    /**
     * If there is a note started at this position, it will change the recurrence length of the
     * note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepRecurrenceLength (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the on/off state of repeat of the
     * note.
     *
     * @param position The position of the note
     * @param isEnabled True to enabled
     */
    void updateStepIsRepeatEnabled (NotePosition position, boolean isEnabled);


    /**
     * If there is a note started at this position, it will update the repeat count of the note.
     *
     * @param position The position of the note
     * @param value The value
     */
    void updateStepRepeatCount (NotePosition position, int value);


    /**
     * If there is a note started at this position, it will change the repeat count of the note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepRepeatCount (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the repeat curve of the note.
     *
     * @param position The position of the note
     * @param value The value
     */
    void updateStepRepeatCurve (NotePosition position, double value);


    /**
     * If there is a note started at this position, it will change the repeat curve of the note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepRepeatCurve (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the repeat velocity curve of the
     * note.
     *
     * @param position The position of the note
     * @param value The value
     */
    void updateStepRepeatVelocityCurve (NotePosition position, double value);


    /**
     * If there is a note started at this position, it will change the repeat velocity curve of the
     * note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepRepeatVelocityCurve (NotePosition position, int control);


    /**
     * If there is a note started at this position, it will update the repeat velocity end of the
     * note.
     *
     * @param position The position of the note
     * @param value The value
     */
    void updateStepRepeatVelocityEnd (NotePosition position, double value);


    /**
     * If there is a note started at this position, it will change the repeat velocity end of the
     * note.
     *
     * @param position The position of the note
     * @param control The change value
     */
    void changeStepRepeatVelocityEnd (NotePosition position, int control);


    /**
     * Start editing one or more notes. Signals to prevent round-trip error by quickly changing
     * values to the DAW, which are not set at the same time.
     *
     * @param editSteps The steps to edit
     */
    void startEdit (List<NotePosition> editSteps);


    /**
     * Stop editing the note step, which was started with startEdit.
     */
    void stopEdit ();


    /**
     * Clear all notes on all channels.
     */
    void clearAll ();


    /**
     * Clear a row (note).
     *
     * @param channel The MIDI channel
     * @param row The row to clear
     */
    void clearRow (int channel, int row);


    /**
     * Does the row contain any notes?
     *
     * @param channel The MIDI channel
     * @param row The row
     * @return True if it contains at least one note
     */
    boolean hasRowData (int channel, int row);


    /**
     * Get the lowest row (note) which contains data, ignores the MIDI channel.
     *
     * @return The lowest row or -1 if all rows are empty
     */
    int getLowestRowWithData ();


    /**
     * Get the highest row (note) which contains data, ignores the MIDI channel.
     *
     * @return The highest row or -1 if all rows are empty
     */
    int getHighestRowWithData ();


    /**
     * Get the lowest row (note) which contains data.
     *
     * @param channel The MIDI channel
     * @return The lowest row or -1 if all rows are empty
     */
    int getLowestRowWithData (int channel);


    /**
     * Get the highest row (note) which contains data.
     *
     * @param channel The MIDI channel
     * @return The highest row or -1 if all rows are empty
     */
    int getHighestRowWithData (int channel);


    /**
     * Get the highest row (note) which contains data of a step.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @return The highest note or -1 if all rows are empty
     */
    int getHighestRow (int channel, int step);


    /**
     * Scroll the clip view to the given page. Depends on the number of the steps of a page.
     *
     * @param page The page to select
     */
    void scrollToPage (int page);


    /**
     * Get the edit page.
     *
     * @return The edit page
     */
    int getEditPage ();


    /**
     * Scroll the steps one page backwards.
     */
    void scrollStepsPageBackwards ();


    /**
     * Scroll the steps one page forwards.
     */
    void scrollStepsPageForward ();


    /**
     * Value that reports if the note grid steps can be scrolled backwards.
     *
     * @return True if it can be scrolled
     */
    boolean canScrollStepsBackwards ();


    /**
     * Value that reports if the note grid steps can be scrolled forwards.
     *
     * @return True if it can be scrolled
     */
    boolean canScrollStepsForwards ();
}
