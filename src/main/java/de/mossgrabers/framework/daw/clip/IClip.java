// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.clip;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.observer.IObserverManagement;


/**
 * Interface to a generic clip.
 *
 * @author Jürgen Moßgraber
 */
public interface IClip extends IObserverManagement
{
    /**
     * Returns true if the clip exits.
     *
     * @return True if the clip exits.
     */
    boolean doesExist ();


    /**
     * Get the color of the clip.
     *
     * @return The color
     */
    ColorEx getColor ();


    /**
     * Set the color of the clip.
     *
     * @param color The color
     */
    void setColor (ColorEx color);


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
     * @param slow Change slowly
     */
    void changePlayStart (int control, boolean slow);


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
     * @param slow Change slowly
     */
    void changePlayEnd (int control, boolean slow);


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
     * @param slow Change slowly
     */
    void changeLoopStart (int control, boolean slow);


    /**
     * Get the length of the loop.
     *
     * @return The length of the loop in beats (quarters)
     */
    double getLoopLength ();


    /**
     * Set the length of the loop.
     *
     * @param length The length of the loop
     */
    void setLoopLength (double length);


    /**
     * Change the length of the loop. Use 1 as fast and 0.1 as slow fraction value since scaling
     * cannot be applied to this.
     *
     * @param control The control value
     * @param slow Change slowly
     */
    void changeLoopLength (int control, boolean slow);


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
     * @param slow Change slowly
     */
    void changeAccent (int control, boolean slow);


    /**
     * Get the number of steps.
     *
     * @return The number of steps
     */
    int getNumSteps ();


    /**
     * Duplicate the clip.
     */
    void duplicate ();


    /**
     * Duplicate the content of a clip (in the clip).
     */
    void duplicateContent ();


    /**
     * Quantizes the start time of all items in the clip according to the given amount. The lengths
     * remain the same as before.
     *
     * @param amount A factor between `0` and `1` that allows to morph between the original start
     *            and the quantized note start.
     */
    void quantize (double amount);


    /**
     * Transposes the clip by the given semitones.
     *
     * @param semitones The number of semitones
     */
    void transpose (int semitones);
}