// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.gamepad;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.gamepad.GamepadConfiguration;
import de.mossgrabers.controller.gamepad.GamepadControllerDefinition;
import de.mossgrabers.controller.gamepad.GamepadControllerSetup;
import de.mossgrabers.controller.gamepad.controller.GamepadControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Gamepad controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GamepadControllerExtensionDefinition extends AbstractControllerExtensionDefinition<GamepadControlSurface, GamepadConfiguration>
{
    /**
     * Constructor.
     */
    public GamepadControllerExtensionDefinition ()
    {
        super (new GamepadControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<GamepadControlSurface, GamepadConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new GamepadControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
