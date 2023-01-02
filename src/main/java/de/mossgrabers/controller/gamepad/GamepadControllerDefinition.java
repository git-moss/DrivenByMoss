// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.gamepad;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the Gamepad controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GamepadControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("CDE3B53F-0DA6-4B3D-90D6-4E82BB2BFCE7");


    /**
     * Constructor.
     */
    public GamepadControllerDefinition ()
    {
        super (EXTENSION_ID, "Gamepad", "Generic", 1, 0);
    }
}
