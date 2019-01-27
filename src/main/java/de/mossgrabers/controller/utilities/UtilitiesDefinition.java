// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.utilities;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the Utilities extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UtilitiesDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("3AC40457-43B5-452D-A2F3-439E40754BDC");


    /**
     * Constructor.
     */
    public UtilitiesDefinition ()
    {
        super ("", "Jürgen Moßgraber", "1.00", EXTENSION_ID, "Utilities", "Generic", 0, 0);
    }
}
