// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

/**
 * Support for handling the colors used in Bitwig.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BitwigColors
{
    /** All Bitwig track colors. */
    private static final Object [] COLORS                     =
    {
        // Gray - not assigned
        new double []
        {
            0.5,
            0.5,
            0.5
        },
        // Dark Gray
        new double []
        {
            0.3294117748737335,
            0.3294117748737335,
            0.3294117748737335
        },
        // Gray
        new double []
        {
            0.47843137383461,
            0.47843137383461,
            0.47843137383461
        },
        // Light Gray
        new double []
        {
            0.7882353067398071,
            0.7882353067398071,
            0.7882353067398071
        },
        // Silver
        new double []
        {
            0.5254902243614197,
            0.5372549295425415,
            0.6745098233222961
        },
        // Dark Brown
        new double []
        {
            0.6392157077789307,
            0.4745098054409027,
            0.26274511218070984
        },
        // Brown
        new double []
        {
            0.7764706015586853,
            0.6235294342041016,
            0.43921568989753723
        },
        // Dark Blue
        new double []
        {
            0.34117648005485535,
            0.3803921639919281,
            0.7764706015586853
        },
        // Light Blue
        new double []
        {
            0.5176470875740051,
            0.5411764979362488,
            0.8784313797950745
        },
        // Purple
        new double []
        {
            0.5843137502670288,
            0.2862745225429535,
            0.7960784435272217
        },
        // Pink
        new double []
        {
            0.8509804010391235,
            0.21960784494876862,
            0.4431372582912445
        },
        // Red
        new double []
        {
            0.8509804010391235,
            0.18039216101169586,
            0.1411764770746231
        },
        // Orange
        new double []
        {
            1,
            0.34117648005485535,
            0.0235294122248888
        },
        // Light Orange
        new double []
        {
            0.8509804010391235,
            0.615686297416687,
            0.062745101749897
        },
        // Green
        new double []
        {
            0.45098039507865906,
            0.5960784554481506,
            0.0784313753247261
        },
        // Cold Green
        new double []
        {
            0,
            0.615686297416687,
            0.27843138575553894
        },
        // Bluish Green
        new double []
        {
            0,
            0.6509804129600525,
            0.5803921818733215
        },
        // Light Blue
        new double []
        {
            0,
            0.6000000238418579,
            0.8509804010391235
        },
        // Light Purple
        new double []
        {
            0.7372549176216125,
            0.4627451002597809,
            0.9411764740943909
        },
        // Light Pink
        new double []
        {
            0.8823529481887817,
            0.4000000059604645,
            0.5686274766921997
        },
        // Skin
        new double []
        {
            0.9254902005195618,
            0.3803921639919281,
            0.34117648005485535
        },
        // Redish Brown
        new double []
        {
            1,
            0.5137255191802979,
            0.24313725531101227
        },

        // Light Brown
        new double []
        {
            0.8941176533699036,
            0.7176470756530762,
            0.30588236451148987
        },
        // Light Green
        new double []
        {
            0.6274510025978088,
            0.7529411911964417,
            0.2980392277240753
        },
        // Bluish Green
        new double []
        {
            0.24313725531101227,
            0.7333333492279053,
            0.3843137323856354
        },
        // Light Blue
        new double []
        {
            0.26274511218070984,
            0.8235294222831726,
            0.7254902124404907
        },
        // Blue
        new double []
        {
            0.2666666805744171,
            0.7843137383460999,
            1
        }
    };

    /** Color off. */
    public static final String     COLOR_OFF                  = "COLOR_OFF";
    /** Color dark grey. */
    public static final String     BITWIG_COLOR_DARK_GRAY     = "BITWIG_COLOR_DARK_GRAY";
    /** Color grey. */
    public static final String     BITWIG_COLOR_GRAY          = "BITWIG_COLOR_GRAY";
    /** Color light grey. */
    public static final String     BITWIG_COLOR_LIGHT_GRAY    = "BITWIG_COLOR_LIGHT_GRAY";
    /** Color silver. */
    public static final String     BITWIG_COLOR_SILVER        = "BITWIG_COLOR_SILVER";
    /** Color dark brown. */
    public static final String     BITWIG_COLOR_DARK_BROWN    = "BITWIG_COLOR_DARK_BROWN";
    /** Color brown. */
    public static final String     BITWIG_COLOR_BROWN         = "BITWIG_COLOR_BROWN";
    /** Color dark blue. */
    public static final String     BITWIG_COLOR_DARK_BLUE     = "BITWIG_COLOR_DARK_BLUE";
    /** Color purple blue. */
    public static final String     BITWIG_COLOR_PURPLE_BLUE   = "BITWIG_COLOR_PURPLE_BLUE";
    /** Color purple. */
    public static final String     BITWIG_COLOR_PURPLE        = "BITWIG_COLOR_PURPLE";
    /** Color pink. */
    public static final String     BITWIG_COLOR_PINK          = "BITWIG_COLOR_PINK";
    /** Color red. */
    public static final String     BITWIG_COLOR_RED           = "BITWIG_COLOR_RED";
    /** Color orange. */
    public static final String     BITWIG_COLOR_ORANGE        = "BITWIG_COLOR_ORANGE";
    /** Color light orange. */
    public static final String     BITWIG_COLOR_LIGHT_ORANGE  = "BITWIG_COLOR_LIGHT_ORANGE";
    /** Color moss green. */
    public static final String     BITWIG_COLOR_MOSS_GREEN    = "BITWIG_COLOR_MOSS_GREEN";
    /** Color green. */
    public static final String     BITWIG_COLOR_GREEN         = "BITWIG_COLOR_GREEN";
    /** Color cold green. */
    public static final String     BITWIG_COLOR_COLD_GREEN    = "BITWIG_COLOR_COLD_GREEN";
    /** Color blue. */
    public static final String     BITWIG_COLOR_BLUE          = "BITWIG_COLOR_BLUE";
    /** Color purple. */
    public static final String     BITWIG_COLOR_LIGHT_PURPLE  = "BITWIG_COLOR_LIGHT_PURPLE";
    /** Color light pink. */
    public static final String     BITWIG_COLOR_LIGHT_PINK    = "BITWIG_COLOR_LIGHT_PINK";
    /** Color skin. */
    public static final String     BITWIG_COLOR_SKIN          = "BITWIG_COLOR_SKIN";
    /** Color reddish brown. */
    public static final String     BITWIG_COLOR_REDDISH_BROWN = "BITWIG_COLOR_REDDISH_BROWN";
    /** Color light brown. */
    public static final String     BITWIG_COLOR_LIGHT_BROWN   = "BITWIG_COLOR_LIGHT_BROWN";
    /** Color light green. */
    public static final String     BITWIG_COLOR_LIGHT_GREEN   = "BITWIG_COLOR_LIGHT_GREEN";
    /** Color bluish green. */
    public static final String     BITWIG_COLOR_BLUISH_GREEN  = "BITWIG_COLOR_BLUISH_GREEN";
    /** Color green blue. */
    public static final String     BITWIG_COLOR_GREEN_BLUE    = "BITWIG_COLOR_GREEN_BLUE";
    /** Color light blue. */
    public static final String     BITWIG_COLOR_LIGHT_BLUE    = "BITWIG_COLOR_LIGHT_BLUE";

    /** All IDs for the Bitwig track colors. */
    public static final String []  BITWIG_COLORS              = new String []
    {
        BITWIG_COLOR_GRAY,
        BITWIG_COLOR_DARK_GRAY,
        BITWIG_COLOR_GRAY,
        BITWIG_COLOR_LIGHT_GRAY,
        BITWIG_COLOR_SILVER,
        BITWIG_COLOR_DARK_BROWN,
        BITWIG_COLOR_BROWN,
        BITWIG_COLOR_DARK_BLUE,
        BITWIG_COLOR_PURPLE_BLUE,
        BITWIG_COLOR_PURPLE,
        BITWIG_COLOR_PINK,
        BITWIG_COLOR_RED,
        BITWIG_COLOR_ORANGE,
        BITWIG_COLOR_LIGHT_ORANGE,
        BITWIG_COLOR_MOSS_GREEN,
        BITWIG_COLOR_GREEN,
        BITWIG_COLOR_COLD_GREEN,
        BITWIG_COLOR_BLUE,
        BITWIG_COLOR_LIGHT_PURPLE,
        BITWIG_COLOR_LIGHT_PINK,
        BITWIG_COLOR_SKIN,
        BITWIG_COLOR_REDDISH_BROWN,
        BITWIG_COLOR_LIGHT_BROWN,
        BITWIG_COLOR_LIGHT_GREEN,
        BITWIG_COLOR_BLUISH_GREEN,
        BITWIG_COLOR_GREEN_BLUE,
        BITWIG_COLOR_LIGHT_BLUE
    };


    /**
     * Private due to utility class.
     */
    private BitwigColors ()
    {
        // Intentionally empty
    }


    /**
     * Get the RGB color for a color constant ID.
     *
     * @param colorId The ID of the color
     * @return The RGB values of the color
     */
    public static double [] getColorEntry (final String colorId)
    {
        for (int i = 0; i < BITWIG_COLORS.length; i++)
        {
            if (BITWIG_COLORS[i] == colorId)
                return (double []) COLORS[i];
        }
        return (double []) COLORS[0];
    }


    /**
     * Get the color ID that is assigned to the given RGB values.
     *
     * @param rgb The red, green and blue value
     * @return The ID or the COLOR_OFF ID if none is mapped
     */
    public static String getColorIndex (final double [] rgb)
    {
        return getColorIndex (rgb[0], rgb[1], rgb[2]);
    }


    /**
     * Get the color ID that is assigned to the given RGB values.
     *
     * @param red The red value
     * @param green The green value
     * @param blue The blue value
     * @return The ID or the COLOR_OFF ID if none is mapped
     */
    public static String getColorIndex (final double red, final double green, final double blue)
    {
        String cid = COLOR_OFF;
        double minError = 1.0;
        for (int i = 0; i < COLORS.length; i++)
        {
            final double [] color = (double []) COLORS[i];
            double error = Math.pow (color[0] - red, 2.0) + Math.pow (color[1] - green, 2.0) + Math.pow (color[2] - blue, 2.0);
            if (error < minError)
            {
                cid = BITWIG_COLORS[i];
                minError = error;
            }
        }
        return cid;
    }
}
