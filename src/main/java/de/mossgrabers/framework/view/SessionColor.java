// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

/**
 * The Session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
/** Wrapper class for clip colors. */
public class SessionColor
{
    private int     color;
    private int     blink;
    private boolean fast;


    /**
     * Constructor.
     *
     * @param color The main color
     * @param blink If a different code is necessary for blinking (if supported by controller)
     * @param fast Blink fast if true
     */
    public SessionColor (final int color, final int blink, final boolean fast)
    {
        this.color = color;
        this.blink = blink;
        this.fast = fast;
    }


    /**
     * Get the main color.
     *
     * @return The color.
     */
    public int getColor ()
    {
        return this.color;
    }


    /**
     * If a different code is necessary for blinking (if supported by controller).
     *
     * @return The blink state/color
     */
    public int getBlink ()
    {
        return this.blink;
    }


    /**
     * Blink fast if true.
     *
     * @return The fast state
     */
    public boolean isFast ()
    {
        return this.fast;
    }
}