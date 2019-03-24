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
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The HUI main display. Note that the original HUI display uses a modified ASCII set (e.g. it
 * supports umlauts) but since emulations do not support it this implementation sticks to basic
 * ASCII.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HUIDisplay extends AbstractDisplay
{
    private static final String      SYSEX_DISPLAY_HEADER = "F0 00 00 66 05 00 10 ";

    private static final String []   SPACES               =
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

    private int                      charactersOfCell;

    private final LatestTaskExecutor executor             = new LatestTaskExecutor ();


    /**
     * Constructor. 1 row (0) with 9 blocks (0-8). Each block consists of 4 characters or 1 cell
     * (0-8).
     *
     * @param host The host
     * @param output The midi output which addresses the display
     */
    public HUIDisplay (final IHost host, final IMidiOutput output)
    {
        super (host, output, 1 /* No of rows */, 9 /* No of cells */, 36);

        this.charactersOfCell = this.noOfCharacters / this.noOfCells;
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
    public HUIDisplay clearCell (final int row, final int cell)
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
        if (this.executor.isShutdown ())
            return;
        this.executor.execute ( () -> {
            try
            {
                this.sendDisplayLine (text);
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
     * @param text The text to send
     */
    private void sendDisplayLine (final String text)
    {
        final String t = text;

        final int [] array = new int [5];
        for (int cell = 0; cell < this.noOfCells; cell++)
        {
            array[0] = cell;
            for (int i = 0; i < 4; i++)
                array[1 + i] = t.charAt (cell * 4 + i);
            this.output.sendSysex (new StringBuilder (SYSEX_DISPLAY_HEADER).append (StringUtils.toHexStr (array)).append ("F7").toString ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.notify ("Please start " + this.host.getName () + "...");

        // Prevent further sends
        this.executor.shutdown ();
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