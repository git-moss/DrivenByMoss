// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrol1.bitwig;

import de.mossgrabers.framework.bitwig.BitwigSetupFactory;
import de.mossgrabers.framework.bitwig.configuration.SettingsUI;
import de.mossgrabers.framework.bitwig.daw.HostImpl;
import de.mossgrabers.framework.bitwig.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.kontrol1.Kontrol1ControllerDefinition;
import de.mossgrabers.kontrol1.Kontrol1ControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for Native Instruments Komplete Kontrol 1 Sxx controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractKontrol1ExtensionDefinition extends AbstractControllerExtensionDefinition
{
    /**
     * Constructor.
     *
     * @param modelIndex The index of the specific model
     */
    public AbstractKontrol1ExtensionDefinition (final int modelIndex)
    {
        super (new Kontrol1ControllerDefinition (modelIndex));
    }


    /** {@inheritDoc} */
    @Override
    public boolean isUsingBetaAPI ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup getControllerSetup (final ControllerHost host)
    {
        return new Kontrol1ControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUI (host.getPreferences ()));
    }
}
