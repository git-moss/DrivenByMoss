// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad;

import de.mossgrabers.framework.controller.AbstractControllerExtensionDefinition;


/**
 * Abstract definition class for the Launchpad controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class LaunchpadControllerExtensionDefinition extends AbstractControllerExtensionDefinition
{
    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Launchpad4Bitwig";
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareVendor ()
    {
        return "Novation";
    }


    /** {@inheritDoc} */
    @Override
    public String getVersion ()
    {
        return "3.20";
    }
}
