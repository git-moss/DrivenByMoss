// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.akai.apcmini;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.akai.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.akai.apcmini.APCminiControllerDefinition;
import de.mossgrabers.controller.akai.apcmini.APCminiControllerSetup;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Akai APCmini controller.
 *
 * @author Jürgen Moßgraber
 */
public class APCminiControllerExtensionDefinition extends AbstractControllerExtensionDefinition<APCminiControlSurface, APCminiConfiguration>
{
    /**
     * Constructor.
     */
    public APCminiControllerExtensionDefinition ()
    {
        super (new APCminiControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<APCminiControlSurface, APCminiConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new APCminiControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
