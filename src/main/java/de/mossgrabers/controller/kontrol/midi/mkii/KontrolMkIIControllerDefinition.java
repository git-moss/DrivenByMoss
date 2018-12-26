// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.midi.mkii;

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
    private static final UUID EXTENSION_ID = UUID.fromString ("E39043C6-301A-448B-879D-B0308C484265");


    /**
     * Constructor.
     */
    public KontrolMkIIControllerDefinition ()
    {
        super ("", "Jürgen Moßgraber", "1.00", EXTENSION_ID, "Komplete Kontrol Mk II (Midi)", "Native Instruments", 2, 2);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            // TODO Lookup with "-" seems to be broken
            case MAC:
            case WINDOWS:
                for (int i = 1; i <= 16; i++)
                {
                    addDeviceDiscoveryPair (new String []
                    {
                        "Komplete Kontrol DAW - " + i,
                        "KOMPLETE KONTROL - " + i
                    }, new String []
                    {
                        "Komplete Kontrol DAW - " + i,
                        "KOMPLETE KONTROL - " + i
                    });
                }
                break;

            case LINUX:
            default:
                // Not supported
                break;
        }
        return midiDiscoveryPairs;
    }
}
