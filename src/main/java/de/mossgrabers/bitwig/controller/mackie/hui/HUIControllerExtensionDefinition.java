// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.mackie.hui;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.mackie.hui.HUIConfiguration;
import de.mossgrabers.controller.mackie.hui.HUIControllerDefinition;
import de.mossgrabers.controller.mackie.hui.HUIControllerSetup;
import de.mossgrabers.controller.mackie.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Mackie MCU protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HUIControllerExtensionDefinition extends AbstractControllerExtensionDefinition<HUIControlSurface, HUIConfiguration>
{
    private final int numHUIDevices;


    /**
     * Constructor.
     *
     * @param numHUIExtenders The number of supported extenders
     */
    HUIControllerExtensionDefinition (final int numHUIExtenders)
    {
        super (new HUIControllerDefinition (numHUIExtenders));
        this.numHUIDevices = numHUIExtenders + 1;
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<HUIControlSurface, HUIConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new HUIControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()), this.numHUIDevices);
    }
}
