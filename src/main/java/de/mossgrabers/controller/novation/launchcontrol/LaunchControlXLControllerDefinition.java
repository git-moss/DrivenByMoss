// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchcontrol;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Novation LauchControl XL controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchControlXLControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID   EXTENSION_ID      = UUID.fromString ("5AFA5045-FF92-4737-A091-5B371CD5E529");
    private static final String LAUNCH_CONTROL_XL = "Launch Control XL";


    /**
     * Constructor.
     */
    public LaunchControlXLControllerDefinition ()
    {
        super (EXTENSION_ID, LAUNCH_CONTROL_XL, "Novation", 1, 1);
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
                final String pattern = "%s" + LAUNCH_CONTROL_XL;
                midiDiscoveryPairs.addAll (this.createWindowsDeviceDiscoveryPairs (pattern, pattern));
                break;

            case MAC:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (LAUNCH_CONTROL_XL));
                break;

            case LINUX:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (LAUNCH_CONTROL_XL));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (LAUNCH_CONTROL_XL + " Launch Contro"));
                break;
        }
        return midiDiscoveryPairs;
    }
}
