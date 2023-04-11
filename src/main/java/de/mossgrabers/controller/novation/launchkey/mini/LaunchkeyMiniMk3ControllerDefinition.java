// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.mini;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Novation Launchkey Mini Mk3 controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchkeyMiniMk3ControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("5359D5B1-28CD-4457-B49D-F8D3D7BC52B9");


    /**
     * Constructor.
     */
    public LaunchkeyMiniMk3ControllerDefinition ()
    {
        super (EXTENSION_ID, "Launchkey Mini Mk3", "Novation", 2, 1);
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
                    "MIDIIN2 (Launchkey Mini MK3)",
                    "Launchkey Mini MK3"
                }, new String []
                {
                    "MIDIOUT2 (Launchkey Mini MK3)"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "MIDIIN2 (Launchkey Mini MK3 MID",
                    "Launchkey Mini MK3 MIDI"
                }, new String []
                {
                    "MIDIOUT2 (Launchkey Mini MK3 MI"
                }));
                break;

            case MAC:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey Mini MK3 DAW Port",
                    "Launchkey Mini MK3 MIDI Port"
                }, new String []
                {
                    "Launchkey Mini MK3 DAW Port"
                }));
                break;

            case LINUX:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "MK3 [hw:1,0,1]",
                    "MK3 [hw:1,0,0]"
                }, new String []
                {
                    "MK3 [hw:1,0,1]"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "MK3 [hw:2,0,1]",
                    "MK3 [hw:2,0,0]"
                }, new String []
                {
                    "MK3 [hw:2,0,1]"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey Mini MK3 MIDI 2",
                    "Launchkey Mini MK3 MIDI 1"
                }, new String []
                {
                    "Launchkey Mini MK3 MIDI 2"
                }));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Launchkey Mini MK3 Launchkey Mi #2",
                    "Launchkey Mini MK3 Launchkey Mi"
                }, new String []
                {
                    "Launchkey Mini MK3 Launchkey Mi #2"
                }));
                break;
        }
        return midiDiscoveryPairs;
    }
}
