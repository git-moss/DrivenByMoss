// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.electra.one;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.electra.one.ElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.ElectraOneControllerDefinition;
import de.mossgrabers.controller.electra.one.ElectraOneControllerSetup;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Electra.One extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ElectraOneControllerExtensionDefinition extends AbstractControllerExtensionDefinition<ElectraOneControlSurface, ElectraOneConfiguration>
{
    /**
     * Constructor.
     */
    public ElectraOneControllerExtensionDefinition ()
    {
        super (new ElectraOneControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<ElectraOneControlSurface, ElectraOneConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new ElectraOneControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
