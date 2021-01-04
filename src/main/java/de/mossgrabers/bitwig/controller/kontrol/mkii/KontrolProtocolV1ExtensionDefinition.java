// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.kontrol.mkii;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.kontrol.mkii.KontrolProtocolControllerDefinition;
import de.mossgrabers.controller.kontrol.mkii.KontrolProtocolControllerSetup;
import de.mossgrabers.controller.kontrol.mkii.controller.KontrolProtocol;
import de.mossgrabers.controller.kontrol.mkii.controller.KontrolProtocolDeviceDescriptorV1;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Extension definition class for devices supporting the Komplete Kontrol MIDI protocl version 1.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolProtocolV1ExtensionDefinition extends AbstractControllerExtensionDefinition
{
    /**
     * Constructor.
     */
    public KontrolProtocolV1ExtensionDefinition ()
    {
        super (new KontrolProtocolControllerDefinition (new KontrolProtocolDeviceDescriptorV1 ()));
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<?, ?> getControllerSetup (final ControllerHost host)
    {
        return new KontrolProtocolControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()), KontrolProtocol.VERSION_1);
    }
}
