// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.melbourne.rotocontrol;

import com.bitwig.extension.controller.api.ControllerHost;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.melbourne.rotocontrol.RotoControlConfiguration;
import de.mossgrabers.controller.melbourne.rotocontrol.RotoControlControllerDefinition;
import de.mossgrabers.controller.melbourne.rotocontrol.RotoControlControllerSetup;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;


/**
 * Definition class for the Melbourne Instruments ROTO CONTROL device.
 *
 * @author Jürgen Moßgraber
 */
public class RotoControlControllerExtensionDefinition extends AbstractControllerExtensionDefinition<RotoControlControlSurface, RotoControlConfiguration>
{
    /**
     * Constructor.
     */
    public RotoControlControllerExtensionDefinition ()
    {
        super (new RotoControlControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<RotoControlControlSurface, RotoControlConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new RotoControlControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
