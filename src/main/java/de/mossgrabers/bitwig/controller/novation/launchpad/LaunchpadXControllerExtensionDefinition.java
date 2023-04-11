// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.novation.launchpad;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.LaunchpadControllerSetup;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.novation.launchpad.definition.LaunchpadXControllerDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Launchpad X extension.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchpadXControllerExtensionDefinition extends AbstractControllerExtensionDefinition<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private static final LaunchpadXControllerDefinition DEFINITION = new LaunchpadXControllerDefinition ();


    /**
     * Constructor.
     */
    public LaunchpadXControllerExtensionDefinition ()
    {
        super (DEFINITION);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<LaunchpadControlSurface, LaunchpadConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new LaunchpadControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()), DEFINITION);
    }
}
