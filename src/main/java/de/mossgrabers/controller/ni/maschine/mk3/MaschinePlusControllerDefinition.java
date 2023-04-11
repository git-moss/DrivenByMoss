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
 * Definition class for the NI Maschine+ controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class MaschinePlusControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("BF0FD105-08A3-4A1D-9F19-A8F5A9C4E7DF");


    /**
     * Constructor.
     */
    public MaschinePlusControllerDefinition ()
    {
        super (EXTENSION_ID, "Maschine Plus", "Native Instruments", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        switch (os)
        {
            case MAC:
            case WINDOWS:
            default:
                return Collections.singletonList (this.addDeviceDiscoveryPair ("Maschine Plus Virtual", "Maschine Plus Virtual"));
        }
    }
}
