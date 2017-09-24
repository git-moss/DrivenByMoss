// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu;

import java.util.UUID;


/**
 * Definition class for the Mackie MCU protocol with 3 extender devices.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUController3ExtenderExtensionDefinition extends MCUControllerExtensionDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("4923C47A-B1DC-48C8-AE89-A332AA26BA87");


    /**
     * Constructor.
     */
    public MCUController3ExtenderExtensionDefinition ()
    {
        super (3);
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareModel ()
    {
        return "Control Universal + 3 Extenders";
    }


    /** {@inheritDoc} */
    @Override
    public UUID getId ()
    {
        return EXTENSION_ID;
    }
}
