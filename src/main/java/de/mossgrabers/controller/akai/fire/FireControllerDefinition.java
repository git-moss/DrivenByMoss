// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Akai Fire controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class FireControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("75CC171F-325C-45B8-B304-DAE96756D5BA");


    /**
     * Constructor.
     */
    public FireControllerDefinition ()
    {
        super (EXTENSION_ID, "Fire", "Akai", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            case WINDOWS:
                midiDiscoveryPairs.addAll (this.createWindowsDeviceDiscoveryPairs ("%sFL STUDIO FIRE", "%sFL STUDIO FIRE"));
                midiDiscoveryPairs.addAll (this.createDeviceDiscoveryPairs ("Akai Fire"));
                break;

            case LINUX:
                midiDiscoveryPairs.addAll (this.createDeviceDiscoveryPairs ("FL STUDIO FIRE"));
                midiDiscoveryPairs.addAll (this.createLinuxDeviceDiscoveryPairs ("FIRE", "FIRE"));
                midiDiscoveryPairs.addAll (this.createLinuxDeviceDiscoveryPairs ("FL STUDIO FIRE Jack 1", "FL STUDIO FIRE Jack 1"));
                break;

            case MAC:
                for (int i = 1; i < 20; i++)
                {
                    final String name = "FL STUDIO FIRE Jack " + i;
                    midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (name, name));
                }
                midiDiscoveryPairs.addAll (this.createDeviceDiscoveryPairs ("FL STUDIO FIRE"));
                break;

            default:
                // Not supported
                break;
        }
        return midiDiscoveryPairs;
    }
}
