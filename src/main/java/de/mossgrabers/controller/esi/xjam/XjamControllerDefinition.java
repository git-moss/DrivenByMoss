// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.esi.xjam;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.usb.UsbMatcher;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


/**
 * Definition class for the ESI Xjam controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XjamControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID  EXTENSION_ID = UUID.fromString ("1988b126-bf8d-4660-86f4-da056a9c3ba7");

    private static final short VENDOR_ID    = 0x2573;
    private static final short PRODUCT_ID   = 0x0036;


    /**
     * Constructor.
     */
    public XjamControllerDefinition ()
    {
        super (EXTENSION_ID, "Xjam", "ESI", 0, 0);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        return Collections.emptyList ();
    }


    /** {@inheritDoc} */
    @Override
    public UsbMatcher claimUSBDevice ()
    {
        return new UsbMatcher (VENDOR_ID, PRODUCT_ID);
    }
}
