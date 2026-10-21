// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.electra.one.mini;

import com.bitwig.extension.controller.api.ControllerHost;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.electra.one.mini.ElectraOneMiniConfiguration;
import de.mossgrabers.controller.electra.one.mini.ElectraOneMiniControllerDefinition;
import de.mossgrabers.controller.electra.one.mini.ElectraOneMiniControllerSetup;
import de.mossgrabers.controller.electra.one.mini.controller.ElectraOneMiniControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;


/**
 * Definition class for the Electra.One Mini extension.
 *
 * @author Jürgen Moßgraber
 */
public class ElectraOneMiniControllerExtensionDefinition extends AbstractControllerExtensionDefinition<ElectraOneMiniControlSurface, ElectraOneMiniConfiguration>
{
    /**
     * Constructor.
     */
    public ElectraOneMiniControllerExtensionDefinition ()
    {
        super (new ElectraOneMiniControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<ElectraOneMiniControlSurface, ElectraOneMiniConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new ElectraOneMiniControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
