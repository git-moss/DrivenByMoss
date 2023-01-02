// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

/**
 * All information about a continuous controller like a knob or fader.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ContinuousInfo
{
    private int value = -1;


    /**
     * Get the cached value which was last sent to the device.
     *
     * @return The value
     */
    public int getValue ()
    {
        return this.value;
    }


    /**
     * Set the cached value which was last sent to the device.
     *
     * @param value The value
     */
    public void setValue (final int value)
    {
        this.value = value;
    }
}