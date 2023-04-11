// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Abstract definition class for all Akai controllers which support the ACVS (Ableton Live MIDI
 * Communications Specification) protocol.
 *
 * @author Jürgen Moßgraber
 */
public class ACVSControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("CE97B67C-FFB7-4309-AFF2-45193C0C87A3");


    /**
     * Constructor.
     */
    public ACVSControllerDefinition ()
    {
        super (EXTENSION_ID, "MPC Live I/II, One, Force", "Akai", 2, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
        {
            "Akai Network - DAW Control",
            "Akai Network - MIDI"
        }, new String []
        {
            "Akai Network - DAW Control"
        }));
        return midiDiscoveryPairs;
    }
}
