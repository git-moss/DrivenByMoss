// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.yaeltex.turn;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnConfiguration;
import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnControllerDefinition;
import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnControllerSetup;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Yaeltex Turn controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class YaeltexTurnControllerExtensionDefinition extends AbstractControllerExtensionDefinition<YaeltexTurnControlSurface, YaeltexTurnConfiguration>
{
    /**
     * Constructor.
     */
    public YaeltexTurnControllerExtensionDefinition ()
    {
        super (new YaeltexTurnControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<YaeltexTurnControlSurface, YaeltexTurnConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new YaeltexTurnControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
