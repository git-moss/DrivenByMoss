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
 * Definition class for the Ableton Push controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class PushControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID  EXTENSION_ID_MK_I  = UUID.fromString ("DBED9610-C474-11E6-9598-0800200C9A66");
    private static final UUID  EXTENSION_ID_MK_II = UUID.fromString ("15176AA0-C476-11E6-9598-0800200C9A66");

    /** Push 2 USB Vendor ID. */
    private static final short VENDOR_ID          = 0x2982;
    /** Push 2 USB Product ID. */
    private static final short PRODUCT_ID         = 0x1967;
    /** Push 2 USB Interface for the display. */
    private static final byte  INTERFACE_NUMBER   = 0;
    /** Push 2 USB display endpoint. */
    private static final byte  ENDPOINT_ADDRESS   = (byte) 0x01;

    private final boolean      isMkII;


    /**
     * Constructor.
     *
     * @param isMkII True if is Mk II other Mk I
     */
    public PushControllerDefinition (final boolean isMkII)
    {
        super (isMkII ? EXTENSION_ID_MK_II : EXTENSION_ID_MK_I, isMkII ? "Push 2" : "Push 1", "Ableton", 1, 1);
        this.isMkII = isMkII;
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            case WINDOWS:
                if (this.isMkII)
                    midiDiscoveryPairs.addAll (this.createDeviceDiscoveryPairs ("Ableton Push 2"));
                else
                    midiDiscoveryPairs.addAll (this.createWindowsDeviceDiscoveryPairs ("MIDIIN2 (%sAbleton Push)", "MIDIOUT2 (%sAbleton Push)"));
                break;

            case LINUX, MAC:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (this.isMkII ? "Ableton Push 2 Live Port" : "Ableton Push User Port"));
                if (this.isMkII)
                    midiDiscoveryPairs.addAll (this.createLinuxDeviceDiscoveryPairs ("A2", "A2"));
                else
                    midiDiscoveryPairs.addAll (this.createLinuxDeviceDiscoveryPairs ("Push", "Push", 1));
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
        return this.isMkII ? new UsbMatcher (VENDOR_ID, PRODUCT_ID, INTERFACE_NUMBER, ENDPOINT_ADDRESS, true) : null;
    }
}
