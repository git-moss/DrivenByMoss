// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the OSC extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("94DD41B0-EFEE-11E3-AC10-0800200C9A66");


    /**
     * Constructor.
     */
    public OSCControllerDefinition ()
    {
        super (EXTENSION_ID, "OSC", "Open Sound Control", 1, 0);
    }
}
