// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.hui.controller;

import de.mossgrabers.framework.controller.display.AbstractDisplay;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.LatestTaskExecutor;


/**
 * The HUI main display. Sadly, I do not own a device to test this.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HUIMainDisplay extends AbstractDisplay
{
    private static final byte []        SYSEX_DISPLAY_HEADER = new byte []
    {
        (byte) 0xF0,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x66,
        (byte) 0x05,
        (byte) 0x00,
        (byte) 0x12
    };

    private static final String []      SPACES               =
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

    private int                         charactersOfCell;

    private final LatestTaskExecutor [] executors            = new LatestTaskExecutor [2];


    /**
     * Constructor. 2 rows (0-1) with 4 blocks (0-3). Each block consists of 2 cells (0-7).
     *
     * @param host The host
     * @param output The midi output which addresses the display
     */
    public HUIMainDisplay (final IHost host, final IMidiOutput output)
    {
        super (host, output, 2 /* No of rows */, 8 /* No of cells */, 40);

        this.charactersOfCell = this.noOfCharacters / this.noOfCells;
        for (int i = 0; i < this.executors.length; i++)
            this.executors[i] = new LatestTaskExecutor ();
    }


    /** {@inheritDoc} */
    @Override
    public AbstractDisplay clearRow (final int row)
    {
        for (int i = 0; i < this.noOfCells; i++)
            this.clearCell (row, i);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public HUIMainDisplay clearCell (final int row, final int cell)
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
            this.cells[row * this.noOfCells + cell] = pad (value.substring (0, this.charactersOfCell), this.charactersOfCell);
            this.cells[row * this.noOfCells + cell + 1] = pad (value.substring (this.charactersOfCell), this.charactersOfCell);
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
        this.cells[row * this.noOfCells + column] = pad (Integer.toString (value), this.charactersOfCell);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display setCell (final int row, final int column, final String value)
    {
        try
        {
            this.cells[row * this.noOfCells + column] = pad (value, this.charactersOfCell);
        }
        catch (final ArrayIndexOutOfBoundsException ex)
        {
            this.host.error ("Display array index out of bounds.", ex);
        }
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        if (this.executors[row].isShutdown ())
            return;
        this.executors[row].execute ( () -> {
            try
            {
                this.sendDisplayLine (row, text);
            }
            catch (final RuntimeException ex)
            {
                this.host.error ("Could not send line to HUI display.", ex);
            }
        });
    }


    /**
     * Send a line to the display
     *
     * @param row The row
     * @param text The text to send
     */
    private void sendDisplayLine (final int row, final String text)
    {
        final byte [] line = new byte [52];
        System.arraycopy (SYSEX_DISPLAY_HEADER, 0, line, 0, SYSEX_DISPLAY_HEADER.length);
        line[line.length - 1] = (byte) 0xF7;

        final int pos = SYSEX_DISPLAY_HEADER.length;
        final int offset = row * 4;

        for (int i = 0; i < 4; i++)
        {
            final int start = 11 * i;
            final int textStart = 10 * i;
            final int index = pos + start;
            line[index] = (byte) (offset + i);
            for (int j = 0; j < 10; j++)
                line[index + j + 1] = (byte) text.charAt (textStart + j);
        }

        this.output.sendSysex (line);
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.notify ("Please start " + this.host.getName () + "...");

        // Prevent further sends
        for (int i = 0; i < 4; i++)
            this.executors[i].shutdown ();
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