// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Akai APCmini controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCminiControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("E7E02A80-3657-11E4-8C21-0800200C9A66");


    /**
     * Constructor.
     */
    public APCminiControllerDefinition ()
    {
        super (EXTENSION_ID, "APCmini", "Akai", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        return this.createDeviceDiscoveryPairs ("APC MINI");
    }
}
