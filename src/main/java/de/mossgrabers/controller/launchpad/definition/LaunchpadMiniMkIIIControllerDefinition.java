// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.definition;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.controller.grid.PadInfo;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


/**
 * Definition class for the Novation Launchpad Mini MkIII controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadMiniMkIIIControllerDefinition extends DefaultControllerDefinition implements ILaunchpadControllerDefinition
{
    private static final UUID   EXTENSION_ID             = UUID.fromString ("A17B269D-2641-452F-B5A2-81BBACDA0D17");
    private static final String SYSEX_HEADER             = "F0 00 20 29 02 0D ";

    private static final int    LAUNCHPAD_BUTTON_UP      = 91;
    private static final int    LAUNCHPAD_BUTTON_DOWN    = 92;
    private static final int    LAUNCHPAD_BUTTON_LEFT    = 93;
    private static final int    LAUNCHPAD_BUTTON_RIGHT   = 94;
    private static final int    LAUNCHPAD_BUTTON_SESSION = 95;
    private static final int    LAUNCHPAD_BUTTON_DRUMS   = 96;
    private static final int    LAUNCHPAD_BUTTON_KEYS    = 97;
    private static final int    LAUNCHPAD_BUTTON_USER    = 98;


    /**
     * Constructor.
     */
    public LaunchpadMiniMkIIIControllerDefinition ()
    {
        super (EXTENSION_ID, "Launchpad Mini MkIII", "Novation", 1, 1);
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
    public boolean isPro ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasFaderSupport ()
    {
        return false;
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
    public String getFaderModeCommand ()
    {
        return this.getProgramModeCommand ();
    }


    /** {@inheritDoc} */
    @Override
    public String getPanModeCommand ()
    {
        return this.getProgramModeCommand ();
    }


    /** {@inheritDoc} */
    @Override
    public void sendBlinkState (final IMidiOutput output, final int note, final int blinkColor, final boolean fast)
    {
        // Start blinking on channel 2, stop it on channel 1
        output.sendNoteEx (blinkColor == 0 ? 1 : 2, note, blinkColor);
    }


    /** {@inheritDoc} */
    @Override
    public Map<ButtonID, Integer> getButtonIDs ()
    {
        final Map<ButtonID, Integer> buttonIDs = new EnumMap<> (ButtonID.class);
        buttonIDs.put (ButtonID.SHIFT, Integer.valueOf (LAUNCHPAD_BUTTON_USER));

        buttonIDs.put (ButtonID.LEFT, Integer.valueOf (LAUNCHPAD_BUTTON_LEFT));
        buttonIDs.put (ButtonID.RIGHT, Integer.valueOf (LAUNCHPAD_BUTTON_RIGHT));
        buttonIDs.put (ButtonID.UP, Integer.valueOf (LAUNCHPAD_BUTTON_UP));
        buttonIDs.put (ButtonID.DOWN, Integer.valueOf (LAUNCHPAD_BUTTON_DOWN));

        buttonIDs.put (ButtonID.SESSION, Integer.valueOf (LAUNCHPAD_BUTTON_SESSION));
        buttonIDs.put (ButtonID.NOTE, Integer.valueOf (LAUNCHPAD_BUTTON_DRUMS));
        buttonIDs.put (ButtonID.DEVICE, Integer.valueOf (LAUNCHPAD_BUTTON_KEYS));
        return buttonIDs;
    }


    /** {@inheritDoc} */
    @Override
    public boolean sceneButtonsUseCC ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public List<String> buildLEDUpdate (final Map<Integer, PadInfo> padInfos)
    {
        final StringBuilder sb = new StringBuilder (this.getSysExHeader ()).append ("03 ");
        for (final Entry<Integer, PadInfo> e: padInfos.entrySet ())
        {
            final int note = e.getKey ().intValue ();
            final PadInfo info = e.getValue ();

            if (info.getBlinkColor () <= 0)
            {
                // 00h: Static colour from palette, Lighting data is 1 byte specifying palette
                // entry.
                sb.append ("00 ").append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getColor ())).append (' ');
            }
            else
            {
                if (info.isFast ())
                {
                    // 01h: Flashing colour, Lighting data is 2 bytes specifying Colour B and
                    // Colour A.
                    sb.append ("01 ").append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getBlinkColor ())).append (' ').append (StringUtils.toHexStr (info.getColor ())).append (' ');
                }
                else
                {
                    // 02h: Pulsing colour, Lighting data is 1 byte specifying palette entry.
                    sb.append ("02 ").append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getColor ())).append (' ');
                }
            }
        }
        return Collections.singletonList (sb.append ("F7").toString ());
    }
}
