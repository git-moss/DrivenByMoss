// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu;

import java.util.UUID;


/**
 * Definition class for the Mackie MCU protocol with 2 extender devices.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUController2ExtenderExtensionDefinition extends MCUControllerExtensionDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("8E0EDA26-ACB9-4F5E-94FB-B886C9468C7A");


    /**
     * Constructor.
     */
    public MCUController2ExtenderExtensionDefinition ()
    {
        super (2);
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareModel ()
    {
        return "Control Universal + 2 Extenders";
    }


    /** {@inheritDoc} */
    @Override
    public UUID getId ()
    {
        return EXTENSION_ID;
    }
}
