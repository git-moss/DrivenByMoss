// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.oxi.one;

import com.bitwig.extension.controller.api.ControllerHost;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.OxiOneControllerDefinition;
import de.mossgrabers.controller.oxi.one.OxiOneControllerSetup;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;


/**
 * Definition class for the OXI One.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneControllerExtensionDefinition extends AbstractControllerExtensionDefinition<OxiOneControlSurface, OxiOneConfiguration>
{
    /**
     * Constructor.
     */
    public OxiOneControllerExtensionDefinition ()
    {
        super (new OxiOneControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<OxiOneControlSurface, OxiOneConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new OxiOneControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }


    /** {@inheritDoc} */
    @Override
    public boolean isUsingBetaAPI ()
    {
        return true;
    }
}
