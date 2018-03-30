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

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.UUID;


/**
 * Definition class for the Mackie MCU protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
abstract class MCUControllerExtensionDefinition extends AbstractControllerExtensionDefinition
{
    private static final UUID []   EXTENSION_ID   =
    {
        UUID.fromString ("5F10A0CD-F866-41C0-B16A-AEA16282B657"),
        UUID.fromString ("7FF808DD-45DB-4026-AA6E-844ED8C05B55"),
        UUID.fromString ("8E0EDA26-ACB9-4F5E-94FB-B886C9468C7A"),
        UUID.fromString ("4923C47A-B1DC-48C8-AE89-A332AA26BA87")
    };

    private static final String [] HARDWARE_MODEL =
    {
        "Control Universal",
        "Control Universal + 1 Extender",
        "Control Universal + 2 Extenders",
        "Control Universal + 3 Extenders"
    };

    private final int              numMCUDevices;


    /**
     * Constructor.
     * 
     * @param numMCUExtenders The number of supported Extenders
     */
    MCUControllerExtensionDefinition (final int numMCUExtenders)
    {
        super (new MCUControllerDefinition (EXTENSION_ID[numMCUExtenders], HARDWARE_MODEL[numMCUExtenders], numMCUExtenders + 1));
        this.numMCUDevices = numMCUExtenders + 1;
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
        return new MCUControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUI (host.getPreferences ()), this.numMCUDevices);
    }
}
