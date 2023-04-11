// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.controller;

import de.mossgrabers.framework.utils.OperatingSystem;

import java.util.UUID;


/**
 * Device descriptor for device which support version 2 of the NIHIA protocol.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolProtocolDeviceDescriptorV2 implements IKontrolProtocolDeviceDescriptor
{
    private static final String       KOMPLETE_KONTROL_DAW_1 = "Komplete Kontrol DAW - 1";
    private static final UUID         EXTENSION_ID           = UUID.fromString ("91A751B5-61C8-4388-8B8B-C2F6AD05A25D");
    private static final String       DEVICE_NAME            = "Komplete Kontrol S-series mkII";

    private static final String [] [] PORTS_WINDOWS          =
    {
        {
            KOMPLETE_KONTROL_DAW_1,
            "KOMPLETE KONTROL - 1"
        }
    };

    private static final String [] [] PORTS_MACOS            =
    {
        {
            KOMPLETE_KONTROL_DAW_1,
            "KOMPLETE KONTROL S49 MK2 Port 1"
        },
        {
            KOMPLETE_KONTROL_DAW_1,
            "KOMPLETE KONTROL S49 MK2 Anschluss 1"
        },
        {
            KOMPLETE_KONTROL_DAW_1,
            "KOMPLETE KONTROL S61 MK2 Port 1"
        },
        {
            KOMPLETE_KONTROL_DAW_1,
            "KOMPLETE KONTROL S61 MK2 Anschluss 1"
        },
        {
            KOMPLETE_KONTROL_DAW_1,
            "KOMPLETE KONTROL S88 MK2 Port 1"
        },
        {
            KOMPLETE_KONTROL_DAW_1,
            "KOMPLETE KONTROL S88 MK2 Anschluss 1"
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
