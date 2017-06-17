// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.UUID;


/**
 * Definition class for the Novation SLmkII controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLMkIIControllerExtensionDefinition extends SLControllerExtensionDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("D1CEE920-1E51-11E4-8C21-0800200C9A66");


    /** {@inheritDoc} */
    @Override
    public String getHardwareModel ()
    {
        return "SL MkII";
    }


    /** {@inheritDoc} */
    @Override
    public UUID getId ()
    {
        return EXTENSION_ID;
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
    public ControllerExtension createInstance (final ControllerHost host)
    {
        return new SLControllerExtension (this, host, false);
    }
}
