// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.definition;

import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.novation.launchpad.definition.button.LaunchpadButton;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Novation Launchpad Pro Mk3 controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchpadProMk3ControllerDefinition extends AbstractLaunchpadDefinition
{
    private static final UUID   EXTENSION_ID = UUID.fromString ("4EDED44C-7817-4C66-A334-66A9E342AAA0");
    private static final String SYSEX_HEADER = "F0 00 20 29 02 0E ";


    /**
     * Constructor.
     */
    public LaunchpadProMk3ControllerDefinition ()
    {
        super (EXTENSION_ID, "Launchpad Pro Mk3");

        this.buttonSetup.setButton (LaunchpadButton.SHIFT, 90);
        this.buttonSetup.setButton (LaunchpadButton.USER, 96);
        this.buttonSetup.setButton (LaunchpadButton.PROJECT, 98);

        this.buttonSetup.setButton (LaunchpadButton.SESSION, 93);
        this.buttonSetup.setButton (LaunchpadButton.NOTE, 94);
        this.buttonSetup.setButton (LaunchpadButton.DEVICE, 7);

        this.buttonSetup.setButton (LaunchpadButton.ARROW_UP, 80);
        this.buttonSetup.setButton (LaunchpadButton.ARROW_DOWN, 70);
        this.buttonSetup.setButton (LaunchpadButton.ARROW_LEFT, 91);
        this.buttonSetup.setButton (LaunchpadButton.ARROW_RIGHT, 92);

        this.buttonSetup.setButton (LaunchpadButton.CLICK, 3, true);
        this.buttonSetup.setButton (LaunchpadButton.UNDO, 1, true);
        this.buttonSetup.setButton (LaunchpadButton.DELETE, 60);
        this.buttonSetup.setButton (LaunchpadButton.QUANTIZE, 40);
        this.buttonSetup.setButton (LaunchpadButton.DUPLICATE, 50);
        this.buttonSetup.setButton (LaunchpadButton.PLAY, 20);
        this.buttonSetup.setButton (LaunchpadButton.RECORD, 10);

        this.buttonSetup.setButton (LaunchpadButton.REC_ARM, 1);
        this.buttonSetup.setButton (LaunchpadButton.MUTE, 2);
        this.buttonSetup.setButton (LaunchpadButton.SOLO, 3);
        this.buttonSetup.setButton (LaunchpadButton.VOLUME, 4);
        this.buttonSetup.setButton (LaunchpadButton.PAN, 5);
        this.buttonSetup.setButton (LaunchpadButton.SENDS, 6);
        this.buttonSetup.setButton (LaunchpadButton.STOP_CLIP, 8);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasTrackSelectionButtons ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            case WINDOWS:
                midiDiscoveryPairs.addAll (this.createWindowsDeviceDiscoveryPairs ("%sLPProMK3 MIDI", "%sLPProMK3 MIDI"));
                break;

            case LINUX:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad Pro MK3 MIDI 1", "Launchpad Pro MK3 MIDI 1"));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad Pro MK3 LPProMK3 MIDI", "Launchpad Pro MK3 LPProMK3 MIDI"));
                break;

            case MAC:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Launchpad Pro MK3 LPProMK3 MIDI", "Launchpad Pro MK3 LPProMK3 MIDI"));
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
    public void resetMode (final LaunchpadControlSurface surface)
    {
        surface.sendLaunchpadSysEx ("0E 00");
    }


    /** {@inheritDoc} */
    @Override
    public void setLogoColor (final LaunchpadControlSurface surface, final int color)
    {
        surface.sendLaunchpadSysEx ("03 00 63 " + StringUtils.toHexStr (color));
    }


    /** {@inheritDoc} */
    @Override
    public boolean sceneButtonsUseCC ()
    {
        return true;
    }
}
