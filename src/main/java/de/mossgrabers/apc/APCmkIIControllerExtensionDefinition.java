// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.UUID;


/**
 * Definition class for the APC40 mkII controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCmkIIControllerExtensionDefinition extends APCControllerExtensionDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("14787D10-35DE-11E4-8C21-0800200C9A66");


    /** {@inheritDoc} */
    @Override
    public String getHardwareModel ()
    {
        return "APC mkII";
    }


    /** {@inheritDoc} */
    @Override
    public UUID getId ()
    {
        return EXTENSION_ID;
    }


    /** {@inheritDoc} */
    @Override
    public void listAutoDetectionMidiPortNames (final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
    {
        this.addDeviceDiscoveryPair ("APC40 mkII", list);
    }


    /** {@inheritDoc} */
    @Override
    public ControllerExtension createInstance (final ControllerHost host)
    {
        return new APCControllerExtension (this, host, true);
    }
}
