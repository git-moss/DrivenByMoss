// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.controller;

import de.mossgrabers.framework.controller.display.AbstractTextDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.hardware.IHwTextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The SLs display. The MkII has only 1 display but is addressed the same as the 2 displays on the
 * MkI. The content is displayed in the display 1 depending if a mode button is active on the left
 * or the right.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLDisplay extends AbstractTextDisplay
{
    /** The right arrow. */
    public static final String   RIGHT_ARROW = ">";

    private static final int []  ROW_MAP     =
    {
        0,
        2,
        1,
        3
    };

    private final IHwTextDisplay hwTextDisplay1;
    private final IHwTextDisplay hwTextDisplay2;


    /**
     * Constructor. 4 rows (0-1) with 4 blocks (0-3). Each block consists of 18 characters or 2
     * cells (0-8).
     *
     * @param host The host
     * @param output The MIDI output which addresses the display
     * @param hwTextDisplay2
     * @param hwTextDisplay1
     */
    public SLDisplay (final IHost host, final IMidiOutput output, final IHwTextDisplay hwTextDisplay1, final IHwTextDisplay hwTextDisplay2)
    {
        super (host, output, 4 /* No of rows */, 8 /* No of cells */, 72);

        this.hwTextDisplay1 = hwTextDisplay1;
        this.hwTextDisplay2 = hwTextDisplay2;
    }


    /** {@inheritDoc} */
    @Override
    protected void updateLine (final int row, final String text)
    {
        if (row == 0)
            this.hwTextDisplay1.setLine (0, this.convertCharacterset (text));
        else if (row == 1)
            this.hwTextDisplay1.setLine (1, this.convertCharacterset (text));
        else if (row == 2)
            this.hwTextDisplay2.setLine (0, this.convertCharacterset (text));
        else if (row == 3)
            this.hwTextDisplay2.setLine (1, this.convertCharacterset (text));

        this.writeLine (row, text);
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay clearCell (final int row, final int column)
    {
        this.cells[row * this.noOfCells + column] = "         ";
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay setBlock (final int row, final int block, final String value)
    {
        final int cell = 2 * block;
        if (value.length () > 9)
        {
            this.cells[row * 8 + cell] = value.substring (0, 9);
            this.cells[row * 8 + cell + 1] = StringUtils.pad (value.substring (9), 8) + " ";
        }
        else
        {
            this.cells[row * 8 + cell] = StringUtils.pad (value, 9);
            this.clearCell (row, cell + 1);
        }
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay setCell (final int row, final int column, final String value)
    {
        try
        {
            this.cells[row * this.noOfCells + column] = StringUtils.pad (value, 8) + " ";
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
        final int length = text.length ();
        final int [] array = new int [length];
        for (int i = 0; i < length; i++)
            array[i] = text.charAt (i);
        this.output.sendSysex (SLControlSurface.SYSEX_HEADER + "02 01 00 " + uint7ToHex (ROW_MAP[row] + 1) + "04 " + StringUtils.toHexStr (array) + "00 F7");
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.notify ("Please start " + this.host.getName () + " to play...");
    }


    private static String uint7ToHex (final int x)
    {
        final int upper = x >> 4 & 0x7;
        final int lower = x & 0xF;
        return Integer.toString (upper, 16) + Integer.toString (lower, 16) + " ";
    }


    /**
     * Get the 1st hardware display.
     *
     * @return The display
     */
    public IHwTextDisplay getHwTextDisplay1 ()
    {
        return this.hwTextDisplay1;
    }


    /**
     * Get the 2nd hardware display.
     *
     * @return The display
     */
    public IHwTextDisplay getHwTextDisplay2 ()
    {
        return this.hwTextDisplay2;
    }
}