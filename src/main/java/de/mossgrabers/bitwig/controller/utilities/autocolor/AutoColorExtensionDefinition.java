// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.utilities.autocolor;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.utilities.autocolor.AutoColorConfiguration;
import de.mossgrabers.controller.utilities.autocolor.AutoColorDefinition;
import de.mossgrabers.controller.utilities.autocolor.AutoColorSetup;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for Auto Color.
 *
 * @author Jürgen Moßgraber
 */
public class AutoColorExtensionDefinition extends AbstractControllerExtensionDefinition<IControlSurface<AutoColorConfiguration>, AutoColorConfiguration>
{
    /**
     * Constructor.
     */
    public AutoColorExtensionDefinition ()
    {
        super (new AutoColorDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<IControlSurface<AutoColorConfiguration>, AutoColorConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new AutoColorSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
