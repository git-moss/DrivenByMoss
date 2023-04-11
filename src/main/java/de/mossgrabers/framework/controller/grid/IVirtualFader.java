// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.grid;

/**
 * Interface to a virtual fader. A virtual fader consists of a number of pads on a grid.
 *
 * @author Jürgen Moßgraber
 */
public interface IVirtualFader
{
    /**
     * Set the color of a fader (8 vertical pads).
     *
     * @param color The color to set
     * @param isPan True for panorama layout
     */
    void setup (int color, boolean isPan);


    /**
     * Set the faders value.
     *
     * @param value The value to set
     */
    void setValue (int value);


    /**
     * Get the state of a color step.
     *
     * @param index The index of the step
     * @return The color state
     */
    int getColorState (int index);


    /**
     * Move the fader to a new position
     *
     * @param row The row to move to
     * @param velocity The velocity (for speed)
     */
    void moveTo (final int row, final int velocity);
}