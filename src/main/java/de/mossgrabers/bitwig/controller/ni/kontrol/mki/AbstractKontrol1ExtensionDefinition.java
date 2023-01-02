// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.ni.kontrol.mki;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.ni.kontrol.mki.Kontrol1Configuration;
import de.mossgrabers.controller.ni.kontrol.mki.Kontrol1ControllerDefinition;
import de.mossgrabers.controller.ni.kontrol.mki.Kontrol1ControllerSetup;
import de.mossgrabers.controller.ni.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for Native Instruments Komplete Kontrol 1 S-series controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractKontrol1ExtensionDefinition extends AbstractControllerExtensionDefinition<Kontrol1ControlSurface, Kontrol1Configuration>
{
    private final int modelIndex;


    /**
     * Constructor.
     *
     * @param modelIndex The index of the specific model
     */
    protected AbstractKontrol1ExtensionDefinition (final int modelIndex)
    {
        super (new Kontrol1ControllerDefinition (modelIndex));
        this.modelIndex = modelIndex;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isUsingBetaAPI ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<Kontrol1ControlSurface, Kontrol1Configuration> getControllerSetup (final ControllerHost host)
    {
        return new Kontrol1ControllerSetup (this.modelIndex, new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
