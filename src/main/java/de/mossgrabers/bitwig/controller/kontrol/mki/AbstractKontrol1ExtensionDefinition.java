// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.kontrol.mki;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.kontrol.mki.Kontrol1ControllerDefinition;
import de.mossgrabers.controller.kontrol.mki.Kontrol1ControllerSetup;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for Native Instruments Komplete Kontrol 1 Sxx controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractKontrol1ExtensionDefinition extends AbstractControllerExtensionDefinition
{
    private final int modelIndex;


    /**
     * Constructor.
     *
     * @param modelIndex The index of the specific model
     */
    public AbstractKontrol1ExtensionDefinition (final int modelIndex)
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
    protected IControllerSetup<?, ?> getControllerSetup (final ControllerHost host)
    {
        return new Kontrol1ControllerSetup (this.modelIndex, new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
