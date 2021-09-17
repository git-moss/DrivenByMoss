// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.akai.acvs;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.akai.acvs.ACVSConfiguration;
import de.mossgrabers.controller.akai.acvs.ACVSControllerDefinition;
import de.mossgrabers.controller.akai.acvs.ACVSControllerSetup;
import de.mossgrabers.controller.akai.acvs.ACVSDevice;
import de.mossgrabers.controller.akai.acvs.controller.ACVSControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Akai MPC Live I and II.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MPCLiveControllerExtensionDefinition extends AbstractControllerExtensionDefinition<ACVSControlSurface, ACVSConfiguration>
{
    /**
     * Constructor.
     */
    public MPCLiveControllerExtensionDefinition ()
    {
        super (new ACVSControllerDefinition (ACVSDevice.MPC_LIVE));
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<ACVSControlSurface, ACVSConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new ACVSControllerSetup (ACVSDevice.MPC_LIVE, new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
