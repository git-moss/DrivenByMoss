// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.color;

/**
 * Some helper constants for Color.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ColorEx
{
    /** Color black. */
    public static final ColorEx BLACK       = ColorEx.fromRGB (0, 0, 0);
    /** Color white. */
    public static final ColorEx WHITE       = ColorEx.fromRGB (255, 255, 255);
    /** Color light gray. */
    public static final ColorEx LIGHT_GRAY  = ColorEx.fromRGB (182, 182, 182);
    /** Color gray. */
    public static final ColorEx GRAY        = ColorEx.fromRGB (128, 128, 128);
    /** Color dark gray. */
    public static final ColorEx DARK_GRAY   = ColorEx.fromRGB (89, 89, 89);
    /** Color red. */
    public static final ColorEx RED         = ColorEx.fromRGB (255, 0, 0);
    /** Color dark red. */
    public static final ColorEx DARK_RED    = ColorEx.fromRGB (128, 0, 0);
    /** Color green. */
    public static final ColorEx GREEN       = ColorEx.fromRGB (0, 255, 0);
    /** Color dark green. */
    public static final ColorEx DARK_GREEN  = ColorEx.fromRGB (0, 128, 0);
    /** Color blue. */
    public static final ColorEx BLUE        = ColorEx.fromRGB (0, 0, 255);
    /** Color dark blue. */
    public static final ColorEx DARK_BLUE   = ColorEx.fromRGB (0, 0, 128);
    /** Color yellow. */
    public static final ColorEx YELLOW      = ColorEx.fromRGB (255, 255, 0);
    /** Color dark yellow. */
    public static final ColorEx DARK_YELLOW = ColorEx.fromRGB (128, 128, 0);
    /** Color orange. */
    public static final ColorEx ORANGE      = ColorEx.fromRGB (255, 80, 0);
    /** Color dark orange. */
    public static final ColorEx DARK_ORANGE = ColorEx.fromRGB (128, 65, 0);
    /** Color pink. */
    public static final ColorEx PINK        = ColorEx.fromRGB (255, 0, 220);
    /** Color skin. */
    public static final ColorEx ROSE        = ColorEx.fromRGB (255, 127, 127);
    /** Color brown. */
    public static final ColorEx BROWN       = ColorEx.fromRGB (183, 73, 0);
    /** Color dark brown. */
    public static final ColorEx DARK_BROWN  = ColorEx.fromRGB (127, 0, 0);
    /** Color mint. */
    public static final ColorEx MINT        = ColorEx.fromRGB (170, 240, 209);
    /** Color olive. */
    public static final ColorEx OLIVE       = ColorEx.fromRGB (128, 128, 0);
    /** Color sky blue. */
    public static final ColorEx SKY_BLUE    = ColorEx.fromRGB (97, 238, 255);
    /** Color purple. */
    public static final ColorEx PURPLE      = ColorEx.fromRGB (116, 80, 164);
    /** Color dark purple. */
    public static final ColorEx DARK_PURPLE = ColorEx.evenDarker (PURPLE);
    /** Color red wine. */
    public static final ColorEx RED_WINE    = ColorEx.fromRGB (123, 42, 57);
    /** Color cyan. */
    public static final ColorEx CYAN        = ColorEx.fromRGB (0, 255, 255);

    private static final double FACTOR      = 0.7;
    private static final double FACTOR2     = 0.4;

    private final double        redValue;
    private final double        greenValue;
    private final double        blueValue;


    /**
     * Constructor.
     *
     * @param color The red, green and blue components (0..1)
     */
    public ColorEx (final double [] color)
    {
        this (color[0], color[1], color[2]);
    }


    /**
     * Constructor.
     *
     * @param red The red component (0..1)
     * @param green The green component (0..1)
     * @param blue The blue component (0..1)
     */
    public ColorEx (final double red, final double green, final double blue)
    {
        this.redValue = red;
        this.greenValue = green;
        this.blueValue = blue;
    }


    /**
     * Create a new color instance from 255 ints.
     *
     * @param red The red component
     * @param green The green component
     * @param blue The blue component
     * @return The new color
     */
    public static ColorEx fromRGB (final int red, final int green, final int blue)
    {
        return new ColorEx (red / 255.0, green / 255.0, blue / 255.0);
    }


    /**
     * Converts the components of a color, as specified by the HSB model, to an equivalent set of
     * values for the default RGB model.
     *
     * @param hue the hue component of the color
     * @param saturation the saturation of the color
     * @param brightness the brightness of the color
     * @return the RGB value of the color with the indicated hue, saturation, and brightness.
     */
    public static ColorEx fromHSB (final float hue, final float saturation, final float brightness)
    {
        if (saturation == 0)
        {
            final int value = (int) (brightness * 255.0f + 0.5f);
            return fromRGB (value, value, value);
        }

        final float h = (hue - (float) Math.floor (hue)) * 6.0f;
        final float f = h - (float) java.lang.Math.floor (h);
        final float p = brightness * (1.0f - saturation);
        final float q = brightness * (1.0f - saturation * f);
        final float t = brightness * (1.0f - saturation * (1.0f - f));

        switch ((int) h)
        {
            case 0:
                return fromRGB ((int) (brightness * 255.0f + 0.5f), (int) (t * 255.0f + 0.5f), (int) (p * 255.0f + 0.5f));

            case 1:
                return fromRGB ((int) (q * 255.0f + 0.5f), (int) (brightness * 255.0f + 0.5f), (int) (p * 255.0f + 0.5f));

            case 2:
                return fromRGB ((int) (p * 255.0f + 0.5f), (int) (brightness * 255.0f + 0.5f), (int) (t * 255.0f + 0.5f));

            case 3:
                return fromRGB ((int) (p * 255.0f + 0.5f), (int) (q * 255.0f + 0.5f), (int) (brightness * 255.0f + 0.5f));

            case 4:
                return fromRGB ((int) (t * 255.0f + 0.5f), (int) (p * 255.0f + 0.5f), (int) (brightness * 255.0f + 0.5f));

            case 5:
                return fromRGB ((int) (brightness * 255.0f + 0.5f), (int) (p * 255.0f + 0.5f), (int) (q * 255.0f + 0.5f));

            default:
                return ColorEx.BLACK;
        }
    }


    /**
     * Convert the internal color state to 3 integer RGB values.
     *
     * @return The 3 int (0-255) values
     */
    public int [] toIntRGB255 ()
    {
        return new int []
        {
            (int) Math.round (this.redValue * 255.0),
            (int) Math.round (this.greenValue * 255.0),
            (int) Math.round (this.blueValue * 255.0)
        };
    }


    /**
     * Convert the internal color state to 3 integer RGB values.
     *
     * @return The 3 integer (0-127) values
     */
    public int [] toIntRGB127 ()
    {
        return new int []
        {
            (int) Math.round (this.redValue * 127.0),
            (int) Math.round (this.greenValue * 127.0),
            (int) Math.round (this.blueValue * 127.0)
        };
    }


    /**
     * Convert the internal color state to 3 double RGB values.
     *
     * @return The 3 integer (0-1) values
     */
    public double [] toDoubleRGB ()
    {
        return new double []
        {
            this.redValue,
            this.greenValue,
            this.blueValue
        };
    }


    /**
     * Converts the components of a color, as specified by the default RGB model, to an equivalent
     * set of values for hue, saturation, and brightness that are the three components of the HSB
     * model.
     *
     * @return hue, saturation, brightness (hsb)
     */
    public float [] toHSB ()
    {
        final int [] rgb = this.toIntRGB255 ();
        final int r = rgb[0];
        final int g = rgb[1];
        final int b = rgb[2];

        int cmax = r > g ? r : g;
        if (b > cmax)
            cmax = b;
        int cmin = r < g ? r : g;
        if (b < cmin)
            cmin = b;

        final float brightness = cmax / 255.0f;
        float hue;
        float saturation;
        if (cmax != 0)
            saturation = (float) (cmax - cmin) / (float) cmax;
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else
        {
            final float redc = (float) (cmax - r) / (float) (cmax - cmin);
            final float greenc = (float) (cmax - g) / (float) (cmax - cmin);
            final float bluec = (float) (cmax - b) / (float) (cmax - cmin);
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }

        return new float []
        {
            hue,
            saturation,
            brightness
        };
    }


    /**
     * Calculates a brighter version of the given color.
     *
     * @param c A color
     * @return The brighter version
     */
    public static ColorEx brighter (final ColorEx c)
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
            return new ColorEx (i, i, i);

        if (r > 0 && r < i)
            r = i;

        if (g > 0 && g < i)
            g = i;

        if (b > 0 && b < i)
            b = i;

        return new ColorEx (Math.min (r / FACTOR, 1.0), Math.min (g / FACTOR, 1.0), Math.min (b / FACTOR, 1.0));
    }


    /**
     * Calculates a darker version of the given color.
     *
     * @param color A color
     * @return The darker version
     */
    public static ColorEx darker (final ColorEx color)
    {
        return new ColorEx (Math.max (color.getRed () * FACTOR, 0), Math.max (color.getGreen () * FACTOR, 0), Math.max (color.getBlue () * FACTOR, 0));
    }


    /**
     * Calculates a even more darker version of the given color.
     *
     * @param color A color
     * @return The even more darker version
     */
    public static ColorEx evenDarker (final ColorEx color)
    {
        return new ColorEx (Math.max (color.getRed () * FACTOR2, 0), Math.max (color.getGreen () * FACTOR2, 0), Math.max (color.getBlue () * FACTOR2, 0));
    }


    /**
     * Dim the color.
     *
     * @param hue The brightness intensity (0-1), 0 is black, 1 is no change
     * @return The dimmed color
     */
    public ColorEx dim (final double hue)
    {
        return new ColorEx (this.redValue * hue, this.greenValue * hue, this.blueValue * hue);
    }


    /**
     * Dim the color (calls evenDarker) and convert it to a gray scale color.
     *
     * @param color The color to dim
     * @return The dimmed color
     */
    public static ColorEx dimToGray (final ColorEx color)
    {
        final double red = color.getRed ();
        final double green = color.getGreen ();
        final double blue = color.getBlue ();

        if (red != green || green != blue)
        {
            final double v = (red + green + blue) / 3.0;
            return ColorEx.evenDarker (new ColorEx (v, v, v));
        }

        return ColorEx.evenDarker (color);
    }


    /**
     * Scale the color to the given brightness and saturation.
     *
     * @param brightness The brightness to scale to in the range of [0..1]
     * @param saturation The saturation to scale to in the range of [0..1]
     * @return The scaled color
     */
    public ColorEx scale (final double brightness, final double saturation)
    {
        final float [] hsb = this.toHSB ();
        return fromHSB (hsb[0], (float) Math.min (1.0, hsb[1] * 2 * saturation), (float) Math.max (hsb[2] * brightness, 0.1));

    }


    /**
     * Calculate the color from the palette which is the closest to the given color.
     *
     * @param color The color
     * @param palette The palette to pick one color from
     * @return The closest color from the palette
     */
    public static ColorEx getClosestColor (final ColorEx color, final ColorEx [] palette)
    {
        return palette[getClosestColorIndex (color, palette)];
    }


    /**
     * Calculate the color from the palette which is the closest to the given color and return the
     * index.
     *
     * @param color The color
     * @param palette The palette to pick one color from
     * @return The index of the closest color from the palette
     */
    public static int getClosestColorIndex (final ColorEx color, final ColorEx [] palette)
    {
        return getClosestColorIndex (color, palette, true);
    }


    /**
     * Calculate the color from the palette which is the closest to the given color and return the
     * index.
     *
     * @param color The color
     * @param palette The palette to pick one color from
     * @param useColorMetric If true uses color metrics to calculate the distance, see
     *            https://www.compuphase.com/cmetric.htm
     * @return The index of the closest color from the palette
     */
    public static int getClosestColorIndex (final ColorEx color, final ColorEx [] palette, final boolean useColorMetric)
    {
        double minError = 5.0;
        int closest = 0;
        for (int i = 0; i < palette.length; i++)
        {
            final double error = ColorEx.calcDistance (palette[i], color, useColorMetric);
            if (error < minError)
            {
                closest = i;
                minError = error;
            }
        }
        return closest;
    }


    /**
     * Calculate the difference between colors. See https://www.compuphase.com/cmetric.htm
     *
     * @param color1 The first color
     * @param color2 The second color
     * @param useColorMetric If true uses color metrics to calculate the distance, see
     *            https://www.compuphase.com/cmetric.htm
     * @return The distance
     */
    public static double calcDistance (final ColorEx color1, final ColorEx color2, final boolean useColorMetric)
    {
        return calcDistance (color1.toDoubleRGB (), color2.toDoubleRGB (), useColorMetric);
    }


    /**
     * Calculate the difference between colors. See https://www.compuphase.com/cmetric.htm
     *
     * @param color1 The first color
     * @param color2 The second color
     * @param useColorMetric If true uses color metrics to calculate the distance, see
     *            https://www.compuphase.com/cmetric.htm
     * @return The distance
     */
    public static double calcDistance (final double [] color1, final double [] color2, final boolean useColorMetric)
    {
        final double deltaR = color1[0] - color2[0];
        final double deltaG = color1[1] - color2[1];
        final double deltaB = color1[2] - color2[2];

        if (useColorMetric)
        {
            final double rmean = (color1[0] + color2[0]) / 2.0;
            return Math.sqrt ((2.0 + rmean) * deltaR * deltaR + 4.0 * deltaG * deltaG + (2.99609375 - rmean) * deltaB * deltaB);
        }

        return Math.abs (deltaR) + Math.abs (deltaG) + Math.abs (deltaB);
    }


    /**
     * Calculates if the color white or black has a higher contrast to the given color.
     *
     * @param c A color
     * @return Black or white, depending on which one has the higher contrast
     */
    public static ColorEx calcContrastColor (final ColorEx c)
    {
        // The formula is based on the W3C Accessibility Guidelines - https://www.w3.org/TR/WCAG20/
        final double l = 0.2126 * c.getRed () + 0.7152 * c.getGreen () + 0.0722 * c.getBlue ();
        return l > 0.179 ? ColorEx.BLACK : ColorEx.WHITE;
    }


    /**
     * Encodes the red, green and blue values as 3 byte values into an integer. Red is the least
     * significant byte.
     *
     * @return The encoded color
     */
    public int encode ()
    {
        final int [] c = this.toIntRGB255 ();
        return c[0] + (c[1] << 8) + (c[2] << 16);
    }


    /**
     * Decodes the red, green and blue values as 3 byte values from an integer. Red is the least
     * significant byte.
     *
     * @param encodedColor The encoded color
     * @return The decoded color
     */
    public static ColorEx decode (final int encodedColor)
    {
        final int red = encodedColor & 0xFF;
        final int green = encodedColor >> 8 & 0xFF;
        final int blue = encodedColor >> 16 & 0xFF;
        return fromRGB (red, green, blue);
    }


    /**
     * Get the red component.
     *
     * @return The red component
     */
    public double getRed ()
    {
        return this.redValue;
    }


    /**
     * Get the green component.
     *
     * @return The green component
     */
    public double getGreen ()
    {
        return this.greenValue;
    }


    /**
     * Get the blue component.
     *
     * @return The blue component
     */
    public double getBlue ()
    {
        return this.blueValue;
    }


    /**
     * Is this a gray value?
     *
     * @return True if all RGB values contain the same value
     */
    public boolean isGrayscale ()
    {
        return this.redValue == this.greenValue && this.greenValue == this.blueValue;
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits (this.blueValue);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits (this.greenValue);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits (this.redValue);
        result = prime * result + (int) (temp ^ temp >>> 32);
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || this.getClass () != obj.getClass ())
            return false;
        final ColorEx other = (ColorEx) obj;
        if (Double.doubleToLongBits (this.blueValue) != Double.doubleToLongBits (other.blueValue) || Double.doubleToLongBits (this.greenValue) != Double.doubleToLongBits (other.greenValue))
            return false;
        return Double.doubleToLongBits (this.redValue) == Double.doubleToLongBits (other.redValue);
    }
}
