// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.novation.slmkiii;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.novation.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.novation.slmkiii.SLMkIIIControllerDefinition;
import de.mossgrabers.controller.novation.slmkiii.SLMkIIIControllerSetup;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Novation SLmkIII controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class SLMkIIIControllerExtensionDefinition extends AbstractControllerExtensionDefinition<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    /**
     * Constructor.
     */
    public SLMkIIIControllerExtensionDefinition ()
    {
        super (new SLMkIIIControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<SLMkIIIControlSurface, SLMkIIIConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new SLMkIIIControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
