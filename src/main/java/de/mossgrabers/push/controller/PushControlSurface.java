// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller;

import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.midi.MidiInput;
import de.mossgrabers.framework.midi.MidiOutput;
import de.mossgrabers.push.PushConfiguration;

import com.bitwig.extension.controller.api.ControllerHost;

import java.util.Arrays;


/**
 * The Push 1 and Push 2 control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushControlSurface extends AbstractControlSurface<PushConfiguration>
{
    private static final int        PUSH1_IDENTITY_MIN_LENGTH = 35;
    private static final int []     PUSH1_ID                  = new int []
    {
        0xF0,
        0x7E,
        0x00,
        0x06,
        0x02,
        0x47,
        0x15
    };

    private static final int        PUSH2_IDENTITY_MIN_LENGTH = 21;
    private static final int []     PUSH2_ID                  = new int []
    {
        0xF0,
        0x7E,
        0x01,
        0x06,
        0x02,
        0x00
    };

    /** The names for the dynamic curves. */
    public static final String []   PUSH_PAD_CURVES_NAME      =
    {
        "Linear",
        "Log 1 (Default)",
        "Log 2",
        "Log 3",
        "Log 4",
        "Log 5"
    };

    /** The names for the pad thresholds. */
    public static final String []   PUSH_PAD_THRESHOLDS_NAME  =
    {
        "-20",
        "-19",
        "-18",
        "-17",
        "-16",
        "-15",
        "-14",
        "-13",
        "-12",
        "-11",
        "-10",
        "-9",
        "-8",
        "-7",
        "-6",
        "-5",
        "-4",
        "-3",
        "-2",
        "-1",
        "0 (Default)",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20"
    };

    /** The tap button. */
    public static final int         PUSH_BUTTON_TAP           = 3;
    /** The metronome button. */
    public static final int         PUSH_BUTTON_METRONOME     = 9;
    /** The small knob 1. */
    public static final int         PUSH_SMALL_KNOB1          = 14;
    /** The small knob 2. */
    public static final int         PUSH_SMALL_KNOB2          = 15;
    /** The button 1 in row 1. */
    public static final int         PUSH_BUTTON_ROW1_1        = 20;
    /** The button 2 in row 1. */
    public static final int         PUSH_BUTTON_ROW1_2        = 21;
    /** The button 3 in row 1. */
    public static final int         PUSH_BUTTON_ROW1_3        = 22;
    /** The button 4 in row 1. */
    public static final int         PUSH_BUTTON_ROW1_4        = 23;
    /** The button 5 in row 1. */
    public static final int         PUSH_BUTTON_ROW1_5        = 24;
    /** The button 6 in row 1. */
    public static final int         PUSH_BUTTON_ROW1_6        = 25;
    /** The button 7 in row 1. */
    public static final int         PUSH_BUTTON_ROW1_7        = 26;
    /** The button 8 in row 1. */
    public static final int         PUSH_BUTTON_ROW1_8        = 27;
    /** The master button. */
    public static final int         PUSH_BUTTON_MASTER        = 28;
    /** The clip stop button. */
    public static final int         PUSH_BUTTON_CLIP_STOP     = 29;
    /** The setup button - only Push 2. */
    public static final int         PUSH_BUTTON_SETUP         = 30;
    /** The layout button - only Push 2. */
    public static final int         PUSH_BUTTON_LAYOUT        = 31;
    /** The convert button - only Push 2. */
    public static final int         PUSH_BUTTON_CONVERT       = 35;
    /** The scene 1 button. */
    public static final int         PUSH_BUTTON_SCENE1        = 36;       // 1/4
    /** The scene 2 button. */
    public static final int         PUSH_BUTTON_SCENE2        = 37;
    /** The scene 3 button. */
    public static final int         PUSH_BUTTON_SCENE3        = 38;
    /** The scene 4 button. */
    public static final int         PUSH_BUTTON_SCENE4        = 39;
    /** The scene 5 button. */
    public static final int         PUSH_BUTTON_SCENE5        = 40;       // ...
    /** The scene 6 button. */
    public static final int         PUSH_BUTTON_SCENE6        = 41;
    /** The scene 7 button. */
    public static final int         PUSH_BUTTON_SCENE7        = 42;
    /** The scene 8 button. */
    public static final int         PUSH_BUTTON_SCENE8        = 43;       // 1/32T
    /** The cursor left button. */
    public static final int         PUSH_BUTTON_LEFT          = 44;
    /** The cursor right button. */
    public static final int         PUSH_BUTTON_RIGHT         = 45;
    /** The cursor up button. */
    public static final int         PUSH_BUTTON_UP            = 46;
    /** The cursor down button. */
    public static final int         PUSH_BUTTON_DOWN          = 47;
    /** The select button. */
    public static final int         PUSH_BUTTON_SELECT        = 48;
    /** The shift button. */
    public static final int         PUSH_BUTTON_SHIFT         = 49;
    /** The note button. */
    public static final int         PUSH_BUTTON_NOTE          = 50;
    /** The session button. */
    public static final int         PUSH_BUTTON_SESSION       = 51;
    /** The add effect button. */
    public static final int         PUSH_BUTTON_ADD_EFFECT    = 52;
    /** The add track button. */
    public static final int         PUSH_BUTTON_ADD_TRACK     = 53;
    /** The octave down button. */
    public static final int         PUSH_BUTTON_OCTAVE_DOWN   = 54;
    /** The octave up button. */
    public static final int         PUSH_BUTTON_OCTAVE_UP     = 55;
    /** The repeat button. */
    public static final int         PUSH_BUTTON_REPEAT        = 56;
    /** The accent button. */
    public static final int         PUSH_BUTTON_ACCENT        = 57;
    /** The scales button. */
    public static final int         PUSH_BUTTON_SCALES        = 58;
    /** The user mode button. */
    public static final int         PUSH_BUTTON_USER_MODE     = 59;
    /** The mute button. */
    public static final int         PUSH_BUTTON_MUTE          = 60;
    /** The solo button. */
    public static final int         PUSH_BUTTON_SOLO          = 61;
    /** The device left button. */
    public static final int         PUSH_BUTTON_DEVICE_LEFT   = 62;
    /** The device right button. */
    public static final int         PUSH_BUTTON_DEVICE_RIGHT  = 63;
    /** The footswitch 1. */
    public static final int         PUSH_FOOTSWITCH1          = 64;
    /** The footswitch 2. */
    public static final int         PUSH_FOOTSWITCH2          = 69;
    /** The knob 1. */
    public static final int         PUSH_KNOB1                = 71;
    /** The knob 2. */
    public static final int         PUSH_KNOB2                = 72;
    /** The knob 3. */
    public static final int         PUSH_KNOB3                = 73;
    /** The knob 4. */
    public static final int         PUSH_KNOB4                = 74;
    /** The knob 5. */
    public static final int         PUSH_KNOB5                = 75;
    /** The knob 6. */
    public static final int         PUSH_KNOB6                = 76;
    /** The knob 7. */
    public static final int         PUSH_KNOB7                = 77;
    /** The knob 8. */
    public static final int         PUSH_KNOB8                = 78;
    /** The knob 9 - master knob. */
    public static final int         PUSH_KNOB9                = 79;
    /** The play button. */
    public static final int         PUSH_BUTTON_PLAY          = 85;
    /** The record button. */
    public static final int         PUSH_BUTTON_RECORD        = 86;
    /** The new button. */
    public static final int         PUSH_BUTTON_NEW           = 87;
    /** The duplicate button. */
    public static final int         PUSH_BUTTON_DUPLICATE     = 88;
    /** The automation button. */
    public static final int         PUSH_BUTTON_AUTOMATION    = 89;
    /** The fixed length button. */
    public static final int         PUSH_BUTTON_FIXED_LENGTH  = 90;
    /** The second row button 1. */
    public static final int         PUSH_BUTTON_ROW2_1        = 102;
    /** The second row button 2. */
    public static final int         PUSH_BUTTON_ROW2_2        = 103;
    /** The second row button 3. */
    public static final int         PUSH_BUTTON_ROW2_3        = 104;
    /** The second row button 4. */
    public static final int         PUSH_BUTTON_ROW2_4        = 105;
    /** The second row button 5. */
    public static final int         PUSH_BUTTON_ROW2_5        = 106;
    /** The second row button 6. */
    public static final int         PUSH_BUTTON_ROW2_6        = 107;
    /** The second row button 7. */
    public static final int         PUSH_BUTTON_ROW2_7        = 108;
    /** The second row button 8. */
    public static final int         PUSH_BUTTON_ROW2_8        = 109;
    /** The device button. */
    public static final int         PUSH_BUTTON_DEVICE        = 110;
    /** The browse button. */
    public static final int         PUSH_BUTTON_BROWSE        = 111;
    /** The track / mix button. */
    public static final int         PUSH_BUTTON_TRACK         = 112;
    /** The clip button. */
    public static final int         PUSH_BUTTON_CLIP          = 113;
    /** The volume button - only Push 1. */
    public static final int         PUSH_BUTTON_VOLUME        = 114;
    /** The pan/send button - only Push 1. */
    public static final int         PUSH_BUTTON_PAN_SEND      = 115;
    /** The quantize button. */
    public static final int         PUSH_BUTTON_QUANTIZE      = 116;
    /** The double button. */
    public static final int         PUSH_BUTTON_DOUBLE        = 117;
    /** The delete button. */
    public static final int         PUSH_BUTTON_DELETE        = 118;
    /** The undo button. */
    public static final int         PUSH_BUTTON_UNDO          = 119;

    /** The note sent when touching knob 1. */
    public static final int         PUSH_KNOB1_TOUCH          = 0;
    /** The note sent when touching knob 2. */
    public static final int         PUSH_KNOB2_TOUCH          = 1;
    /** The note sent when touching knob 3. */
    public static final int         PUSH_KNOB3_TOUCH          = 2;
    /** The note sent when touching knob 4. */
    public static final int         PUSH_KNOB4_TOUCH          = 3;
    /** The note sent when touching knob 5. */
    public static final int         PUSH_KNOB5_TOUCH          = 4;
    /** The note sent when touching knob 6. */
    public static final int         PUSH_KNOB6_TOUCH          = 5;
    /** The note sent when touching knob 7. */
    public static final int         PUSH_KNOB7_TOUCH          = 6;
    /** The note sent when touching knob 8. */
    public static final int         PUSH_KNOB8_TOUCH          = 7;
    /** The note sent when touching the master knob. */
    public static final int         PUSH_KNOB9_TOUCH          = 8;
    /** The note sent when touching the small knob 1. */
    public static final int         PUSH_SMALL_KNOB1_TOUCH    = 10;
    /** The note sent when touching the small knob 2. */
    public static final int         PUSH_SMALL_KNOB2_TOUCH    = 9;

    private static final int []     PUSH_BUTTONS_ALL          =
    {
        PUSH_BUTTON_TAP,
        PUSH_BUTTON_METRONOME,
        PUSH_BUTTON_MASTER,
        PUSH_BUTTON_CLIP_STOP,
        PUSH_BUTTON_LEFT,
        PUSH_BUTTON_RIGHT,
        PUSH_BUTTON_UP,
        PUSH_BUTTON_DOWN,
        PUSH_BUTTON_SELECT,
        PUSH_BUTTON_SHIFT,
        PUSH_BUTTON_NOTE,
        PUSH_BUTTON_SESSION,
        PUSH_BUTTON_ADD_EFFECT,
        PUSH_BUTTON_ADD_TRACK,
        PUSH_BUTTON_OCTAVE_DOWN,
        PUSH_BUTTON_OCTAVE_UP,
        PUSH_BUTTON_REPEAT,
        PUSH_BUTTON_ACCENT,
        PUSH_BUTTON_SCALES,
        PUSH_BUTTON_USER_MODE,
        PUSH_BUTTON_MUTE,
        PUSH_BUTTON_SOLO,
        PUSH_BUTTON_DEVICE_LEFT,
        PUSH_BUTTON_DEVICE_RIGHT,
        PUSH_BUTTON_PLAY,
        PUSH_BUTTON_RECORD,
        PUSH_BUTTON_NEW,
        PUSH_BUTTON_DUPLICATE,
        PUSH_BUTTON_AUTOMATION,
        PUSH_BUTTON_FIXED_LENGTH,
        PUSH_BUTTON_DEVICE,
        PUSH_BUTTON_BROWSE,
        PUSH_BUTTON_TRACK,
        PUSH_BUTTON_CLIP,
        PUSH_BUTTON_VOLUME,
        PUSH_BUTTON_PAN_SEND,
        PUSH_BUTTON_QUANTIZE,
        PUSH_BUTTON_DOUBLE,
        PUSH_BUTTON_DELETE,
        PUSH_BUTTON_UNDO,
        PUSH_BUTTON_SETUP,
        PUSH_BUTTON_LAYOUT,
        PUSH_BUTTON_CONVERT,
        PUSH_BUTTON_SCENE1,
        PUSH_BUTTON_SCENE2,
        PUSH_BUTTON_SCENE3,
        PUSH_BUTTON_SCENE4,
        PUSH_BUTTON_SCENE5,
        PUSH_BUTTON_SCENE6,
        PUSH_BUTTON_SCENE7,
        PUSH_BUTTON_SCENE8,
        PUSH_BUTTON_ROW1_1,
        PUSH_BUTTON_ROW1_2,
        PUSH_BUTTON_ROW1_3,
        PUSH_BUTTON_ROW1_4,
        PUSH_BUTTON_ROW1_5,
        PUSH_BUTTON_ROW1_6,
        PUSH_BUTTON_ROW1_7,
        PUSH_BUTTON_ROW1_8,
        PUSH_BUTTON_ROW2_1,
        PUSH_BUTTON_ROW2_2,
        PUSH_BUTTON_ROW2_3,
        PUSH_BUTTON_ROW2_4,
        PUSH_BUTTON_ROW2_5,
        PUSH_BUTTON_ROW2_6,
        PUSH_BUTTON_ROW2_7,
        PUSH_BUTTON_ROW2_8
    };

    private static final boolean [] PUSH_BUTTON_UPDATE;
    static
    {
        PUSH_BUTTON_UPDATE = new boolean [128];
        Arrays.fill (PUSH_BUTTON_UPDATE, false);
        PUSH_BUTTON_UPDATE[PUSH_BUTTON_TAP] = true;
        PUSH_BUTTON_UPDATE[PUSH_BUTTON_SELECT] = true;
        PUSH_BUTTON_UPDATE[PUSH_BUTTON_SHIFT] = true;
        PUSH_BUTTON_UPDATE[PUSH_BUTTON_ADD_EFFECT] = true;
        PUSH_BUTTON_UPDATE[PUSH_BUTTON_ADD_TRACK] = true;
        PUSH_BUTTON_UPDATE[PUSH_BUTTON_REPEAT] = true;
        PUSH_BUTTON_UPDATE[PUSH_BUTTON_USER_MODE] = true;
        PUSH_BUTTON_UPDATE[PUSH_BUTTON_NEW] = true;
        PUSH_BUTTON_UPDATE[PUSH_BUTTON_DUPLICATE] = true;
        PUSH_BUTTON_UPDATE[PUSH_BUTTON_QUANTIZE] = true;
        PUSH_BUTTON_UPDATE[PUSH_BUTTON_DOUBLE] = true;
        PUSH_BUTTON_UPDATE[PUSH_BUTTON_DELETE] = true;
        PUSH_BUTTON_UPDATE[PUSH_BUTTON_UNDO] = true;
        PUSH_BUTTON_UPDATE[PUSH_BUTTON_LAYOUT] = true;
    }

    /** The midi note which is sent when touching the ribbon. */
    public static final int        PUSH_RIBBON_TOUCH             = 12;

    /** Configure Ribbon as pitchbend. */
    public static final int        PUSH_RIBBON_PITCHBEND         = 0;
    /** Configure Ribbon as volume slider. */
    public static final int        PUSH_RIBBON_VOLUME            = 1;
    /** Configure Ribbon as panorama. */
    public static final int        PUSH_RIBBON_PAN               = 2;
    /** Configure Ribbon discrete values. */
    public static final int        PUSH_RIBBON_DISCRETE          = 3;

    private static final String [] PUSH_PAD_CURVES_DATA          =
    {
        "00 00 00 01 08 06 0A 00 00 00 00 00 0A 0F 0C 08 00 00 00 00 00 00 00 00",
        "00 00 00 01 04 0C 00 08 00 00 00 01 0D 04 0C 00 00 00 00 00 0E 0A 06 00",
        "00 00 00 01 04 0C 00 08 00 00 00 01 0D 04 0C 00 00 00 00 00 0C 03 05 00",
        "00 00 00 01 08 06 0A 00 00 00 00 01 0D 04 0C 00 00 00 00 00 0C 03 05 00",
        "00 00 00 01 0F 0B 0D 00 00 00 00 01 0D 04 0C 00 00 00 00 00 0C 03 05 00",
        "00 00 00 02 02 02 0E 00 00 00 00 01 0D 04 0C 00 00 00 00 00 00 00 00 00"
    };

    private static final String [] PUSH_PAD_THRESHOLDS_DATA      =
    {
        // 4 Byte: peak_sampling_time, 4 Byte: aftertouch_gate_time
        "00 00 00 0A 00 00 00 0A",
        "00 00 01 03 00 00 01 04",
        "00 00 01 0C 00 00 01 0E",
        "00 00 02 05 00 00 02 08",
        "00 00 02 0E 00 00 03 02",
        "00 00 03 07 00 00 03 0C",
        "00 00 04 00 00 00 04 06",
        "00 00 04 09 00 00 05 00",
        "00 00 05 02 00 00 05 0A",
        "00 00 05 0B 00 00 06 04",
        "00 00 06 04 00 00 06 0E",
        "00 00 06 0D 00 00 07 08",
        "00 00 07 06 00 00 08 02",
        "00 00 07 0F 00 00 08 0C",
        "00 00 08 08 00 00 09 06",
        "00 00 09 01 00 00 0A 00",
        "00 00 09 0A 00 00 0A 0A",
        "00 00 0A 03 00 00 0B 04",
        "00 00 0A 0C 00 00 0B 0E",
        "00 00 0B 05 00 00 0C 08",
        "00 00 0B 0E 00 00 0D 02",
        "00 00 0C 07 00 00 0D 0C",
        "00 00 0D 00 00 00 0E 06",
        "00 00 0D 08 00 00 0E 0F",
        "00 00 0E 02 00 00 0F 0A",
        "00 00 0E 0B 00 01 00 04",
        "00 00 0F 04 00 01 00 0E",
        "00 00 0F 0D 00 01 01 08",
        "00 01 00 06 00 01 02 02",
        "00 01 00 0F 00 01 02 0C",
        "00 01 01 08 00 01 03 06",
        "00 01 02 01 00 01 04 00",
        "00 01 02 0A 00 01 04 0A",
        "00 01 03 03 00 01 05 04",
        "00 01 03 0C 00 01 05 0E",
        "00 01 04 05 00 01 06 08",
        "00 01 04 0E 00 01 07 02",
        "00 01 05 07 00 01 07 0C",
        "00 01 06 00 00 01 08 06",
        "00 01 06 09 00 01 09 00",
        "00 01 07 02 00 01 09 0A"
    };

    private static final int []    MAXW                          =
    {
        1700,
        1660,
        1590,
        1510,
        1420,
        1300,
        1170,
        1030,
        860,
        640,
        400
    };
    private static final int []    PUSH2_CPMIN                   =
    {
        1650,
        1580,
        1500,
        1410,
        1320,
        1220,
        1110,
        1000,
        900,
        800,
        700
    };
    private static final int []    PUSH2_CPMAX                   =
    {
        2050,
        1950,
        1850,
        1750,
        1650,
        1570,
        1490,
        1400,
        1320,
        1240,
        1180
    };
    private static final double [] GAMMA                         =
    {
        0.7,
        0.64,
        0.58,
        0.54,
        0.5,
        0.46,
        0.43,
        0.4,
        0.36,
        0.32,
        0.25
    };
    private static final int []    MINV                          =
    {
        1,
        1,
        1,
        1,
        1,
        1,
        3,
        6,
        12,
        24,
        36
    };
    private static final int []    MAXV                          =
    {
        96,
        102,
        116,
        121,
        124,
        127,
        127,
        127,
        127,
        127,
        127
    };
    private static final int []    ALPHA                         =
    {
        90,
        70,
        54,
        40,
        28,
        20,
        10,
        -5,
        -25,
        -55,
        -90
    };

    private static final int       PAD_VELOCITY_CURVE_CHUNK_SIZE = 16;
    private static final int       NUM_VELOCITY_CURVE_ENTRIES    = 128;

    private int                    ribbonMode                    = -1;
    private int                    ribbonValue                   = -1;

    private int                    majorVersion                  = -1;
    private int                    minorVersion                  = -1;
    private int                    buildNumber                   = -1;
    private int                    serialNumber                  = -1;
    private int                    boardRevision                 = -1;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
     */
    public PushControlSurface (final ControllerHost host, final ColorManager colorManager, final PushConfiguration configuration, final MidiOutput output, final MidiInput input)
    {
        super (host, configuration, colorManager, output, input, PUSH_BUTTONS_ALL);

        this.selectButtonId = PUSH_BUTTON_SELECT;
        this.shiftButtonId = PUSH_BUTTON_SHIFT;
        this.deleteButtonId = PUSH_BUTTON_DELETE;
        this.soloButtonId = PUSH_BUTTON_SOLO;
        this.muteButtonId = PUSH_BUTTON_MUTE;
        this.leftButtonId = PUSH_BUTTON_LEFT;
        this.rightButtonId = PUSH_BUTTON_RIGHT;
        this.upButtonId = PUSH_BUTTON_UP;
        this.downButtonId = PUSH_BUTTON_DOWN;

        this.pads = new PadGridImpl (colorManager, output);

        this.input.setSysexCallback (this::handleSysEx);
        this.output.sendIdentityRequest ();
    }


    /** {@inheritDoc} */
    @Override
    public int getSceneButton (final int index)
    {
        return PUSH_BUTTON_SCENE1 + index;
    }


    /**
     * Get the name of the selected pad threshold.
     *
     * @return The name of the selected pad threshold
     */
    public String getSelectedPadThreshold ()
    {
        return PUSH_PAD_THRESHOLDS_NAME[this.configuration.getPadThreshold ()];
    }


    /**
     * Get the name of the selected velocity curve.
     *
     * @return The name of the selected velocity curve
     */
    public String getSelectedVelocityCurve ()
    {
        return PUSH_PAD_CURVES_NAME[this.configuration.getVelocityCurve ()];
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        // Turn off all buttons
        for (final int button: this.getButtons ())
            this.setButton (button, 0);

        // Turn off 1st/2nd row buttons
        for (int i = 20; i < 28; i++)
            this.setButton (i, 0);
        for (int i = 102; i < 110; i++)
            this.setButton (i, 0);

        this.pads.turnOff ();
        this.display.shutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public void setButton (final int button, final int state)
    {
        this.output.sendCC (button, state);
    }


    /**
     * Set the ribbon mode on the Push controller.
     *
     * @param mode The mode to set
     */
    public void setRibbonMode (final int mode)
    {
        if (this.ribbonMode == mode)
            return;
        this.ribbonMode = mode;
        if (this.configuration.isPush2 ())
        {
            // See section 2.10.1 in Push 2 programmer manual for status codes
            int status = 0;
            switch (mode)
            {
                case PUSH_RIBBON_PITCHBEND:
                    status = 122;
                    break;
                case PUSH_RIBBON_VOLUME:
                    status = 1;
                    break;
                case PUSH_RIBBON_PAN:
                    status = 17;
                    break;
                default:
                    break;
            }
            this.sendPush2SysEx (new int []
            {
                23,
                status
            });
        }
        else
            this.output.sendSysex ("F0 47 7F 15 63 00 01 0" + mode + " F7");
    }


    /**
     * Set the display value of the ribbon on the Push controller.
     *
     * @param value The value to set
     */
    public void setRibbonValue (final int value)
    {
        if (this.ribbonValue == value)
            return;
        this.ribbonValue = value;
        this.output.sendPitchbend (0, value);
    }


    /**
     * Set the pad sensitivity of Push 1.
     */
    public void sendPadSensitivity ()
    {
        this.output.sendSysex ("F0 47 7F 15 5D 00 20 " + PUSH_PAD_THRESHOLDS_DATA[this.configuration.getPadThreshold ()] + " " + PUSH_PAD_CURVES_DATA[this.configuration.getVelocityCurve ()] + " F7");
    }


    /**
     * Set the pad velocity of Push 2.
     */
    public void sendPadVelocityCurve ()
    {
        final int [] velocities = generateVelocityCurve (this.configuration.getPadSensitivity (), this.configuration.getPadGain (), this.configuration.getPadDynamics ());
        for (int index = 0; index < velocities.length; index += PAD_VELOCITY_CURVE_CHUNK_SIZE)
        {
            final int [] args = new int [2 + PAD_VELOCITY_CURVE_CHUNK_SIZE];
            args[0] = 32;
            args[1] = index;
            System.arraycopy(velocities, index + 0, args, 2, PAD_VELOCITY_CURVE_CHUNK_SIZE);
            this.sendPush2SysEx (args);
        }
    }


    private static int [] generateVelocityCurve (final int sensitivity, final int gain, final int dynamics)
    {
        final int minw = 160;
        final int maxw = MAXW[sensitivity];
        final int minv = MINV[gain];
        final int maxv = MAXV[gain];
        final double [] result = calculatePoints (ALPHA[dynamics]);
        final double p1x = result[0];
        final double p1y = result[1];
        final double p2x = result[2];
        final double p2y = result[3];
        final int [] curve = new int [NUM_VELOCITY_CURVE_ENTRIES];
        final int minwIndex = minw / 32;
        final int maxwIndex = maxw / 32;
        double t = 0.0;

        double w;
        for (int index = 0; index < NUM_VELOCITY_CURVE_ENTRIES; index++)
        {
            w = index * 32.0;
            double velocity;

            if (w <= minw)
                velocity = 1.0 + (minv - 1.0) * index / minwIndex;
            else if (w >= maxw)
                velocity = maxv + (127.0 - maxv) * (index - maxwIndex) / (128 - maxwIndex);
            else
            {
                final double wnorm = (w - minw) / (maxw - minw);
                final double [] bez = bezier (wnorm, t, p1x, p1y, p2x, p2y);
                final double b = bez[0];
                t = bez[1];
                final double velonorm = gammaFunc (b, GAMMA[gain]);
                velocity = minv + velonorm * (maxv - minv);
            }
            curve[index] = (int) Math.min (Math.max (Math.round (velocity), 1), 127);
        }
        return curve;
    }


    private static double [] bezier (final double x, final double t, final double p1x, final double p1y, final double p2x, final double p2y)
    {
        final double p0x = 0.0;
        final double p0y = 0.0;
        final double p3x = 1.0;
        final double p3y = 1.0;
        double s;
        double t2;
        double t3;
        double s2;
        double s3;
        double xt;
        double tl = t;
        while (tl <= 1.0)
        {
            s = 1 - tl;
            t2 = tl * tl;
            t3 = t2 * tl;
            s2 = s * s;
            s3 = s2 * s;
            xt = s3 * p0x + 3 * tl * s2 * p1x + 3 * t2 * s * p2x + t3 * p3x;
            if (xt >= x)
                return new double []
                {
                    s3 * p0y + 3 * tl * s2 * p1y + 3 * t2 * s * p2y + t3 * p3y,
                    tl
                };
            tl += 0.0001;
        }
        return new double []
        {
            1.0,
            tl
        };
    }


    private static double [] calculatePoints (final double alpha)
    {
        final double a1 = (225.0 - alpha) * Math.PI / 180.0;
        final double a2 = (45.0 - alpha) * Math.PI / 180.0;
        final double r = 0.4;
        return new double []
        {
            0.5 + r * Math.cos (a1),
            0.5 + r * Math.sin (a1),
            0.5 + r * Math.cos (a2),
            0.5 + r * Math.sin (a2)
        };
    }


    private static double gammaFunc (final double x, final double gamma)
    {
        return Math.pow (x, Math.exp (-4.0 + 8.0 * gamma));
    }


    /**
     * Send the pad threshold.
     */
    public void sendPadThreshold ()
    {
        final int [] args = new int [9];
        args[0] = 27;
        add7L5M (args, 1, 33); // threshold0
        add7L5M (args, 3, 31); // threshold1
        final int padSensitivity = this.configuration.getPadSensitivity ();
        add7L5M (args, 5, PUSH2_CPMIN[padSensitivity]); // cpmin
        add7L5M (args, 7, PUSH2_CPMAX[padSensitivity]); // cpmax
        this.sendPush2SysEx (args);
    }


    private static void add7L5M (final int [] array, final int index, final int value)
    {
        array[index] = value & 127;
        array[index + 1] = value >> 7 & 31;
    }


    /**
     * Send the display brightness.
     */
    public void sendDisplayBrightness ()
    {
        final int brightness = this.configuration.getDisplayBrightness () * 255 / 100;
        this.sendPush2SysEx (new int []
        {
            8,
            brightness & 127,
            brightness >> 7 & 1
        });
    }


    /**
     * Send the LED brightness.
     */
    public void sendLEDBrightness ()
    {
        final int brightness = this.configuration.getLedBrightness () * 127 / 100;
        this.sendPush2SysEx (new int []
        {
            6,
            brightness
        });
    }


    /**
     * Send the aftertouch mode.
     *
     * @param mode Push 2 : 0 = Channel Aftertouch, 1 = Poly Aftertouch
     */
    public void sendAftertouchMode (final int mode)
    {
        this.sendPush2SysEx (new int []
        {
            30,
            mode
        });
    }


    /**
     * Send SysEx to the Push 2.
     *
     * @param parameters The parameters to send
     */
    public void sendPush2SysEx (final int [] parameters)
    {
        this.output.sendSysex ("F0 00 21 1D 01 01 " + MidiOutput.toHexStr (parameters) + "F7");
    }


    private void handleSysEx (final String data)
    {
        if (this.configuration.isPush2 ())
        {
            final int byteLength = data.length () / 2;
            if (byteLength < PUSH2_IDENTITY_MIN_LENGTH)
            {
                this.errorln ("Wrong Push 2 identifier length " + byteLength + " but must be " + PUSH2_IDENTITY_MIN_LENGTH);
                this.errorln (data);
                return;
            }

            for (int i = 0; i < PUSH2_ID.length; i++)
            {
                final int value = hexByteAt (data, i);
                if (value != PUSH2_ID[i])
                {
                    this.errorln ("Wrong identifier value at index " + i + ": " + value + " : " + PUSH2_ID[i]);
                    return;
                }
            }

            this.majorVersion = hexByteAt (data, 12);
            this.minorVersion = hexByteAt (data, 13);
            this.buildNumber = hexByteAt (data, 14) + (hexByteAt (data, 15) << 7);
            this.serialNumber = hexByteAt (data, 16) + (hexByteAt (data, 17) << 7) + (hexByteAt (data, 18) << 14) + (hexByteAt (data, 19) << 21) + (hexByteAt (data, 20) << 28);
            this.boardRevision = byteLength > 21 ? hexByteAt (data, 21) : 0;
        }
        else
        {
            final int byteLength = data.length () / 2;
            if (byteLength < PUSH1_IDENTITY_MIN_LENGTH)
            {
                this.errorln ("Wrong Push 1 identifier length " + byteLength + " but must be " + PUSH1_IDENTITY_MIN_LENGTH);
                return;
            }

            for (int i = 0; i < PUSH1_ID.length; i++)
            {
                final int value = hexByteAt (data, i);
                if (value != PUSH1_ID[i])
                {
                    this.errorln ("Wrong identifier value at index " + i + ": " + value + " : " + PUSH1_ID[i]);
                    return;
                }
            }

            this.majorVersion = hexByteAt (data, 10);
            this.minorVersion = hexByteAt (data, 12) + hexByteAt (data, 11) * 10;
            this.buildNumber = 0;
            this.serialNumber = 0;
            this.boardRevision = 0;
        }
    }


    private static int hexByteAt (final String data, final int index)
    {
        final int pos = index * 2;
        return Integer.parseInt (data.substring (pos, pos + 2), 16);
    }


    /**
     * Get the major hardware version.
     *
     * @return The major hardware version.
     */
    public int getMajorVersion ()
    {
        return this.majorVersion;
    }


    /**
     * Set the major hardware version.
     *
     * @param majorVersion The major hardware version.
     */
    public void setMajorVersion (final int majorVersion)
    {
        this.majorVersion = majorVersion;
    }


    /**
     * Get the minor hardware version.
     *
     * @return The minor hardware version.
     */
    public int getMinorVersion ()
    {
        return this.minorVersion;
    }


    /**
     * Set the minor hardware version.
     *
     * @param minorVersion The major hardware version.
     */
    public void setMinorVersion (final int minorVersion)
    {
        this.minorVersion = minorVersion;
    }


    /**
     * Get the firmware build number.
     *
     * @return The build number
     */
    public int getBuildNumber ()
    {
        return this.buildNumber;
    }


    /**
     * Get the hardware board revision number.
     *
     * @return The number
     */
    public int getBoardRevision ()
    {
        return this.boardRevision;
    }


    /**
     * Get the controller serial number.
     *
     * @return The number
     */
    public int getSerialNumber ()
    {
        return this.serialNumber;
    }


    /**
     * Check if a button should be updated by the main update routine.
     *
     * @param button The button to check
     * @return True if it should be updated
     */
    public boolean shouldUpdateButton (final int button)
    {
        return PUSH_BUTTON_UPDATE[button];
    }
}