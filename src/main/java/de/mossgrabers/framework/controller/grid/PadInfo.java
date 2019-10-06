// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.grid;

/**
 * Info for pad updates.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PadInfo
{
    private int     color      = -1;
    private int     blinkColor = -1;
    private boolean fast;


    /**
     * Get the color of the pad.
     *
     * @return The color
     */
    public int getColor ()
    {
        return this.color;
    }


    /**
     * Set the color of the pad.
     *
     * @param color The color
     */
    public void setColor (final int color)
    {
        this.color = color;
    }


    /**
     * Get the blink color.
     *
     * @return The blink color
     */
    public int getBlinkColor ()
    {
        return this.blinkColor;
    }


    /**
     * Set the blink color.
     *
     * @param blinkColor The new blink color
     */
    public void setBlinkColor (final int blinkColor)
    {
        this.blinkColor = blinkColor;
    }


    /**
     * Blink fast or slow?
     *
     * @return True if fast
     */
    public boolean isFast ()
    {
        return this.fast;
    }


    /**
     * Set to blink fast or slow.
     *
     * @param fast True to blink fast
     */
    public void setFast (final boolean fast)
    {
        this.fast = fast;
    }
}