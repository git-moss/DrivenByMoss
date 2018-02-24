// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.bitwig;

import java.util.UUID;


/**
 * Definition class for the Mackie MCU protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUController0ExtenderExtensionDefinition extends MCUControllerExtensionDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("5F10A0CD-F866-41C0-B16A-AEA16282B657");


    /**
     * Constructor.
     */
    public MCUController0ExtenderExtensionDefinition ()
    {
        super (0);
    }


    /** {@inheritDoc} */
    @Override
    public UUID getId ()
    {
        return EXTENSION_ID;
    }
}
