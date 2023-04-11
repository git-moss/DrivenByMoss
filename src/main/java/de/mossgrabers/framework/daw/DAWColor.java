// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.controller.color.ColorEx;

import java.util.List;


/**
 * Support for handling the colors used in the DAW.
 *
 * @author Jürgen Moßgraber
 */
public enum DAWColor
{
    /** Color off. */
    COLOR_OFF("Off", new ColorEx (0.5, 0.5, 0.5)),
    /** Color dark grey. */
    DAW_COLOR_DARK_GRAY("Dark Gray", new ColorEx (0.3294117748737335, 0.3294117748737335, 0.3294117748737335)),
    /** Color gray. */
    DAW_COLOR_GRAY("Gray", new ColorEx (0.47843137383461, 0.47843137383461, 0.47843137383461)),
    /** Color half grey. */
    DAW_COLOR_GRAY_HALF("Gray half", new ColorEx (0.5, 0.5, 0.5)),
    /** Color light grey. */
    DAW_COLOR_LIGHT_GRAY("Light Gray", new ColorEx (0.7882353067398071, 0.7882353067398071, 0.7882353067398071)),
    /** Color silver. */
    DAW_COLOR_SILVER("Silver", new ColorEx (0.5254902243614197, 0.5372549295425415, 0.6745098233222961)),
    /** Color dark brown. */
    DAW_COLOR_DARK_BROWN("Dark Brown", new ColorEx (0.6392157077789307, 0.4745098054409027, 0.26274511218070984)),
    /** Color brown. */
    DAW_COLOR_BROWN("Brown", new ColorEx (0.7764706015586853, 0.6235294342041016, 0.43921568989753723)),
    /** Color dark blue. */
    DAW_COLOR_DARK_BLUE("Dark Blue", new ColorEx (0.34117648005485535, 0.3803921639919281, 0.7764706015586853)),
    /** Color purple blue. */
    DAW_COLOR_PURPLE_BLUE("Purplish Blue", new ColorEx (0.5176470875740051, 0.5411764979362488, 0.8784313797950745)),
    /** Color purple. */
    DAW_COLOR_PURPLE("Purple", new ColorEx (0.5843137502670288, 0.2862745225429535, 0.7960784435272217)),
    /** Color pink. */
    DAW_COLOR_PINK("Pink", new ColorEx (0.8509804010391235, 0.21960784494876862, 0.4431372582912445)),
    /** Color red. */
    DAW_COLOR_RED("Red", new ColorEx (0.8509804010391235, 0.18039216101169586, 0.1411764770746231)),
    /** Color orange. */
    DAW_COLOR_ORANGE("Orange", new ColorEx (1, 0.34117648005485535, 0.0235294122248888)),
    /** Color light orange. */
    DAW_COLOR_LIGHT_ORANGE("Light Orange", new ColorEx (0.8509804010391235, 0.615686297416687, 0.062745101749897)),
    /** Color moss green. */
    DAW_COLOR_MOSS_GREEN("Moss Green", new ColorEx (0.26274511218070984, 0.8235294222831726, 0.7254902124404907)),
    /** Color green. */
    DAW_COLOR_GREEN("Green", new ColorEx (0.45098039507865906, 0.5960784554481506, 0.0784313753247261)),
    /** Color cold green. */
    DAW_COLOR_COLD_GREEN("Cold Green", new ColorEx (0, 0.615686297416687, 0.27843138575553894)),
    /** Color blue. */
    DAW_COLOR_BLUE("Blue", new ColorEx (0.2666666805744171, 0.7843137383460999, 1)),
    /** Color purple. */
    DAW_COLOR_LIGHT_PURPLE("Light Purple", new ColorEx (0.7372549176216125, 0.4627451002597809, 0.9411764740943909)),
    /** Color light pink. */
    DAW_COLOR_LIGHT_PINK("Light Pink", new ColorEx (0.8823529481887817, 0.4000000059604645, 0.5686274766921997)),
    /** Color skin. */
    DAW_COLOR_ROSE("Rose", new ColorEx (0.9254902005195618, 0.3803921639919281, 0.34117648005485535)),
    /** Color reddish brown. */
    DAW_COLOR_REDDISH_BROWN("Redish Brown", new ColorEx (1, 0.5137255191802979, 0.24313725531101227)),
    /** Color light brown. */
    DAW_COLOR_LIGHT_BROWN("Light Brown", new ColorEx (0.8941176533699036, 0.7176470756530762, 0.30588236451148987)),
    /** Color light green. */
    DAW_COLOR_LIGHT_GREEN("Light Green", new ColorEx (0.6274510025978088, 0.7529411911964417, 0.2980392277240753)),
    /** Color bluish green. */
    DAW_COLOR_BLUISH_GREEN("Bluish Green", new ColorEx (0, 0.6509804129600525, 0.5803921818733215)),
    /** Color green blue. */
    DAW_COLOR_GREEN_BLUE("Greenish Blue", new ColorEx (0.24313725531101227, 0.7333333492279053, 0.3843137323856354)),
    /** Color light blue. */
    DAW_COLOR_LIGHT_BLUE("Light Blue", new ColorEx (0, 0.6000000238418579, 0.8509804010391235));


