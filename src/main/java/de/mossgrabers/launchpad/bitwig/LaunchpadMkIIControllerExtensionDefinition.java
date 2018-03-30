// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.bitwig;

import de.mossgrabers.framework.bitwig.BitwigSetupFactory;
import de.mossgrabers.framework.bitwig.configuration.SettingsUI;
import de.mossgrabers.framework.bitwig.daw.HostImpl;
import de.mossgrabers.framework.bitwig.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.launchpad.LaunchpadControllerDefinition;
import de.mossgrabers.launchpad.LaunchpadControllerSetup;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Launchpad MkII controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadMkIIControllerExtensionDefinition extends AbstractControllerExtensionDefinition
{
    /**
     * Constructor.
     */
    public LaunchpadMkIIControllerExtensionDefinition ()
    {
        super (new LaunchpadControllerDefinition (true));
    }


    /** {@inheritDoc} */
    @Override
    public void listAutoDetectionMidiPortNames (final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
    {
        this.addDeviceDiscoveryPair ("Launchpad MK2", list);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup getControllerSetup (final ControllerHost host)
    {
        return new LaunchpadControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUI (host.getPreferences ()), false);
    }
}
