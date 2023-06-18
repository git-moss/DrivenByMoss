// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.usb.UsbMatcher;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Ableton Push 2 controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class Push2ControllerDefinition extends DefaultControllerDefinition
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


    /**
     * Constructor.
     */
    public Push2ControllerDefinition ()
    {
        super (EXTENSION_ID, "Push 2", "Ableton", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            case WINDOWS:
                midiDiscoveryPairs.addAll (this.createDeviceDiscoveryPairs ("Ableton Push 2"));
                break;

            case MAC:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Ableton Push 2 Live Port"));
                break;

            case LINUX:
                midiDiscoveryPairs.addAll (this.createLinuxDeviceDiscoveryPairs ("A2", "A2"));
                break;

            default:
                // Not supported
                break;
        }
        return midiDiscoveryPairs;
    }


    /** {@inheritDoc} */
    @Override
    public UsbMatcher claimUSBDevice ()
    {
        return new UsbMatcher (VENDOR_ID, PRODUCT_ID, INTERFACE_NUMBER, ENDPOINT_ADDRESS, true);
    }
}
