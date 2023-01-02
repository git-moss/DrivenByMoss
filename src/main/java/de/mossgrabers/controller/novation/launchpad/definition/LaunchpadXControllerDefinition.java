// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.definition;

import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Novation Launchpad X controller extension.
 *
 * Note: If the DAW mode on the X is selected to use faders one cannot use the program mode (and
 * mode buttons cannot be configured), therefore we implement the faders ourselves!
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadXControllerDefinition extends AbstractLaunchpadDefinition
{
    private static final UUID   EXTENSION_ID = UUID.fromString ("CD196CCF-DF98-4AB0-9ABC-F0F29A60ACED");
    private static final String SYSEX_HEADER = "F0 00 20 29 02 0C ";


    /**
     * Constructor.
     */
    public LaunchpadXControllerDefinition ()
    {
        super (EXTENSION_ID, "Launchpad X");
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            case WINDOWS:
                midiDiscoveryPairs.addAll (this.createWindowsDeviceDiscoveryPairs ("MIDIIN2 (%sLPX MIDI)", "MIDIOUT2 (%sLPX MIDI)"));
                break;

            case LINUX:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad X MIDI 2", "Launchpad X MIDI 2"));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad X LPX MIDI Out", "Launchpad X LPX MIDI In"));
                break;

            case MAC:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad X LPX MIDI Out", "Launchpad X LPX MIDI In"));
                break;

            default:
                // Not supported
                break;
        }
        return midiDiscoveryPairs;
    }


    /** {@inheritDoc} */
    @Override
    public String getSysExHeader ()
    {
        return SYSEX_HEADER;
    }


    /** {@inheritDoc} */
    @Override
    public String getStandaloneModeCommand ()
    {
        return "10 00";
    }


    /** {@inheritDoc} */
    @Override
    public String getProgramModeCommand ()
    {
        return "0E 01";
    }
}
