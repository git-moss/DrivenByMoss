// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.definition;

import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


/**
 * Definition class for the Novation Launchpad MkII controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadMkIIControllerDefinition extends DefaultControllerDefinition implements ILaunchpadControllerDefinition
{
    private static final UUID   EXTENSION_ID             = UUID.fromString ("4E01A0B0-67B1-11E5-A837-0800200C9A66");
    private static final String SYSEX_HEADER             = "F0 00 20 29 02 18 ";

    private static final int    LAUNCHPAD_BUTTON_UP      = 104;
    private static final int    LAUNCHPAD_BUTTON_DOWN    = 105;
    private static final int    LAUNCHPAD_BUTTON_LEFT    = 106;
    private static final int    LAUNCHPAD_BUTTON_RIGHT   = 107;
    private static final int    LAUNCHPAD_BUTTON_SESSION = 108;
    private static final int    LAUNCHPAD_BUTTON_USER1   = 109;
    private static final int    LAUNCHPAD_BUTTON_USER2   = 110;
    private static final int    LAUNCHPAD_BUTTON_MIXER   = 111;


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
        return "21 01";
    }


    /** {@inheritDoc} */
    @Override
    public String getProgramModeCommand ()
    {
        return "22 00";
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
        buttonIDs.put (ButtonID.SHIFT, Integer.valueOf (LAUNCHPAD_BUTTON_MIXER));
        buttonIDs.put (ButtonID.LEFT, Integer.valueOf (LAUNCHPAD_BUTTON_LEFT));
        buttonIDs.put (ButtonID.RIGHT, Integer.valueOf (LAUNCHPAD_BUTTON_RIGHT));
        buttonIDs.put (ButtonID.UP, Integer.valueOf (LAUNCHPAD_BUTTON_UP));
        buttonIDs.put (ButtonID.DOWN, Integer.valueOf (LAUNCHPAD_BUTTON_DOWN));
        buttonIDs.put (ButtonID.SESSION, Integer.valueOf (LAUNCHPAD_BUTTON_SESSION));
        buttonIDs.put (ButtonID.DEVICE, Integer.valueOf (LAUNCHPAD_BUTTON_USER2));
        buttonIDs.put (ButtonID.NOTE, Integer.valueOf (LAUNCHPAD_BUTTON_USER1));

        buttonIDs.put (ButtonID.SCENE1, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1));
        buttonIDs.put (ButtonID.SCENE2, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2));
        buttonIDs.put (ButtonID.SCENE3, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3));
        buttonIDs.put (ButtonID.SCENE4, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4));
        buttonIDs.put (ButtonID.SCENE5, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5));
        buttonIDs.put (ButtonID.SCENE6, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6));
        buttonIDs.put (ButtonID.SCENE7, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7));
        buttonIDs.put (ButtonID.SCENE8, Integer.valueOf (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8));
        return buttonIDs;
    }


    /** {@inheritDoc} */
    @Override
    public boolean sceneButtonsUseCC ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public List<String> buildLEDUpdate (final Map<Integer, LightInfo> padInfos)
    {
        final StringBuilder sbNormal = new StringBuilder ();
        final StringBuilder sbFlash = new StringBuilder ();
        final StringBuilder sbPulse = new StringBuilder ();

        for (final Entry<Integer, LightInfo> e: padInfos.entrySet ())
        {
            final int note = e.getKey ().intValue ();
            final LightInfo info = e.getValue ();

            sbNormal.append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getColor ())).append (' ');

            if (info.getBlinkColor () > 0)
            {
                // Note: The MkII has an additional prefixed 00 instead of the Pro!
                if (info.isFast ())
                    sbFlash.append ("00 ").append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getBlinkColor ())).append (' ');
                else
                    sbPulse.append ("00 ").append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getBlinkColor ())).append (' ');
            }
        }

        final List<String> result = new ArrayList<> (3);
        if (sbNormal.length () > 0)
            result.add (new StringBuilder (this.getSysExHeader ()).append ("0A ").append (sbNormal).append ("F7").toString ());
        if (sbFlash.length () > 0)
            result.add (new StringBuilder (this.getSysExHeader ()).append ("23 ").append (sbFlash).append ("F7").toString ());
        if (sbPulse.length () > 0)
            result.add (new StringBuilder (this.getSysExHeader ()).append ("28 ").append (sbPulse).append ("F7").toString ());
        return result;
    }
}
