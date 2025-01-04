// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui.mode;

/**
 * Interface for additional HUI mode methods.
 *
 * @author Jürgen Moßgraber
 */
public interface HUIMode
{
    /**
     * Update the knob LEDs.
     */
    void updateKnobLEDs ();


    /**
     * Reset the parameter.
     *
     * @param index THe index of the parameter
     */
    void resetParameter (int index);
}
