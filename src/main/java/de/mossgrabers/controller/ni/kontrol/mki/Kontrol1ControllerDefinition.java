// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mki;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.usb.UsbMatcher;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Native Instruments Kontrol MkI controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1ControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID []   EXTENSION_ID   =
    {
        UUID.fromString ("457ef1d3-d197-4a94-a1d0-b4322ecbdd7d"),
        UUID.fromString ("90817073-0c11-41cf-8c56-f3334ec91fc4"),
        UUID.fromString ("99ff3646-3a65-47e5-a0e2-58c1c1799e93"),
        UUID.fromString ("18d5c565-f496-406d-8c3f-5af1004f61ff")
    };

    private static final String [] HARDWARE_MODEL =
    {
        "Komplete Kontrol S25 mk I",
        "Komplete Kontrol S49 mk I",
        "Komplete Kontrol S61 mk I",
        "Komplete Kontrol S88 mk I"
    };

    private static final short     VENDOR_ID      = 0x17cc;
    private static final short []  PRODUCT_ID     =
    {
        0x1340,
        0x1350,
        0x1360,
        0x1410
    };

    private final int              modelIndex;


    /**
     * Constructor.
     *
     * @param modelIndex The index of the specific model (S25, S49, S61, S88)
     */
    public Kontrol1ControllerDefinition (final int modelIndex)
    {
        super (EXTENSION_ID[modelIndex], HARDWARE_MODEL[modelIndex], "Native Instruments", 1, 0);
        this.modelIndex = modelIndex;
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        return Collections.singletonList (this.addDeviceDiscoveryPair ("Komplete Kontrol - 1", null));
    }


    /** {@inheritDoc} */
    @Override
    public UsbMatcher claimUSBDevice ()
    {
        return new UsbMatcher (VENDOR_ID, PRODUCT_ID[this.modelIndex]);
    }
}
