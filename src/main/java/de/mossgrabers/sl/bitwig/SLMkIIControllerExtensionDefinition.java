// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.bitwig;

import de.mossgrabers.framework.bitwig.BitwigSetupFactory;
import de.mossgrabers.framework.bitwig.configuration.SettingsUI;
import de.mossgrabers.framework.bitwig.daw.HostImpl;
import de.mossgrabers.framework.bitwig.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.sl.SLControllerDefinition;
import de.mossgrabers.sl.SLControllerSetup;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Novation SLmkII controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLMkIIControllerExtensionDefinition extends AbstractControllerExtensionDefinition
{
    /**
     * Constructor.
     */
    public SLMkIIControllerExtensionDefinition ()
    {
        super (new SLControllerDefinition (true));
    }


    /** {@inheritDoc} */
    @Override
    public void listAutoDetectionMidiPortNames (final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
    {
        if (platformType == PlatformType.MAC)
        {
            list.add (new String []
            {
                "SL MkII MIDI 2",
                "SL MkII MIDI 1"
            }, new String []
            {
                "SL MkII MIDI 2"
            });
        }
        else
        {
            // WINDOWS + MAC
            list.add (new String []
            {
                "MIDIIN2 (SL MkII)",
                "SL MkII"
            }, new String []
            {
                "MIDIOUT2 (SL MkII)"
            });
        }
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup getControllerSetup (final ControllerHost host)
    {
        return new SLControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUI (host.getPreferences ()), true);
    }
}
