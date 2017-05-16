// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.UUID;


/**
 * Definition class for the Launchpad 1 extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BeatstepProControllerExtensionDefinition extends BaseBeatstepControllerExtensionDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("7264A210-5EFE-11E5-A837-0800200C9A66");


    /** {@inheritDoc} */
    @Override
    public String getHardwareModel ()
    {
        return "Beatstep Pro";
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
        this.createDeviceDiscoveryPairs ("Arturia BeatStep Pro", list);
    }


    /** {@inheritDoc} */
    @Override
    public ControllerExtension createInstance (final ControllerHost host)
    {
        return new BeatstepControllerExtension (this, host, true);
    }
}
