// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.osc;

import de.mossgrabers.framework.bitwig.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the OSC extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCExtensionDefinition extends AbstractControllerExtensionDefinition
{
    /**
     * Constructor.
     */
    public OSCExtensionDefinition ()
    {
        super (new OSCControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    public ControllerExtension createInstance (final ControllerHost host)
    {
        return new OSCExtension (this, host);
    }


    /** {@inheritDoc} */
    @Override
    public void listAutoDetectionMidiPortNames (final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup getControllerSetup (final ControllerHost host)
    {
        // Not used
        return null;
    }
}
