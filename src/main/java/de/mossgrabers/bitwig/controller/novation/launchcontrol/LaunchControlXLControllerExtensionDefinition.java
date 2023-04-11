// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.novation.launchcontrol;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLControllerDefinition;
import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLControllerSetup;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the LaunchControl XL controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchControlXLControllerExtensionDefinition extends AbstractControllerExtensionDefinition<LaunchControlXLControlSurface, LaunchControlXLConfiguration>
{
    private static final LaunchControlXLControllerDefinition DEFINITION = new LaunchControlXLControllerDefinition ();


    /**
     * Constructor.
     */
    public LaunchControlXLControllerExtensionDefinition ()
    {
        super (DEFINITION);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<LaunchControlXLControlSurface, LaunchControlXLConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new LaunchControlXLControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
