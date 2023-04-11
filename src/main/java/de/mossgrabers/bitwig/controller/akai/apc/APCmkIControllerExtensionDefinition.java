// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.akai.apc;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.APCControllerDefinition;
import de.mossgrabers.controller.akai.apc.APCControllerSetup;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the APC40 mkI extension.
 *
 * @author Jürgen Moßgraber
 */
public class APCmkIControllerExtensionDefinition extends AbstractControllerExtensionDefinition<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     */
    public APCmkIControllerExtensionDefinition ()
    {
        super (new APCControllerDefinition (false));
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<APCControlSurface, APCConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new APCControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()), false);
    }
}
