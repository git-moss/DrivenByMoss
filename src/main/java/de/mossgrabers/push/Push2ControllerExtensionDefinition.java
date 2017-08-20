// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.UsbEndpointInfo;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Push 2 controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Push2ControllerExtensionDefinition extends PushControllerExtensionDefinition
{
    private static final UUID  EXTENSION_ID     = UUID.fromString ("15176AA0-C476-11E6-9598-0800200C9A66");

    /** Push 2 USB Vendor ID. */
    private static final short VENDOR_ID        = 0x2982;
    /** Push 2 USB Product ID. */
    private static final short PRODUCT_ID       = 0x1967;
    /** Push 2 USB Interface for the display. */
    private static final byte  INTERFACE_NUMBER = 0;
    /** Push 2 USB display endpoint. */
    private static final byte  ENDPOINT_ADDRESS = (byte) 0x01;


    /** {@inheritDoc} */
    @Override
    public String getHardwareModel ()
    {
        return "Push 2";
    }


    /** {@inheritDoc} */
    @Override
    public UUID getId ()
    {
        return EXTENSION_ID;
    }


    /** {@inheritDoc} */
    @Override
    public void listAutoDetectionMidiPortNames (final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
    {
        switch (platformType)
        {
            case WINDOWS:
                this.addDeviceDiscoveryPair ("Ableton Push 2", list);
                break;

            case LINUX:
                this.addDeviceDiscoveryPair ("Ableton Push 2 MIDI 1", list);
                break;

            case MAC:
                this.addDeviceDiscoveryPair ("Ableton Push 2 Live Port", list);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void listUsbOutputEndpoints (final List<UsbEndpointInfo> endpoints)
    {
        endpoints.add (new UsbEndpointInfo (VENDOR_ID, PRODUCT_ID, INTERFACE_NUMBER, ENDPOINT_ADDRESS));
    }


    /** {@inheritDoc} */
    @Override
    public ControllerExtension createInstance (final ControllerHost host)
    {
        return new PushControllerExtension (this, host, true);
    }
}
