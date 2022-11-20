// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.display;

import de.mossgrabers.framework.controller.hardware.IHwTextDisplay;
import de.mossgrabers.framework.daw.IHost;


/**
 * Only supports notification messages via a notification dialog.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DummyDisplay implements ITextDisplay
{
    private final IHost host;
    private String      lastMessage;


    /**
     * Constructor.
     *
     * @param host The host
     */
    public DummyDisplay (final IHost host)
    {
        this.host = host;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay clearCell (final int row, final int column)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay setCell (final int row, final int column, final int value, final Format format)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay setCell (final int row, final int column, final String value)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay setBlock (final int row, final int block, final String value)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay setRow (final int row, final String str)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay clear ()
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay clearRow (final int row)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay clearBlock (final int row, final int block)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay clearColumn (final int column)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay done (final int row)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay allDone ()
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void notify (final String message)
    {
        if (message != null && !message.equals (this.lastMessage))
            this.host.showNotification (message);

        this.lastMessage = message;
    }


    /** {@inheritDoc} */
    @Override
    public void cancelNotification ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean isNotificationActive ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void forceFlush ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getNoOfLines ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void setHardwareDisplay (final IHwTextDisplay display)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public IHwTextDisplay getHardwareDisplay ()
    {
        return null;
    }
}
