// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;


/**
 * Device descriptor for device which support version 3 of the NIHIA protocol.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolProtocolDeviceDescriptorV3 implements IKontrolProtocolDeviceDescriptor
{
    private static final UUID            EXTENSION_ID  = UUID.fromString ("DE2479B8-79B9-411F-8746-5E59032544AF");
    private static final String          DEVICE_NAME   = "Komplete Kontrol S-series mk3";

    private static final String [] [] [] PORTS_WINDOWS =
    {
        {
            {
                "MIDIIN2 (KONTROL S49 MK3)",
                "KONTROL S49 MK3"
            },
            {
                "MIDIOUT2 (KONTROL S49 MK3)",
                "KONTROL S49 MK3"
            },
        },

        {
            {
                "MIDIIN2 (KONTROL S61 MK3)",
                "KONTROL S61 MK3"
            },
            {
                "MIDIOUT2 (KONTROL S61 MK3)",
                "KONTROL S61 MK3"
            },
        },

        {
            {
                "MIDIIN2 (KONTROL S88 MK3)",
                "KONTROL S88 MK3"
            },
            {
                "MIDIOUT2 (KONTROL S88 MK3)",
                "KONTROL S88 MK3"
            },
        },
    };

    private static final String [] []    PORTS_MACOS   =
    {
        {
            "KOMPLETE KONTROL S49 MK3 Port 2",
            "KOMPLETE KONTROL S49 MK3 Port 1"
        },
        {
            "KOMPLETE KONTROL S49 MK3 Anschluss 2",
            "KOMPLETE KONTROL S49 MK3 Anschluss 1"
        },
        {
            "KOMPLETE KONTROL S61 MK3 Port 2",
            "KOMPLETE KONTROL S61 MK3 Port 1"
        },
        {
            "KOMPLETE KONTROL S61 MK3 Anschluss 2",
            "KOMPLETE KONTROL S61 MK3 Anschluss 1"
        },
        {
            "KOMPLETE KONTROL S88 MK3 Port 2",
            "KOMPLETE KONTROL S88 MK3 Port 1"
        },
        {
            "KOMPLETE KONTROL S88 MK3 Anschluss 2",
            "KOMPLETE KONTROL S88 MK3 Anschluss 1"
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
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = new ArrayList<> ();
        switch (os)
        {
            case MAC:
                for (final String [] ports: PORTS_MACOS)
                    midiDiscoveryPairs.add (new Pair<> (ports, ports));
                break;

            case WINDOWS:
                for (final String [] [] ports: PORTS_WINDOWS)
                    midiDiscoveryPairs.add (new Pair<> (ports[0], ports[1]));
                break;

            case LINUX:
            default:
                // Not supported
                break;
        }

        return midiDiscoveryPairs;
    }
}
