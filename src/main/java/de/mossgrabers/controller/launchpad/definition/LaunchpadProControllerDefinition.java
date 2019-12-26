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
 * Definition class for the Novation Launchpad Pro controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class LaunchpadProControllerDefinition extends DefaultControllerDefinition implements ILaunchpadControllerDefinition
{
    private static final UUID   EXTENSION_ID               = UUID.fromString ("80B63970-64F1-11E5-A837-0800200C9A66");
    private static final String SYSEX_HEADER               = "F0 00 20 29 02 10 ";

    public static final int     LAUNCHPAD_BUTTON_SHIFT     = 80;
    public static final int     LAUNCHPAD_BUTTON_CLICK     = 70;
    public static final int     LAUNCHPAD_BUTTON_UNDO      = 60;
    public static final int     LAUNCHPAD_BUTTON_DELETE    = 50;
    public static final int     LAUNCHPAD_BUTTON_QUANTIZE  = 40;
    public static final int     LAUNCHPAD_BUTTON_DUPLICATE = 30;
    public static final int     LAUNCHPAD_BUTTON_DOUBLE    = 20;
    public static final int     LAUNCHPAD_BUTTON_RECORD    = 10;

    public static final int     LAUNCHPAD_BUTTON_REC_ARM   = 1;
    public static final int     LAUNCHPAD_BUTTON_TRACK     = 2;
    public static final int     LAUNCHPAD_BUTTON_MUTE      = 3;
    public static final int     LAUNCHPAD_BUTTON_SOLO      = 4;
    public static final int     LAUNCHPAD_BUTTON_VOLUME    = 5;
    public static final int     LAUNCHPAD_BUTTON_PAN       = 6;
    public static final int     LAUNCHPAD_BUTTON_SENDS     = 7;
    public static final int     LAUNCHPAD_BUTTON_STOP_CLIP = 8;

    public static final int     LAUNCHPAD_BUTTON_UP        = 91;
    public static final int     LAUNCHPAD_BUTTON_DOWN      = 92;
    public static final int     LAUNCHPAD_BUTTON_LEFT      = 93;
    public static final int     LAUNCHPAD_BUTTON_RIGHT     = 94;
    public static final int     LAUNCHPAD_BUTTON_SESSION   = 95;
    public static final int     LAUNCHPAD_BUTTON_NOTE      = 96;
    public static final int     LAUNCHPAD_BUTTON_DEVICE    = 97;
    public static final int     LAUNCHPAD_BUTTON_USER      = 98;


    /**
     * Constructor.
     */
    public LaunchpadProControllerDefinition ()
    {
        super (EXTENSION_ID, "Launchpad Pro", "Novation", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            case WINDOWS:
                midiDiscoveryPairs.addAll (this.createWindowsDeviceDiscoveryPairs ("MIDIIN2 (%sLaunchpad Pro)", "MIDIOUT2 (%sLaunchpad Pro)"));
                break;

            case LINUX:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad Pro MIDI 2", "Launchpad Pro MIDI 2"));
                break;

            case MAC:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad Pro Standalone Port", "Launchpad Pro Standalone Port"));
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
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasFaderSupport ()
    {
        return true;
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
        return "2C 03";
    }


    /** {@inheritDoc} */
    @Override
    public String getFaderModeCommand ()
    {
        return "2C 02";
    }


    /** {@inheritDoc} */
    @Override
    public String getPanModeCommand ()
    {
        return this.getFaderModeCommand ();
    }


    /** {@inheritDoc} */
    @Override
    public void sendBlinkState (final IMidiOutput output, final int note, final int blinkColor, final boolean fast)
    {
        output.sendSysex (SYSEX_HEADER + "23 " + StringUtils.toHexStr (note) + " " + StringUtils.toHexStr (blinkColor) + " F7");
    }


    /** {@inheritDoc} */
    @Override
    public Map<ButtonID, Integer> getButtonIDs ()
    {
        final Map<ButtonID, Integer> buttonIDs = new EnumMap<> (ButtonID.class);
        buttonIDs.put (ButtonID.SHIFT, Integer.valueOf (LAUNCHPAD_BUTTON_SHIFT));

        buttonIDs.put (ButtonID.LEFT, Integer.valueOf (LAUNCHPAD_BUTTON_LEFT));
        buttonIDs.put (ButtonID.RIGHT, Integer.valueOf (LAUNCHPAD_BUTTON_RIGHT));
        buttonIDs.put (ButtonID.UP, Integer.valueOf (LAUNCHPAD_BUTTON_UP));
        buttonIDs.put (ButtonID.DOWN, Integer.valueOf (LAUNCHPAD_BUTTON_DOWN));

        buttonIDs.put (ButtonID.DELETE, Integer.valueOf (LAUNCHPAD_BUTTON_DELETE));
        buttonIDs.put (ButtonID.DUPLICATE, Integer.valueOf (LAUNCHPAD_BUTTON_DUPLICATE));

        buttonIDs.put (ButtonID.SOLO, Integer.valueOf (LAUNCHPAD_BUTTON_SOLO));
        buttonIDs.put (ButtonID.MUTE, Integer.valueOf (LAUNCHPAD_BUTTON_MUTE));

        buttonIDs.put (ButtonID.SESSION, Integer.valueOf (LAUNCHPAD_BUTTON_SESSION));
        buttonIDs.put (ButtonID.DEVICE, Integer.valueOf (LAUNCHPAD_BUTTON_DEVICE));
        buttonIDs.put (ButtonID.USER, Integer.valueOf (LAUNCHPAD_BUTTON_USER));
        buttonIDs.put (ButtonID.NOTE, Integer.valueOf (LAUNCHPAD_BUTTON_NOTE));

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
        return true;
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
                if (info.isFast ())
                    sbFlash.append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getBlinkColor ())).append (' ');
                else
                    sbPulse.append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getBlinkColor ())).append (' ');
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
