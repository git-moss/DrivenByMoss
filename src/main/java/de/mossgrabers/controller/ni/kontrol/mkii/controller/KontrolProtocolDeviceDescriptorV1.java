// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.controller;

import de.mossgrabers.framework.utils.OperatingSystem;

import java.util.UUID;


/**
 * Device descriptor for device which support version 1 of the NIHIA protocol.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolProtocolDeviceDescriptorV1 implements IKontrolProtocolDeviceDescriptor
{
    private static final String       KOMPLETE_KONTROL_A_DAW = "Komplete Kontrol A DAW";

    private static final UUID         EXTENSION_ID           = UUID.fromString ("4EA6215E-C7EB-4184-AEA4-67D03D8EF7A8");
    private static final String       DEVICE_NAME            = "Komplete Kontrol A-series / M32";

    private static final String [] [] PORTS_WINDOWS          =
    {
        {
            KOMPLETE_KONTROL_A_DAW,
            "KOMPLETE KONTROL A25 MIDI",
        },
        {
            KOMPLETE_KONTROL_A_DAW,
            "KOMPLETE KONTROL A49 MIDI",
        },
        {
            KOMPLETE_KONTROL_A_DAW,
            "KOMPLETE KONTROL A61 MIDI",
        },
        {
            "Komplete Kontrol M DAW",
            "KOMPLETE KONTROL M32 MIDI"
        }
    };

    private static final String [] [] PORTS_MACOS            =
    {
        {
            KOMPLETE_KONTROL_A_DAW,
            "KOMPLETE KONTROL A25",
        },
        {
            KOMPLETE_KONTROL_A_DAW,
            "KOMPLETE KONTROL A49",
        },
        {
            KOMPLETE_KONTROL_A_DAW,
            "KOMPLETE KONTROL A61",
        },
        {
            "Komplete Kontrol M DAW",
            "KOMPLETE KONTROL M32"
        }
    };


    /** {@inheritDoc} */
    @Override
    public UUID getID ()
    {
        return EXTENSION_ID;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return DEVICE_NAME;
    }


    /** {@inheritDoc} */
    @Override
    public String [] [] getMidiDiscoveryPairs (final OperatingSystem os)
    {
        switch (os)
        {
            case MAC:
                return PORTS_MACOS;

            case WINDOWS:
                return PORTS_WINDOWS;

            // Not supported
            case LINUX:
            default:
                return new String [0] [0];
        }
    }
}
