// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.controller;

import de.mossgrabers.framework.controller.display.AbstractDisplay;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.midi.MidiOutput;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * The SLs display.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLDisplay extends AbstractDisplay
{
    /** The right arrow. */
    public static final String     RIGHT_ARROW = ">";

    private static final String [] SPACES      =
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
        "         "
    };


    /**
     * Constructor. 2 rows (0-1) with 4 blocks (0-3). Each block consists of 18 characters or 2
     * cells (0-8).
     *
     * @param host The host
     * @param output The midi output which addresses the display
     */
    public SLDisplay (final ControllerHost host, final MidiOutput output)
    {
        super (host, output, 4 /* No of rows */, 8 /* No of cells */, 8);
    }


    /** {@inheritDoc} */
    @Override
    public SLDisplay clearCell (final int row, final int cell)
    {
        this.cells[row * this.noOfCells + cell] = "         ";
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display setBlock (final int row, final int block, final String value)
    {
        final int cell = 2 * block;
        if (value.length () > 9)
        {
            this.cells[row * 8 + cell] = value.substring (0, 9);
            this.cells[row * 8 + cell + 1] = pad (value.substring (9), 8) + " ";
        }
        else
        {
            this.cells[row * 8 + cell] = pad (value, 9);
            this.clearCell (row, cell + 1);
        }
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display setCell (final int row, final int column, final int value, final Format format)
    {
        this.cells[row * this.noOfCells + column] = pad (Integer.toString (value), 8) + " ";
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display setCell (final int row, final int column, final String value)
    {
        try
        {
            this.cells[row * this.noOfCells + column] = pad (value, 8) + " ";
        }
        catch (final ArrayIndexOutOfBoundsException ex)
        {
            ex.printStackTrace ();
        }
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        final int length = text.length ();
        final int [] array = new int [length];
        for (int i = 0; i < length; i++)
            array[i] = text.charAt (i);
        this.output.sendSysex (SLControlSurface.SYSEX_HEADER + "02 01 00 " + uint7ToHex (row + 1) + "04 " + MidiOutput.toHexStr (array) + "00 F7");
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.notify ("Please start Bitwig to play...");
    }


    private static String pad (final String str, final int length)
    {
        final String text = str == null ? "" : str;
        final int diff = length - text.length ();
        if (diff < 0)
            return text.substring (0, length);
        if (diff > 0)
            return text + SPACES[diff];
        return text;
    }


    private static String uint7ToHex (final int x)
    {
        final int upper = x >> 4 & 0x7;
        final int lower = x & 0xF;
        return Integer.toString (upper, 16) + Integer.toString (lower, 16) + " ";
    }
}