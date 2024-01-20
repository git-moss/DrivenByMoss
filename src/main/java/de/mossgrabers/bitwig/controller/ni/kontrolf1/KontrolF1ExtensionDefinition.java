// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.ni.kontrolf1;

import com.bitwig.extension.controller.api.ControllerHost;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.ni.kontrolf1.KontrolF1Configuration;
import de.mossgrabers.controller.ni.kontrolf1.KontrolF1ControllerDefinition;
import de.mossgrabers.controller.ni.kontrolf1.KontrolF1ControllerSetup;
import de.mossgrabers.controller.ni.kontrolf1.controller.KontrolF1ControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;


/**
 * Definition class for the KontrolF1 extension.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolF1ExtensionDefinition extends AbstractControllerExtensionDefinition<KontrolF1ControlSurface, KontrolF1Configuration>
{
    /**
     * Constructor.
     */
    public KontrolF1ExtensionDefinition ()
    {
        super (new KontrolF1ControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<KontrolF1ControlSurface, KontrolF1Configuration> getControllerSetup (final ControllerHost host)
    {
        return new KontrolF1ControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
