// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

/**
 * Interface to the Cursor clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ICursorClip extends ObserverManagement
{
    /**
     * Get the color of the clip.
     *
     * @return The color
     */
    double [] getColor ();


    /**
     * Set the color of the clip.
     *
     * @param red The red
     * @param green The green
     * @param blue The blue
     */
    void setColor (double red, double green, double blue);


    /**
     * Returns the start of the clip in beat time.
     *
     * @return The clips start time.
     */
    double getPlayStart ();


    /**
     * Set the start of the clip in beat time.
     *
     * @param start The clips start time
     */
    void setPlayStart (double start);


    /**
     * Change the start of the clip. Use 1 as fast and 0.1 as slow fraction value since scaling
     * cannot be applied to this.
     *
     * @param control The control value
     */
    void changePlayStart (int control);


    /**
     * Returns the end of the clip in beat time.
     *
     * @return The clips start time.
     */
    double getPlayEnd ();


    /**
     * Set the end of the clip in beat time.
     *
     * @param end The clips start time
     */
    void setPlayEnd (double end);


    /**
     * Change the end of the clip. Use 1 as fast and 0.1 as slow fraction value since scaling cannot
     * be applied to this.
     *
     * @param control The control value
     */
    void changePlayEnd (int control);


    /**
     * Sets the start and the end of the clip. Ensure that the start is before the end.
     *
     * @param start The start to set
     * @param end The end to set
     */
    void setPlayRange (double start, double end);


    /**
     * Get the start of the loop.
     *
     * @return The start of the loop
     */
    double getLoopStart ();


    /**
     * Set the start of the loop.
     *
     * @param start The start of the loop
     */
    void setLoopStart (double start);


    /**
     * Change the start of the loop. Use 1 as fast and 0.1 as slow fraction value since scaling
     * cannot be applied to this.
     *
     * @param control The control value
     */
    void changeLoopStart (int control);


    /**
     * Get the length of the loop.
     *
     * @return The length of the loop
     */
    double getLoopLength ();


    /**
     * Set the length of the loop.
     *
     * @param length The length of the loop
     */
    void setLoopLength (int length);


    /**
     * Change the length of the loop. Use 1 as fast and 0.1 as slow fraction value since scaling
     * cannot be applied to this.
     *
     * @param control The control value
     */
    void changeLoopLength (int control);


    /**
     * Is the loop enabled?
     *
     * @return True if enabled
     */
    boolean isLoopEnabled ();


    /**
     * Set if the loop is enabled.
     *
     * @param enable True if enabled
     */
    void setLoopEnabled (boolean enable);


    /**
     * Is shuffle enabled?
     *
     * @return True if shuffle is enabled
     */
    boolean isShuffleEnabled ();


    /**
     * Set if shuffle is enabled?
     *
     * @param enable True if shuffle is enabled
     */
    void setShuffleEnabled (boolean enable);


    /**
     * Get the accent value as a formatted string.
     *
     * @return The formatted string
     */
    String getFormattedAccent ();


    /**
     * Get the accent value.
     *
     * @return The accent value
     */
    double getAccent ();


    /**
     * Reset the accent value to its default.
     */
    void resetAccent ();


    /**
     * Change the accent value.
     *
     * @param control The control value
     */
    void changeAccent (int control);


    /**
     * Get the number of steps.
     *
     * @return The number of steps
     */
    int getNumSteps ();


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
     * @return 0: not set, 1: note continues playing, 2: start of note
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
     * Scroll the clip view to a step and note.
     *
     * @param step The step
     * @param row The row
     */
    void scrollTo (int step, int row);


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


    /**
     * Duplicate the clip.
     */
    void duplicate ();


    /**
     * Duplicate the content of a clip (in the clip).
     */
    void duplicateContent ();


    /**
     * Quantizes the start time of all notes in the clip according to the given amount. The note
     * lengths remain the same as before.
     *
     * @param amount A factor between `0` and `1` that allows to morph between the original note
     *            start and the quantized note start.
     */
    void quantize (double amount);


    /**
     * Transposes the notes in the clip by the given semitones.
     *
     * @param semitones The number of semitones
     */
    void transpose (int semitones);
}