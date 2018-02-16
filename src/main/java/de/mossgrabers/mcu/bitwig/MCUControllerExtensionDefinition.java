// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.bitwig;

import de.mossgrabers.framework.bitwig.BitwigSetupFactory;
import de.mossgrabers.framework.bitwig.configuration.SettingsUI;
import de.mossgrabers.framework.bitwig.daw.HostProxy;
import de.mossgrabers.framework.bitwig.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.mcu.MCUControllerSetup;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
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
     * @param numExtenders The number of extenders to support
     */
    MCUControllerExtensionDefinition (final int numExtenders)
    {
        this.numMCUDevices = numExtenders + 1;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "MCU4Bitwig";
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareVendor ()
    {
        return "Mackie";
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareModel ()
    {
        return "Control Universal";
    }


    /** {@inheritDoc} */
    @Override
    public String getVersion ()
    {
        return "2.5";
    }


    /** {@inheritDoc} */
    @Override
    public int getNumMidiInPorts ()
    {
        return this.numMCUDevices;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumMidiOutPorts ()
    {
        return this.numMCUDevices;
    }


    /** {@inheritDoc} */
    @Override
    public void listAutoDetectionMidiPortNames (final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
    {
        // If names are added for the different number of extenders Bitwig offers them with EVERY
        // start of the program, so we leave that to manual config
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup getControllerSetup (final ControllerHost host)
    {
        return new MCUControllerSetup (new HostProxy (host), new BitwigSetupFactory (host), new SettingsUI (host.getPreferences ()), this.numMCUDevices);
    }
}
