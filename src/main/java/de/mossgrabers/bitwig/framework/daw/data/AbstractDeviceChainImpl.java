// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.daw.data.AbstractItemImpl;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.DeviceChain;


/**
 * The data of a device chain.
 *
 * @param <T> The exact type
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractDeviceChainImpl<T extends DeviceChain> extends AbstractItemImpl
{
    protected T deviceChain;


    /**
     * Constructor.
     *
     * @param index The index of the device chain
     * @param deviceChain The Bitwig device chain
     */
    protected AbstractDeviceChainImpl (final int index, final T deviceChain)
    {
        super (index);

        this.deviceChain = deviceChain;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.deviceChain.name ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return this.deviceChain.name ().getLimited (limit);
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        this.deviceChain.name ().addValueObserver (observer::update);
    }


    /** {@inheritDoc} */
    @Override
    public void setName (final String name)
    {
        this.deviceChain.name ().set (name);
    }


    /**
     * Get the device chain.
     *
     * @return The device chain
     */
    public T getDeviceChain ()
    {
        return this.deviceChain;
    }
}
