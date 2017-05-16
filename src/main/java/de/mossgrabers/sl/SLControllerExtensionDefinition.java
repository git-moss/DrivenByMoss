// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl;

import de.mossgrabers.framework.controller.AbstractControllerExtensionDefinition;


/**
 * Abstract definition class for the Launchpad controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class SLControllerExtensionDefinition extends AbstractControllerExtensionDefinition
{
    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "SLMkII4Bitwig";
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
        return "5.0";
    }


    /** {@inheritDoc} */
    @Override
    public int getNumMidiInPorts ()
    {
        return 2;
    }
}
