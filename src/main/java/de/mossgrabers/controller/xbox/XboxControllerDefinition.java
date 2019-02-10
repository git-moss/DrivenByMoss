// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.xbox;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.usb.UsbMatcher;

import java.util.UUID;


/**
 * Definition class for the Xbox game controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XboxControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID  EXTENSION_ID     = UUID.fromString ("F62AABD1-5C3D-4393-B0E3-7D8A60F0875C");

    /** Xbox USB Vendor ID. */
    private static final short VENDOR_ID        = 0x045E;
    /** Xbox USB Product ID. */
    private static final short PRODUCT_ID       = 0x02EA;
    /** Xbox USB Interface for the display. */
    private static final byte  INTERFACE_NUMBER = 0;
    /** Xbox USB display endpoint. */
    private static final byte  ENDPOINT_ADDRESS = (byte) 0x82;


    /**
     * Constructor.
     */
    public XboxControllerDefinition ()
    {
        super ("", "Jürgen Moßgraber", "1.00", EXTENSION_ID, "Xbox One Controller", "Microsoft", 0, 0);
    }


    /** {@inheritDoc} */
    @Override
    public UsbMatcher claimUSBDevice ()
    {
        return new UsbMatcher (VENDOR_ID, PRODUCT_ID, INTERFACE_NUMBER, ENDPOINT_ADDRESS, true);
    }
}
