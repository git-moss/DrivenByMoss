// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.mackie.mcu;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.MCUControllerDefinition;
import de.mossgrabers.controller.mackie.mcu.MCUControllerSetup;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Mackie MCU protocol.
 *
 * @author Jürgen Moßgraber
 */
abstract class MCUControllerExtensionDefinition extends AbstractControllerExtensionDefinition<MCUControlSurface, MCUConfiguration>
{
    private final int numMCUDevices;


    /**
     * Constructor.
     *
     * @param numMCUExtenders The number of supported extenders
     */
    MCUControllerExtensionDefinition (final int numMCUExtenders)
    {
        super (new MCUControllerDefinition (numMCUExtenders));
        this.numMCUDevices = numMCUExtenders + 1;
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<MCUControlSurface, MCUConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new MCUControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()), this.numMCUDevices);
    }
}
