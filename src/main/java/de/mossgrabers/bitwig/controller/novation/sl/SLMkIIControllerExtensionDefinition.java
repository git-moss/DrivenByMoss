// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.novation.sl;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.SLControllerDefinition;
import de.mossgrabers.controller.novation.sl.SLControllerSetup;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Novation SLmkII controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLMkIIControllerExtensionDefinition extends AbstractControllerExtensionDefinition<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     */
    public SLMkIIControllerExtensionDefinition ()
    {
        super (new SLControllerDefinition (true));
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<SLControlSurface, SLConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new SLControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()), true);
    }
}
