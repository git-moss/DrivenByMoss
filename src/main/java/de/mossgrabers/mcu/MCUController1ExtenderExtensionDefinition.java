// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu;

import java.util.UUID;


/**
 * Definition class for the Mackie MCU protocol with 1 extender device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUController1ExtenderExtensionDefinition extends MCUControllerExtensionDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("7FF808DD-45DB-4026-AA6E-844ED8C05B55");


    /**
     * Constructor.
     */
    public MCUController1ExtenderExtensionDefinition ()
    {
        super (1);
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareModel ()
    {
        return "Control Universal + 1 Extender";
    }


    /** {@inheritDoc} */
    @Override
    public UUID getId ()
    {
        return EXTENSION_ID;
    }
}
