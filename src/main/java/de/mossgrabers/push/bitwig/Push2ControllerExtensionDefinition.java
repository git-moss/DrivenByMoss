// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.bitwig;

import de.mossgrabers.framework.bitwig.BitwigSetupFactory;
import de.mossgrabers.framework.bitwig.configuration.SettingsUI;
import de.mossgrabers.framework.bitwig.daw.HostImpl;
import de.mossgrabers.framework.bitwig.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.push.PushControllerDefinition;
import de.mossgrabers.push.PushControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Push 2 controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Push2ControllerExtensionDefinition extends AbstractControllerExtensionDefinition
{
    /**
     * Constructor.
     */
    public Push2ControllerExtensionDefinition ()
    {
        super (new PushControllerDefinition (true));
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup getControllerSetup (final ControllerHost host)
    {
        return new PushControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUI (host.getPreferences ()), true);
    }
}
