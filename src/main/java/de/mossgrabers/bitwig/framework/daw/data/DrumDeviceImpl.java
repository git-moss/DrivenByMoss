// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IDrumDevice;

import com.bitwig.extension.controller.api.Device;


/**
 * Encapsulates the data of a drum machine device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumDeviceImpl extends SpecificDeviceImpl implements IDrumDevice
{
    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param device The device to encapsulate
     * @param numSends The number of sends
     * @param numParamPages The number of parameter pages
     * @param numParams The number of parameters
     * @param numDevicesInBank The number of devices
     * @param numDeviceLayers The number of layers
     * @param numDrumPadLayers The number of drum pad layers
     */
    public DrumDeviceImpl (final IHost host, final IValueChanger valueChanger, final Device device, final int numSends, final int numParamPages, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        super (host, valueChanger, device, numSends, numParamPages, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
    }
}
