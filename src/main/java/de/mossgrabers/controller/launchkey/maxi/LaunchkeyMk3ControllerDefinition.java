// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchkey.maxi;

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
                    "LKMK3 DAW Port",
                    "LKMK3 MIDI Port"
                }, new String []
                {
                    "LKMK3 DAW Port"
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
                break;
        }
        return midiDiscoveryPairs;
    }
}
