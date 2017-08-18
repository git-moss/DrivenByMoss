package de.mossgrabers.push.controller.display.model;

import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;


/**
 * Caches the pixel scaled variants of a given (base) font.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FontCache
{
    private Font              baseFont;
    private Map<Double, Font> scaledFonts = new HashMap<> ();


    /**
     * Constructor.
     */
    public FontCache ()
    {
        this ("monospace");
    }


    /**
     * Constructor.
     *
     * @param baseName The font family of the font
     */
    public FontCache (final String baseName)
    {
        this (baseName, Font.PLAIN, 16);
    }


    /**
     * Constructor.
     *
     * @param baseName The font family of the font
     * @param baseStyle The style settings of the font
     * @param baseSize The base size (in points) of the font from which all other pixel scales are
     *            calculated
     */
    public FontCache (final String baseName, final int baseStyle, final int baseSize)
    {
        this.baseFont = new Font (baseName, baseStyle, baseSize);
    }


    /**
     * Gets the base font.
     *
     * @return The base font
     */
    public Font getBaseFont ()
    {
        return this.baseFont;
    }


    /**
     * Get an instance of the font scaled to the given pixel size.
     *
     * @param size The pixel size of the font
     * @return The scaled font
     */
    public Font getFont (final int size)
    {
        final double scale = (double) size / (double) this.baseFont.getSize ();
        final Double s = Double.valueOf (scale);
        Font font;
        synchronized (this.scaledFonts)
        {
            font = this.scaledFonts.get (s);
            if (font == null)
            {
                font = this.baseFont.deriveFont (AffineTransform.getScaleInstance (scale, scale));
                this.scaledFonts.put (s, font);
            }
        }
        return font;
    }
}
