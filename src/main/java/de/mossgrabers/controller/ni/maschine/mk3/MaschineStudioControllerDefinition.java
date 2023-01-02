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
 * Definition class for the NI Maschine Studio controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineStudioControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("9E2D8B7C-D032-11EB-B8BC-0242AC130003");


    /**
     * Constructor.
     */
    public MaschineStudioControllerDefinition ()
    {
        super (EXTENSION_ID, "Maschine Studio", "Native Instruments", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        switch (os)
        {
            case MAC:
                return Collections.singletonList (this.addDeviceDiscoveryPair ("Maschine Studio Virtual Input", "Maschine Studio Virtual Output"));

            case WINDOWS:
            default:
                return this.createWindowsDeviceDiscoveryPairs ("Maschine Studio In", "Maschine Studio Out 1");
        }
    }
}
