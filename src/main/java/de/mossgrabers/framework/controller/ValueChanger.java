// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

/**
 * Interface for changing values.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ValueChanger
{
    /**
     * Get the limit for the maximum value for parameters. The value is in the range of 0 to
     * upperbound-1.
     *
     * @return The upper bound value
     */
    int getUpperBound ();


    /**
     * Get the value for de-/incrementing values.
     *
     * @return The value
     */
    int getFractionValue ();


    /**
     * Get the value for de-/incrementing values slowly.
     *
     * @return The value
     */
    double getSlowFractionValue ();


    /**
     * Sets the speed of the value changes.
     *
     * @param isSlow If true the slowFractionValue is used, otherwise the fractionValue
     */
    void setSpeed (boolean isSlow);


    /**
     * Returns true if slow speed is enabled.
     *
     * @return True if slow speed is enabled.
     */
    boolean isSlow ();


    /**
     * Calculate the amount by which to change a value from the control speed, depending on the slow
     * setting.
     *
     * @param control The control speed, depending on the specific hardware controller
     * @return The speed
     */
    double calcKnobSpeed (int control);


    /**
     * Calculate the amount by which to change a value from the control speed.
     *
     * @param control The control speed, depending on the specific hardware controller
     * @param fractionValue The value for de-/incrementing values
     * @return The speed
     */
    double calcKnobSpeed (int control, double fractionValue);


    /**
     * Change a value by the amount of the control speed. Uses the default fraction values
     * (depending on the slow setting) and upper bound. The lower bound is 0.
     *
     * @param control The control speed, depending on the specific hardware controller
     * @param value The current value
     * @return The new value
     */
    int changeValue (int control, int value);


    /**
     * Change a value by the amount of the control speed. The lower bound is 0.
     *
     * @param control The control speed, depending on the specific hardware controller
     * @param value The current value
     * @param fractionValue The value for de-/incrementing values
     * @param upperBound The maximum value for parameters plus 1
     * @return The new value
     */
    int changeValue (int control, int value, double fractionValue, int upperBound);


    /**
     * Change a value by the amount of the control speed. The lower bound is 0.
     *
     * @param control The control speed, depending on the specific hardware controller
     * @param value The current value
     * @param fractionValue The value for de-/incrementing values
     * @param upperBound The maximum value for parameters plus 1
     * @param lowerBound A lower bound for the value
     * @return The new value
     */
    int changeValue (int control, int value, double fractionValue, int upperBound, int lowerBound);


    /**
     * Change an integer value by the amount of the control speed. The lower bound is 0.
     *
     * @param control The control speed, depending on the specific hardware controller
     * @param value The current value
     * @param fractionValue The value for de-/incrementing values
     * @param upperBound The maximum value for parameters plus 1
     * @return The new value
     */
    int changeIntValue (int control, int value, double fractionValue, int upperBound);


    /**
     * Change an integer value by the amount of the control speed. The lower bound is 0.
     *
     * @param control The control speed, depending on the specific hardware controller
     * @param value The current value
     * @param fractionValue The value for de-/incrementing values
     * @param upperBound The maximum value for parameters plus 1
     * @param lowerBound A lower bound for the value
     * @return The new value
     */
    int changeIntValue (int control, int value, double fractionValue, int upperBound, int lowerBound);


    /**
     * Translate the value in the range from 0 to max-1 to 0-127.
     *
     * @param value The DAW value
     * @return The midi value
     */
    int toMidiValue (int value);


    /**
     * Translate the value in the range from 0-127 to 0 to max-1 .
     *
     * @param value The midi value
     * @return The DAW value
     */
    int toDAWValue (int value);


    /**
     * Translate the value in the range from 0 to max-1 to the range necessary for addressing a
     * display.
     *
     * @param value The DAW value
     * @return The display value
     */
    int toDisplayValue (int value);
}
