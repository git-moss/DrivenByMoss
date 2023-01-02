// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


/**
 * Definition class for the NI Maschine Mk2 controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineMk2ControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("015E4908-C049-4808-A645-A7A44BEFE36B");


    /**
     * Constructor.
     */
    public MaschineMk2ControllerDefinition ()
    {
        super (EXTENSION_ID, "Maschine Mk2", "Native Instruments", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        switch (os)
        {
            case MAC:
                return Collections.singletonList (this.addDeviceDiscoveryPair ("Maschine MK2 Virtual Input", "Maschine MK2 Virtual Output"));

            case WINDOWS:
            default:
                return Collections.singletonList (this.addDeviceDiscoveryPair ("Maschine MK2 In", "Maschine MK2 Out"));
        }
    }
}
