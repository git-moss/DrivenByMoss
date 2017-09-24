// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu;

import de.mossgrabers.framework.controller.AbstractControllerExtensionDefinition;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtension;
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
        return "2.0";
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
    public ControllerExtension createInstance (final ControllerHost host)
    {
        return new MCUControllerExtension (this, host, this.numMCUDevices);
    }
}
