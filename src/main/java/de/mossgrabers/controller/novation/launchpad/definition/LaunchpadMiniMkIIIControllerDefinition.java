// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.definition;

import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Definition class for the Novation Launchpad Mini MkIII controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchpadMiniMkIIIControllerDefinition extends AbstractLaunchpadDefinition
{
    private static final UUID             EXTENSION_ID     = UUID.fromString ("A17B269D-2641-452F-B5A2-81BBACDA0D17");
    private static final String           SYSEX_HEADER     = "F0 00 20 29 02 0D ";
    private static final Optional<String> BRIGHTNESS_SYSEX = Optional.of ("F0 00 20 29 02 0D 08 %02X F7");


    /**
     * Constructor.
     */
    public LaunchpadMiniMkIIIControllerDefinition ()
    {
        super (EXTENSION_ID, "Launchpad Mini MkIII");
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            case WINDOWS:
                midiDiscoveryPairs.addAll (this.createWindowsDeviceDiscoveryPairs ("MIDIIN2 (%sLPMiniMK3 MIDI)", "MIDIOUT2 (%sLPMiniMK3 MIDI)"));
                break;

            case LINUX:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad Mini MK3 MIDI 2", "Launchpad Mini MK3 MIDI 2"));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad Mini MK3 LPMiniMK3 MI", "Launchpad Mini MK3 LPMiniMK3 MI"));
                break;

            case MAC:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad Mini MK3 LPMiniMK3 MIDI Out", "Launchpad Mini MK3 LPMiniMK3 MIDI In"));
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


    /** {@inheritDoc} */
    @Override
    public Optional<String> getBrightnessSysex ()
    {
        return BRIGHTNESS_SYSEX;
    }
}
