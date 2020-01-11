// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.display;

import de.mossgrabers.framework.controller.hardware.IHwTextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Abstract implementation of a display.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTextDisplay implements ITextDisplay
{
    /** Time to keep a notification displayed in ms. */
    public static final int  NOTIFICATION_TIME    = 1000;

    protected IHost          host;
    protected IMidiOutput    output;

    protected int            noOfLines;
    protected int            noOfCells;
    protected int            noOfCharacters;
    protected int            charactersOfCell;

    protected final String   emptyLine;
    protected String         notificationMessage;
    protected int            isNotificationActive = 0;
    protected final Object   notificationLock     = new Object ();

    private final String     emptyCell;
    protected String []      currentMessage;
    protected String []      message;
    protected String []      cells;

    protected IHwTextDisplay hwDisplay;


    /**
     * Constructor.
     *
     * @param host The host
     * @param output The midi output which addresses the display
     * @param noOfLines The number of rows that the display supports
     * @param noOfCells The number of cells that the display supports
     * @param noOfCharacters The number of characters of 1 row that the display supports
     */
    public AbstractTextDisplay (final IHost host, final IMidiOutput output, final int noOfLines, final int noOfCells, final int noOfCharacters)
    {
        this.host = host;
        this.output = output;

        this.noOfLines = noOfLines;
        this.noOfCells = noOfCells;
        this.noOfCharacters = noOfCharacters;
        this.charactersOfCell = this.noOfCharacters / this.noOfCells;

        this.emptyCell = "                                                                     ".substring (0, this.charactersOfCell);

        final StringBuilder sb = new StringBuilder (this.noOfCharacters);
        for (int i = 0; i < this.noOfCharacters; i++)
            sb.append (' ');
        this.emptyLine = sb.toString ();
        this.notificationMessage = this.emptyLine;

        this.currentMessage = new String [this.noOfLines];

        this.message = new String [this.noOfLines];
        this.cells = new String [this.noOfLines * this.noOfCells];
    }


    /** {@inheritDoc} */
    @Override
    public int getNoOfLines ()
    {
        return this.noOfLines;
    }


    /** {@inheritDoc} */
    @Override
    public void setHardwareDisplay (final IHwTextDisplay display)
    {
        this.hwDisplay = display;
    }


    /** {@inheritDoc} */
    @Override
    public IHwTextDisplay getHardwareDisplay ()
    {
        return this.hwDisplay;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay setRow (final int row, final String str)
    {
        this.message[row] = str;
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay clear ()
    {
        for (int i = 0; i < this.noOfLines; i++)
            this.clearRow (i);
        return this;
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
    public ITextDisplay clearBlock (final int row, final int block)
    {
        final int cell = 2 * block;
        this.clearCell (row, cell);
        this.clearCell (row, cell + 1);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay clearColumn (final int column)
    {
        for (int i = 0; i < this.noOfLines; i++)
            this.clearCell (i, column);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay done (final int row)
    {
        final int index = row * this.noOfCells;
        this.message[row] = "";
        for (int i = 0; i < this.noOfCells; i++)
            this.message[row] += this.cells[index + i];
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay allDone ()
    {
        for (int row = 0; row < this.noOfLines; row++)
            this.done (row);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay clearCell (final int row, final int column)
    {
        this.cells[row * this.noOfCells + column] = this.emptyCell;
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay setCell (final int row, final int column, final int value, final Format format)
    {
        this.setCell (row, column, Integer.toString (value));
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay setCell (final int row, final int column, final String value)
    {
        try
        {
            this.cells[row * this.noOfCells + column] = StringUtils.pad (value, this.charactersOfCell);
        }
        catch (final ArrayIndexOutOfBoundsException ex)
        {
            this.host.error ("Display array index out of bounds.", ex);
        }
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
    public void notify (final String message)
    {
        if (message == null)
            return;
        this.host.showNotification (message);
        this.notifyOnDisplay (message);
    }


    protected void notifyOnDisplay (final String message)
    {
        final int padLength = (this.noOfCharacters - message.length ()) / 2 + 1;
        final String padding = padLength > 0 ? this.emptyLine.substring (0, padLength) : "";
        this.notificationMessage = (padding + message + padding).substring (0, this.noOfCharacters);

        synchronized (this.notificationLock)
        {
            final boolean isRunning = this.isNotificationActive > 0;
            this.isNotificationActive = AbstractTextDisplay.NOTIFICATION_TIME;
            this.flush ();
            if (!isRunning)
                this.host.scheduleTask (this::watch, 100);
        }
    }


    protected void watch ()
    {
        synchronized (this.notificationLock)
        {
            this.isNotificationActive -= 100;

            if (this.isNotificationActive <= 0)
                this.forceFlush ();
            else
                this.host.scheduleTask (this::watch, 100);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        synchronized (this.notificationLock)
        {
            if (this.isNotificationActive > 0)
            {
                this.updateLine (0, this.notificationMessage);
                for (int row = 1; row < this.noOfLines; row++)
                    this.updateLine (row, this.emptyLine);
                return;
            }
        }

        for (int row = 0; row < this.noOfLines; row++)
        {
            // Has anything changed?
            if (this.currentMessage[row] != null && this.currentMessage[row].equals (this.message[row]))
                continue;
            this.currentMessage[row] = this.message[row];
            if (this.currentMessage[row] != null)
                this.updateLine (row, this.currentMessage[row]);
        }
    }


    /**
     * Update the line on the hardware and simulation display.
     *
     * @param row The text row
     * @param text The text
     */
    protected void updateLine (final int row, final String text)
    {
        this.hwDisplay.setLine (row, this.convertCharacterset (text));
        this.writeLine (row, text);
    }


    /**
     * Overwrite if the device display uses a non-standard characterset.
     *
     * @param text The text
     * @return The text adapted to the simulator GUI character set
     */
    protected String convertCharacterset (final String text)
    {
        return text;
    }


    /** {@inheritDoc} */
    @Override
    public void forceFlush ()
    {
        for (int row = 0; row < this.noOfLines; row++)
            this.currentMessage[row] = "";
    }
}