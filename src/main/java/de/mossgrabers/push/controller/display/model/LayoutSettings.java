// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller.display.model;

import de.mossgrabers.push.controller.display.model.grid.ColorEx;

import com.bitwig.extension.api.Color;


/**
 * Manages the settings of the layout (color and fonts).
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayoutSettings
{
    private static final Color DEFAULT_COLOR_TEXT       = ColorEx.WHITE;
    private static final Color DEFAULT_COLOR_BACKGROUND = Color.fromRGB255 (83, 83, 83);
    private static final Color DEFAULT_COLOR_BORDER     = ColorEx.BLACK;
    private static final Color DEFAULT_COLOR_FADER      = Color.fromRGB255 (69, 44, 19);
    private static final Color DEFAULT_COLOR_VU         = ColorEx.GREEN;
    private static final Color DEFAULT_COLOR_EDIT       = Color.fromRGB255 (240, 127, 17);
    private static final Color DEFAULT_COLOR_RECORD     = ColorEx.RED;
    private static final Color DEFAULT_COLOR_SOLO       = ColorEx.YELLOW;
    private static final Color DEFAULT_COLOR_MUTE       = Color.fromRGB255 (245, 129, 17);


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


    public Color getRecordColor ()
    {
        return DEFAULT_COLOR_RECORD;
    }


    public Color getSoloColor ()
    {
        return DEFAULT_COLOR_SOLO;
    }


    public Color getMuteColor ()
    {
        return DEFAULT_COLOR_MUTE;
    }


    /**
     * Reset the font and color settings.
     */
    public void reset ()
    {
        // this.textColorProperty.set (DEFAULT_COLOR_TEXT);
        // this.backgroundColorProperty.set (DEFAULT_COLOR_BACKGROUND);
        // this.borderColorProperty.set (DEFAULT_COLOR_BORDER);
        // this.faderColorProperty.set (DEFAULT_COLOR_FADER);
        // this.vuColorProperty.set (DEFAULT_COLOR_VU);
        // this.editColorProperty.set (DEFAULT_COLOR_EDIT);
    }
}
