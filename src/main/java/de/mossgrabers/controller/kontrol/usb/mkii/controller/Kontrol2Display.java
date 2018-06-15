// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.controller;

import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2Configuration;
import de.mossgrabers.framework.controller.display.AbstractDisplay;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IHost;


/**
 * The display of Kontrol 1.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol2Display extends AbstractDisplay
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

    private Kontrol2UsbDevice      usbDevice;


    /**
     * Constructor. 2 rows (0-1) with 9 blocks (0-8). Each block consists of 8 characters.
     *
     * @param host The host
     * @param configuration The configuration
     * @param usbDevice The USB device
     */
    public Kontrol2Display (final IHost host, final Kontrol2Configuration configuration, final Kontrol2UsbDevice usbDevice)
    {
        super (host, null, 2 /* No of rows */, 9 /* No of cells */, 72 /* No of characters */);
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
    public Kontrol2Display clearRow (final int row)
    {
        for (int i = 0; i < this.noOfCells; i++)
            this.clearCell (row, i);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Kontrol2Display clearCell (final int row, final int cell)
    {
        this.cells[row * this.noOfCells + cell] = "        ";
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Kontrol2Display setBlock (final int row, final int block, final String value)
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
    public Kontrol2Display setCell (final int row, final int cell, final String value)
    {
        this.cells[row * this.noOfCells + cell] = pad (value, 8);
        return this;
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
            return text + Kontrol2Display.SPACES[diff];
        return text;
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        // Not a line based display
    }
}