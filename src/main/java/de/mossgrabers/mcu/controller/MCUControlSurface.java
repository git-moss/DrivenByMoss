// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.controller;

import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.midi.MidiInput;
import de.mossgrabers.framework.midi.MidiOutput;
import de.mossgrabers.mcu.MCUConfiguration;

import com.bitwig.extension.controller.api.ControllerHost;

import java.util.Arrays;


/**
 * A control surface which supports the MCU protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class MCUControlSurface extends AbstractControlSurface<MCUConfiguration>
{
    // Notes 0x90

    public static final int         MCU_ARM1              = 0x00;
    public static final int         MCU_ARM2              = 0x01;
    public static final int         MCU_ARM3              = 0x02;
    public static final int         MCU_ARM4              = 0x03;
    public static final int         MCU_ARM5              = 0x04;
    public static final int         MCU_ARM6              = 0x05;
    public static final int         MCU_ARM7              = 0x06;
    public static final int         MCU_ARM8              = 0x07;

    public static final int         MCU_SOLO1             = 0x08;
    public static final int         MCU_SOLO2             = 0x09;
    public static final int         MCU_SOLO3             = 0x0A;
    public static final int         MCU_SOLO4             = 0x0B;
    public static final int         MCU_SOLO5             = 0x0C;
    public static final int         MCU_SOLO6             = 0x0D;
    public static final int         MCU_SOLO7             = 0x0E;
    public static final int         MCU_SOLO8             = 0x0F;

    public static final int         MCU_MUTE1             = 0x10;
    public static final int         MCU_MUTE2             = 0x11;
    public static final int         MCU_MUTE3             = 0x12;
    public static final int         MCU_MUTE4             = 0x13;
    public static final int         MCU_MUTE5             = 0x14;
    public static final int         MCU_MUTE6             = 0x15;
    public static final int         MCU_MUTE7             = 0x16;
    public static final int         MCU_MUTE8             = 0x17;

    public static final int         MCU_SELECT1           = 0x18;
    public static final int         MCU_SELECT2           = 0x19;
    public static final int         MCU_SELECT3           = 0x1A;
    public static final int         MCU_SELECT4           = 0x1B;
    public static final int         MCU_SELECT5           = 0x1C;
    public static final int         MCU_SELECT6           = 0x1D;
    public static final int         MCU_SELECT7           = 0x1E;
    public static final int         MCU_SELECT8           = 0x1F;

    public static final int         MCU_VSELECT1          = 0x20;
    public static final int         MCU_VSELECT2          = 0x21;
    public static final int         MCU_VSELECT3          = 0x22;
    public static final int         MCU_VSELECT4          = 0x23;
    public static final int         MCU_VSELECT5          = 0x24;
    public static final int         MCU_VSELECT6          = 0x25;
    public static final int         MCU_VSELECT7          = 0x26;
    public static final int         MCU_VSELECT8          = 0x27;

    public static final int         MCU_MODE_IO           = 0x28;
    public static final int         MCU_MODE_SENDS        = 0x29;
    public static final int         MCU_MODE_PAN          = 0x2A;
    public static final int         MCU_MODE_PLUGIN       = 0x2B;
    public static final int         MCU_MODE_EQ           = 0x2C;
    public static final int         MCU_MODE_DYN          = 0x2D;
    public static final int         MCU_BANK_LEFT         = 0x2E;
    public static final int         MCU_BANK_RIGHT        = 0x2F;
    public static final int         MCU_TRACK_LEFT        = 0x30;
    public static final int         MCU_TRACK_RIGHT       = 0x31;

    public static final int         MCU_FLIP              = 0x32;
    public static final int         MCU_EDIT              = 0x33;
    public static final int         MCU_NAME_VALUE        = 0x34;
    public static final int         MCU_SMPTE_BEATS       = 0x35;

    public static final int         MCU_F1                = 0x36;
    public static final int         MCU_F2                = 0x37;
    public static final int         MCU_F3                = 0x38;
    public static final int         MCU_F4                = 0x39;
    public static final int         MCU_F5                = 0x3A;
    public static final int         MCU_F6                = 0x3B;
    public static final int         MCU_F7                = 0x3C;
    public static final int         MCU_F8                = 0x3D;

    public static final int         MCU_MIDI_TRACKS       = 0x3E;
    public static final int         MCU_INPUTS            = 0x3F;
    public static final int         MCU_AUDIO_TRACKS      = 0x40;
    public static final int         MCU_AUDIO_INSTR       = 0x41;
    public static final int         MCU_AUX               = 0x42;
    public static final int         MCU_BUSSES            = 0x43;
    public static final int         MCU_OUTPUTS           = 0x44;
    public static final int         MCU_USER              = 0x45;
    public static final int         MCU_SHIFT             = 0x46;
    public static final int         MCU_OPTION            = 0x47;
    public static final int         MCU_CONTROL           = 0x48;
    public static final int         MCU_ALT               = 0x49;
    public static final int         MCU_READ              = 0x4A;
    public static final int         MCU_WRITE             = 0x4B;
    public static final int         MCU_TRIM              = 0x4C;
    public static final int         MCU_TOUCH             = 0x4D;
    public static final int         MCU_LATCH             = 0x4E;
    public static final int         MCU_GROUP             = 0x4F;
    public static final int         MCU_SAVE              = 0x50;
    public static final int         MCU_UNDO              = 0x51;
    public static final int         MCU_CANCEL            = 0x52;
    public static final int         MCU_ENTER             = 0x53;
    public static final int         MCU_MARKER            = 0x54;
    public static final int         MCU_NUDGE             = 0x55;
    public static final int         MCU_REPEAT            = 0x56;
    public static final int         MCU_DROP              = 0x57;
    public static final int         MCU_REPLACE           = 0x58;
    public static final int         MCU_CLICK             = 0x59;
    public static final int         MCU_SOLO              = 0x5A;
    public static final int         MCU_REWIND            = 0x5B;
    public static final int         MCU_FORWARD           = 0x5C;
    public static final int         MCU_STOP              = 0x5D;
    public static final int         MCU_PLAY              = 0x5E;
    public static final int         MCU_RECORD            = 0x5F;
    public static final int         MCU_ARROW_UP          = 0x60;
    public static final int         MCU_ARROW_DOWN        = 0x61;
    public static final int         MCU_ARROW_LEFT        = 0x62;
    public static final int         MCU_ARROW_RIGHT       = 0x63;
    public static final int         MCU_ZOOM              = 0x64;
    public static final int         MCU_SCRUB             = 0x65;
    public static final int         MCU_USER_A            = 0x66;
    public static final int         MCU_USER_B            = 0x67;
    public static final int         MCU_FADER_TOUCH1      = 0x68;
    public static final int         MCU_FADER_TOUCH2      = 0x69;
    public static final int         MCU_FADER_TOUCH3      = 0x6A;
    public static final int         MCU_FADER_TOUCH4      = 0x6B;
    public static final int         MCU_FADER_TOUCH5      = 0x6C;
    public static final int         MCU_FADER_TOUCH6      = 0x6D;
    public static final int         MCU_FADER_TOUCH7      = 0x6E;
    public static final int         MCU_FADER_TOUCH8      = 0x6F;
    public static final int         MCU_FADER_MASTER      = 0x70;
    public static final int         MCU_SMPTE_LED         = 0x71;
    public static final int         MCU_BEATS_LED         = 0x72;
    public static final int         MCU_RUDE_SOLO_L       = 0x73;
    public static final int         MCU_RELAY_CLICK       = 0x76;

    // CC: 41-43 inc, 1-3 dec
    public static final int         MCU_CC_VPOT1          = 0x10;
    public static final int         MCU_CC_VPOT2          = 0x11;
    public static final int         MCU_CC_VPOT3          = 0x12;
    public static final int         MCU_CC_VPOT4          = 0x13;
    public static final int         MCU_CC_VPOT5          = 0x14;
    public static final int         MCU_CC_VPOT6          = 0x15;
    public static final int         MCU_CC_VPOT7          = 0x16;
    public static final int         MCU_CC_VPOT8          = 0x17;

    public static final int         MCU_CC_JOG            = 0x3C;

    // Sysex

    public static final int []      MCU_SYSEX_HEADER      = new int []
    {
        0xF0,
        0x00,
        0x00,
        0x66,
        0x14
    };

    public static final String      SYSEX_HDR             = "F0 00 00 66 14 ";

    public static final int         MCU_SYSEX_CMD_DISPLAY = 0x12;

    private static final int []     MCU_BUTTONS_ALL       =
    {
        MCU_ARM1,
        MCU_ARM2,
        MCU_ARM3,
        MCU_ARM4,
        MCU_ARM5,
        MCU_ARM6,
        MCU_ARM7,
        MCU_ARM8,
        MCU_SOLO1,
        MCU_SOLO2,
        MCU_SOLO3,
        MCU_SOLO4,
        MCU_SOLO5,
        MCU_SOLO6,
        MCU_SOLO7,
        MCU_SOLO8,
        MCU_MUTE1,
        MCU_MUTE2,
        MCU_MUTE3,
        MCU_MUTE4,
        MCU_MUTE5,
        MCU_MUTE6,
        MCU_MUTE7,
        MCU_MUTE8,
        MCU_SELECT1,
        MCU_SELECT2,
        MCU_SELECT3,
        MCU_SELECT4,
        MCU_SELECT5,
        MCU_SELECT6,
        MCU_SELECT7,
        MCU_SELECT8,
        MCU_VSELECT1,
        MCU_VSELECT2,
        MCU_VSELECT3,
        MCU_VSELECT4,
        MCU_VSELECT5,
        MCU_VSELECT6,
        MCU_VSELECT7,
        MCU_VSELECT8,
        MCU_FADER_TOUCH1,
        MCU_FADER_TOUCH2,
        MCU_FADER_TOUCH3,
        MCU_FADER_TOUCH4,
        MCU_FADER_TOUCH5,
        MCU_FADER_TOUCH6,
        MCU_FADER_TOUCH7,
        MCU_FADER_TOUCH8,
        MCU_FADER_MASTER,
        MCU_MODE_IO,
        MCU_MODE_SENDS,
        MCU_MODE_PAN,
        MCU_MODE_PLUGIN,
        MCU_MODE_EQ,
        MCU_MODE_DYN,
        MCU_BANK_LEFT,
        MCU_BANK_RIGHT,
        MCU_TRACK_LEFT,
        MCU_TRACK_RIGHT,
        MCU_FLIP,
        MCU_EDIT,
        MCU_NAME_VALUE,
        MCU_SMPTE_BEATS,
        MCU_MIDI_TRACKS,
        MCU_INPUTS,
        MCU_AUDIO_TRACKS,
        MCU_AUDIO_INSTR,
        MCU_SHIFT,
        MCU_OPTION,
        MCU_REWIND,
        MCU_FORWARD,
        MCU_STOP,
        MCU_PLAY,
        MCU_RECORD,
        MCU_ARROW_UP,
        MCU_ARROW_DOWN,
        MCU_ARROW_LEFT,
        MCU_ARROW_RIGHT,
        MCU_ZOOM,
        MCU_SCRUB,
        MCU_USER_A,
        MCU_USER_B,
        MCU_REPEAT,
        MCU_READ,
        MCU_WRITE,
        MCU_TRIM,
        MCU_TOUCH,
        MCU_LATCH,
        MCU_UNDO,
        MCU_USER,
        MCU_CLICK,
        MCU_SOLO,
        MCU_REPLACE,
        MCU_F1,
        MCU_F2,
        MCU_F3,
        MCU_F4,
        MCU_F5,
        MCU_F6,
        MCU_F7,
        MCU_F8,
        MCU_CANCEL,
        MCU_ENTER,
        MCU_SAVE,
        MCU_MARKER
    };

    private static final boolean [] MCU_BUTTON_UPDATE;
    static
    {
        MCU_BUTTON_UPDATE = new boolean [128];
        Arrays.fill (MCU_BUTTON_UPDATE, false);
    }

    public static final int   VUMODE_LED               = 1;
    public static final int   VUMODE_OFF               = 2;
    public static final int   VUMODE_LED_AND_LCD       = 3;
    public static final int   VUMODE_LCD               = 4;

    public static final int   KNOB_LED_MODE_SINGLE_DOT = 0;
    public static final int   KNOB_LED_MODE_BOOST_CUT  = 1;
    public static final int   KNOB_LED_MODE_WRAP       = 2;
    public static final int   KNOB_LED_MODE_SPREAD     = 3;

    private MCUDisplay        secondDisplay;
    private MCUSegmentDisplay segmentDisplay;
    private int               activeVuMode             = VUMODE_LED;
    private int []            knobValues               = new int [8];

    private final int         extenderOffset;
    private boolean           isMainDevice;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
     * @param extenderOffset The channel/bank offset if multiple extenders are used
     * @param isMainDevice True if it is the main MCU controller (and not an extender)
     */
    public MCUControlSurface (final ControllerHost host, final ColorManager colorManager, final MCUConfiguration configuration, final MidiOutput output, final MidiInput input, final int extenderOffset, final boolean isMainDevice)
    {
        super (host, configuration, colorManager, output, input, MCU_BUTTONS_ALL);

        this.extenderOffset = extenderOffset;
        this.isMainDevice = isMainDevice;

        this.shiftButtonId = MCU_SHIFT;
        this.selectButtonId = MCU_OPTION;
        this.leftButtonId = MCU_ARROW_LEFT;
        this.rightButtonId = MCU_ARROW_RIGHT;
        this.upButtonId = MCU_ARROW_UP;
        this.downButtonId = MCU_ARROW_DOWN;

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
        this.secondDisplay.shutdown ();
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
        return MCU_BUTTON_UPDATE[button];
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
                this.handleCC (0, data1, data2);
                break;

            case 0xB0:
                // Handle knobs and jog wheel
                this.handleCC (1, data1, data2);
                break;

            default:
                super.handleMidi (status, data1, data2);
                break;
        }
    }


    /**
     * Set the second display (only on icon QCon Pro X).
     *
     * @param secondDisplay The second display
     */
    public void setSecondDisplay (final MCUDisplay secondDisplay)
    {
        this.secondDisplay = secondDisplay;
    }


    /**
     * Get the second display (only on icon QCon Pro X).
     *
     * @return The second display
     */
    public MCUDisplay getSecondDisplay ()
    {
        return this.secondDisplay;
    }


    /**
     * Set the segment display.
     *
     * @param segmentDisplay The segment display
     */
    public void setSegmentDisplay (final MCUSegmentDisplay segmentDisplay)
    {
        this.segmentDisplay = segmentDisplay;
    }


    /**
     * Get the segment display.
     *
     * @return The segment display
     */
    public MCUSegmentDisplay getSegmentDisplay ()
    {
        return this.segmentDisplay;
    }


    /**
     * Get the channel/bank offset if multiple extenders are used.
     *
     * @return The offset 0, 8, 16 or 24
     */
    public int getExtenderOffset ()
    {
        return this.extenderOffset;
    }


    /** {@inheritDoc} */
    @Override
    protected void scheduledFlush ()
    {
        super.scheduledFlush ();
        if (this.secondDisplay != null)
            this.secondDisplay.flush ();
    }


    // leds on/leds and vu-meter on display on/vu-meter on display on/all off
    public void switchVuMode (final int mode)
    {
        // Always horizontal
        this.output.sendSysex (SYSEX_HDR + "21 00 F7");

        if (this.activeVuMode != mode)
        {
            if (this.activeVuMode < 5)
                this.activeVuMode = mode;
            else
                this.activeVuMode = VUMODE_LED;
        }
        final MidiOutput out = this.getOutput ();
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
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean isGridNote (final int note)
    {
        return false;
    }


    /**
     * True if it is the main device.
     *
     * @return Returns true if it is the main device
     */
    public boolean isMainDevice ()
    {
        return this.isMainDevice;
    }
}