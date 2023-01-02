// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.utilities.midimonitor;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the MIDI Monitor extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiMonitorDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("A897AFE1-E5C5-43F2-B7C7-761B39768A15");


    /**
     * Constructor.
     */
    public MidiMonitorDefinition ()
    {
        super (EXTENSION_ID, "Midi Monitor", "Utilities", 1, 0);
    }
}
