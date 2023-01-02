// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.arturia.beatstep;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.arturia.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.arturia.beatstep.BeatstepControllerDefinition;
import de.mossgrabers.controller.arturia.beatstep.BeatstepControllerSetup;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Beatstep controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BeatstepControllerExtensionDefinition extends AbstractControllerExtensionDefinition<BeatstepControlSurface, BeatstepConfiguration>
{
    /**
     * Constructor.
     */
    public BeatstepControllerExtensionDefinition ()
    {
        super (new BeatstepControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<BeatstepControlSurface, BeatstepConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new BeatstepControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
