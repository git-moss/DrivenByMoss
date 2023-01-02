// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.valuechanger;

/**
 * Interface for changing values.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IValueChanger
{
    /**
     * Get the limit for the upper bound (maximum) for parameters. The value is in the range of 0 to
     * upper bound - 1.
     *
     * @return The upper bound value
     */
    int getUpperBound ();


    /**
     * Set the upper bound.
     *
     * @param upperBound The new upper bound
     */
    void setUpperBound (int upperBound);


    /**
     * The value for de-/increasing the value by '1' without any scaling.
     *
     * @param stepSize The step size
     */
    void setStepSize (int stepSize);


    /**
     * Set the sensitivity of the relative knob.
     *
     * @param sensitivity The sensitivity in the range [-100..100], 0 is the default, negative
     *            values are slower, positive faster
     */
    void setSensitivity (double sensitivity);


    /**
     * Calculate the amount by which to change a value from the control value, depending on the step
     * size and the sensitivity.
     *
     * @param control The control value, depending on the specific encoding
     * @return The amount to change the value
     */
    double calcKnobChange (int control);


    /**
     * Calculate the amount by which to change a value from the control value, depending on the step
     * size and the given sensitivity.
     *
     * @param control The control value, depending on the specific encoding
     * @param sensitivity The sensitivity in the range [-100..100], 0 is the default, negative
     *            values are slower, positive faster
     * @return The amount to change the value
     */
    double calcKnobChange (int control, double sensitivity);


    /**
     * Calculate the amount by which to change a value from the control value, depending on the step
     * size and the given sensitivity. The result is rounded to integer and never 0.
     *
     * @param control The control value, depending on the specific encoding
     * @return The amount to change the value
     */
    int calcSteppedKnobChange (int control);


    /**
     * Returns true if the change is positive (increase).
     *
     * @param control The control value, depending on the specific encoding
     * @return True if the change is positive
     */
    boolean isIncrease (int control);


    /**
     * Encode the control value with this encoding.
     *
     * @param speed The value to encode (-63 to 63)
     * @return The encoded value (0-127)
     */
    int encode (int speed);


    /**
     * Encode the control value with this encoding.
     *
     * @param control The encoded value (0-127)
     * @return The decoded value (-63 to 63)
     */
    int decode (int control);


    /**
     * Change a value by the amount of the control value, step size and sensitivity. The result is
     * in the range of 0 and upperBound - 1.
     *
     * @param control The control value, depending on the specific encoding
     * @param value The current value
     * @return The new value
     */
    int changeValue (int control, int value);


    /**
     * Change a value by the amount of the control value, step size and given sensitivity. The
     * result is in the range of 0 and upperBound - 1.
     *
     * @param control The control speed, depending on the specific hardware controller
     * @param value The current value
     * @param sensitivity The sensitivity in the range [-100..100], 0 is the default, negative
     *            values are slower, positive faster
     * @param upperBound The maximum value for parameters plus 1
     * @return The new value
     */
    int changeValue (int control, int value, double sensitivity, int upperBound);


    /**
     * Translate the value in the range from [0, max-1] to [0, 127].
     *
     * @param value The DAW value
     * @return The MIDI value
     */
    int toMidiValue (int value);


    /**
     * Translate the value in the range from [0, 127] to [0, max-1].
     *
     * @param value The MIDI value
     * @return The DAW value
     */
    int toDAWValue (int value);


    /**
     * Translate the value in the range from [0, max-1] to the range necessary for addressing a
     * display.
     *
     * @param value The DAW value
     * @return The display value
     */
    int toDisplayValue (int value);


    /**
     * Translate the value in the range from [0, max-1] to [0.0, 1.0]. Allows doubles as input for
     * calculating small offset values.
     *
     * @param value The DAW value
     * @return The MIDI value
     */
    double toNormalizedValue (double value);


    /**
     * Translate the value in the range from [0.0, 1.0] to [0, max-1].
     *
     * @param value The DAW value
     * @return The MIDI value
     */
    int fromNormalizedValue (double value);
}
