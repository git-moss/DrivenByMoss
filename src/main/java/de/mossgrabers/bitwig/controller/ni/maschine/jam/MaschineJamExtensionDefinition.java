// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.ni.maschine.jam;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamControllerDefinition;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamControllerSetup;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the NI Maschine Jam controller.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamExtensionDefinition extends AbstractControllerExtensionDefinition<MaschineJamControlSurface, MaschineJamConfiguration>
{
    /**
     * Constructor.
     */
    public MaschineJamExtensionDefinition ()
    {
        super (new MaschineJamControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<MaschineJamControlSurface, MaschineJamConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new MaschineJamControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
