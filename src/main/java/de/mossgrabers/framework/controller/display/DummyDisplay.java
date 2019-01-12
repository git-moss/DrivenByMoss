// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.display;

import de.mossgrabers.framework.daw.IHost;


/**
 * Only supports notification messages via a notification dialog.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DummyDisplay implements Display
{
    private IHost host;


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
    public Display clearCell (final int row, final int column)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display setCell (final int row, final int column, final int value, final Format format)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display setCell (final int row, final int column, final String value)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display setBlock (final int row, final int block, final String value)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display setRow (final int row, final String str)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display clear ()
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display clearRow (final int row)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display clearBlock (final int row, final int block)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display clearColumn (final int column)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display done (final int row)
    {
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Display allDone ()
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
        if (message != null)
            this.host.showNotification (message);
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
}
