// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.generic;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.GenericFlexiControllerDefinition;
import de.mossgrabers.controller.generic.GenericFlexiControllerSetup;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Generic Flexi controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GenericFlexiControllerExtensionDefinition extends AbstractControllerExtensionDefinition<GenericFlexiControlSurface, GenericFlexiConfiguration>
{
    /**
     * Constructor.
     */
    public GenericFlexiControllerExtensionDefinition ()
    {
        super (new GenericFlexiControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<GenericFlexiControlSurface, GenericFlexiConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new GenericFlexiControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
