// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.bitwig;

import de.mossgrabers.framework.bitwig.BitwigSetupFactory;
import de.mossgrabers.framework.bitwig.configuration.SettingsUI;
import de.mossgrabers.framework.bitwig.daw.HostImpl;
import de.mossgrabers.framework.bitwig.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.mcu.MCUControllerDefinition;
import de.mossgrabers.mcu.MCUControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Mackie MCU protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
abstract class MCUControllerExtensionDefinition extends AbstractControllerExtensionDefinition
{
    private final int numMCUDevices;


    /**
     * Constructor.
     *
     * @param numMCUExtenders The number of supported Extenders
     */
    MCUControllerExtensionDefinition (final int numMCUExtenders)
    {
        super (new MCUControllerDefinition (numMCUExtenders));
        this.numMCUDevices = numMCUExtenders + 1;
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup getControllerSetup (final ControllerHost host)
    {
        return new MCUControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUI (host.getPreferences ()), this.numMCUDevices);
    }
}
