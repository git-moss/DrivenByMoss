// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.faderfox.ec4;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.faderfox.ec4.EC4Configuration;
import de.mossgrabers.controller.faderfox.ec4.EC4ControllerDefinition;
import de.mossgrabers.controller.faderfox.ec4.EC4ControllerSetup;
import de.mossgrabers.controller.faderfox.ec4.controller.EC4ControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for Faderfox EC4 controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EC4ControllerExtensionDefinition extends AbstractControllerExtensionDefinition<EC4ControlSurface, EC4Configuration>
{
    /**
     * Constructor.
     */
    public EC4ControllerExtensionDefinition ()
    {
        super (new EC4ControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean isUsingBetaAPI ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<EC4ControlSurface, EC4Configuration> getControllerSetup (final ControllerHost host)
    {
        return new EC4ControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
