// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc;

import de.mossgrabers.framework.controller.AbstractControllerExtensionDefinition;


/**
 * Abstract definition class for the APC controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class APCControllerExtensionDefinition extends AbstractControllerExtensionDefinition
{
    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "APC4Bitwig";
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareVendor ()
    {
        return "Akai";
    }


    /** {@inheritDoc} */
    @Override
    public String getVersion ()
    {
        return "5.11";
    }
}
