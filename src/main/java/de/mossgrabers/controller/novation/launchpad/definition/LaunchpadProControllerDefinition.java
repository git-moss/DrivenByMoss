// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.definition;

import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.novation.launchpad.definition.button.LaunchpadButton;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


/**
 * Definition class for the Novation Launchpad Pro controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchpadProControllerDefinition extends AbstractLaunchpadDefinition
{
    private static final UUID   EXTENSION_ID = UUID.fromString ("80B63970-64F1-11E5-A837-0800200C9A66");
    private static final String SYSEX_HEADER = "F0 00 20 29 02 10 ";


    /**
     * Constructor.
     */
    public LaunchpadProControllerDefinition ()
    {
        super (EXTENSION_ID, "Launchpad Pro");

        this.buttonSetup.setButton (LaunchpadButton.SHIFT, 80);
        this.buttonSetup.setButton (LaunchpadButton.USER, 98);

        this.buttonSetup.setButton (LaunchpadButton.CLICK, 70);
        this.buttonSetup.setButton (LaunchpadButton.UNDO, 60);
        this.buttonSetup.setButton (LaunchpadButton.DELETE, 50);
        this.buttonSetup.setButton (LaunchpadButton.QUANTIZE, 40);
        this.buttonSetup.setButton (LaunchpadButton.DUPLICATE, 30);
        // Uses the Double button
        this.buttonSetup.setButton (LaunchpadButton.PLAY, 20);
        this.buttonSetup.setButton (LaunchpadButton.RECORD, 10);

        this.buttonSetup.setButton (LaunchpadButton.REC_ARM, 1);
        this.buttonSetup.setButton (LaunchpadButton.TRACK_SELECT, 2);
        this.buttonSetup.setButton (LaunchpadButton.MUTE, 3);
        this.buttonSetup.setButton (LaunchpadButton.SOLO, 4);
        this.buttonSetup.setButton (LaunchpadButton.VOLUME, 5);
        this.buttonSetup.setButton (LaunchpadButton.PAN, 6);
        this.buttonSetup.setButton (LaunchpadButton.SENDS, 7);
        this.buttonSetup.setButton (LaunchpadButton.STOP_CLIP, 8);
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
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad Pro Standalone Port", "Launchpad Pro Standalone Port"));
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
    public void resetMode (final LaunchpadControlSurface surface)
    {
        surface.sendLaunchpadSysEx ("2C 00");
    }


    /** {@inheritDoc} */
    @Override
    public void setLogoColor (final LaunchpadControlSurface surface, final int color)
    {
        surface.sendLaunchpadSysEx ("0A 63 " + StringUtils.toHexStr (color));
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
        final String sysExHeader = this.getSysExHeader ();
        if (sbNormal.length () > 0)
            result.add (new StringBuilder (sysExHeader).append ("0A ").append (sbNormal).append ("F7").toString ());
        if (sbFlash.length () > 0)
            result.add (new StringBuilder (sysExHeader).append ("23 ").append (sbFlash).append ("F7").toString ());
        if (sbPulse.length () > 0)
            result.add (new StringBuilder (sysExHeader).append ("28 ").append (sbPulse).append ("F7").toString ());
        return result;
    }
}
