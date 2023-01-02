// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.yaeltex.turn;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Yaeltex Turn controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class YaeltexTurnControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("8F86E710-83CF-4A2F-B07B-E2BE6F0D030F");


    /**
     * Constructor.
     */
    public YaeltexTurnControllerDefinition ()
    {
        super (EXTENSION_ID, "Turn", "Yaeltex", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        return this.createDeviceDiscoveryPairs ("TURN");
    }
}
