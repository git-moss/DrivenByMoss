// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mkii;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Kontrol MkII extension using the NI MIDI host protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolMkIIControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID         EXTENSION_ID  = UUID.fromString ("E39043C6-301A-448B-879D-B0308C484265");

    private static final String [] [] PORTS_WINDOWS = new String [] []
    {
        new String []
        {
            "Komplete Kontrol DAW - 1",
            "KOMPLETE KONTROL - 1"
        },
        new String []
        {
            "Komplete Kontrol A DAW",
            "KOMPLETE KONTROL A25 MIDI",
        },
        new String []
        {
            "Komplete Kontrol A DAW",
            "KOMPLETE KONTROL A49 MIDI",
        },
        new String []
        {
            "Komplete Kontrol A DAW",
            "KOMPLETE KONTROL A61 MIDI",
        },
        new String []
        {
            "Komplete Kontrol M DAW",
            "KOMPLETE KONTROL M32 MIDI"
        }
    };

    private static final String [] [] PORTS_MACOS   = new String [] []
    {
        new String []
        {
            "Komplete Kontrol DAW - 1",
            "KOMPLETE KONTROL S49 MK2 Port 1"
        },
        new String []
        {
            "Komplete Kontrol A DAW",
            "KOMPLETE KONTROL A25 MIDI",
        },
        new String []
        {
            "Komplete Kontrol A DAW",
            "KOMPLETE KONTROL A49 MIDI",
        },
        new String []
        {
            "Komplete Kontrol A DAW",
            "KOMPLETE KONTROL A61 MIDI",
        },
        new String []
        {
            "Komplete Kontrol M DAW",
            "KOMPLETE KONTROL M32 MIDI"
        }
    };


    /**
     * Constructor.
     */
    public KontrolMkIIControllerDefinition ()
    {
        super (EXTENSION_ID, "Komplete Kontrol A / M32 / S mkII", "Native Instruments", 2, 2);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            case MAC:
                for (final String [] ports: PORTS_MACOS)
                    midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (ports, ports));
                break;

            case WINDOWS:
                for (final String [] ports: PORTS_WINDOWS)
                    midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (ports, ports));
                break;

            case LINUX:
            default:
                // Not supported
                break;
        }
        return midiDiscoveryPairs;
    }
}
