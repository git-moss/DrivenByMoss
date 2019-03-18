// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.hui.controller;

import de.mossgrabers.controller.hui.HUIConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Arrays;


/**
 * A control surface which supports the HUI protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class HUIControlSurface extends AbstractControlSurface<HUIConfiguration>
{
    // TODO adapt to HUI

    // Notes 0x90

    public static final int         HUI_ARM1              = 0x00;
    public static final int         HUI_ARM2              = 0x01;
    public static final int         HUI_ARM3              = 0x02;
    public static final int         HUI_ARM4              = 0x03;
    public static final int         HUI_ARM5              = 0x04;
    public static final int         HUI_ARM6              = 0x05;
    public static final int         HUI_ARM7              = 0x06;
    public static final int         HUI_ARM8              = 0x07;

    public static final int         HUI_SOLO1             = 0x08;
    public static final int         HUI_SOLO2             = 0x09;
    public static final int         HUI_SOLO3             = 0x0A;
    public static final int         HUI_SOLO4             = 0x0B;
    public static final int         HUI_SOLO5             = 0x0C;
    public static final int         HUI_SOLO6             = 0x0D;
    public static final int         HUI_SOLO7             = 0x0E;
    public static final int         HUI_SOLO8             = 0x0F;

    public static final int         HUI_MUTE1             = 0x10;
    public static final int         HUI_MUTE2             = 0x11;
    public static final int         HUI_MUTE3             = 0x12;
    public static final int         HUI_MUTE4             = 0x13;
    public static final int         HUI_MUTE5             = 0x14;
    public static final int         HUI_MUTE6             = 0x15;
    public static final int         HUI_MUTE7             = 0x16;
    public static final int         HUI_MUTE8             = 0x17;

    public static final int         HUI_SELECT1           = 0x18;
    public static final int         HUI_SELECT2           = 0x19;
    public static final int         HUI_SELECT3           = 0x1A;
    public static final int         HUI_SELECT4           = 0x1B;
    public static final int         HUI_SELECT5           = 0x1C;
    public static final int         HUI_SELECT6           = 0x1D;
    public static final int         HUI_SELECT7           = 0x1E;
    public static final int         HUI_SELECT8           = 0x1F;

    public static final int         HUI_VSELECT1          = 0x20;
    public static final int         HUI_VSELECT2          = 0x21;
    public static final int         HUI_VSELECT3          = 0x22;
    public static final int         HUI_VSELECT4          = 0x23;
    public static final int         HUI_VSELECT5          = 0x24;
    public static final int         HUI_VSELECT6          = 0x25;
    public static final int         HUI_VSELECT7          = 0x26;
    public static final int         HUI_VSELECT8          = 0x27;

    public static final int         HUI_MODE_IO           = 0x28;
    public static final int         HUI_MODE_SENDS        = 0x29;
    public static final int         HUI_MODE_PAN          = 0x2A;
    public static final int         HUI_MODE_PLUGIN       = 0x2B;
    public static final int         HUI_MODE_EQ           = 0x2C;
    public static final int         HUI_MODE_DYN          = 0x2D;
    public static final int         HUI_BANK_LEFT         = 0x2E;
    public static final int         HUI_BANK_RIGHT        = 0x2F;
    public static final int         HUI_TRACK_LEFT        = 0x30;
    public static final int         HUI_TRACK_RIGHT       = 0x31;

    public static final int         HUI_FLIP              = 0x32;
    public static final int         HUI_EDIT              = 0x33;
    public static final int         HUI_NAME_VALUE        = 0x34;
    public static final int         HUI_SMPTE_BEATS       = 0x35;

    public static final int         HUI_F1                = 0x36;
    public static final int         HUI_F2                = 0x37;
    public static final int         HUI_F3                = 0x38;
    public static final int         HUI_F4                = 0x39;
    public static final int         HUI_F5                = 0x3A;
    public static final int         HUI_F6                = 0x3B;
    public static final int         HUI_F7                = 0x3C;
    public static final int         HUI_F8                = 0x3D;

    public static final int         HUI_MIDI_TRACKS       = 0x3E;
    public static final int         HUI_INPUTS            = 0x3F;
    public static final int         HUI_AUDIO_TRACKS      = 0x40;
    public static final int         HUI_AUDIO_INSTR       = 0x41;
    public static final int         HUI_AUX               = 0x42;
    public static final int         HUI_BUSSES            = 0x43;
    public static final int         HUI_OUTPUTS           = 0x44;
    public static final int         HUI_USER              = 0x45;
    public static final int         HUI_SHIFT             = 0x46;
    public static final int         HUI_OPTION            = 0x47;
    public static final int         HUI_CONTROL           = 0x48;
    public static final int         HUI_ALT               = 0x49;
    public static final int         HUI_READ              = 0x4A;
    public static final int         HUI_WRITE             = 0x4B;
    public static final int         HUI_TRIM              = 0x4C;
    public static final int         HUI_TOUCH             = 0x4D;
    public static final int         HUI_LATCH             = 0x4E;
    public static final int         HUI_GROUP             = 0x4F;
    public static final int         HUI_SAVE              = 0x50;
    public static final int         HUI_UNDO              = 0x51;
    public static final int         HUI_CANCEL            = 0x52;
    public static final int         HUI_ENTER             = 0x53;
    public static final int         HUI_MARKER            = 0x54;
    public static final int         HUI_NUDGE             = 0x55;
    public static final int         HUI_REPEAT            = 0x56;
    public static final int         HUI_DROP              = 0x57;
    public static final int         HUI_REPLACE           = 0x58;
    public static final int         HUI_CLICK             = 0x59;
    public static final int         HUI_SOLO              = 0x5A;
    public static final int         HUI_REWIND            = 0x5B;
    public static final int         HUI_FORWARD           = 0x5C;
    public static final int         HUI_STOP              = 0x5D;
    public static final int         HUI_PLAY              = 0x5E;
    public static final int         HUI_RECORD            = 0x5F;
    public static final int         HUI_ARROW_UP          = 0x60;
    public static final int         HUI_ARROW_DOWN        = 0x61;
    public static final int         HUI_ARROW_LEFT        = 0x62;
    public static final int         HUI_ARROW_RIGHT       = 0x63;
    public static final int         HUI_ZOOM              = 0x64;
    public static final int         HUI_SCRUB             = 0x65;
    public static final int         HUI_USER_A            = 0x66;
    public static final int         HUI_USER_B            = 0x67;
    public static final int         HUI_FADER_TOUCH1      = 0x68;
    public static final int         HUI_FADER_TOUCH2      = 0x69;
    public static final int         HUI_FADER_TOUCH3      = 0x6A;
    public static final int         HUI_FADER_TOUCH4      = 0x6B;
    public static final int         HUI_FADER_TOUCH5      = 0x6C;
    public static final int         HUI_FADER_TOUCH6      = 0x6D;
    public static final int         HUI_FADER_TOUCH7      = 0x6E;
    public static final int         HUI_FADER_TOUCH8      = 0x6F;
    public static final int         HUI_FADER_MASTER      = 0x70;
    public static final int         HUI_SMPTE_LED         = 0x71;
    public static final int         HUI_BEATS_LED         = 0x72;
    public static final int         HUI_RUDE_SOLO_L       = 0x73;
    public static final int         HUI_RELAY_CLICK       = 0x76;

    // CC: 41-43 inc, 1-3 dec
    public static final int         HUI_CC_VPOT1          = 0x10;
    public static final int         HUI_CC_VPOT2          = 0x11;
    public static final int         HUI_CC_VPOT3          = 0x12;
    public static final int         HUI_CC_VPOT4          = 0x13;
    public static final int         HUI_CC_VPOT5          = 0x14;
    public static final int         HUI_CC_VPOT6          = 0x15;
    public static final int         HUI_CC_VPOT7          = 0x16;
    public static final int         HUI_CC_VPOT8          = 0x17;

    public static final int         HUI_CC_JOG            = 0x3C;

    // Sysex

    public static final int []      HUI_SYSEX_HEADER      = new int []
    {
        0xF0,
        0x00,
        0x00,
        0x66,
        0x14
    };

    public static final String      SYSEX_HDR             = "F0 00 00 66 14 ";

    public static final int         HUI_SYSEX_CMD_DISPLAY = 0x12;

    private static final int []     HUI_BUTTONS_ALL       =
    {
        HUI_ARM1,
        HUI_ARM2,
        HUI_ARM3,
        HUI_ARM4,
        HUI_ARM5,
        HUI_ARM6,
        HUI_ARM7,
        HUI_ARM8,
        HUI_SOLO1,
        HUI_SOLO2,
        HUI_SOLO3,
        HUI_SOLO4,
        HUI_SOLO5,
        HUI_SOLO6,
        HUI_SOLO7,
        HUI_SOLO8,
        HUI_MUTE1,
        HUI_MUTE2,
        HUI_MUTE3,
        HUI_MUTE4,
        HUI_MUTE5,
        HUI_MUTE6,
        HUI_MUTE7,
        HUI_MUTE8,
        HUI_SELECT1,
        HUI_SELECT2,
        HUI_SELECT3,
        HUI_SELECT4,
        HUI_SELECT5,
        HUI_SELECT6,
        HUI_SELECT7,
        HUI_SELECT8,
        HUI_VSELECT1,
        HUI_VSELECT2,
        HUI_VSELECT3,
        HUI_VSELECT4,
        HUI_VSELECT5,
        HUI_VSELECT6,
        HUI_VSELECT7,
        HUI_VSELECT8,
        HUI_FADER_TOUCH1,
        HUI_FADER_TOUCH2,
        HUI_FADER_TOUCH3,
        HUI_FADER_TOUCH4,
        HUI_FADER_TOUCH5,
        HUI_FADER_TOUCH6,
        HUI_FADER_TOUCH7,
        HUI_FADER_TOUCH8,
        HUI_FADER_MASTER,
        HUI_MODE_IO,
        HUI_MODE_SENDS,
        HUI_MODE_PAN,
        HUI_MODE_PLUGIN,
        HUI_MODE_EQ,
        HUI_MODE_DYN,
        HUI_BANK_LEFT,
        HUI_BANK_RIGHT,
        HUI_TRACK_LEFT,
        HUI_TRACK_RIGHT,
        HUI_FLIP,
        HUI_EDIT,
        HUI_NAME_VALUE,
        HUI_SMPTE_BEATS,
        HUI_MIDI_TRACKS,
        HUI_INPUTS,
        HUI_AUDIO_TRACKS,
        HUI_AUDIO_INSTR,
        HUI_SHIFT,
        HUI_OPTION,
        HUI_REWIND,
        HUI_FORWARD,
        HUI_STOP,
        HUI_PLAY,
        HUI_RECORD,
        HUI_ARROW_UP,
        HUI_ARROW_DOWN,
        HUI_ARROW_LEFT,
        HUI_ARROW_RIGHT,
        HUI_ZOOM,
        HUI_SCRUB,
        HUI_USER_A,
        HUI_USER_B,
        HUI_REPEAT,
        HUI_READ,
        HUI_WRITE,
        HUI_TRIM,
        HUI_TOUCH,
        HUI_LATCH,
        HUI_UNDO,
        HUI_USER,
        HUI_CLICK,
        HUI_SOLO,
        HUI_REPLACE,
        HUI_F1,
        HUI_F2,
        HUI_F3,
        HUI_F4,
        HUI_F5,
        HUI_F6,
        HUI_F7,
        HUI_F8,
        HUI_CANCEL,
        HUI_ENTER,
        HUI_SAVE,
        HUI_MARKER,
        HUI_AUX,
        HUI_BUSSES,
        HUI_OUTPUTS,
        HUI_GROUP,
        HUI_NUDGE,
        HUI_DROP
    };

    private static final boolean [] HUI_BUTTON_UPDATE;
    static
    {
        HUI_BUTTON_UPDATE = new boolean [128];
        Arrays.fill (HUI_BUTTON_UPDATE, false);
    }

    public static final int   VUMODE_LED               = 1;
    public static final int   VUMODE_OFF               = 2;
    public static final int   VUMODE_LED_AND_LCD       = 3;
    public static final int   VUMODE_LCD               = 4;

    public static final int   KNOB_LED_MODE_SINGLE_DOT = 0;
    public static final int   KNOB_LED_MODE_BOOST_CUT  = 1;
    public static final int   KNOB_LED_MODE_WRAP       = 2;
    public static final int   KNOB_LED_MODE_SPREAD     = 3;

    private HUISegmentDisplay segmentDisplay;
    private int               activeVuMode             = VUMODE_LED;
    private int []            knobValues               = new int [8];


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
     */
    public HUIControlSurface (final IHost host, final ColorManager colorManager, final HUIConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, null, HUI_BUTTONS_ALL);

        this.shiftButtonId = HUI_SHIFT;
        this.selectButtonId = HUI_OPTION;
        this.leftButtonId = HUI_ARROW_LEFT;
        this.rightButtonId = HUI_ARROW_RIGHT;
        this.upButtonId = HUI_ARROW_UP;
        this.downButtonId = HUI_ARROW_DOWN;

        Arrays.fill (this.knobValues, -1);
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        // Turn off all buttons
        for (final int button: this.getButtons ())
            this.setButton (button, 0);

        this.display.shutdown ();
        this.segmentDisplay.shutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public void setButton (final int button, final int state)
    {
        this.output.sendNote (button, state);
    }


    /**
     * Check if a button should be updated by the main update routine.
     *
     * @param button The button to check
     * @return True if it should be updated
     */
    public boolean shouldUpdateButton (final int button)
    {
        return HUI_BUTTON_UPDATE[button];
    }


    /**
     * Sets the LED ring of the knobs.
     *
     * @param index The index of the knob (0-7)
     * @param knobLEDMode The mode, use constants
     * @param value The value to set
     * @param maxValue The maximum possible value
     */
    public void setKnobLED (final int index, final int knobLEDMode, final int value, final int maxValue)
    {
        final int rescale = (int) Math.round (value * 11.0 / maxValue);
        int v = knobLEDMode << 4;
        v += rescale;

        if (this.knobValues[index] == v)
            return;

        this.knobValues[index] = v;
        this.output.sendCC (0x30 + index, v);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleMidi (final int status, final int data1, final int data2)
    {
        final int code = status & 0xF0;
        switch (code)
        {
            // Note on/off
            case 0x80:
            case 0x90:
                // Reroute all notes to CC buttons
                // TODO
                // this.handleCC (0, data1, data2);
                break;

            case 0xB0:
                // Handle knobs and jog wheel
                this.handleCC (1, data1, data2);

                host.println (status + ":" + StringUtils.toHexStr (data1) + ":" + StringUtils.toHexStr (data2));

                // 176:0F:0E REC
                // 176:2F:45
                // 176:0F:0E
                // 176:2F:05
                // >
                // 176:0F:0F LOOP
                // 176:2F:43
                // 176:0F:0F
                // 176:2F:03
                // >
                // 176:0F:0E PLAY
                // 176:2F:44
                // 176:0F:0E
                // 176:2F:04
                // >
                // 176:0F:0E STOP
                // 176:2F:43
                // 176:0F:0E
                // 176:2F:03
                // >
                // 176:0F:0E FFWD
                // 176:2F:42
                // 176:0F:0E
                // 176:2F:02
                // >
                // 176:0F:0E BACK
                // 176:2F:41
                // 176:0F:0E
                // 176:2F:01

                break;

            default:
                super.handleMidi (status, data1, data2);
                break;
        }
    }


    /**
     * Set the segment display.
     *
     * @param segmentDisplay The segment display
     */
    public void setSegmentDisplay (final HUISegmentDisplay segmentDisplay)
    {
        this.segmentDisplay = segmentDisplay;
    }


    /**
     * Get the segment display.
     *
     * @return The segment display
     */
    public HUISegmentDisplay getSegmentDisplay ()
    {
        return this.segmentDisplay;
    }


    // leds on/leds and vu-meter on display on/vu-meter on display on/all off
    public void switchVuMode (final int mode)
    {
        // Always horizontal
        this.output.sendSysex (new StringBuilder (SYSEX_HDR).append ("21 00 F7").toString ());

        if (this.activeVuMode != mode)
        {
            if (this.activeVuMode < 5)
                this.activeVuMode = mode;
            else
                this.activeVuMode = VUMODE_LED;
        }
        final IMidiOutput out = this.getOutput ();
        // the mcu changes the vu-meter mode when receiving the
        // corresponding sysex message
        switch (this.activeVuMode)
        {
            case VUMODE_LED:
                for (int i = 0; i < 8; i++)
                {
                    // resets the leds (and vu-meters on the display?)
                    out.sendChannelAftertouch (0 + (i << 4), 0);
                    out.sendSysex (SYSEX_HDR + "20 0" + i + " 01 F7");
                }
                break;
            case VUMODE_LED_AND_LCD:
                for (int i = 0; i < 8; i++)
                {
                    out.sendChannelAftertouch (0 + (i << 4), 0);
                    out.sendSysex (SYSEX_HDR + "20 0" + i + " 03 F7");
                }
                break;
            case VUMODE_LCD:
                for (int i = 0; i < 8; i++)
                {
                    out.sendChannelAftertouch (0 + (i << 4), 0);
                    out.sendSysex (SYSEX_HDR + "20 0" + i + " 06 F7");
                }
                break;
            case VUMODE_OFF:
                for (int i = 0; i < 8; i++)
                {
                    out.sendChannelAftertouch (0 + (i << 4), 0);
                    out.sendSysex (SYSEX_HDR + "20 0" + i + " 00 F7");
                }
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean isGridNote (final int note)
    {
        return false;
    }
}