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
    private static final UUID      EXTENSION_ID = UUID.fromString ("E39043C6-301A-448B-879D-B0308C484265");
    private static final String [] A_SERIES     =
    {
        "KOMPLETE KONTROL A25 MIDI",
        "KOMPLETE KONTROL A49 MIDI",
        "KOMPLETE KONTROL A61 MIDI"
    };


    /**
     * Constructor.
     */
    public KontrolMkIIControllerDefinition ()
    {
        super (EXTENSION_ID, "Komplete Kontrol A / MkII", "Native Instruments", 2, 2);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            // TODO Bugfix required: Lookup with "-" seems to be broken
            case MAC:
            case WINDOWS:
                for (int i = 1; i <= 16; i++)
                {
                    midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                    {
                        "Komplete Kontrol DAW - " + i,
                        "KOMPLETE KONTROL - " + i
                    }, new String []
                    {
                        "Komplete Kontrol DAW - " + i,
                        "KOMPLETE KONTROL - " + i
                    }));
                }
                for (final String element: A_SERIES)
                {
                    midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                    {
                        "Komplete Kontrol A DAW",
                        element
                    }, new String []
                    {
                        "Komplete Kontrol A DAW",
                        element
                    }));
                }
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Komplete Kontrol M DAW",
                    "KOMPLETE KONTROL M32 MIDI"
                }, new String []
                {
                    "Komplete Kontrol M DAW",
                    "KOMPLETE KONTROL M32 MIDI"
                }));
                break;

            case LINUX:
            default:
                // Not supported
                break;
        }
        return midiDiscoveryPairs;
    }
}
