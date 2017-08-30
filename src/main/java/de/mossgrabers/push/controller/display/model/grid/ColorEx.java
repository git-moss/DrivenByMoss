// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller.display.model.grid;

import com.bitwig.extension.api.Color;


public class ColorEx
{
    public static final Color   BLACK  = Color.fromRGB255 (0, 0, 0);
    public static final Color   WHITE  = Color.fromRGB255 (255, 255, 255);
    public static final Color   GRAY   = Color.fromRGB255 (128, 128, 128);
    public static final Color   RED    = Color.fromRGB255 (255, 0, 0);
    public static final Color   GREEN  = Color.fromRGB255 (0, 255, 0);
    public static final Color   YELLOW = Color.fromRGB255 (255, 255, 0);

    private static final double FACTOR = 0.7;


    private ColorEx ()
    {
    }


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


    public static Color darker (final Color c)
    {
        return Color.fromRGB (Math.max (c.getRed () * FACTOR, 0), Math.max (c.getGreen () * FACTOR, 0), Math.max (c.getBlue () * FACTOR, 0));
    }
}
