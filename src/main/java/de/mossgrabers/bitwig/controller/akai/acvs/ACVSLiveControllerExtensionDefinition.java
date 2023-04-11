// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.akai.acvs;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.akai.acvs.ACVSConfiguration;
import de.mossgrabers.controller.akai.acvs.ACVSControllerDefinition;
import de.mossgrabers.controller.akai.acvs.ACVSControllerSetup;
import de.mossgrabers.controller.akai.acvs.controller.ACVSControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Akai devices supporting the ACVS protocol. Currently, the MPC Live I,
 * II, One, X and Force.
 *
 * @author Jürgen Moßgraber
 */
public class ACVSLiveControllerExtensionDefinition extends AbstractControllerExtensionDefinition<ACVSControlSurface, ACVSConfiguration>
{
    /**
     * Constructor.
     */
    public ACVSLiveControllerExtensionDefinition ()
    {
        super (new ACVSControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<ACVSControlSurface, ACVSConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new ACVSControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
