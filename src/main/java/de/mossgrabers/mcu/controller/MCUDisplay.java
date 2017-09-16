// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.controller;

import de.mossgrabers.framework.controller.display.AbstractDisplay;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.midi.MidiOutput;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * The MCU main display.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUDisplay extends AbstractDisplay
{
    private static final String    SYSEX_DISPLAY_HEADER1 = "F0 00 00 66 14 12 ";
    private static final String    SYSEX_DISPLAY_HEADER2 = "F0 00 00 67 15 13 ";

    private static final String [] SPACES                =
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

    private boolean                isFirst;
    private int                    charactersOfCell;


    /**
     * Constructor. 2 rows (0-1) with 4 blocks (0-3). Each block consists of 18 characters or 2
     * cells (0-8).
     *
     * @param host The host
     * @param output The midi output which addresses the display
     * @param isFirst True if it is the first display, otherwise the second
     */
    public MCUDisplay (final ControllerHost host, final MidiOutput output, final boolean isFirst)
    {
        super (host, output, 2 /* No of rows */, isFirst ? 8 : 9 /* No of cells */, 56);

        this.isFirst = isFirst;
        this.charactersOfCell = this.noOfCharacters / this.noOfCells;
    }


    /** {@inheritDoc} */
    @Override
    public MCUDisplay clearCell (final int row, final int cell)
    {
        this.cells[row * this.noOfCells + cell] = "         ".substring (0, this.charactersOfCell);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display setBlock (final int row, final int block, final String value)
    {
        final int cell = 2 * block;
        if (value.length () >= this.charactersOfCell)
        {
            this.cells[row * this.noOfCells + cell] = pad (value.substring (0, this.charactersOfCell), this.charactersOfCell - 1);
            this.cells[row * this.noOfCells + cell + 1] = pad (value.substring (this.charactersOfCell - 1), this.charactersOfCell);
        }
        else
        {
            this.setCell (row, cell, value);
            this.clearCell (row, cell + 1);
        }
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display setCell (final int row, final int column, final int value, final Format format)
    {
        this.cells[row * this.noOfCells + column] = pad (Integer.toString (value), this.charactersOfCell - 1) + " ";
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display setCell (final int row, final int column, final String value)
    {
        try
        {
            this.cells[row * this.noOfCells + column] = pad (value, this.charactersOfCell - 1) + " ";
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
        final String t = this.isFirst ? text : " " + text + (row == 0 ? "r" : " ");
        final int length = t.length ();
        final int [] array = new int [length];
        for (int i = 0; i < length; i++)
            array[i] = t.charAt (i);
        final StringBuilder code = new StringBuilder ();
        if (this.isFirst)
            code.append (SYSEX_DISPLAY_HEADER1);
        else
            code.append (SYSEX_DISPLAY_HEADER2);
        if (row == 0)
            code.append ("00 ");
        else
            code.append ("38 ");
        this.output.sendSysex (code.append (MidiOutput.toHexStr (array)).append ("F7").toString ());
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.notify ("Please start Bitwig Studio...", true, false);
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
}