// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Novation SL controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID_MK_II = UUID.fromString ("4E01A0B0-67B1-11E5-A837-0800200C9A66");
    private static final UUID EXTENSION_ID_PRO   = UUID.fromString ("80B63970-64F1-11E5-A837-0800200C9A66");

    private final boolean     isMkII;


    /**
     * Constructor.
     *
     * @param isMkII True if is Mk II other Mk I
     */
    public LaunchpadControllerDefinition (final boolean isMkII)
    {
        super ("", "Jürgen Moßgraber", "3.41", isMkII ? EXTENSION_ID_MK_II : EXTENSION_ID_PRO, isMkII ? "Launchpad MkII" : "Launchpad Pro", "Novation", 1, 1);
        this.isMkII = isMkII;
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);

        if (this.isMkII)
        {
            midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad MK2"));
            return midiDiscoveryPairs;
        }

        switch (os)
        {
            case WINDOWS:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("MIDIIN2 (Launchpad Pro)", "MIDIOUT2 (Launchpad Pro)"));
                break;

            case LINUX:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad Pro MIDI 2", "Launchpad Pro MIDI 2"));
                break;

            case MAC:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad Pro Standalone Port", "Launchpad Pro Standalone Port"));
                break;

            default:
                // Not supported
                break;
        }
        return midiDiscoveryPairs;
    }
}
