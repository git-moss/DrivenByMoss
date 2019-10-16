// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchkey.controller;

import de.mossgrabers.controller.launchkey.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.DeviceInquiry;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The Launchkey Mini Mk3 control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class LaunchkeyMiniMk3ControlSurface extends AbstractControlSurface<LaunchkeyMiniMk3Configuration>
{
    // Buttons & Knobs

    public static final int LAUNCHKEY_KNOB_1 = 0x15;
    public static final int LAUNCHKEY_KNOB_2 = 0x16;
    public static final int LAUNCHKEY_KNOB_3 = 0x17;
    public static final int LAUNCHKEY_KNOB_4 = 0x18;
    public static final int LAUNCHKEY_KNOB_5 = 0x19;
    public static final int LAUNCHKEY_KNOB_6 = 0x1A;
    public static final int LAUNCHKEY_KNOB_7 = 0x1B;
    public static final int LAUNCHKEY_KNOB_8 = 0x1C;

    public static final int LAUNCHKEY_RIGHT  = 0x66;
    public static final int LAUNCHKEY_LEFT   = 0x67;

    public static final int LAUNCHKEY_SCENE1 = 0x68;
    public static final int LAUNCHKEY_SCENE2 = 0x69;

    public static final int LAUNCHKEY_SHIFT  = 0x6C;
    public static final int LAUNCHKEY_PLAY   = 0x73;
    public static final int LAUNCHKEY_RECORD = 0x75;

    // Knob Modes
    public static final int KNOB_MODE_CUSTOM = 0;
    public static final int KNOB_MODE_VOLUME = 1;
    public static final int KNOB_MODE_PARAMS = 2;
    public static final int KNOB_MODE_PAN    = 3;
    public static final int KNOB_MODE_SEND1  = 4;
    public static final int KNOB_MODE_SEND2  = 5;

    // Pad Modes
    public static final int PAD_MODE_CUSTOM  = 0;
    public static final int PAD_MODE_DRUM    = 1;
    public static final int PAD_MODE_SESSION = 2;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The DAW midi output
     * @param input The DAW midi input
     * @param definition The Launchpad definition
     */
    public LaunchkeyMiniMk3ControlSurface (final IHost host, final ColorManager colorManager, final LaunchkeyMiniMk3Configuration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, new LaunchkeyPadGrid (colorManager, output));

        this.setTriggerId (ButtonID.SHIFT, LAUNCHKEY_SHIFT);

        this.input.setSysexCallback (this::handleSysEx);
        this.output.sendSysex (DeviceInquiry.createQuery ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShiftPressed ()
    {
        return super.isShiftPressed () && !this.isPressed (0x0F, LAUNCHKEY_LEFT) && !this.isPressed (0x0F, LAUNCHKEY_RIGHT);
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.setLaunchpadToDAW (false);

        super.shutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int channel, final int cc, final int state)
    {
        this.output.sendCCEx (channel, cc, state);
    }


    /**
     * Set the launchkey to DAW mode.
     */
    public void setLaunchpadToDAW (final boolean enable)
    {
        this.output.sendNoteEx (0x0F, 0x0C, enable ? 0x7F : 0x00);
    }


    /** {@inheritDoc} */
    @Override
    public int getSceneTrigger (final int index)
    {
        return 0; // TODO LAUNCHPAD_BUTTON_SCENE1 - 10 * index;
    }


    /**
     * Activate a Launchkey knob mode.
     *
     * @param knobMode A knob mode (use KNOB_MODE_* constants)
     */
    public void setKnobMode (final int knobMode)
    {
        this.output.sendCCEx (0x0F, 0x09, knobMode);
    }


    /**
     * Activate a Launchkey pad mode.
     *
     * @param padMode A pad mode (use PAD_MODE_* constants)
     */
    public void setPadMode (final int padMode)
    {
        this.output.sendCCEx (0x0F, 0x03, padMode);
    }


    private void handleSysEx (final String data)
    {
        final int [] byteData = StringUtils.fromHexStr (data);

        final DeviceInquiry deviceInquiry = new DeviceInquiry (byteData);
        if (deviceInquiry.isValid ())
            this.handleDeviceInquiryResponse (deviceInquiry);
    }


    /**
     * Handle the response of a device inquiry.
     *
     * @param deviceInquiry The parsed response
     */
    private void handleDeviceInquiryResponse (final DeviceInquiry deviceInquiry)
    {
        final int [] revisionLevel = deviceInquiry.getRevisionLevel ();
        if (revisionLevel.length == 4)
        {
            final String firmwareVersion = String.format ("%d%d%d%d", Integer.valueOf (revisionLevel[0]), Integer.valueOf (revisionLevel[1]), Integer.valueOf (revisionLevel[2]), Integer.valueOf (revisionLevel[3]));
            this.host.println ("Firmware version: " + (firmwareVersion.charAt (0) == '0' ? firmwareVersion.substring (1) : firmwareVersion));
        }
    }
}