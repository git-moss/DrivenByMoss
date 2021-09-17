// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;


/**
 * Abstract definition class for all Akai controllers which support the ACVS (Ableton Live MIDI
 * Communications Specification) protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ACVSControllerDefinition extends DefaultControllerDefinition
{
    /**
     * Constructor.
     *
     * @param acvsDevice The specific device supporting the ACVS protocol
     */
    public ACVSControllerDefinition (final ACVSDevice acvsDevice)
    {
        super (acvsDevice.getUuid (), acvsDevice.getName (), "Akai", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            case WINDOWS:
                midiDiscoveryPairs.addAll (this.createDeviceDiscoveryPairs ("Akai Network - DAW Control"));
                break;

            case MAC:
                // TODO Test
                midiDiscoveryPairs.addAll (this.createDeviceDiscoveryPairs ("Akai Network - DAW Control"));
                break;

            case LINUX:
                // Not supported
                break;

            default:
                // Not supported
                break;
        }
        return midiDiscoveryPairs;
    }
}
