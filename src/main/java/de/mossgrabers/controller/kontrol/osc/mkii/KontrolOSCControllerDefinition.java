// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.osc.mkii;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the OSC extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolOSCControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("EE5E731C-742F-4867-81C0-17536D8AD316");


    /**
     * Constructor.
     */
    public KontrolOSCControllerDefinition ()
    {
        super ("", "Jürgen Moßgraber", "1.20", EXTENSION_ID, "Komplete Kontrol Mk II", "Native Instruments", 1, 0);
    }
}
