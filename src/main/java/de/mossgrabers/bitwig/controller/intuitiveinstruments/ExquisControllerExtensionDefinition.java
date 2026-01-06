// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.intuitiveinstruments;

import com.bitwig.extension.controller.api.ControllerHost;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisControllerDefinition;
import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisControllerSetup;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;


/**
 * Definition class for the Intuitive Instruments Exquis controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisControllerExtensionDefinition extends AbstractControllerExtensionDefinition<ExquisControlSurface, ExquisConfiguration>
{
    /**
     * Constructor.
     */
    public ExquisControllerExtensionDefinition ()
    {
        super (new ExquisControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<ExquisControlSurface, ExquisConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new ExquisControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
