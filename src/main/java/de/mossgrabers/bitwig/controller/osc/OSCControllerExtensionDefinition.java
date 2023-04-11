// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.osc;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.osc.OSCConfiguration;
import de.mossgrabers.controller.osc.OSCControllerDefinition;
import de.mossgrabers.controller.osc.OSCControllerSetup;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Akai OSC controller.
 *
 * @author Jürgen Moßgraber
 */
public class OSCControllerExtensionDefinition extends AbstractControllerExtensionDefinition<IControlSurface<OSCConfiguration>, OSCConfiguration>
{
    /**
     * Constructor.
     */
    public OSCControllerExtensionDefinition ()
    {
        super (new OSCControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<IControlSurface<OSCConfiguration>, OSCConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new OSCControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
