// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.display;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * Abstract implementation of a display.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractDisplay implements Display
{
    /** Time to keep a notification displayed in ms. */
    public static final int NOTIFICATION_TIME = 1000;

    protected IHost         host;
    protected IMidiOutput   output;

    protected int           noOfLines;
    protected int           noOfCells;
    protected int           noOfCharacters;

    protected final String  emptyLine;
    protected String        notificationMessage;
    protected boolean       isNotificationActive;

    protected String []     currentMessage;
    protected String []     message;
    protected String []     cells;


    /**
     * Constructor.
     *
     * @param host The host
     * @param output The midi output which addresses the display
     * @param noOfLines The number of rows that the display supports
     * @param noOfCells The number of cells that the display supports
     * @param noOfCharacters The number of characters of 1 row that the display supports
     */
    public AbstractDisplay (final IHost host, final IMidiOutput output, final int noOfLines, final int noOfCells, final int noOfCharacters)
    {
        this.host = host;
        this.output = output;

        this.noOfLines = noOfLines;
        this.noOfCells = noOfCells;
        this.noOfCharacters = noOfCharacters;

        final StringBuilder sb = new StringBuilder (this.noOfCharacters);
        for (int i = 0; i < this.noOfCharacters; i++)
            sb.append (' ');
        this.emptyLine = sb.toString ();
        this.notificationMessage = this.emptyLine;
        this.isNotificationActive = false;

        this.currentMessage = new String [this.noOfLines];

        this.message = new String [this.noOfLines];
        this.cells = new String [this.noOfLines * this.noOfCells];
    }


    /** {@inheritDoc} */
    @Override
    public AbstractDisplay setRow (final int row, final String str)
    {
        this.message[row] = str;
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public AbstractDisplay clear ()
    {
        for (int i = 0; i < this.noOfLines; i++)
            this.clearRow (i);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public AbstractDisplay clearRow (final int row)
    {
        for (int i = 0; i < 4; i++)
            this.clearBlock (row, i);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public AbstractDisplay clearBlock (final int row, final int block)
    {
        final int cell = 2 * block;
        this.clearCell (row, cell);
        this.clearCell (row, cell + 1);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public AbstractDisplay clearColumn (final int column)
    {
        for (int i = 0; i < this.noOfLines; i++)
            this.clearCell (i, column);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public AbstractDisplay done (final int row)
    {
        final int index = row * this.noOfCells;
        this.message[row] = "";
        for (int i = 0; i < this.noOfCells; i++)
            this.message[row] += this.cells[index + i];
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public AbstractDisplay allDone ()
    {
        for (int row = 0; row < this.noOfLines; row++)
            this.done (row);
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
        this.isNotificationActive = true;
        this.flush ();
        this.host.scheduleTask ( () -> {
            this.isNotificationActive = false;
            this.forceFlush ();
        }, AbstractDisplay.NOTIFICATION_TIME);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        if (this.isNotificationActive)
        {
            this.writeLine (0, this.notificationMessage);
            for (int row = 1; row < this.noOfLines; row++)
                this.writeLine (row, this.emptyLine);
            return;
        }

        for (int row = 0; row < this.noOfLines; row++)
        {
            // Has anything changed?
            if (this.currentMessage[row] != null && this.currentMessage[row].equals (this.message[row]))
                continue;
            this.currentMessage[row] = this.message[row];
            if (this.currentMessage[row] != null)
                this.writeLine (row, this.currentMessage[row]);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void forceFlush ()
    {
        for (int row = 0; row < this.noOfLines; row++)
            this.currentMessage[row] = "";
    }
}