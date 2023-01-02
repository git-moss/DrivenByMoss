// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.daw.data.AbstractItemImpl;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.Device;


/**
 * Encapsulates the data of a device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceImpl extends AbstractItemImpl implements IDevice
{
    protected final Device device;


    /**
     * Constructor.
     *
     * @param device The device to encapsulate
     * @param index The index of the device
     */
    public DeviceImpl (final Device device, final int index)
    {
        super (index);

        this.device = device;

        this.device.exists ().markInterested ();
        this.device.isEnabled ().markInterested ();
        this.device.position ().markInterested ();
        this.device.name ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        Util.setIsSubscribed (this.device.exists (), enable);
        Util.setIsSubscribed (this.device.isEnabled (), enable);
        Util.setIsSubscribed (this.device.position (), enable);
        Util.setIsSubscribed (this.device.name (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.device.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return this.device.position ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.device.name ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return this.device.name ().getLimited (limit);
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        this.device.name ().addValueObserver (observer::update);
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.device.selectInEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEnabled ()
    {
        return this.device.isEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleEnabledState ()
    {
        this.device.isEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        this.device.deleteObject ();
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.device.afterDeviceInsertionPoint ().copyDevices (this.device);
    }


    /**
     * Get the Bitwig device.
     *
     * @return The Bitwig device
     */
    public Device getDevice ()
    {
        return this.device;
    }
}
