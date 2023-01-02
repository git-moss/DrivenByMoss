// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.bitwig.framework.daw.data.bank.DeviceBankImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;

import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.PinnableCursorDevice;


/**
 * Proxy to the Bitwig Cursor device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorDeviceImpl extends SpecificDeviceImpl implements ICursorDevice
{
    private static final int           NUM_DEVICES_LARGE_BANK = 100;

    private final PinnableCursorDevice cursorDevice;
    private final IDeviceBank          deviceBank;
    private final DeviceBank           largeDeviceBank;


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param cursorDevice The cursor device
     * @param numSends The number of sends
     * @param numParamPages The number of parameter pages
     * @param numParams The number of parameters
     * @param numDevicesInBank The number of devices
     * @param numDeviceLayers The number of layers
     * @param numDrumPadLayers The number of drum pad layers
     */
    public CursorDeviceImpl (final IHost host, final IValueChanger valueChanger, final PinnableCursorDevice cursorDevice, final int numSends, final int numParamPages, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        super (host, valueChanger, cursorDevice, numSends, numParamPages, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);

        this.cursorDevice = cursorDevice;
        this.largeDeviceBank = cursorDevice.channel ().createDeviceBank (NUM_DEVICES_LARGE_BANK);

        final int checkedNumDevices = numDevicesInBank >= 0 ? numDevicesInBank : 8;

        this.cursorDevice.hasPrevious ().markInterested ();
        this.cursorDevice.hasNext ().markInterested ();
        this.cursorDevice.isPinned ().markInterested ();

        // Monitor the sibling devices of the cursor device
        final DeviceBank siblings = checkedNumDevices > 0 ? this.cursorDevice.createSiblingsDeviceBank (checkedNumDevices) : null;
        this.deviceBank = new DeviceBankImpl (host, valueChanger, this, siblings, checkedNumDevices);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        Util.setIsSubscribed (this.cursorDevice.hasPrevious (), enable);
        Util.setIsSubscribed (this.cursorDevice.hasNext (), enable);
        Util.setIsSubscribed (this.cursorDevice.isPinned (), enable);

        this.deviceBank.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.getPosition () % this.deviceBank.getPageSize ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPinned ()
    {
        return this.cursorDevice.isPinned ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectPrevious ()
    {
        return this.cursorDevice.hasPrevious ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectNext ()
    {
        return this.cursorDevice.hasNext ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPrevious ()
    {
        final boolean moveBank = this.getIndex () == 0;
        this.cursorDevice.selectPrevious ();
        if (moveBank)
            this.deviceBank.selectPreviousPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNext ()
    {
        final boolean moveBank = this.getIndex () == this.getDeviceBank ().getPageSize () - 1;
        this.cursorDevice.selectNext ();
        if (moveBank)
            this.deviceBank.selectNextPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void togglePinned ()
    {
        this.cursorDevice.isPinned ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setPinned (final boolean isPinned)
    {
        this.cursorDevice.isPinned ().set (isPinned);
    }


    /** {@inheritDoc} */
    @Override
    public IDeviceBank getDeviceBank ()
    {
        return this.deviceBank;
    }


    /** {@inheritDoc} */
    @Override
    public void selectParent ()
    {
        this.cursorDevice.selectParent ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectChannel ()
    {
        this.cursorDevice.channel ().selectInEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public void swapWithPrevious ()
    {
        final int position = this.getPosition ();
        if (position == 0)
            return;
        final Device prevDevice = this.largeDeviceBank.getItemAt (position - 1);
        this.device.afterDeviceInsertionPoint ().moveDevices (prevDevice);
        prevDevice.selectInEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public void swapWithNext ()
    {
        final int position = this.getPosition ();
        if (position >= NUM_DEVICES_LARGE_BANK - 1)
            return;
        final Device nextDevice = this.largeDeviceBank.getItemAt (position + 1);
        this.device.beforeDeviceInsertionPoint ().moveDevices (nextDevice);
        nextDevice.selectInEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSlotChains ()
    {
        return this.cursorDevice.slotNames ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectSlotChain (final String slotChainName)
    {
        this.cursorDevice.selectFirstInSlot (slotChainName);
    }


    /**
     * Get the Bitwig cursor device.
     *
     * @return The cursor device
     */
    public PinnableCursorDevice getCursorDevice ()
    {
        return this.cursorDevice;
    }
}