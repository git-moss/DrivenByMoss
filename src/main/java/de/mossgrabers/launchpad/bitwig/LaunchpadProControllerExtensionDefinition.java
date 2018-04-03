// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.bitwig;

import de.mossgrabers.framework.bitwig.BitwigSetupFactory;
import de.mossgrabers.framework.bitwig.configuration.SettingsUI;
import de.mossgrabers.framework.bitwig.daw.HostImpl;
import de.mossgrabers.framework.bitwig.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.launchpad.LaunchpadControllerDefinition;
import de.mossgrabers.launchpad.LaunchpadControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Launchpad Pro extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadProControllerExtensionDefinition extends AbstractControllerExtensionDefinition
{
    /**
     * Constructor.
     */
    public LaunchpadProControllerExtensionDefinition ()
    {
        super (new LaunchpadControllerDefinition (false));
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup getControllerSetup (final ControllerHost host)
    {
        return new LaunchpadControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUI (host.getPreferences ()), true);
    }
}
