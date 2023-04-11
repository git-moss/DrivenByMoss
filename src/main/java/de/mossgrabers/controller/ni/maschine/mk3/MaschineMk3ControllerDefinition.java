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
 * Definition class for the NI Maschine Mk3 controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineMk3ControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("9055C36B-0A41-48AD-8675-4D3F133E53AC");


    /**
     * Constructor.
     */
    public MaschineMk3ControllerDefinition ()
    {
        super (EXTENSION_ID, "Maschine Mk3", "Native Instruments", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        switch (os)
        {
            case MAC:
                return Collections.singletonList (this.addDeviceDiscoveryPair ("Maschine MK3 Virtual Input", "Maschine MK3 Virtual Output"));

            case WINDOWS:
            default:
                return this.createDeviceDiscoveryPairs ("Maschine MK3 Ctrl MIDI");
        }
    }
}
