// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.controller;

/**
 * Configuration of a fader (color, bar type, etc.).
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FaderConfig
{
    /** The fader is a bar of several dots. */
    public static final int TYPE_SINGLE = 0;
    /** The fader is a single dot. */
    public static final int TYPE_DOT    = 1;
    /** The fader is a bar of several dots but starting in the center. */
    public static final int TYPE_PAN    = 2;
    /** The fader combines a bar of several dots and a single dot in a different color. */
    public static final int TYPE_DUAL   = 3;

    private final int       type;
    private final int       color;
    private final int       value;
    private final int       dualValue;


    /**
     * Constructor for non-dual types.
     *
     * @param type The type of the fader, use the TYPE_* constants.
     * @param color The color index of the fader, 0-127
     * @param value The value of the fader, 0-127
     */
    public FaderConfig (final int type, final int color, final int value)
    {
        this (type, color, value, -1);
    }


    /**
     * Constructor.
     *
     * @param type The type of the fader, use the TYPE_* constants.
     * @param color The color index of the fader, 0-127
     * @param value The value of the fader, 0-127
     * @param dualValue If dual type is set, the second value
     */
    public FaderConfig (final int type, final int color, final int value, final int dualValue)
    {
        this.type = type;
        this.color = color;
        this.value = value;
        this.dualValue = dualValue;
    }


    /**
     * Get the type of the fader.
     *
     * @return the type The type
     */
    public int getType ()
    {
        return this.type;
    }


    /**
     * Get the color of the fader.
     *
     * @return The color, 0-127
     */
    public int getColor ()
    {
        return this.color;
    }


    /**
     * Get the value of the fader.
     *
     * @return The value, 0-127
     */
    public int getValue ()
    {
        return this.value;
    }


    /**
     * Get the second value of the fader, if type is DUAL.
     *
     * @return The value, 0-127
     */
    public int getDualValue ()
    {
        return this.dualValue;
    }
}
