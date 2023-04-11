// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.novation.launchkey;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.novation.launchkey.mini.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.mini.LaunchkeyMiniMk3ControllerDefinition;
import de.mossgrabers.controller.novation.launchkey.mini.LaunchkeyMiniMk3ControllerSetup;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Launchkey Mini Mk3 controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchkeyMiniMk3ControllerExtensionDefinition extends AbstractControllerExtensionDefinition<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
{
    private static final LaunchkeyMiniMk3ControllerDefinition DEFINITION = new LaunchkeyMiniMk3ControllerDefinition ();


    /**
     * Constructor.
     */
    public LaunchkeyMiniMk3ControllerExtensionDefinition ()
    {
        super (DEFINITION);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration> getControllerSetup (final ControllerHost host)
    {
        return new LaunchkeyMiniMk3ControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
