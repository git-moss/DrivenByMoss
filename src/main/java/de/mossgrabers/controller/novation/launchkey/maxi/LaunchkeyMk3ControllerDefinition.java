// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
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
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchkeyMk3ControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("10CB5692-541C-4A5D-9EB4-07D80F34A02C");


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
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey MK3 25 LKMK3 DAW Out",
                    "Launchkey MK3 25 LKMK3 MIDI Out"
                }, new String []
                {
                    "Launchkey MK3 25 LKMK3 DAW In"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey MK3 37 LKMK3 DAW Out",
                    "Launchkey MK3 37 LKMK3 MIDI Out"
                }, new String []
                {
                    "Launchkey MK3 37 LKMK3 DAW In"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey MK3 49 LKMK3 DAW Out",
                    "Launchkey MK3 49 LKMK3 MIDI Out"
                }, new String []
                {
                    "Launchkey MK3 49 LKMK3 DAW In"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey MK3 61 LKMK3 DAW Out",
                    "Launchkey MK3 61 LKMK3 MIDI Out"
                }, new String []
                {
                    "Launchkey MK3 61 LKMK3 DAW In"
                }));
                break;

            case LINUX:
                // Kernel 5.13+
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey MK3 25 LKMK3 DAW Out",
                    "Launchkey MK3 25 LKMK3 MIDI Out"
                }, new String []
                {
                    "Launchkey MK3 25 LKMK3 DAW In"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey MK3 37 LKMK3 DAW Out",
                    "Launchkey MK3 37 LKMK3 MIDI Out"
                }, new String []
                {
                    "Launchkey MK3 37 LKMK3 DAW In"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey MK3 49 LKMK3 DAW Out",
                    "Launchkey MK3 49 LKMK3 MIDI Out"
                }, new String []
                {
                    "Launchkey MK3 49 LKMK3 DAW In"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey MK3 61 LKMK3 DAW Out",
                    "Launchkey MK3 61 LKMK3 MIDI Out"
                }, new String []
                {
                    "Launchkey MK3 61 LKMK3 DAW In"
                }));

                // Kernel prior 5.13
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey MK3 25 MIDI 2",
                    "Launchkey MK3 25 MIDI 1"
                }, new String []
                {
                    "Launchkey MK3 25 MIDI 2"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey MK3 37 MIDI 2",
                    "Launchkey MK3 37 MIDI 1"
                }, new String []
                {
                    "Launchkey MK3 37 MIDI 2"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey MK3 49 MIDI 2",
                    "Launchkey MK3 49 MIDI 1"
                }, new String []
                {
                    "Launchkey MK3 49 MIDI 2"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey MK3 61 MIDI 2",
                    "Launchkey MK3 61 MIDI 1"
                }, new String []
                {
                    "Launchkey MK3 61 MIDI 2"
                }));

                // Reaper specific
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "L25 [hw:1,0,1]",
                    "L25 [hw:1,0,0]"
                }, new String []
                {
                    "L25 [hw:1,0,1]"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "L37 [hw:1,0,1]",
                    "L37 [hw:1,0,0]"
                }, new String []
                {
                    "L37 [hw:1,0,1]"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "L49 [hw:1,0,1]",
                    "L49 [hw:1,0,0]"
                }, new String []
                {
                    "L49 [hw:1,0,1]"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "L61 [hw:1,0,1]",
                    "L61 [hw:1,0,0]"
                }, new String []
                {
                    "L61 [hw:1,0,1]"
                }));
                break;
        }
        return midiDiscoveryPairs;
    }
}
