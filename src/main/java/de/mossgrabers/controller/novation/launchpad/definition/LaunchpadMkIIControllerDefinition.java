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
 * Definition class for the Novation Launchpad MkII controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadMkIIControllerDefinition extends AbstractLaunchpadDefinition
{
    private static final UUID   EXTENSION_ID = UUID.fromString ("4E01A0B0-67B1-11E5-A837-0800200C9A66");
    private static final String SYSEX_HEADER = "F0 00 20 29 02 18 ";


    /**
     * Constructor.
     */
    public LaunchpadMkIIControllerDefinition ()
    {
        super (EXTENSION_ID, "Launchpad MkII");

        this.buttonSetup.setButton (LaunchpadButton.SHIFT, 111);

        this.buttonSetup.setButton (LaunchpadButton.ARROW_UP, 104);
        this.buttonSetup.setButton (LaunchpadButton.ARROW_DOWN, 105);
        this.buttonSetup.setButton (LaunchpadButton.ARROW_LEFT, 106);
        this.buttonSetup.setButton (LaunchpadButton.ARROW_RIGHT, 107);

        this.buttonSetup.setButton (LaunchpadButton.SESSION, 108);
        this.buttonSetup.setButton (LaunchpadButton.NOTE, 109);
        this.buttonSetup.setButton (LaunchpadButton.DEVICE, 110);
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
    public void resetMode (final LaunchpadControlSurface surface)
    {
        // No hardware MIDI mode available
    }


    /** {@inheritDoc} */
    @Override
    public void setLogoColor (final LaunchpadControlSurface surface, final int color)
    {
        // No logo on the Mk II
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
