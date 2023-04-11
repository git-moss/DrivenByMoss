// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the NI Maschine Mikro Mk3 controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineMikroMk3ControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("E87516A3-5401-44B1-9952-B4F821ED3DD5");


    /**
     * Constructor.
     */
    public MaschineMikroMk3ControllerDefinition ()
    {
        super (EXTENSION_ID, "Maschine Mikro Mk3", "Native Instruments", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        return this.createDeviceDiscoveryPairs ("Maschine Mikro MK3");
    }
}
