// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.data.DeviceImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;

import com.bitwig.extension.controller.api.DeviceBank;

import java.util.Optional;


/**
 * Encapsulates the data of a device bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceBankImpl extends AbstractItemBankImpl<DeviceBank, IDevice> implements IDeviceBank
{
    private final ICursorDevice cursorDevice;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param cursorDevice The cursor device
     * @param deviceBank The device bank
     * @param numDevices The number of devices in the page of the bank
     */
    public DeviceBankImpl (final IHost host, final IValueChanger valueChanger, final ICursorDevice cursorDevice, final DeviceBank deviceBank, final int numDevices)
    {
        super (host, valueChanger, deviceBank, numDevices);

        this.cursorDevice = cursorDevice;

        if (this.bank.isEmpty ())
            return;

        final DeviceBank db = this.bank.get ();
        for (int i = 0; i < this.getPageSize (); i++)
            this.items.add (new DeviceImpl (db.getItemAt (i), i));
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.cursorDevice.selectNext ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.cursorDevice.selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public Optional<IDevice> getSelectedItem ()
    {
        return this.cursorDevice.doesExist () ? Optional.of (this.cursorDevice) : Optional.empty ();
    }
}