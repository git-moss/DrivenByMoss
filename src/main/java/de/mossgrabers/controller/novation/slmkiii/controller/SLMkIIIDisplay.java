// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.controller;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.AbstractTextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The displays of SL MkIII.
 *
 * @author Jürgen Moßgraber
 */
public class SLMkIIIDisplay extends AbstractTextDisplay
{
    private static final String  MKIII_SYSEX_HEADER               = "F0 00 20 29 02 0A 01 ";
    private static final String  MKIII_SYSEX_LAYOUT_COMMAND       = MKIII_SYSEX_HEADER + "01 %02d F7";
    private static final String  MKIII_SYSEX_PROPERTY_COMMAND     = MKIII_SYSEX_HEADER + "02 %02d %02d %02d %s F7";
    private static final String  MKIII_SYSEX_LED_COMMAND          = MKIII_SYSEX_HEADER + "03 %02X 01 %02X %02X %02X F7";

    private static final String  MKIII_SYSEX_NOTIFICATION_COMMAND = MKIII_SYSEX_HEADER + "04 %s F7";

    /** The empty layout. */
    public static final Integer  SCREEN_LAYOUT_EMPTY              = Integer.valueOf (0);
    /** The layout with knobs. */
    public static final Integer  SCREEN_LAYOUT_KNOB               = Integer.valueOf (1);
    /** The layout with larger selection boxes. */
    public static final Integer  SCREEN_LAYOUT_BOX                = Integer.valueOf (2);

    private static final Integer PROPERTY_TEXT                    = Integer.valueOf (1);
    private static final Integer PROPERTY_COLOR                   = Integer.valueOf (2);
    private static final Integer PROPERTY_VALUE                   = Integer.valueOf (3);

    private final String []      ledCache                         = new String [8];
    private final int [] []      displayColorCache                = new int [9] [4];
    private final int [] []      displayValueCache                = new int [9] [4];


    /**
     * Constructor. 4 rows (0-3) with 9 blocks (0-8). Each block consists of 18 characters.
     *
     * @param host The host
     * @param output The output to which the display is connected
     */
    public SLMkIIIDisplay (final IHost host, final IMidiOutput output)
    {
        super (host, output, 4 /* No of rows */, 9 /* No of cells */, 9 * 9 /* No of characters */);

        for (int i = 0; i < 8; i++)
            this.ledCache[i] = "";
        this.clearDisplayCache ();
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        for (int i = 0; i < this.noOfCells; i++)
        {
            final int pos = 9 * i;
            final String cellText = text.substring (pos, pos + 9).trim ();
            this.setPropertyText (i, row, cellText);
        }
    }


    /**
     * Set the layout of the display.
     *
     * @param layout Options are SCREEN_LAYOUT_EMPTY, SCREEN_LAYOUT_KNOB or SCREEN_LAYOUT_BOX
     */
    public void setDisplayLayout (final Integer layout)
    {
        this.output.sendSysex (String.format (MKIII_SYSEX_LAYOUT_COMMAND, layout));
        this.clearDisplayCache ();
        this.forceFlush ();
    }


    /**
     * Hide all knob elements.
     */
    public void hideAllElements ()
    {
        for (int i = 0; i < 8; i++)
        {
            this.setPropertyColor (i, 0, SLMkIIIColorManager.SLMKIII_BLACK);
            this.setPropertyColor (i, 1, SLMkIIIColorManager.SLMKIII_BLACK);
        }
    }


    /**
     * Set one of the colors of the LED faders.
     *
     * @param led The LED index (0-7)
     * @param color The color to set
     */
    public void setFaderLEDColor (final int led, final ColorEx color)
    {
        final int [] rgb = color.toIntRGB127 ();
        this.output.sendSysex (String.format (MKIII_SYSEX_LED_COMMAND, Integer.valueOf (led), Integer.valueOf (rgb[0]), Integer.valueOf (rgb[1]), Integer.valueOf (rgb[2])));
    }


    /**
     * Set a color property.
     *
     * @param hPosition The horizontal position (0-8)
     * @param vPosition The vertical position (0-5)
     * @param color The color index (0-127)
     */
    public void setPropertyColor (final int hPosition, final int vPosition, final int color)
    {
        if (this.displayColorCache[hPosition][vPosition] == color)
            return;
        this.displayColorCache[hPosition][vPosition] = color;

        this.setProperty (PROPERTY_COLOR, hPosition, vPosition, StringUtils.toHexStr (color));
    }


    /**
     * Set a text property.
     *
     * @param hPosition The horizontal position (0-8)
     * @param vPosition The vertical position (0-5), 0 (Parametername),1 (Parametertextvalue), 5
     *            (Trackname)
     * @param text The text
     */
    private void setPropertyText (final int hPosition, final int vPosition, final String text)
    {
        String ascii = StringUtils.fixASCII (text);
        if (ascii.length () > 9)
            ascii = ascii.substring (0, 9);
        final String value = StringUtils.toHexStr (ascii.getBytes ()) + "00";
        this.setProperty (PROPERTY_TEXT, hPosition, vPosition, value);
    }


    /**
     * Set a value property. Turns on/off the bottom box.
     *
     * @param hPosition The horizontal position (0-8)
     * @param vPosition The vertical position (0-5)
     * @param value The value
     */
    public void setPropertyValue (final int hPosition, final int vPosition, final int value)
    {
        if (this.displayValueCache[hPosition][vPosition] == value)
            return;
        this.displayValueCache[hPosition][vPosition] = value;

        this.setProperty (PROPERTY_VALUE, hPosition, vPosition, StringUtils.toHexStr (value));
    }


    /**
     * Set a display property.
     *
     * @param property The property: PROPERTY_TEXT, PROPERTY_COLOR or PROPERTY_VALUE
     * @param hPosition The horizontal position (0-8)
     * @param vPosition The vertical position (0-5)
     * @param values The formatted values to insert
     */
    private void setProperty (final Integer property, final int hPosition, final int vPosition, final String values)
    {
        final String msg = String.format (MKIII_SYSEX_PROPERTY_COMMAND, Integer.valueOf (hPosition), property, Integer.valueOf (vPosition), values);
        this.output.sendSysex (msg);
    }


    /** {@inheritDoc} */
    @Override
    protected void notifyOnDisplay (final String message)
    {
        final String ascii = StringUtils.fixASCII (message);

        String text1;
        String text2;
        if (ascii.length () > 18)
        {
            text1 = ascii.substring (0, 18);
            text2 = ascii.substring (18, message.length ());
        }
        else
        {
            text1 = ascii;
            text2 = "";
        }

        final String content = StringUtils.asciiToHex (text1) + "00 " + StringUtils.asciiToHex (text2) + "00";
        final String msg = String.format (MKIII_SYSEX_NOTIFICATION_COMMAND, content);
        this.output.sendSysex (msg);
    }


    /**
     * Clear the cache of the display colors and values.
     */
    private void clearDisplayCache ()
    {
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                this.displayColorCache[i][j] = -1;
                this.displayValueCache[i][j] = -1;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.hideAllElements ();
        this.setPropertyColor (8, 0, SLMkIIIColorManager.SLMKIII_BLACK);
        this.setPropertyColor (8, 1, SLMkIIIColorManager.SLMKIII_BLACK);

        for (int i = 0; i < 9; i++)
        {
            this.setPropertyColor (i, 2, SLMkIIIColorManager.SLMKIII_BLACK);
            this.setPropertyValue (i, 1, 0);
        }

        this.clear ().setCell (1, 2, "Please").setCell (1, 3, "start").setCell (1, 4, this.host.getName () + "...").allDone ();
        this.flush ();
    }
}