// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.controller;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.display.DisplayModel;
import de.mossgrabers.controller.push.controller.display.PushUsbDisplay;
import de.mossgrabers.controller.push.controller.display.VirtualDisplay;
import de.mossgrabers.controller.push.controller.display.grid.GridChangeListener;
import de.mossgrabers.framework.controller.display.AbstractDisplay;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * The display of Push 1 and Push 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushDisplay extends AbstractDisplay implements GridChangeListener
{
    /** Push character codes for value bars - a dash. */
    public static final String     BARS_NON      = Character.toString ((char) 6);
    /** Push character codes for value bars - one bar. */
    public static final String     BARS_ONE      = Character.toString ((char) 3);
    /** Push character codes for value bars - two bars. */
    public static final String     BARS_TWO      = Character.toString ((char) 5);
    /** Push character codes for value bars - one bar to the left. */
    private static final String    BARS_ONE_L    = Character.toString ((char) 4);
    /** Push character codes for value bars - four dashes. */
    private static final String    NON_4         = BARS_NON + BARS_NON + BARS_NON + BARS_NON;
    /** Push character codes for value bars - the right arrow. */
    public static final String     RIGHT_ARROW   = Character.toString ((char) 127);

    private static final String [] SPACES        =
    {
        "",
        " ",
        "  ",
        "   ",
        "    ",
        "     ",
        "      ",
        "       ",
        "        ",
        "         ",
        "          ",
        "           ",
        "            ",
        "             "
    };

    private static final String [] DASHES        =
    {
        "",
        BARS_NON,
        BARS_NON + BARS_NON,
        BARS_NON + BARS_NON + BARS_NON,
        BARS_NON + BARS_NON + BARS_NON + BARS_NON,
        BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON,
        BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON,
        BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON,
        BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON,
        BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON + BARS_NON
    };

    private static final String [] SYSEX_MESSAGE =
    {
        "F0 47 7F 15 18 00 45 00 ",
        "F0 47 7F 15 19 00 45 00 ",
        "F0 47 7F 15 1A 00 45 00 ",
        "F0 47 7F 15 1B 00 45 00 "
    };

    private int                    maxParameterValue;
    private boolean                isPush2;
    private DisplayModel           model;

    private final VirtualDisplay   virtualDisplay;
    private final PushUsbDisplay   usbDisplay;


    /**
     * Constructor. 4 rows (0-3) with 4 blocks (0-3). Each block consists of 17 characters or 2
     * cells (0-7).
     *
     * @param host The host
     * @param isPush2 True if Push 2
     * @param maxParameterValue
     * @param output The midi output
     * @param configuration The Push configuration
     */
    public PushDisplay (final IHost host, final boolean isPush2, final int maxParameterValue, final IMidiOutput output, final PushConfiguration configuration)
    {
        super (host, output, 4 /* No of rows */, 8 /* No of cells */, 68 /* No of characters */);
        this.maxParameterValue = maxParameterValue;
        this.isPush2 = isPush2;
        this.model = new DisplayModel ();
        this.model.addGridElementChangeListener (this);

        this.virtualDisplay = this.isPush2 ? new VirtualDisplay (host, this.model, configuration) : null;
        this.usbDisplay = this.isPush2 ? new PushUsbDisplay (host) : null;
    }


    /**
     * Create a message.
     *
     * @return The message
     */
    public DisplayModel getModel ()
    {
        return this.model;
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        if (this.isPush2)
        {
            this.model.setMessage (3, "Please start " + this.host.getName () + " to play...").send ();
            if (this.usbDisplay != null)
                this.usbDisplay.shutdown ();
            this.model.shutdown ();
        }
        else
            this.clear ().setBlock (1, 1, "     Please start").setBlock (1, 2, this.host.getName () + " to play...").allDone ().flush ();
    }


    /** {@inheritDoc} */
    @Override
    public PushDisplay clearCell (final int row, final int cell)
    {
        this.cells[row * 8 + cell] = cell % 2 == 0 ? "         " : "        ";
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public PushDisplay setBlock (final int row, final int block, final String value)
    {
        final int cell = 2 * block;
        if (value.length () > 9)
        {
            this.cells[row * 8 + cell] = value.substring (0, 9);
            this.cells[row * 8 + cell + 1] = pad (value.substring (9), 8, " ");
        }
        else
        {
            this.cells[row * 8 + cell] = pad (value, 9, " ");
            this.clearCell (row, cell + 1);
        }
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public PushDisplay setCell (final int row, final int cell, final int value, final Format format)
    {
        return this.setCell (row, cell, formatStr (value, format, this.maxParameterValue));
    }


    /** {@inheritDoc} */
    @Override
    public PushDisplay setCell (final int row, final int cell, final String value)
    {
        this.cells[row * 8 + cell] = pad (value, 8, " ") + (cell % 2 == 0 ? " " : "");
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        final int [] array = new int [text.length ()];
        for (int i = 0; i < text.length (); i++)
            array[i] = text.charAt (i);
        this.output.sendSysex (PushDisplay.SYSEX_MESSAGE[row] + toHexStr (array) + "F7");
    }


    /** {@inheritDoc} */
    @Override
    protected void notifyOnDisplay (final String message)
    {
        if (this.isPush2)
            this.model.setNotificationMessage (message);
        else
            super.notifyOnDisplay (message);
    }


    private static String formatStr (final int value, final Format format, final int maxParam)
    {
        switch (format)
        {
            case FORMAT_VALUE:
                return formatValue (value, maxParam);
            case FORMAT_PAN:
                return formatPan (value, maxParam);
            default:
                return Integer.toString (value);
        }
    }


    private static String formatValue (final int value, final int maxParam)
    {
        final int noOfBars = (int) Math.round (16.0 * value / maxParam);
        final StringBuilder n = new StringBuilder ();
        for (int j = 0; j < noOfBars / 2; j++)
            n.append (PushDisplay.BARS_TWO);
        if (noOfBars % 2 == 1)
            n.append (PushDisplay.BARS_ONE);
        return pad (n.toString (), 8, PushDisplay.BARS_NON);
    }


    private static String formatPan (final int pan, final int maxParam)
    {
        final int middle = maxParam / 2;
        if (pan == middle)
            return PushDisplay.NON_4 + PushDisplay.NON_4;
        final boolean isLeft = pan < middle;
        final int pos = isLeft ? middle - pan : pan - middle;
        final int noOfBars = 16 * pos / maxParam;
        StringBuilder n = new StringBuilder ();
        for (int i = 0; i < noOfBars / 2; i++)
            n.append (BARS_TWO);
        if (noOfBars % 2 == 1)
            n.append (isLeft ? PushDisplay.BARS_ONE_L : PushDisplay.BARS_ONE);
        n = new StringBuilder (PushDisplay.NON_4).append (pad (n.toString (), 4, PushDisplay.BARS_NON));
        return isLeft ? n.reverse ().toString () : n.toString ();
    }


    /**
     * Pad the given text with the given character until it reaches the given length.
     *
     * @param str The text to pad
     * @param length The maximum length
     * @param character The character to use for padding
     * @return The padded text
     */
    public static String pad (final String str, final int length, final String character)
    {
        final String text = str == null ? "" : str;
        final int diff = length - text.length ();
        if (diff < 0)
            return text.substring (0, length);
        if (diff > 0)
            return text + (" ".equals (character) ? PushDisplay.SPACES[diff] : PushDisplay.DASHES[diff]);
        return text;
    }


    private static String toHexStr (final int [] data)
    {
        final StringBuilder sysex = new StringBuilder ();
        for (final int d: data)
        {
            final String v = Integer.toHexString (d).toUpperCase ();
            if (v.length () < 2)
                sysex.append ('0');
            sysex.append (v).append (' ');
        }
        return sysex.toString ();
    }


    /**
     * Show the display debug window.
     */
    public void showDebugWindow ()
    {
        if (this.virtualDisplay != null)
            this.virtualDisplay.getImage ().showDisplayWindow ();
    }


    /** {@inheritDoc} */
    @Override
    public void gridHasChanged ()
    {
        if (this.usbDisplay != null)
            this.usbDisplay.send (this.virtualDisplay.getImage ());
    }
}