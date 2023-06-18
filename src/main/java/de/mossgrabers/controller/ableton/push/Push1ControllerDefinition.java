// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Ableton Push 1 controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class Push1ControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("DBED9610-C474-11E6-9598-0800200C9A66");


    /**
     * Constructor.
     */
    public Push1ControllerDefinition ()
    {
        super (EXTENSION_ID, "Push 1", "Ableton", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            case WINDOWS:
                midiDiscoveryPairs.addAll (this.createWindowsDeviceDiscoveryPairs ("MIDIIN2 (%sAbleton Push)", "MIDIOUT2 (%sAbleton Push)"));
                break;

            case LINUX, MAC:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Ableton Push User Port"));
                midiDiscoveryPairs.addAll (this.createLinuxDeviceDiscoveryPairs ("Push", "Push", 1));
                break;

            default:
                // Not supported
                break;
        }
        return midiDiscoveryPairs;
    }
}
