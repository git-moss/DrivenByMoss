// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.novation.launchkey;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.novation.launchkey.maxi.LaunchkeyMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.maxi.LaunchkeyMk3ControllerDefinition;
import de.mossgrabers.controller.novation.launchkey.maxi.LaunchkeyMk3ControllerSetup;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Launchkey Mk3 controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchkeyMk3ControllerExtensionDefinition extends AbstractControllerExtensionDefinition<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    private static final LaunchkeyMk3ControllerDefinition DEFINITION = new LaunchkeyMk3ControllerDefinition ();


    /**
     * Constructor.
     */
    public LaunchkeyMk3ControllerExtensionDefinition ()
    {
        super (DEFINITION);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration> getControllerSetup (final ControllerHost host)
    {
        return new LaunchkeyMk3ControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
