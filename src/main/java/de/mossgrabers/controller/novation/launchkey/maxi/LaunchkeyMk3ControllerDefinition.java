// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.maxi;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Novation Launchkey Mk3 controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchkeyMk3ControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID   EXTENSION_ID = UUID.fromString ("10CB5692-541C-4A5D-9EB4-07D80F34A02C");

    private static final int [] KEY_SIZES    =
    {
        25,
        37,
        49,
        61,
        88
    };


    /**
     * Constructor.
     */
    public LaunchkeyMk3ControllerDefinition ()
    {
        super (EXTENSION_ID, "Launchkey Mk3", "Novation", 2, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            default:
            case WINDOWS:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "MIDIIN2 (LKMK3 MIDI)",
                    "LKMK3 MIDI"
                }, new String []
                {
                    "MIDIOUT2 (LKMK3 MIDI)"
                }));
                break;

            case MAC:
                for (final int element: KEY_SIZES)
                {
                    final String deviceName = "Launchkey MK3 " + element;
                    midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                    {
                        deviceName + " LKMK3 DAW Out",
                        deviceName + " LKMK3 MIDI Out"
                    }, new String []
                    {
                        deviceName + " LKMK3 DAW In"
                    }));
                }
                break;

            case LINUX:
                for (final int element: KEY_SIZES)
                {
                    final String deviceName = "Launchkey MK3 " + element;

                    // Kernel 5.13+
                    midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                    {
                        deviceName + " LKMK3 DAW Out",
                        deviceName + " LKMK3 MIDI Out"
                    }, new String []
                    {
                        deviceName + " LKMK3 DAW In"
                    }));

                    // Kernel prior 5.13
                    midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                    {
                        deviceName + " MIDI 2",
                        deviceName + " MIDI 1"
                    }, new String []
                    {
                        deviceName + " MIDI 2"
                    }));

                    // Reaper specific
                    final String deviceID = "L" + element;
                    midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                    {
                        deviceID + " [hw:1,0,1]",
                        deviceID + " [hw:1,0,0]"
                    }, new String []
                    {
                        deviceID + " [hw:1,0,1]"
                    }));
                }
                break;
        }
        return midiDiscoveryPairs;
    }
}
