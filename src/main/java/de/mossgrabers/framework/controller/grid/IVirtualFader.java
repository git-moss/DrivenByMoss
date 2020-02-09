// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.grid;

/**
 * Interface to a virtual fader. A virtual fader consists of a number of pads on a grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
}
