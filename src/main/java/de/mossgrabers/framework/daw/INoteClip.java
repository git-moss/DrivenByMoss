// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

/**
 * Interface to a clip, which contains note data.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface INoteClip extends IClip
{
    /** Constant for getStep result for note off. */
    int NOTE_OFF      = 0;
    /** Constant for getStep result for note continue. */
    int NOTE_CONTINUE = 1;
    /** Constant for getStep result for note start. */
    int NOTE_START    = 2;


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
     * Get the state of a note.
     *
     * @param step The step
     * @param row The row
     * @return 0: not set, 1: note continues playing, 2: start of note, see the defined constants
     */
    int getStep (int step, int row);


    /**
     * Toggle a note at a step.
     *
     * @param step The step
     * @param row The note row
     * @param velocity The velocity of the note
     */
    void toggleStep (int step, int row, int velocity);


    /**
     * Set a note at a step.
     *
     * @param step The step
     * @param row The note row
     * @param velocity The velocity of the note
     * @param duration The length of the note
     */
    void setStep (int step, int row, int velocity, double duration);


    /**
     * Clear a note at a step.
     *
     * @param step The step
     * @param row The note row
     */
    void clearStep (final int step, final int row);


    /**
     * Clear a row (note).
     *
     * @param row The row to clear
     */
    void clearRow (int row);


    /**
     * Does the row contain any notes?
     *
     * @param row The row
     * @return True if it contains at least one note
     */
    boolean hasRowData (int row);


    /**
     * Get the lowest row (note) which contains data.
     *
     * @return The lowest row or -1 if all rows are empty
     */
    int getLowerRowWithData ();


    /**
     * Get the highest row (note) which contains data.
     *
     * @return The highest row or -1 if all rows are empty
     */
    int getUpperRowWithData ();


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
