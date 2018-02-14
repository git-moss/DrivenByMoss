// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apcmini;

import de.mossgrabers.framework.controller.AbstractControllerExtensionDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.framework.daw.bitwig.BitwigSetupFactory;
import de.mossgrabers.framework.daw.bitwig.HostProxy;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.UUID;


/**
 * Definition class for the Akai APCmini controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCminiControllerExtensionDefinition extends AbstractControllerExtensionDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("E7E02A80-3657-11E4-8C21-0800200C9A66");


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "APCmini4Bitwig";
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareVendor ()
    {
        return "Akai";
    }


    /** {@inheritDoc} */
    @Override
    public String getVersion ()
    {
        return "5.02";
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareModel ()
    {
        return "APCmini";
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
        this.createDeviceDiscoveryPairs ("APC MINI", list);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup getControllerSetup (final ControllerHost host)
    {
        return new APCminiControllerSetup (new HostProxy (host), new BitwigSetupFactory (host), host.getPreferences ());
    }
}
