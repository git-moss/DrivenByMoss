// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc;

import de.mossgrabers.framework.scale.Scales;

import java.util.HashMap;
import java.util.Map;


/**
 * Different colors to use with OSC.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCColors
{
    /** Black. */
    public static final double []               COLOR_BLACK      =
    {
        0,
        0,
        0
    };
    /** White. */
    public static final double []               COLOR_WHITE      =
    {
        1,
        1,
        1
    };
    /** Green. */
    public static final double []               COLOR_GREEN      =
    {
        0,
        1,
        0
    };
    /** Red. */
    public static final double []               COLOR_RED        =
    {
        1,
        0,
        0
    };
    /** Ocean blue. */
    public static final double []               COLOR_OCEAN_BLUE =
    {
        0.2666666805744171,
        0.7843137383460999,
        1
    };

    private static final Map<String, double []> COLORS           = new HashMap<> (4);

    static
    {
        COLORS.put (Scales.SCALE_COLOR_OFF, COLOR_BLACK);
        COLORS.put (Scales.SCALE_COLOR_OCTAVE, COLOR_OCEAN_BLUE);
        COLORS.put (Scales.SCALE_COLOR_NOTE, COLOR_WHITE);
        COLORS.put (Scales.SCALE_COLOR_OUT_OF_SCALE, COLOR_BLACK);
    }


    /**
     * Private due to utility class.
     */
    private OSCColors ()
    {
        // Intentionally empty
    }


    /**
     * Get a color from the ID.
     *
     * @param colorID The ID for which to get the color
     * @return The RGB color
     */
    public static double [] getColor (final String colorID)
    {
        return COLORS.get (colorID);
    }
}