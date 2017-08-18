package de.mossgrabers.push.controller.display.model;

import java.awt.Color;
import java.awt.Font;


/**
 * Manages the settings of the layout (color and fonts).
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayoutSettings
{
    private static final Color DEFAULT_COLOR_TEXT       = Color.WHITE;
    private static final Color DEFAULT_COLOR_BACKGROUND = new Color (83, 83, 83);
    private static final Color DEFAULT_COLOR_BORDER     = Color.BLACK;
    private static final Color DEFAULT_COLOR_FADER      = new Color (69, 44, 19);
    private static final Color DEFAULT_COLOR_VU         = Color.GREEN;
    private static final Color DEFAULT_COLOR_EDIT       = new Color (240, 127, 17);

    private FontCache          textFont                 = new FontCache ();

    // private final SimpleObjectProperty<FontCache> textFontProperty = new SimpleObjectProperty<>
    // (new FontCache ());
    // private final SimpleObjectProperty<Color> textColorProperty = new SimpleObjectProperty<>
    // (DEFAULT_COLOR_TEXT);
    // private final SimpleObjectProperty<Color> backgroundColorProperty = new
    // SimpleObjectProperty<> (DEFAULT_COLOR_BACKGROUND);
    // private final SimpleObjectProperty<Color> borderColorProperty = new SimpleObjectProperty<>
    // (DEFAULT_COLOR_BORDER);
    // private final SimpleObjectProperty<Color> faderColorProperty = new SimpleObjectProperty<>
    // (DEFAULT_COLOR_FADER);
    // private final SimpleObjectProperty<Color> vuColorProperty = new SimpleObjectProperty<>
    // (DEFAULT_COLOR_VU);
    // private final SimpleObjectProperty<Color> editColorProperty = new SimpleObjectProperty<>
    // (DEFAULT_COLOR_EDIT);


    /**
     * Set a new font.
     *
     * @param fontName The name of the font
     */
    public void setTextFont (final String fontName)
    {
        this.textFont = new FontCache (fontName);
    }


    /**
     * Get the currently selected font.
     *
     * @return The font
     */
    public Font getTextFont ()
    {
        return this.textFont.getBaseFont ();
    }


    /**
     * Get the currently selected font with the preferred size.
     *
     * @param size The size of the font
     * @return The font
     */
    public Font getTextFont (final int size)
    {
        return this.textFont.getFont (size);
    }


    /**
     * Set a new color for the text.
     *
     * @param textColor The new color
     */
    public void setTextColor (final Color textColor)
    {
        // this.textColorProperty.set (textColor);
    }


    /**
     * Get the current color for the text.
     *
     * @return The color
     */
    public Color getTextColor ()
    {
        return DEFAULT_COLOR_TEXT;
    }


    /**
     * Set a new background color for the text.
     *
     * @param backgroundColor The new color
     */
    public void setBackgroundColor (final Color backgroundColor)
    {
        // this.backgroundColorProperty.set (backgroundColor);
    }


    /**
     * Get the current background color for the text.
     *
     * @return The color
     */
    public Color getBackgroundColor ()
    {
        return DEFAULT_COLOR_BACKGROUND;
    }


    /**
     * Set a new border color for the text.
     *
     * @param borderColor The new color
     */
    public void setBorderColor (final Color borderColor)
    {
        // this.borderColorProperty.set (borderColor);
    }


    /**
     * Get the current border color for the text.
     *
     * @return The color
     */
    public Color getBorderColor ()
    {
        return DEFAULT_COLOR_BORDER;
    }


    /**
     * Set a new fader color for the text.
     *
     * @param faderColor The new color
     */
    public void setFaderColor (final Color faderColor)
    {
        // this.faderColorProperty.set (faderColor);
    }


    /**
     * Get the current fader color for the text.
     *
     * @return The color
     */
    public Color getFaderColor ()
    {
        return DEFAULT_COLOR_FADER;
    }


    /**
     * Set a new VU color for the text.
     *
     * @param vuColor The new color
     */
    public void setVuColor (final Color vuColor)
    {
        // this.vuColorProperty.set (vuColor);
    }


    /**
     * Get the current VU color for the text.
     *
     * @return The color
     */
    public Color getVuColor ()
    {
        return DEFAULT_COLOR_VU;
    }


    /**
     * Set a new edit color for the text.
     *
     * @param editColor The new color
     */
    public void setEditColor (final Color editColor)
    {
        // this.editColorProperty.set (editColor);
    }


    /**
     * Get the current edit color for the text.
     *
     * @return The color
     */
    public Color getEditColor ()
    {
        return DEFAULT_COLOR_EDIT;
    }


    /**
     * Reset the font and color settings.
     */
    public void reset ()
    {
        // this.textFontProperty.set (new FontCache ());
        // this.textColorProperty.set (DEFAULT_COLOR_TEXT);
        // this.backgroundColorProperty.set (DEFAULT_COLOR_BACKGROUND);
        // this.borderColorProperty.set (DEFAULT_COLOR_BORDER);
        // this.faderColorProperty.set (DEFAULT_COLOR_FADER);
        // this.vuColorProperty.set (DEFAULT_COLOR_VU);
        // this.editColorProperty.set (DEFAULT_COLOR_EDIT);
    }
}
