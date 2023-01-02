// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.utilities.autocolor;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the Auto Color extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AutoColorDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("3AC40457-43B5-452D-A2F3-439E40754BDC");


    /**
     * Constructor.
     */
    public AutoColorDefinition ()
    {
        super (EXTENSION_ID, "Auto Color", "Utilities", 0, 0);
    }
}
