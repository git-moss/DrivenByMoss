// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.faderfox.ec4;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Faderfox EC4 controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class EC4ControllerDefinition extends DefaultControllerDefinition
{
    private static final String FADERFOX_PORT = "Faderfox EC4";


    /**
     * Constructor.
     */
    public EC4ControllerDefinition ()
    {
        super (UUID.fromString ("96F106D3-449D-43F7-ADC9-1ECE301817D2"), "EC4", "Faderfox", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> pairs = super.getMidiDiscoveryPairs (os);

        switch (os)
        {
            // TODO Test
            default:
            case WINDOWS, MAC:
                pairs.addAll (this.createDeviceDiscoveryPairs (FADERFOX_PORT));
                break;

            case LINUX:
                pairs.addAll (this.createLinuxDeviceDiscoveryPairs (FADERFOX_PORT, FADERFOX_PORT));
                break;
        }

        return pairs;
    }
}
