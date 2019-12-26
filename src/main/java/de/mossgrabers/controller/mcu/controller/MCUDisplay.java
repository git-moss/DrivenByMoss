// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.controller;

import de.mossgrabers.framework.controller.display.AbstractTextDisplay;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.LatestTaskExecutor;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The MCU main display.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUDisplay extends AbstractTextDisplay
{
    private static final String         SYSEX_DISPLAY_HEADER1 = "F0 00 00 66 14 12 ";
    private static final String         SYSEX_DISPLAY_HEADER2 = "F0 00 00 67 15 13 ";

    private boolean                     isFirst;
    private int                         charactersOfCell;
    private boolean                     hasMaster;

    private final LatestTaskExecutor [] executors             = new LatestTaskExecutor [4];


    /**
     * Constructor. 2 rows (0-1) with 4 blocks (0-3). Each block consists of 18 characters or 2
     * cells (0-8).
     *
     * @param host The host
     * @param output The midi output which addresses the display
     * @param isFirst True if it is the first display, otherwise the second
     * @param hasMaster True if a 9th master cell should be added
     */
    public MCUDisplay (final IHost host, final IMidiOutput output, final boolean isFirst, final boolean hasMaster)
    {
        super (host, output, 2 /* No of rows */, !isFirst && hasMaster ? 9 : 8 /* No of cells */, 56);

        this.isFirst = isFirst;
        this.hasMaster = hasMaster;
        this.charactersOfCell = this.noOfCharacters / this.noOfCells;

        for (int i = 0; i < 4; i++)
            this.executors[i] = new LatestTaskExecutor ();
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay clearRow (final int row)
    {
        for (int i = 0; i < this.noOfCells; i++)
            this.clearCell (row, i);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay clearCell (final int row, final int cell)
    {
        this.cells[row * this.noOfCells + cell] = "         ".substring (0, this.charactersOfCell);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay setBlock (final int row, final int block, final String value)
    {
        final int cell = 2 * block;
        if (value.length () >= this.charactersOfCell)
        {
            this.cells[row * this.noOfCells + cell] = StringUtils.pad (value.substring (0, this.charactersOfCell), this.charactersOfCell);
            this.cells[row * this.noOfCells + cell + 1] = StringUtils.pad (value.substring (this.charactersOfCell), this.charactersOfCell);
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
    public ITextDisplay setCell (final int row, final int column, final int value, final Format format)
    {
        this.cells[row * this.noOfCells + column] = StringUtils.pad (Integer.toString (value), this.charactersOfCell - 1) + " ";
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay setCell (final int row, final int column, final String value)
    {
        try
        {
            this.cells[row * this.noOfCells + column] = StringUtils.pad (value, this.charactersOfCell - 1) + " ";
        }
        catch (final ArrayIndexOutOfBoundsException ex)
        {
            this.host.error ("Display array index out of bounds.", ex);
        }
        return this;
    }


    /** {@inheritDoc} */
    @Override
    protected void updateLine (final int row, final String text)
    {
        String t = text;
        if (!this.isFirst && this.hasMaster)
        {
            if (row == 0)
                t = t.substring (0, t.length () - 1) + 'r';
            t = "  " + t;
        }

        super.updateLine (row, t);
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        final LatestTaskExecutor executor = this.executors[row + (this.isFirst ? 0 : 2)];
        if (executor.isShutdown ())
            return;

        executor.execute ( () -> {
            try
            {
                final int length = text.length ();
                final int [] array = new int [length];
                for (int i = 0; i < length; i++)
                    array[i] = text.charAt (i);
                this.output.sendSysex (new StringBuilder (this.isFirst ? SYSEX_DISPLAY_HEADER1 : SYSEX_DISPLAY_HEADER2).append (row == 0 ? "00 " : "38 ").append (StringUtils.toHexStr (array)).append ("F7").toString ());
            }
            catch (final RuntimeException ex)
            {
                this.host.error ("Could not send line to MCU display.", ex);
            }
        });
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
}