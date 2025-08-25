// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bitwig.extension.controller.api.CursorDeviceLayer;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceBank;

import de.mossgrabers.bitwig.framework.daw.data.bank.DeviceBankImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ICursorLayer;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;


/**
 * Implementation which monitors the selected layer of a device which supports layers.
 *
 * @author Jürgen Moßgraber
 */
public class CursorLayerImpl implements ICursorLayer
{
    private final List<SpecificDeviceImpl> devices = new ArrayList<> ();
    private final IDeviceBank              deviceBank;
    private final ILayerBank               layerBank;
    private final CursorDeviceLayer        cursorDeviceLayer;
    private final ICursorDevice            cursorDevice;


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param layerBank The layer bank which is monitored by this cursor layer
     * @param cursorDeviceLayer The cursor device layer
     * @param cursorDevice The cursor device
     * @param numParamPages The number of parameter pages
     * @param numParams The number of parameters
     * @param numDevicesInBank The number of devices
     */
    public CursorLayerImpl (final IHost host, final IValueChanger valueChanger, final ILayerBank layerBank, final CursorDeviceLayer cursorDeviceLayer, final ICursorDevice cursorDevice, final int numParamPages, final int numParams, final int numDevicesInBank)
    {
        this.layerBank = layerBank;
        this.cursorDeviceLayer = cursorDeviceLayer;
        this.cursorDevice = cursorDevice;

        final DeviceBank deviceBank = cursorDeviceLayer.createDeviceBank (numDevicesInBank);
        for (int i = 0; i < numDevicesInBank; i++)
        {
            final Device device = deviceBank.getItemAt (i);
            this.devices.add (new SpecificDeviceImpl (host, valueChanger, device, 0, numParamPages, numParams, numDevicesInBank, 0, 0, 0));
        }

        this.deviceBank = new DeviceBankImpl (host, valueChanger, null, deviceBank, numDevicesInBank);
    }


    /** {@inheritDoc} */
    @Override
    public ILayerBank getLayerBank ()
    {
        return this.layerBank;
    }


    /** {@inheritDoc} */
    @Override
    public IDeviceBank getDeviceBank ()
    {
        return this.deviceBank;
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ISpecificDevice> getSelectedDevice ()
    {
        // Make sure we are on the layer level
        if (this.cursorDevice.isNested ())
        {
            final int index = this.cursorDevice.getIndex ();
            if (index >= 0 && index < this.devices.size ())
                return Optional.of (this.devices.get (index));
        }

        return Optional.empty ();
    }


    /**
     * Get the Bitwig cursor device layer.
     *
     * @return The cursor device layer
     */
    public CursorDeviceLayer getCursorDeviceLayer ()
    {
        return this.cursorDeviceLayer;
    }
}
