// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.melbourne.rotocontrol;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Abstract definition class for the Melbourne Instruments ROTO CONTROL.
 *
 * @author Jürgen Moßgraber
 */
public class RotoControlControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("CFEB4D14-B6D8-47A4-A251-E11C023CAF6F");


    /**
     * Constructor.
     */
    public RotoControlControllerDefinition ()
    {
        super (EXTENSION_ID, "ROTO CONTROL", "Melbourne Instruments", 1, 1);
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
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Roto-Control"));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("ROTO CONTROL"));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Melbourne Instruments ROTO CONTROL"));
                break;

            case LINUX:
                midiDiscoveryPairs.addAll (this.createLinuxDeviceDiscoveryPairs ("Roto-Control", "Roto-Control"));
                break;
        }

        return midiDiscoveryPairs;
    }
}
