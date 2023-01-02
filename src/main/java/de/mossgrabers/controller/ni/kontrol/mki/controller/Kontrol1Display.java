// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mki.controller;

import de.mossgrabers.controller.ni.kontrol.mki.Kontrol1Configuration;
import de.mossgrabers.framework.controller.display.AbstractTextDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The display of Kontrol 1.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1Display extends AbstractTextDisplay
{
    private final int               maxParameterValue;
    private final Kontrol1UsbDevice usbDevice;


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
    public ITextDisplay clear ()
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
    public ITextDisplay setBlock (final int row, final int block, final String value)
    {
        final int cell = 2 * block;
        if (value.length () > 9)
        {
            this.cells[row * this.noOfCells + cell] = value.substring (0, 9);
            this.cells[row * this.noOfCells + cell + 1] = StringUtils.pad (value.substring (9), 8);
        }
        else
        {
            this.cells[row * this.noOfCells + cell] = StringUtils.pad (value, 9);
            this.clearCell (row, cell + 1);
        }
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
        synchronized (this.notificationLock)
        {
            if (this.notificationTimeout <= 0)
                this.usbDevice.setBar (column, hasBorder, value, this.maxParameterValue);
        }
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
        synchronized (this.notificationLock)
        {
            if (this.notificationTimeout <= 0)
                this.usbDevice.setPanBar (column, hasBorder, value, this.maxParameterValue);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void notifyOnDisplay (final String message)
    {
        final int padLength = (this.noOfCharacters - message.length ()) / 2;
        final String padding = padLength > 0 ? this.emptyLine.substring (0, padLength) : "";
        this.notificationMessage = (padding + message + padding + "  ").substring (0, this.noOfCharacters);

        synchronized (this.notificationLock)
        {
            final boolean isRunning = this.notificationTimeout > 0;
            this.notificationTimeout = AbstractTextDisplay.NOTIFICATION_TIME;
            this.clear ();
            this.flush ();

            if (!isRunning)
                this.host.scheduleTask (this::watch, 100);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected String convertCharacterset (final String text)
    {
        final StringBuilder sb = new StringBuilder (text.length ());
        int beginIndex = 0;
        int endIndex = 8;
        for (int i = 0; i < 9; i++)
        {
            sb.append (text.substring (beginIndex, endIndex));
            beginIndex += 8;
            endIndex += 8;
            if (i != 8)
                sb.append (' ');
        }
        return sb.toString ();
    }
}