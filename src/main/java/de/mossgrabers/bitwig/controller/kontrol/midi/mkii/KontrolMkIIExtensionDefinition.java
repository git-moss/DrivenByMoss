// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.kontrol.midi.mkii;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUI;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.kontrol.midi.mkii.KontrolMkIIControllerDefinition;
import de.mossgrabers.controller.kontrol.midi.mkii.KontrolMkIIControllerSetup;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Komplete Kontrol MkII extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolMkIIExtensionDefinition extends AbstractControllerExtensionDefinition
{
    /**
     * Constructor.
     */
    public KontrolMkIIExtensionDefinition ()
    {
        super (new KontrolMkIIControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup getControllerSetup (final ControllerHost host)
    {
        return new KontrolMkIIControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUI (host.getPreferences ()));
    }
}
