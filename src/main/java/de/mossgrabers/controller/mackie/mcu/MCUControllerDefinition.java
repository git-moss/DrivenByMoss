// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Mackie MCU protocol extension.
 *
 * @author Jürgen Moßgraber
 */
public class MCUControllerDefinition extends DefaultControllerDefinition
{
    private static final String    I_CON_QCON_PRO_X_V1_15 = "iCON QCON Pro X V1.15";

    private static final UUID []   EXTENSION_ID           =
    {
        UUID.fromString ("5F10A0CD-F866-41C0-B16A-AEA16282B657"),
        UUID.fromString ("7FF808DD-45DB-4026-AA6E-844ED8C05B55"),
        UUID.fromString ("8E0EDA26-ACB9-4F5E-94FB-B886C9468C7A"),
        UUID.fromString ("4923C47A-B1DC-48C8-AE89-A332AA26BA87")
    };

    private static final String [] HARDWARE_MODEL         =
    {
        "MCU - Control Universal",
        "MCU - Control Universal + 1 Extender",
        "MCU - Control Universal + 2 Extenders",
        "MCU - Control Universal + 3 Extenders"
    };


    /**
     * Constructor.
     *
     * @param numMCUExtenders The number of supported Extenders
     */
    public MCUControllerDefinition (final int numMCUExtenders)
    {
        super (EXTENSION_ID[numMCUExtenders], HARDWARE_MODEL[numMCUExtenders], "Mackie", numMCUExtenders + 1, numMCUExtenders + 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> pairs = super.getMidiDiscoveryPairs (os);

        switch (this.getNumMidiInPorts ())
        {
            case 1:
                pairs.addAll (this.createDeviceDiscoveryPairs (I_CON_QCON_PRO_X_V1_15));
                pairs.addAll (this.createDeviceDiscoveryPairs ("Platform M V1.14"));
                pairs.addAll (this.createDeviceDiscoveryPairs ("Platform M+ V1.07"));
                pairs.addAll (this.createDeviceDiscoveryPairs ("X-Touch One"));
                pairs.addAll (this.createDeviceDiscoveryPairs ("ZOOM R16_R24"));
                pairs.addAll (this.createDeviceDiscoveryPairs ("ZOOM R16_R24 Audio Interface"));
                break;

            case 2:
                this.addDeviceDiscoveryPair (new String []
                {
                    I_CON_QCON_PRO_X_V1_15,
                    "iCON QCON Pro XS1 V1.08"
                }, new String []
                {
                    I_CON_QCON_PRO_X_V1_15,
                    "iCON QCON Pro XS1 V1.08"
                });
                break;

            default:
                break;
        }

        return pairs;
    }
}
