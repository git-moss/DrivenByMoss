// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller.display.model.grid;

import com.bitwig.extension.api.Color;


/**
 * Some helper constans for Color.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ColorEx
{
    /** Color black. */
    public static final Color   BLACK      = Color.fromRGB255 (0, 0, 0);
    /** Color white. */
    public static final Color   WHITE      = Color.fromRGB255 (255, 255, 255);
    /** Color light gray. */
    public static final Color   LIGHT_GRAY = Color.fromRGB255 (182, 182, 182);
    /** Color gray. */
    public static final Color   GRAY       = Color.fromRGB255 (128, 128, 128);
    /** Color dark gray. */
    public static final Color   DARK_GRAY  = Color.fromRGB255 (89, 89, 89);
    /** Color red. */
    public static final Color   RED        = Color.fromRGB255 (255, 0, 0);
    /** Color green. */
    public static final Color   GREEN      = Color.fromRGB255 (0, 255, 0);
    /** Color yellow. */
    public static final Color   YELLOW     = Color.fromRGB255 (255, 255, 0);

    private static final double FACTOR     = 0.7;


    /**
     * Constructor.
     */
    private ColorEx ()
    {
        // Private due to helper class
    }


    /**
     * Calculates a brighter version of the given color.
     *
     * @param c A color
     * @return The brighter version
     */
    public static Color brighter (final Color c)
    {
        double r = c.getRed ();
        double g = c.getGreen ();
        double b = c.getBlue ();

        // From 2D group:
        // 1. black.brighter() should return grey
        // 2. applying brighter to blue will always return blue, brighter
        // 3. non pure color (non zero rgb) will eventually return white

        final double i = 1.0 / (1.0 - FACTOR) / 255.0;

        if (r == 0 && g == 0 && b == 0)
            return Color.fromRGB (i, i, i);

        if (r > 0 && r < i)
            r = i;

        if (g > 0 && g < i)
            g = i;

        if (b > 0 && b < i)
            b = i;

        return Color.fromRGB (Math.min (r / FACTOR, 1.0), Math.min (g / FACTOR, 1.0), Math.min (b / FACTOR, 1.0));
    }


    /**
     * Calculates a darker version of the given color.
     *
     * @param c A color
     * @return The brighter version
     */
    public static Color darker (final Color c)
    {
        return Color.fromRGB (Math.max (c.getRed () * FACTOR, 0), Math.max (c.getGreen () * FACTOR, 0), Math.max (c.getBlue () * FACTOR, 0));
    }
}
