// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.controller;

import de.mossgrabers.controller.novation.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.DeviceInquiry;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The Novation SLmkIII control surface.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings("javadoc")
public class SLMkIIIControlSurface extends AbstractControlSurface<SLMkIIIConfiguration>
{
    public static final int MKIII_KNOB_1            = 0x15;
    public static final int MKIII_KNOB_2            = 0x16;
    public static final int MKIII_KNOB_3            = 0x17;
    public static final int MKIII_KNOB_4            = 0x18;
    public static final int MKIII_KNOB_5            = 0x19;
    public static final int MKIII_KNOB_6            = 0x1A;
    public static final int MKIII_KNOB_7            = 0x1B;
    public static final int MKIII_KNOB_8            = 0x1C;

    public static final int MKIII_FADER_1           = 0x29;
    public static final int MKIII_FADER_2           = 0x2A;
    public static final int MKIII_FADER_3           = 0x2B;
    public static final int MKIII_FADER_4           = 0x2C;
    public static final int MKIII_FADER_5           = 0x2D;
    public static final int MKIII_FADER_6           = 0x2E;
    public static final int MKIII_FADER_7           = 0x2F;
    public static final int MKIII_FADER_8           = 0x30;

    public static final int MKIII_DISPLAY_BUTTON_1  = 0x33;
    public static final int MKIII_DISPLAY_BUTTON_2  = 0x34;
    public static final int MKIII_DISPLAY_BUTTON_3  = 0x35;
    public static final int MKIII_DISPLAY_BUTTON_4  = 0x36;
    public static final int MKIII_DISPLAY_BUTTON_5  = 0x37;
    public static final int MKIII_DISPLAY_BUTTON_6  = 0x38;
    public static final int MKIII_DISPLAY_BUTTON_7  = 0x39;
    public static final int MKIII_DISPLAY_BUTTON_8  = 0x3A;

    public static final int MKIII_BUTTON_ROW1_1     = 0x3B;
    public static final int MKIII_BUTTON_ROW1_2     = 0x3C;
    public static final int MKIII_BUTTON_ROW1_3     = 0x3D;
    public static final int MKIII_BUTTON_ROW1_4     = 0x3E;
    public static final int MKIII_BUTTON_ROW1_5     = 0x3F;
    public static final int MKIII_BUTTON_ROW1_6     = 0x40;
    public static final int MKIII_BUTTON_ROW1_7     = 0x41;
    public static final int MKIII_BUTTON_ROW1_8     = 0x42;

    public static final int MKIII_BUTTON_ROW2_1     = 0x43;
    public static final int MKIII_BUTTON_ROW2_2     = 0x44;
    public static final int MKIII_BUTTON_ROW2_3     = 0x45;
    public static final int MKIII_BUTTON_ROW2_4     = 0x46;
    public static final int MKIII_BUTTON_ROW2_5     = 0x47;
    public static final int MKIII_BUTTON_ROW2_6     = 0x48;
    public static final int MKIII_BUTTON_ROW2_7     = 0x49;
    public static final int MKIII_BUTTON_ROW2_8     = 0x4A;

    public static final int MKIII_DISPLAY_UP        = 0x51;
    public static final int MKIII_DISPLAY_DOWN      = 0x52;
    public static final int MKIII_SCENE_1           = 0x53;
    public static final int MKIII_SCENE_2           = 0x54;
    public static final int MKIII_SCENE_UP          = 0x55;
    public static final int MKIII_SCENE_DOWN        = 0x56;

    public static final int MKIII_BUTTONS_UP        = 0x57;
    public static final int MKIII_BUTTONS_DOWN      = 0x58;

    public static final int MKIII_GRID              = 0x59;
    public static final int MKIII_OPTIONS           = 0x5A;
    public static final int MKIII_SHIFT             = 0x5B;
    public static final int MKIII_DUPLICATE         = 0x5C;
    public static final int MKIII_CLEAR             = 0x5D;

    public static final int MKIII_TRACK_LEFT        = 0x66;
    public static final int MKIII_TRACK_RIGHT       = 0x67;

    public static final int MKIII_TRANSPORT_REWIND  = 0x70;
    public static final int MKIII_TRANSPORT_FORWARD = 0x71;
    public static final int MKIII_TRANSPORT_STOP    = 0x72;
    public static final int MKIII_TRANSPORT_PLAY    = 0x73;
    public static final int MKIII_TRANSPORT_LOOP    = 0x74;
    public static final int MKIII_TRANSPORT_RECORD  = 0x75;

    public static final int MKIII_FADER_LED_1       = 0x36;

    public static final int MKIII_BUTTON_STATE_OFF  = 0;
    public static final int MKIII_BUTTON_STATE_ON   = 1;

    private boolean         isMuteSolo              = true;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     * @param lightGuide The light guide
     */
    public SLMkIIIControlSurface (final IHost host, final ColorManager colorManager, final SLMkIIIConfiguration configuration, final IMidiOutput output, final IMidiInput input, final SLMkIIILightGuide lightGuide)
    {
        super (0, host, configuration, colorManager, output, input, new SLMkIIIPadGrid (colorManager, output), lightGuide, 1000, 360);

        this.defaultMidiChannel = 15;

        this.addTextDisplay (new SLMkIIIDisplay (host, output));

        this.input.setSysexCallback (this::handleSysEx);

        ((SLMkIIILightGuide) this.lightGuide).setActive (true);
    }


    /**
     * Handle incoming system exclusive data.
     *
     * @param data The data
     */
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


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        super.internalShutdown ();

        final SLMkIIIDisplay d = this.getDisplay ();
        for (int i = 0; i < 8; i++)
            d.setFaderLEDColor (MKIII_FADER_LED_1 + i, ColorEx.BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public SLMkIIIDisplay getDisplay ()
    {
        return (SLMkIIIDisplay) super.getDisplay ();
    }


    /**
     * Check if mute/solo or monitor/record arm is active.
     *
     * @return True if mute/solo is active
     */
    public boolean isMuteSolo ()
    {
        return this.isMuteSolo;
    }


    /**
     * Toggle if mute/solo or monitor/racarm is active.
     */
    public void toggleMuteSolo ()
    {
        this.isMuteSolo = !this.isMuteSolo;
    }
}