// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.data.GridStep;
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
     * Get the state of a note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The row
     * @return The step info
     */
    IStepInfo getStep (int channel, int step, int row);


    /**
     * Toggle a note at a step.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param velocity The velocity of the note
     */
    void toggleStep (int channel, int step, int row, int velocity);


    /**
     * Set a note at a step.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param velocity The velocity of the note
     * @param duration The length of the note
     */
    void setStep (int channel, int step, int row, int velocity, double duration);


    /**
     * Set a step and copy all data from the given note step.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param noteStep The note step
     */
    void setStep (int channel, int step, int row, IStepInfo noteStep);


    /**
     * Clear a note at a step.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     */
    void clearStep (int channel, int step, int row);


    /**
     * If there is a note started at this position, it will update the mute state of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param isMuted Is the note muted?
     */
    void updateMuteState (int channel, int step, int row, boolean isMuted);


    /**
     * If there is a note started at this position, it will change the mute state of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeMuteState (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the duration of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param duration The new length of the note
     */
    void updateStepDuration (int channel, int step, int row, double duration);


    /**
     * If there is a note started at this position, it will change the duration of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepDuration (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the velocity of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param velocity The velocity of the note
     */
    void updateStepVelocity (int channel, int step, int row, double velocity);


    /**
     * If there is a note started at this position, it will change the velocity of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepVelocity (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the velocity spread of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param velocitySpread The velocity spread of the note
     */
    void updateVelocitySpread (int channel, int step, int row, double velocitySpread);


    /**
     * If there is a note started at this position, it will change the velocity spread of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeVelocitySpread (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the release velocity of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param releaseVelocity The release velocity of the note
     */
    void updateStepReleaseVelocity (int channel, int step, int row, double releaseVelocity);


    /**
     * If there is a note started at this position, it will change the release velocity of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepReleaseVelocity (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the pressure of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param pressure The pressure of the note from 0 to +1
     */
    void updateStepPressure (int channel, int step, int row, double pressure);


    /**
     * If there is a note started at this position, it will change the pressure of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepPressure (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the timbre of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param timbre The timbre of the note from -1 to +1
     */
    void updateStepTimbre (int channel, int step, int row, double timbre);


    /**
     * If there is a note started at this position, it will change the timbre of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepTimbre (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the panorama of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param panorama The panorama of the note, -1 for left, +1 for right
     */
    void updateStepPan (int channel, int step, int row, double panorama);


    /**
     * If there is a note started at this position, it will change the panorama of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepPan (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the transposition of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param semitones The transposition of the note in semitones, from -24 to +24
     */
    void updateStepTranspose (int channel, int step, int row, double semitones);


    /**
     * If there is a note started at this position, it will change the transposition of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepTranspose (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the gain of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param gain The gain to set
     */
    void updateStepGain (int channel, int step, int row, double gain);


    /**
     * If there is a note started at this position, it will change the gain of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepGain (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the on/off state of the chance of
     * the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param isEnabled True to enabled
     */
    void updateIsChanceEnabled (int channel, int step, int row, boolean isEnabled);


    /**
     * If there is a note started at this position, it will update the chance of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param chance The chance to set
     */
    void updateChance (int channel, int step, int row, double chance);


    /**
     * If there is a note started at this position, it will change the chance of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeChance (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the on/off state of occurrence of
     * the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param isEnabled True to enabled
     */
    void updateIsOccurrenceEnabled (int channel, int step, int row, boolean isEnabled);


    /**
     * If there is a note started at this position, it will change the occurrence of the note to the
     * next or previous.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param increase True to increase otherwise decrease
     */
    void setPrevNextOccurrence (int channel, int step, int row, boolean increase);


    /**
     * If there is a note started at this position, it will change the occurrence of the note to the
     * next or previous.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param occurrence The occurrence to set
     */
    void setOccurrence (int channel, int step, int row, NoteOccurrenceType occurrence);


    /**
     * If there is a note started at this position, it will update the on/off state of the
     * recurrence of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param isEnabled True to enabled
     */
    void updateIsRecurrenceEnabled (int channel, int step, int row, boolean isEnabled);


    /**
     * If there is a note started at this position, it will update the recurrence length of the
     * note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param recurrenceLength The recurrence length to set
     */
    void updateRecurrenceLength (int channel, int step, int row, int recurrenceLength);


    /**
     * If there is a note started at this position, it will update the recurrence mask of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param mask The recurrence length to set
     */
    void updateRecurrenceMask (int channel, int step, int row, int mask);


    /**
     * If there is a note started at this position, it will change the recurrence length of the
     * note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeRecurrenceLength (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the on/off state of repeat of the
     * note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param isEnabled True to enabled
     */
    void updateIsRepeatEnabled (int channel, int step, int row, boolean isEnabled);


    /**
     * If there is a note started at this position, it will update the repeat count of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param value The value
     */
    void updateRepeatCount (int channel, int step, int row, int value);


    /**
     * If there is a note started at this position, it will change the repeat count of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeRepeatCount (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the repeat curve of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param value The value
     */
    void updateRepeatCurve (int channel, int step, int row, double value);


    /**
     * If there is a note started at this position, it will change the repeat curve of the note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeRepeatCurve (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the repeat velocity curve of the
     * note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param value The value
     */
    void updateRepeatVelocityCurve (int channel, int step, int row, double value);


    /**
     * If there is a note started at this position, it will change the repeat velocity curve of the
     * note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeRepeatVelocityCurve (int channel, int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the repeat velocity end of the
     * note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param value The value
     */
    void updateRepeatVelocityEnd (int channel, int step, int row, double value);


    /**
     * If there is a note started at this position, it will change the repeat velocity end of the
     * note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeRepeatVelocityEnd (int channel, int step, int row, int control);


    /**
     * Start editing one or more notes. Signals to prevent round-trip error by quickly changing
     * values to the DAW, which are not set at the same time.
     *
     * @param editSteps The steps to edit
     */
    void startEdit (List<GridStep> editSteps);


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
    int getLowerRowWithData ();


    /**
     * Get the highest row (note) which contains data, ignores the MIDI channel.
     *
     * @return The highest row or -1 if all rows are empty
     */
    int getUpperRowWithData ();


    /**
     * Get the lowest row (note) which contains data.
     *
     * @param channel The MIDI channel
     * @return The lowest row or -1 if all rows are empty
     */
    int getLowerRowWithData (int channel);


    /**
     * Get the highest row (note) which contains data.
     *
     * @param channel The MIDI channel
     * @return The highest row or -1 if all rows are empty
     */
    int getUpperRowWithData (int channel);


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
