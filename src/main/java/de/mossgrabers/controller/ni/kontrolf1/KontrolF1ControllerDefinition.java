// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrolf1;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

/**
 * Definition class for the Kontrol F1 controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolF1ControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("39c880f8-b7c3-4a99-9dd8-2bd478fe14d8");


    /**
     * Constructor.
     */
    public KontrolF1ControllerDefinition ()
    {
        super (EXTENSION_ID, "Kontrol F1 Haszari", "Native Instruments", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        return Collections.singletonList (this.addDeviceDiscoveryPair ("Traktor Kontrol F1 - 1 Input", "Traktor Kontrol F1 - 1 Output"));
    }
}
