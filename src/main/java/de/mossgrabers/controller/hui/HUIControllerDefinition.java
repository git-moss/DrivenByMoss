// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.hui;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the Mackie Human User Interface (HUI) protocol extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HUIControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("AD354AAA-E499-4B89-B86C-3E1F74647FF5");


    /**
     * Constructor.
     */
    public HUIControllerDefinition ()
    {
        super (EXTENSION_ID, "HUI - Human User Interface", "Mackie", 1, 1);
    }
}