    private static final List<DAWColor> NEW_TRACK_COLORS = List.of (DAW_COLOR_PURPLE, DAW_COLOR_PINK, DAW_COLOR_RED, DAW_COLOR_ORANGE, DAW_COLOR_LIGHT_ORANGE, DAW_COLOR_MOSS_GREEN, DAW_COLOR_GREEN, DAW_COLOR_COLD_GREEN, DAW_COLOR_BLUE);
    private static DAWColor             newTrackColor    = DAW_COLOR_DARK_BLUE;

    private String                      name;
    private ColorEx                     color;


    /**
     * Constructor.
     *
     * @param name The name of the color
     * @param color The color
     */
    DAWColor (final String name, final ColorEx color)
    {
        this.name = name;
        this.color = color;
    }


    /**
     * Get the name of the color.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Get the color.
     *
     * @return The color
     */
    public ColorEx getColor ()
    {
        return this.color;
    }


    /**
     * Get the RGB color for a color constant ID.
     *
     * @param colorId The ID of the color
     * @return The RGB values of the color
     */
    public static ColorEx getColorEntry (final String colorId)
    {
        final DAWColor value = DAWColor.valueOf (colorId);
        return value == null ? COLOR_OFF.getColor () : value.getColor ();
    }


    /**
     * Get the RGB color at the given index.
     *
     * @param colorIndex The index of the color
     * @return The RGB values of the color
     */
    public static ColorEx getColorEntry (final int colorIndex)
    {
        final DAWColor [] values = DAWColor.values ();
        return values[colorIndex >= 0 && colorIndex < values.length ? colorIndex : 0].getColor ();
    }


    /**
     * Get the color ID that is assigned to the given RGB values.
     *
     * @param rgb The red, green and blue value
     * @return The ID or the COLOR_OFF ID if none is mapped
     */
    public static String getColorID (final double [] rgb)
    {
        return getColorID (rgb[0], rgb[1], rgb[2]);
    }


    /**
     * Get the color ID that is assigned to the closest given RGB values.
     *
     * @param red The red value
     * @param green The green value
     * @param blue The blue value
     * @return The ID or the COLOR_OFF ID if none is mapped
     */
    public static String getColorID (final double red, final double green, final double blue)
    {
        return getColorID (new ColorEx (red, green, blue));
    }


    /**
     * Get the color ID that is assigned to the given RGB values.
     *
     * @param color The color
     * @return The ID or the COLOR_OFF ID if none is mapped
     */
    public static String getColorID (final ColorEx color)
    {
        final DAWColor [] values = DAWColor.values ();
        DAWColor cid = values[0];
        double minError = 5.0;
        for (int i = 1; i < values.length; i++)
        {
            final double error = ColorEx.calcDistance (values[i].getColor (), color, true);
            if (error < minError)
            {
                cid = values[i];
                minError = error;
            }
        }
        return cid.name ();
    }


    /**
     * Cycle through some of the DAWColors to assign to a new track.
     *
     * @return The next color to use
     */
    public static DAWColor getNextColor ()
    {
        final DAWColor color = newTrackColor;

        int position = NEW_TRACK_COLORS.indexOf (color) + 1;
        if (position >= NEW_TRACK_COLORS.size ())
            position = 0;
        newTrackColor = NEW_TRACK_COLORS.get (position);

        return color;
    }
}
