// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.grid;

import de.mossgrabers.framework.utils.FrameworkException;


/**
 * Info for pad updates.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public final class LightInfo
{
    private int     color      = 0;
    private int     blinkColor = 0;
    private boolean fast       = false;
    private int     encoded    = 0;


    /**
     * Default constructor.
     */
    public LightInfo ()
    {
        // Intentionally empty
    }


    /**
     * Constructor.
     *
     * @param color The main color
     * @param blinkColor If a different code is necessary for blinking (if supported by controller)
     * @param fast Blink fast if true
     */
    public LightInfo (final int color, final int blinkColor, final boolean fast)
    {
        this.setColors (color, blinkColor, fast);
    }


    /**
     * Set all pad info values at once.
     *
     * @param color The color
     * @param blinkColor The new blink color
     * @param fast True to blink fast
     */
    public void setColors (final int color, final int blinkColor, final boolean fast)
    {
        if (color < 0 || color > 127)
            throw new FrameworkException ("color must be in the range of 0..127.");
        if (blinkColor < -1 || blinkColor > 127)
            throw new FrameworkException ("blinkColor must be in the range of 0..127 or -1 for off.");

        this.color = color;
        this.blinkColor = blinkColor;
        this.fast = fast;

        this.encode ();
    }


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
        this.encode ();
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
        this.encode ();
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
        this.encode ();
    }


    /**
     * Get the encoded state.
     *
     * @return The encoded state
     */
    public int getEncoded ()
    {
        return this.encoded;
    }


    /**
     * Encode the color and blink states as one integer and store it in the encode field.
     */
    private void encode ()
    {
        if (this.color < 0 || this.color > 127)
            throw new FrameworkException ("Color indices must be in the range of [0..127] but is " + this.color + "!");
        if (this.blinkColor > 127)
            throw new FrameworkException ("Color indices may not be larger than 127 but blink index is " + this.blinkColor + "!");

        final int codeBlinkColor = this.blinkColor < 0 ? 1 << 15 : this.blinkColor << 8;
        final int codeFast = this.fast ? 1 << 16 : 0;
        this.encoded = codeFast + codeBlinkColor + this.color;
    }
}