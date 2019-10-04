// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.definition;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Definition class for the Novation Launchpad MkII controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadMkIIControllerDefinition extends DefaultControllerDefinition implements ILaunchpadControllerDefinition
{
    private static final UUID   EXTENSION_ID                  = UUID.fromString ("4E01A0B0-67B1-11E5-A837-0800200C9A66");
    private static final String SYSEX_HEADER                  = "F0 00 20 29 02 18 ";

    private static final int    LAUNCHPAD_MKII_BUTTON_UP      = 104;
    private static final int    LAUNCHPAD_MKII_BUTTON_DOWN    = 105;
    private static final int    LAUNCHPAD_MKII_BUTTON_LEFT    = 106;
    private static final int    LAUNCHPAD_MKII_BUTTON_RIGHT   = 107;
    private static final int    LAUNCHPAD_MKII_BUTTON_SESSION = 108;
    private static final int    LAUNCHPAD_MKII_BUTTON_USER1   = 109;
    private static final int    LAUNCHPAD_MKII_BUTTON_USER2   = 110;
    private static final int    LAUNCHPAD_MKII_BUTTON_MIXER   = 111;


    /**
     * Constructor.
     */
    public LaunchpadMkIIControllerDefinition ()
    {
        super (EXTENSION_ID, "Launchpad MkII", "Novation", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        midiDiscoveryPairs.addAll (this.createDeviceDiscoveryPairs ("Launchpad MK2"));
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
    public String getSysExHeader ()
    {
        return SYSEX_HEADER;
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
        buttonIDs.put (ButtonID.SHIFT, Integer.valueOf (LAUNCHPAD_MKII_BUTTON_MIXER));
        buttonIDs.put (ButtonID.LEFT, Integer.valueOf (LAUNCHPAD_MKII_BUTTON_LEFT));
        buttonIDs.put (ButtonID.RIGHT, Integer.valueOf (LAUNCHPAD_MKII_BUTTON_RIGHT));
        buttonIDs.put (ButtonID.UP, Integer.valueOf (LAUNCHPAD_MKII_BUTTON_UP));
        buttonIDs.put (ButtonID.DOWN, Integer.valueOf (LAUNCHPAD_MKII_BUTTON_DOWN));
        buttonIDs.put (ButtonID.SESSION, Integer.valueOf (LAUNCHPAD_MKII_BUTTON_SESSION));
        buttonIDs.put (ButtonID.DEVICE, Integer.valueOf (LAUNCHPAD_MKII_BUTTON_USER2));
        buttonIDs.put (ButtonID.NOTE, Integer.valueOf (LAUNCHPAD_MKII_BUTTON_USER1));
        return buttonIDs;
    }
}
