// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.akai.fire;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.FireControllerDefinition;
import de.mossgrabers.controller.akai.fire.FireControllerSetup;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Akai Fire.
 *
 * @author Jürgen Moßgraber
 */
public class FireControllerExtensionDefinition extends AbstractControllerExtensionDefinition<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     */
    public FireControllerExtensionDefinition ()
    {
        super (new FireControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<FireControlSurface, FireConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new FireControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }


    /** {@inheritDoc} */
    @Override
    public boolean isUsingBetaAPI ()
    {
        return true;
    }
}
