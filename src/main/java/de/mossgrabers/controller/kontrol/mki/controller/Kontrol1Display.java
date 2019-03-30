// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mki.controller;

import de.mossgrabers.controller.kontrol.mki.Kontrol1Configuration;
import de.mossgrabers.framework.controller.display.AbstractDisplay;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IHost;


/**
 * The display of Kontrol 1.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1Display extends AbstractDisplay
{
    private static final String [] SPACES =
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

    private int                    maxParameterValue;
    private Kontrol1UsbDevice      usbDevice;


    /**
     * Constructor. 2 rows (0-1) with 9 blocks (0-8). Each block consists of 8 characters.
     *
     * @param host The host
     * @param maxParameterValue
     * @param configuration The configuration
     * @param usbDevice The USB device
     */
    public Kontrol1Display (final IHost host, final int maxParameterValue, final Kontrol1Configuration configuration, final Kontrol1UsbDevice usbDevice)
    {
        super (host, null, 2 /* No of rows */, 9 /* No of cells */, 72 /* No of characters */);
        this.maxParameterValue = maxParameterValue;
        this.usbDevice = usbDevice;
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.usbDevice.shutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();
        this.usbDevice.sendDisplayData ();
    }


    /** {@inheritDoc} */
    @Override
    public AbstractDisplay clear ()
    {
        for (int i = 0; i < 9; i++)
        {
            this.usbDevice.setBar (i, false, 0, 1);
            for (int j = 0; j < 7; j++)
            {
                this.usbDevice.setDot (0, j, false);
                this.usbDevice.setDot (1, j, false);
            }
        }
        return super.clear ();
    }


    /** {@inheritDoc} */
    @Override
    public Kontrol1Display clearRow (final int row)
    {
        for (int i = 0; i < this.noOfCells; i++)
            this.clearCell (row, i);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Kontrol1Display clearCell (final int row, final int cell)
    {
        this.cells[row * this.noOfCells + cell] = "        ";
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Kontrol1Display setBlock (final int row, final int block, final String value)
    {
        final int cell = 2 * block;
        if (value.length () > 9)
        {
            this.cells[row * this.noOfCells + cell] = value.substring (0, 9);
            this.cells[row * this.noOfCells + cell + 1] = pad (value.substring (9), 8);
        }
        else
        {
            this.cells[row * this.noOfCells + cell] = pad (value, 9);
            this.clearCell (row, cell + 1);
        }
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display setCell (final int row, final int column, final int value, final Format format)
    {
        this.setCell (row, column, Integer.toString (value));
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Kontrol1Display setCell (final int row, final int cell, final String value)
    {
        this.cells[row * this.noOfCells + cell] = pad (value, 8);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        String t = text;
        for (int i = 0; i < t.length (); i++)
        {
            char c = t.charAt (i);
            final boolean isDot = c == '.';

            if (!isDot || i % 8 == 0)
            {
                this.usbDevice.setDot (row, i, isDot);
                this.usbDevice.setCharacter (row, i, isDot ? ' ' : c);
            }
            else
            {
                this.usbDevice.setDot (row, i - 1, true);
                this.usbDevice.setDot (row, i, false);
                final int end = (i / 8 + 1) * 8;
                t = t.substring (0, i) + t.substring (i + 1, end) + " " + t.substring (end);
                c = t.charAt (i);
                this.usbDevice.setCharacter (row, i, c == '.' ? ' ' : c);
            }
        }
    }


    /**
     * Set a value bar.
     *
     * @param column The column (0, ..., 8)
     * @param hasBorder True to draw a border around the value bar
     * @param value The value to set the bar to
     */
    public void setBar (final int column, final boolean hasBorder, final int value)
    {
        if (!this.isNotificationActive)
            this.usbDevice.setBar (column, hasBorder, value, this.maxParameterValue);
    }


    /**
     * Set a value bar drawn as panorama.
     *
     * @param column The column (0, ..., 8)
     * @param hasBorder True to draw a border around the value bar
     * @param value The value to set the bar to
     */
    public void setPanBar (final int column, final boolean hasBorder, final int value)
    {
        if (!this.isNotificationActive)
            this.usbDevice.setPanBar (column, hasBorder, value, this.maxParameterValue);
    }


    /**
     * Pad the given text with the given character until it reaches the given length.
     *
     * @param str The text to pad
     * @param length The maximum length
     * @return The padded text
     */
    public static String pad (final String str, final int length)
    {
        final String text = str == null ? "" : str;
        final int diff = length - text.length ();
        if (diff < 0)
            return text.substring (0, length);
        if (diff > 0)
            return text + Kontrol1Display.SPACES[diff];
        return text;
    }


    /** {@inheritDoc} */
    @Override
    protected void notifyOnDisplay (final String message)
    {
        this.isNotificationActive = true;
        this.clear ();
        final int padLength = (this.noOfCharacters - message.length ()) / 2;
        final String padding = padLength > 0 ? this.emptyLine.substring (0, padLength) : "";
        this.notificationMessage = (padding + message + padding + "  ").substring (0, this.noOfCharacters);
        this.flush ();
        this.host.scheduleTask ( () -> {
            this.isNotificationActive = false;
            this.forceFlush ();
        }, AbstractDisplay.NOTIFICATION_TIME);
    }
}