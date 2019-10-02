// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.autocolor;

/**
 * All Bitwig colors with a name and their RGB value.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum NamedColor
{
    /** The color dark gray. */
    DARK_GRAY("Dark Gray", 0.3294117748737335, 0.3294117748737335, 0.3294117748737335),
    /** The color gray. */
    GRAY("Gray", 0.47843137383461, 0.47843137383461, 0.47843137383461),
    /** The color light gray. */
    LIGHT_GRAY("Light Gray", 0.7882353067398071, 0.7882353067398071, 0.7882353067398071),
    /** The color silver. */
    SILVER("Silver", 0.5254902243614197, 0.5372549295425415, 0.6745098233222961),
    /** The color dark brown. */
    DARK_BROWN("Dark Brown", 0.6392157077789307, 0.4745098054409027, 0.26274511218070984),
    /** The color brown. */
    BROWN("Brown", 0.7764706015586853, 0.6235294342041016, 0.43921568989753723),
    /** The color dark blue. */
    DARK_BLUE("Dark Blue", 0.34117648005485535, 0.3803921639919281, 0.7764706015586853),
    /** The color purplish blue. */
    PURPLISH_BLUE("Purplish Blue", 0.5176470875740051, 0.5411764979362488, 0.8784313797950745),
    /** The color purple. */
    PURPLE("Purple", 0.5843137502670288, 0.2862745225429535, 0.7960784435272217),
    /** The color pink. */
    PINK("Pink", 0.8509804010391235, 0.21960784494876862, 0.4431372582912445),
    /** The color red. */
    RED("Red", 0.8509804010391235, 0.18039216101169586, 0.1411764770746231),
    /** The color orange. */
    ORANGE("Orange", 1, 0.34117648005485535, 0.0235294122248888),
    /** The color light orange. */
    LIGHT_ORANGE("Light Orange", 0.8509804010391235, 0.615686297416687, 0.062745101749897),
    /** The color green. */
    GREEN("Green", 0.45098039507865906, 0.5960784554481506, 0.0784313753247261),
    /** The color cold green. */
    COLD_GREEN("Cold Green", 0, 0.615686297416687, 0.27843138575553894),
    /** The color bluish green. */
    BLUISH_GREEN("Bluish Green", 0, 0.6509804129600525, 0.5803921818733215),
    /** The color blue. */
    BLUE("Blue", 0, 0.6000000238418579, 0.8509804010391235),
    /** The color light purple. */
    LIGHT_PURPLE("Light Purple", 0.7372549176216125, 0.4627451002597809, 0.9411764740943909),
    /** The color light pink. */
    LIGHT_PINK("Light Pink", 0.8823529481887817, 0.4000000059604645, 0.5686274766921997),
    /** The color skin. */
    SKIN("Skin", 0.9254902005195618, 0.3803921639919281, 0.34117648005485535),
    /** The color redish brown. */
    REDISH_BROWN("Redish Brown", 1, 0.5137255191802979, 0.24313725531101227),
    /** The color light brown. */
    LIGHT_BROWN("Light Brown", 0.8941176533699036, 0.7176470756530762, 0.30588236451148987),
    /** The color light green. */
    LIGHT_GREEN("Light Green", 0.6274510025978088, 0.7529411911964417, 0.2980392277240753),
    /** The color grass green. */
    GRASS_GREEN("Grass Green", 0.24313725531101227, 0.7333333492279053, 0.3843137323856354),
    /** The color light blue. */
    LIGHT_BLUE("Light Blue", 0.26274511218070984, 0.8235294222831726, 0.7254902124404907),
    /** The color greenish blue. */
    GREENISH_BLUE("Greenish Blue", 0.2666666805744171, 0.7843137383460999, 1);


    private String name;
    private double red;
    private double green;
    private double blue;


    /**
     * Constructor.
     *
     * @param name The name of the color
     * @param red The red value
     * @param green The green value
     * @param blue The blue
     */
    NamedColor (final String name, final double red, final double green, final double blue)
    {
        this.name = name;
        this.red = red;
        this.green = green;
        this.blue = blue;
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
     * Get the red value of the color.
     *
     * @return The red value
     */
    public double getRed ()
    {
        return this.red;
    }


    /**
     * Get the green value of the color.
     *
     * @return The green value
     */
    public double getGreen ()
    {
        return this.green;
    }


    /**
     * Get the blue value of the color.
     *
     * @return The blue value
     */
    public double getBlue ()
    {
        return this.blue;
    }
}
