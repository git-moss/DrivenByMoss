// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.ni.maschine.mk3;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.ni.maschine.Maschine;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineControllerSetup;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineStudioControllerDefinition;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the NI Maschine Studio controller.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineStudioExtensionDefinition extends AbstractControllerExtensionDefinition<MaschineControlSurface, MaschineConfiguration>
{
    /**
     * Constructor.
     */
    public MaschineStudioExtensionDefinition ()
    {
        super (new MaschineStudioControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<MaschineControlSurface, MaschineConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new MaschineControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()), Maschine.STUDIO);
    }
}
