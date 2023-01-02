// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.utilities.midimonitor;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.utilities.midimonitor.MidiMonitorConfiguration;
import de.mossgrabers.controller.utilities.midimonitor.MidiMonitorDefinition;
import de.mossgrabers.controller.utilities.midimonitor.MidiMonitorSetup;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the MIDI Monitor.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiMonitorExtensionDefinition extends AbstractControllerExtensionDefinition<IControlSurface<MidiMonitorConfiguration>, MidiMonitorConfiguration>
{
    /**
     * Constructor.
     */
    public MidiMonitorExtensionDefinition ()
    {
        super (new MidiMonitorDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<IControlSurface<MidiMonitorConfiguration>, MidiMonitorConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new MidiMonitorSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
