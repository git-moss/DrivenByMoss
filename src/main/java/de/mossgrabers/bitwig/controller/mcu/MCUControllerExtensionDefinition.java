// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.mcu;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.mcu.MCUControllerDefinition;
import de.mossgrabers.controller.mcu.MCUControllerSetup;
import de.mossgrabers.framework.controller.IControllerSetup;

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
    protected IControllerSetup<?, ?> getControllerSetup (final ControllerHost host)
    {
        return new MCUControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()), this.numMCUDevices);
    }
}
