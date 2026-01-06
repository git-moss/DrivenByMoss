// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.controller;

import java.util.List;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.PushVersion;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.AbstractMidiOutput;
import de.mossgrabers.framework.daw.midi.DeviceInquiry;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.featuregroup.IExpressionView;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.scale.MPEStatus;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The Push 1, 2 and 3 control surface.
 *
 * @author Jürgen Moßgraber
 */
public class PushControlSurface extends AbstractControlSurface<PushConfiguration>
{
    private static final int []      CHROMATIC_SCALE                      = new int []
    {
        0,
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        11
    };

    /** The names for the dynamic curves. */
    public static final List<String> PUSH_PAD_CURVES_NAME                 = List.of ("Linear", "Log 1 (Default)", "Log 2", "Log 3", "Log 4", "Log 5");

    /** The names for the pad thresholds. */
    public static final List<String> PUSH_PAD_THRESHOLDS_NAME             = List.of ("-20", "-19", "-18", "-17", "-16", "-15", "-14", "-13", "-12", "-11", "-10", "-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "0 (Default)", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20");

    /** The tap button. */
    public static final int          PUSH_BUTTON_TAP                      = 3;
    /** The metronome button. */
    public static final int          PUSH_BUTTON_METRONOME                = 9;
    /** The small knob 1 turned. */
    public static final int          PUSH_SMALL_KNOB1                     = 14;
    /** The small knob 1 pushed - only Push 3. */
    public static final int          PUSH_SMALL_KNOB1_PRESS               = 15;
    /** The small knob 2 turned - only Push 1/2. */
    public static final int          PUSH_SMALL_KNOB2                     = 15;
    /** The button 1 in row 1. */
    public static final int          PUSH_BUTTON_ROW1_1                   = 20;
    /** The button 2 in row 1. */
    public static final int          PUSH_BUTTON_ROW1_2                   = 21;
    /** The button 3 in row 1. */
    public static final int          PUSH_BUTTON_ROW1_3                   = 22;
    /** The button 4 in row 1. */
    public static final int          PUSH_BUTTON_ROW1_4                   = 23;
    /** The button 5 in row 1. */
    public static final int          PUSH_BUTTON_ROW1_5                   = 24;
    /** The button 6 in row 1. */
    public static final int          PUSH_BUTTON_ROW1_6                   = 25;
    /** The button 7 in row 1. */
    public static final int          PUSH_BUTTON_ROW1_7                   = 26;
    /** The button 8 in row 1. */
    public static final int          PUSH_BUTTON_ROW1_8                   = 27;
    /** The master button. */
    public static final int          PUSH_BUTTON_MASTER                   = 28;
    /** The clip stop button. */
    public static final int          PUSH_BUTTON_STOP_CLIP                = 29;
    /** The setup button - only Push 2/3. */
    public static final int          PUSH_BUTTON_SETUP                    = 30;
    /** The layout button - only Push 2/3. */
    public static final int          PUSH_BUTTON_LAYOUT                   = 31;
    /** The Add button - only Push 3. */
    public static final int          PUSH_BUTTON_ADD                      = 32;
    /** The Hot Swap button - only Push 3. */
    public static final int          PUSH_BUTTON_HOT_SWAP                 = 33;
    /** The Session Display button - only Push 3. */
    public static final int          PUSH_BUTTON_SESSION_DISPLAY          = 34;
    /** The convert button - only Push 2/3. */
    public static final int          PUSH_BUTTON_CONVERT                  = 35;
    /** The scene 1 button. */
    public static final int          PUSH_BUTTON_SCENE1                   = 36;                                                                                                                                                                                                                                                                       // 1/4
    /** The scene 2 button. */
    public static final int          PUSH_BUTTON_SCENE2                   = 37;
    /** The scene 3 button. */
    public static final int          PUSH_BUTTON_SCENE3                   = 38;
    /** The scene 4 button. */
    public static final int          PUSH_BUTTON_SCENE4                   = 39;
    /** The scene 5 button. */
    public static final int          PUSH_BUTTON_SCENE5                   = 40;                                                                                                                                                                                                                                                                       // ...
    /** The scene 6 button. */
    public static final int          PUSH_BUTTON_SCENE6                   = 41;
    /** The scene 7 button. */
    public static final int          PUSH_BUTTON_SCENE7                   = 42;
    /** The scene 8 button. */
    public static final int          PUSH_BUTTON_SCENE8                   = 43;                                                                                                                                                                                                                                                                       // 1/32T
    /** The cursor left button. */
    public static final int          PUSH_BUTTON_LEFT                     = 44;
    /** The cursor right button. */
    public static final int          PUSH_BUTTON_RIGHT                    = 45;
    /** The cursor up button. */
    public static final int          PUSH_BUTTON_UP                       = 46;
    /** The cursor down button. */
    public static final int          PUSH_BUTTON_DOWN                     = 47;
    /** The select button. */
    public static final int          PUSH_BUTTON_SELECT                   = 48;
    /** The shift button. */
    public static final int          PUSH_BUTTON_SHIFT                    = 49;
    /** The note button. */
    public static final int          PUSH_BUTTON_NOTE                     = 50;
    /** The session button. */
    public static final int          PUSH_BUTTON_SESSION                  = 51;
    /** The add effect button. */
    public static final int          PUSH_BUTTON_ADD_EFFECT               = 52;
    /** The add track button. */
    public static final int          PUSH_BUTTON_ADD_TRACK                = 53;
    /** The octave down button. */
    public static final int          PUSH_BUTTON_OCTAVE_DOWN              = 54;
    /** The octave up button. */
    public static final int          PUSH_BUTTON_OCTAVE_UP                = 55;
    /** The repeat button. */
    public static final int          PUSH_BUTTON_REPEAT                   = 56;
    /** The accent button. */
    public static final int          PUSH_BUTTON_ACCENT                   = 57;
    /** The scales button. */
    public static final int          PUSH_BUTTON_SCALES                   = 58;
    /** The user mode button. */
    public static final int          PUSH_BUTTON_USER_MODE                = 59;
    /** The mute button. */
    public static final int          PUSH_BUTTON_MUTE                     = 60;
    /** The solo button. */
    public static final int          PUSH_BUTTON_SOLO                     = 61;
    /** The device left button. */
    public static final int          PUSH_BUTTON_DEVICE_LEFT              = 62;
    /** The device right button. */
    public static final int          PUSH_BUTTON_DEVICE_RIGHT             = 63;
    /** The footswitch 1. */
    public static final int          PUSH_FOOTSWITCH1                     = 64;
    /** The Capture MIDI knob - Only Push 3. */
    public static final int          PUSH_CAPTURE_MIDI                    = 65;

    /** The footswitch 2. */
    public static final int          PUSH_FOOTSWITCH2                     = 69;
    /** Turning the knob encoder - only Push 3. */
    public static final int          PUSH_KNOB_ENCODER                    = 70;
    /** The knob 1. */
    public static final int          PUSH_KNOB1                           = 71;
    /** The knob 2. */
    public static final int          PUSH_KNOB2                           = 72;
    /** The knob 3. */
    public static final int          PUSH_KNOB3                           = 73;
    /** The knob 4. */
    public static final int          PUSH_KNOB4                           = 74;
    /** The knob 5. */
    public static final int          PUSH_KNOB5                           = 75;
    /** The knob 6. */
    public static final int          PUSH_KNOB6                           = 76;
    /** The knob 7. */
    public static final int          PUSH_KNOB7                           = 77;
    /** The knob 8. */
    public static final int          PUSH_KNOB8                           = 78;
    /** The knob 9 - master knob. */
    public static final int          PUSH_KNOB9                           = 79;
    /** The Files knob - only Push 3. */
    public static final int          PUSH_BUTTON_FILES                    = 80;
    /** The Help knob - only Push 3. */
    public static final int          PUSH_BUTTON_HELP                     = 81;
    /** The Save knob - only Push 3. */
    public static final int          PUSH_BUTTON_SAVE                     = 82;
    /** The Lock knob - only Push 3. */
    public static final int          PUSH_BUTTON_LOCK                     = 83;
    /** The play button. */
    public static final int          PUSH_BUTTON_PLAY                     = 85;
    /** The record button. */
    public static final int          PUSH_BUTTON_RECORD                   = 86;
    /** The new button. */
    public static final int          PUSH_BUTTON_NEW                      = 87;
    /** The duplicate button. */
    public static final int          PUSH_BUTTON_DUPLICATE                = 88;
    /** The automation button. */
    public static final int          PUSH_BUTTON_AUTOMATION               = 89;
    /** The fixed length button. */
    public static final int          PUSH_BUTTON_FIXED_LENGTH             = 90;
    /** The button at the center of the cursor keys - only Push 3. */
    public static final int          PUSH_BUTTON_CURSOR_CENTER            = 91;
    /** The new button on Push 3. */
    public static final int          PUSH_3_BUTTON_NEW                    = 92;
    /** Moving the knob encoder left - only Push 3. */
    public static final int          PUSH_ENCODER_LEFT                    = 93;
    /** Pressing the knob encoder - only Push 3. */
    public static final int          PUSH_BUTTON_ENCODER                  = 94;
    /** Moving the knob encoder right - only Push 3. */
    public static final int          PUSH_ENCODER_RIGHT                   = 95;
    /** The second row button 1. */
    public static final int          PUSH_BUTTON_ROW2_1                   = 102;
    /** The second row button 2. */
    public static final int          PUSH_BUTTON_ROW2_2                   = 103;
    /** The second row button 3. */
    public static final int          PUSH_BUTTON_ROW2_3                   = 104;
    /** The second row button 4. */
    public static final int          PUSH_BUTTON_ROW2_4                   = 105;
    /** The second row button 5. */
    public static final int          PUSH_BUTTON_ROW2_5                   = 106;
    /** The second row button 6. */
    public static final int          PUSH_BUTTON_ROW2_6                   = 107;
    /** The second row button 7. */
    public static final int          PUSH_BUTTON_ROW2_7                   = 108;
    /** The second row button 8. */
    public static final int          PUSH_BUTTON_ROW2_8                   = 109;
    /** The device button. */
    public static final int          PUSH_BUTTON_DEVICE                   = 110;
    /** The browse button. - only Push 1/2 */
    public static final int          PUSH_BUTTON_BROWSE                   = 111;
    /** The button to toggle master volume and cue volume - only Push 3. */
    public static final int          PUSH_BUTTON_TOGGLE_MASTER_CUE_VOLUME = 111;
    /** The track / mix button. */
    public static final int          PUSH_BUTTON_TRACK                    = 112;
    /** The clip button. */
    public static final int          PUSH_BUTTON_CLIP                     = 113;
    /** The volume button - only Push 1. */
    public static final int          PUSH_BUTTON_VOLUME                   = 114;
    /** The pan/send button - only Push 1. */
    public static final int          PUSH_BUTTON_PAN_SEND                 = 115;
    /** The quantize button. */
    public static final int          PUSH_BUTTON_QUANTIZE                 = 116;
    /** The double button. */
    public static final int          PUSH_BUTTON_DOUBLE                   = 117;
    /** The delete button. */
    public static final int          PUSH_BUTTON_DELETE                   = 118;
    /** The undo button. */
    public static final int          PUSH_BUTTON_UNDO                     = 119;

    /** The note sent when touching knob 1. */
    public static final int          PUSH_KNOB1_TOUCH                     = 0;
    /** The note sent when touching knob 2. */
    public static final int          PUSH_KNOB2_TOUCH                     = 1;
    /** The note sent when touching knob 3. */
    public static final int          PUSH_KNOB3_TOUCH                     = 2;
    /** The note sent when touching knob 4. */
    public static final int          PUSH_KNOB4_TOUCH                     = 3;
    /** The note sent when touching knob 5. */
    public static final int          PUSH_KNOB5_TOUCH                     = 4;
    /** The note sent when touching knob 6. */
    public static final int          PUSH_KNOB6_TOUCH                     = 5;
    /** The note sent when touching knob 7. */
    public static final int          PUSH_KNOB7_TOUCH                     = 6;
    /** The note sent when touching knob 8. */
    public static final int          PUSH_KNOB8_TOUCH                     = 7;
    /** The note sent when touching the master knob. */
    public static final int          PUSH_KNOB9_TOUCH                     = 8;
    /** The note sent when touching the small knob 1. */
    public static final int          PUSH_SMALL_KNOB1_TOUCH               = 10;
    /** The note sent when touching the small knob 2. */
    public static final int          PUSH_SMALL_KNOB2_TOUCH               = 9;

    /** The MIDI note which is sent when touching the ribbon. */
    public static final int          PUSH_RIBBON_TOUCH                    = 12;

    /** Configure Ribbon as pitch-bend. */
    public static final int          PUSH_RIBBON_PITCHBEND                = 0;
    /** Configure Ribbon as volume slider. */
    public static final int          PUSH_RIBBON_VOLUME                   = 1;
    /** Configure Ribbon as panning. */
    public static final int          PUSH_RIBBON_PAN                      = 2;
    /** Configure Ribbon discrete values. */
    public static final int          PUSH_RIBBON_DISCRETE                 = 3;

    private static final String []   PUSH_PAD_CURVES_DATA                 =
    {
        "00 00 00 01 08 06 0A 00 00 00 00 00 0A 0F 0C 08 00 00 00 00 00 00 00 00",
        "00 00 00 01 04 0C 00 08 00 00 00 01 0D 04 0C 00 00 00 00 00 0E 0A 06 00",
        "00 00 00 01 04 0C 00 08 00 00 00 01 0D 04 0C 00 00 00 00 00 0C 03 05 00",
        "00 00 00 01 08 06 0A 00 00 00 00 01 0D 04 0C 00 00 00 00 00 0C 03 05 00",
        "00 00 00 01 0F 0B 0D 00 00 00 00 01 0D 04 0C 00 00 00 00 00 0C 03 05 00",
        "00 00 00 02 02 02 0E 00 00 00 00 01 0D 04 0C 00 00 00 00 00 00 00 00 00"
    };

    private static final String []   PUSH_PAD_THRESHOLDS_DATA             =
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

    private static final int []      MAXW                                 =
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
    private static final int []      PUSH2_CPMIN                          =
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
    private static final int []      PUSH2_CPMAX                          =
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
    private static final double []   GAMMA                                =
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
    private static final int []      MINV                                 =
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
    private static final int []      MAXV                                 =
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
    private static final int []      ALPHA                                =
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

    private static final int []      TUNE_WIDTH_VALUES                    =
    {
        0x00,                                                                                                                                                                                                                                                                                                                                         // 0mm
        0x04,                                                                                                                                                                                                                                                                                                                                         // 1mm
        0x08,                                                                                                                                                                                                                                                                                                                                         // 2mm
        0x0C,                                                                                                                                                                                                                                                                                                                                         // 2.5mm
        0x10,                                                                                                                                                                                                                                                                                                                                         // 3mm
        0x14,                                                                                                                                                                                                                                                                                                                                         // 4mm
        0x18,                                                                                                                                                                                                                                                                                                                                         // 5mm
        0x1C,                                                                                                                                                                                                                                                                                                                                         // 6mm
        0x20,                                                                                                                                                                                                                                                                                                                                         // 7mm
        0x30,                                                                                                                                                                                                                                                                                                                                         // 10mm
        0x40,                                                                                                                                                                                                                                                                                                                                         // 13mm
        0x60                                                                                                                                                                                                                                                                                                                                          // 20mm
    };

    private static final int []      SLIDE_HEIGHT_VALUES                  =
    {
        0x13,                                                                                                                                                                                                                                                                                                                                         // 16mm
        0x19,                                                                                                                                                                                                                                                                                                                                         // 15mm
        0x20,                                                                                                                                                                                                                                                                                                                                         // 14mm
        0x27,                                                                                                                                                                                                                                                                                                                                         // 13mm
        0x2D,                                                                                                                                                                                                                                                                                                                                         // 12mm
        0x34,                                                                                                                                                                                                                                                                                                                                         // 11mm
        0x3B                                                                                                                                                                                                                                                                                                                                          // 10mm
    };

    private static final String      SYSEX_HEADER_TEXT_PUSH1              = "F0 47 7F 15 ";
    private static final String      SYSEX_HEADER_TEXT                    = "F0 00 21 1D 01 01 ";
    private static final int []      SYSEX_HEADER_BYTES                   =
    {
        0xF0,
        0x00,
        0x21,
        0x1D,
        0x01,
        0x01
    };
    private static final String      SYSEX_ZERO_PADDING                   = " 00 00 00 00 00 00 00 00 00 00 00 00 00";

    private static final int         PAD_VELOCITY_CURVE_CHUNK_SIZE        = 16;
    private static final int         NUM_VELOCITY_CURVE_ENTRIES           = 128;

    private static final int         MIN_OUT                              = 1;
    private static final int         MAX_OUT                              = 127;

    private final ColorPalette       colorPalette;

    private int                      ribbonMode                           = -1;
    private int                      ribbonValue                          = -1;

    private int                      majorVersion                         = -1;
    private int                      minorVersion                         = -1;
    private int                      buildNumber                          = -1;
    private int                      serialNumber                         = -1;
    private int                      boardRevision                        = -1;

    // Push 2
    private int                      currentPadSensitivityPush2           = -1;
    private int                      currentPadGainPush2                  = -1;
    private int                      currentPadDynamicsPush2              = -1;
    // Push 3
    private int                      currentThreshold                     = -1;
    private int                      currentDrive                         = -1;
    private int                      currentCompand                       = -1;
    private int                      currentRange                         = -1;
    private int []                   currentCurve                         = null;
    private final MPEStatus          mpeStatus                            = new MPEStatus ();


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     */
    public PushControlSurface (final IHost host, final ColorManager colorManager, final PushConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, configuration.getPushVersion () == PushVersion.VERSION_3 ? new PushPadGrid (colorManager, output) : new PadGridImpl (colorManager, output), 200.0, 156.0);

        this.notifyViewChange = false;
        this.colorPalette = new ColorPalette (this);

        if (this.padGrid instanceof final PushPadGrid pushPadGrid)
            pushPadGrid.setSurface (this);

        this.input.setSysexCallback (this::handleSysEx);
    }


    /**
     * Get the name of the selected pad threshold.
     *
     * @return The name of the selected pad threshold
     */
    public String getSelectedPadThreshold ()
    {
        return PUSH_PAD_THRESHOLDS_NAME.get (this.configuration.getPadThresholdPush1 ());
    }


    /**
     * Get the name of the selected velocity curve.
     *
     * @return The name of the selected velocity curve
     */
    public String getSelectedVelocityCurve ()
    {
        return PUSH_PAD_CURVES_NAME.get (this.configuration.getVelocityCurve ());
    }


    /**
     *
     * @param channel The MIDI channel (1-15)
     * @param data1 The LSB value byte
     * @param data2 The MSB value byte
     */
    public void handleMPEPitchbend (final int channel, final int data1, final int data2)
    {
        if (this.configuration.isMPEEnabled ())
        {
            final IView view = this.viewManager.getActive ();
            if (view instanceof IExpressionView)
            {
                final int note = this.mpeStatus.getNoteStatus (channel);
                if (note != -1)
                {
                    final Scales scales = view.getKeyManager ().getScales ();
                    final int [] intervals = scales.isChromatic () ? CHROMATIC_SCALE : scales.getScale ().getIntervals ();

                    // Range of -10 to 10
                    final int bendIndex = data2 - 64;

                    // Range of -1279 to 1279
                    final int value = bendIndex * 128 + (data1 >= 64 ? data1 - 127 : data1);
                    // Scale to 0..7
                    final int padIndex = (int) Math.round (value / 170.6);

                    final int tonic = scales.getScaleOffset ();

                    final int noteIndex = calcNoteIndex (intervals, note, tonic);

                    // Work in a single linear index, then wrap with floorDiv/floorMod
                    final int linear = noteIndex + padIndex;
                    final int deg = Math.floorMod (linear, intervals.length);
                    final int oct = Math.floorDiv (linear, intervals.length);

                    // Semitone offset to target degree
                    final int semitones = 12 * oct + intervals[deg] - intervals[noteIndex];

                    // Next/prev degree for cents interpolation (handles negative correctly)
                    final boolean positive = value >= 0;
                    final int linearNext = linear + (positive ? 1 : -1);
                    final int nextDeg = Math.floorMod (linearNext, intervals.length);
                    final int nextOct = Math.floorDiv (linearNext, intervals.length);
                    final int nextPrevSemitones = 12 * nextOct + intervals[nextDeg] - intervals[noteIndex];

                    final double diff = (nextPrevSemitones - semitones) * 100.0 / 2.0;

                    // Residual within the current pad, in your scaling
                    final double residual = value - padIndex * 170.6;
                    final int cents = (int) Math.round (residual / 85.3 * diff);

                    // Convert to pitch-bend and send (unchanged)
                    final int bendRange = 24;
                    final double pitchSemis = semitones + cents / 100.0;
                    final double f = Math.max (-1.0, Math.min (1.0, pitchSemis / bendRange));
                    final int delta = (int) Math.round (8191.0 * f);
                    final int value14 = Math.max (0, Math.min (16383, 8192 + delta));
                    final int lsb = value14 & 0x7F;
                    final int msb = value14 >> 7 & 0x7F;
                    this.input.sendRawMidiEvent (MidiConstants.CMD_PITCHBEND + channel, lsb, msb);
                }
            }
            return;
        }

        this.input.sendRawMidiEvent (MidiConstants.CMD_PITCHBEND + channel, data1, data2);
    }


    private static int calcNoteIndex (final int [] intervals, final int note,
            final int tonic /* 0=C .. 11=B */)
    {
        final int rel = Math.floorMod (note - tonic, 12);
        for (int i = 0; i < intervals.length; i++)
            if (intervals[i] == rel)
                return i;
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        this.setRibbonMode (PUSH_RIBBON_PITCHBEND);
        this.setRibbonValue (0);

        super.internalShutdown ();
    }


    /** {@inheritDoc} */
    @Override
    protected void handleMidi (final int status, final int data1, final int data2)
    {
        // Ignore active sensing, which seems to be sent from some Push devices
        if (status == 254)
            return;

        if (this.configuration.getPushVersion () == PushVersion.VERSION_3)
        {
            final int code = status & 0xF0;
            final int channel = status & 0xF;

            // Ignore 'All MIDI Note off' on startup
            if (channel == 0)
            {
                if (code == MidiConstants.CMD_CC && data1 == 123)
                    return;
            }
            else
            {
                switch (code)
                {
                    // Ignore MPE messages
                    case MidiConstants.CMD_CC:
                        if (data1 == 74)
                            return;
                        break;

                    case MidiConstants.CMD_NOTE_ON:
                    case MidiConstants.CMD_NOTE_OFF:
                        if (this.configuration.isMPEEnabled ())
                        {
                            final IView view = this.getViewManager ().getActive ();
                            if (view instanceof IExpressionView)
                            {
                                final boolean isNoteOn = code == MidiConstants.CMD_NOTE_ON & data2 > 0;
                                // Reset pitch-bend
                                if (!isNoteOn)
                                    this.input.sendRawMidiEvent (MidiConstants.CMD_PITCHBEND + channel, 0, 64);
                                this.mpeStatus.handleNote (channel, view.getKeyManager ().getMidiNoteFromGrid (data1), isNoteOn);
                            }
                            return;
                        }
                        break;
                }
            }
        }

        super.handleMidi (status, data1, data2);
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
        if (this.configuration.isPushModern ())
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
                case PUSH_RIBBON_DISCRETE:
                    status = 9;
                    break;
                default:
                    break;
            }
            this.sendSysex (new int []
            {
                23,
                status
            });
        }
        else
            this.sendSysExPush1 ("63 00 01 0" + mode);
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
    public void sendPadSensitivityPush1 ()
    {
        this.sendSysExPush1 ("5D 00 20 " + PUSH_PAD_THRESHOLDS_DATA[this.configuration.getPadThresholdPush1 ()] + " " + PUSH_PAD_CURVES_DATA[this.configuration.getVelocityCurve ()]);
    }


    /**
     * Set the pad sensitivity of Push 2.
     */
    public void sendPadSensitivityPush2 ()
    {
        this.sendPadVelocityCurvePush2 ();
        this.sendPadThresholdPush2 ();
    }


    /**
     * Set the pad sensitivity of Push 3.
     */
    public void sendPadSensitivityPush3 ()
    {
        final int [] curve = this.createPadSensitivityCurvePush3 ();
        final int [] data = new int [129];
        // The command
        data[0] = 0x43;
        System.arraycopy (curve, 0, data, 1, curve.length);
        this.sendSysex (curve);
    }


    /**
     * Get the pad sensitivity curve for the Push 3.
     *
     * @return The curve with 128 entries
     */
    public int [] createPadSensitivityCurvePush3 ()
    {
        final int threshold = this.configuration.getPadCurveThresholdPush3 ();
        final int drive = this.configuration.getPadCurveDrivePush3 ();
        final int compand = this.configuration.getPadCurveCompandPush3 ();
        final int range = this.configuration.getPadCurveRangePush3 ();
        if (this.currentThreshold == threshold && this.currentDrive == drive && this.currentCompand == compand && this.currentRange == range)
            return this.currentCurve;
        this.currentThreshold = threshold;
        this.currentDrive = drive;
        this.currentCompand = compand;
        this.currentRange = range;

        // Scale threshold to a maximum of 16 values
        final int numThresholdValues = (int) Math.round (Math.clamp (threshold, 0, 100) * 16 / 100.0);
        // Scale range to a maximum of 103 values
        final int numRangeValues = (int) Math.round (Math.clamp (range, 0, 100) * 103 / 100.0);
        // Calculate 2 more curve values since the first is 1 and the last 127
        final int numCurveValues = 128 - numThresholdValues - numRangeValues + 2;

        final int [] curve = new int [128];

        // Fill the threshold part
        for (int i = 0; i < numThresholdValues; i++)
            curve[i] = 1;

        // Create the curve
        // Compand: g>0.5 = slow attack, g<0.5 = fast attack
        final double g = Math.clamp (0.5 + compand / 100.0, 0.01, 0.99);
        // Drive: b>0.5 = faster early increase, b<0.5 = slower early increase
        final double b = Math.clamp (0.5 + drive / 100.0, 0.01, 0.99);
        for (int i = 0; i < numCurveValues; i++)
        {
            final double x = (double) i / (numCurveValues - 1); // 0..1
            double y = gain (x, g); // S-shape (Compand)
            y = bias (y, b); // skew (Drive)

            int v = (int) Math.round (MIN_OUT + y * (MAX_OUT - MIN_OUT));
            v = Math.clamp (v, MIN_OUT, MAX_OUT);
            if (numThresholdValues + i <= 128)
                curve[Math.max (0, numThresholdValues + i - 1)] = v;
        }

        // Fill the range part
        final int offset = 128 - numRangeValues;
        for (int i = offset; i < curve.length; i++)
            curve[i] = 127;

        this.currentCurve = curve;
        return curve;
    }


    // Schlick bias: b in (0,1); 0.5 ≈ linear; >0.5 faster early rise; <0.5 slower early rise
    private static double bias (final double x, final double b)
    {
        return x / ((1.0 / b - 2.0) * (1.0 - x) + 1.0);
    }


    // Schlick gain: g in (0,1); 0.5 = linear; g>0.5 slow attack; g<0.5 fast attack
    private static double gain (final double x, final double g)
    {
        if (x < 0.5)
            return 0.5 * bias (2.0 * x, 1.0 - g);
        return 1.0 - 0.5 * bias (2.0 - 2.0 * x, 1.0 - g);
    }


    /**
     * Sets the Push 1/2 pads aftertouch either to poly or channel pressure.
     *
     * @param isPolyPressure Set poly pressure if true otherwise channel pressure
     */
    public void sendPressureMode (final boolean isPolyPressure)
    {
        if (this.configuration.isPushModern ())
            this.sendSysex ("1E 0" + (isPolyPressure ? "1" : "0"));
        else
            this.sendSysExPush1 ("5C 00 01 0" + (isPolyPressure ? "0" : "1"));
    }


    /**
     * Send the pad threshold.
     */
    private void sendPadThresholdPush2 ()
    {
        final int [] args = new int [9];
        args[0] = 27;
        add7L5M (args, 1, 33); // threshold0
        add7L5M (args, 3, 31); // threshold1
        final int padSensitivity = this.configuration.getPadSensitivityPush2 ();
        add7L5M (args, 5, PUSH2_CPMIN[padSensitivity]); // cpmin
        add7L5M (args, 7, PUSH2_CPMAX[padSensitivity]); // cpmax
        this.sendSysex (args);
    }


    /**
     * Set the pad velocity of Push 2.
     */
    private void sendPadVelocityCurvePush2 ()
    {
        final int [] velocities = this.createPadSensitivityCurvePush2 ();
        for (int index = 0; index < velocities.length; index += PAD_VELOCITY_CURVE_CHUNK_SIZE)
        {
            final int [] args = new int [2 + PAD_VELOCITY_CURVE_CHUNK_SIZE];
            args[0] = 32;
            args[1] = index;
            for (int i = 0; i < PAD_VELOCITY_CURVE_CHUNK_SIZE; i++)
                args[i + 2] = velocities[index + i];
            this.sendSysex (args);
        }
    }


    /**
     * Send the display brightness.
     */
    public void sendDisplayBrightness ()
    {
        final int brightness = this.configuration.getDisplayBrightness () * 255 / 100;
        this.sendSysex (new int []
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
        this.sendSysex (new int []
        {
            6,
            brightness
        });
    }


    /**
     * Turn MPE on/off (only Push 3).
     *
     * @param enable True to enable MPE
     */
    public void sendMPEActive (final boolean enable)
    {
        // Same command as sendPressureMode
        this.sendSysex (enable ? "1E 02" : "1E 01");
    }


    /**
     * Turn per-pad pitchbend on/off (only Push 3).
     *
     * @param enable True to enable
     */
    public void sendPerPadPitchbendActive (final boolean enable)
    {
        this.sendSysex ("26 07 08 0" + (enable ? "2" : "0") + " 00");
    }


    /**
     * Set 'In tune' location to Finger (1) or Pad (0) (only Push 3).
     *
     * @param inTuneLocation The in-tune location
     */
    public void sendInTuneLocation (final int inTuneLocation)
    {
        this.sendSysex ("26 07 0E 0" + inTuneLocation + " 00");
    }


    /**
     * Set 'In tune' width (only Push 3).
     *
     * @param inTuneWidthIndex The index of the in-tune width option
     */
    public void sendInTuneWidth (final int inTuneWidthIndex)
    {
        this.sendSysex ("26 07 14 " + StringUtils.toHexStr (TUNE_WIDTH_VALUES[inTuneWidthIndex]) + " 00");
    }


    /**
     * Set slide height (only Push 3).
     *
     * @param slideHeightIndex The index of the slide height option
     */
    public void sendSlideHeight (final int slideHeightIndex)
    {
        this.sendSysex ("26 07 24 " + StringUtils.toHexStr (SLIDE_HEIGHT_VALUES[slideHeightIndex]) + " 00");
    }


    /**
     * Send pedal updates to either foot switch or as a CV output (only Push 3). Read from the
     * configuration.
     */
    public void sendPedals ()
    {
        final boolean useCV1 = this.configuration.getPedal1 () > 0;
        final boolean useCV2 = this.configuration.getPedal2 () > 0;
        final int value;
        if (useCV1)
            value = useCV2 ? 0x0F : 0x43;
        else
            value = useCV2 ? 0x1C : 0x50;
        this.sendSysex ("37 26 " + StringUtils.toHexStr (value) + SYSEX_ZERO_PADDING);
    }


    /**
     * Set the pre-amp 1 type (only Push 3).
     */
    public void sendPreamp1Type ()
    {
        // 0 = Line, 1 = Instrument, 2 = High
        final int preampType = this.configuration.getPreamp1Type ();
        this.sendSysex ("37 1A " + StringUtils.toHexStr (preampType) + SYSEX_ZERO_PADDING);
    }


    /**
     * Set the pre-amp 2 type (only Push 3).
     */
    public void sendPreamp2Type ()
    {
        // 0 = Line, 1 = Instrument, 2 = High
        final int preampType = this.configuration.getPreamp2Type ();
        this.sendSysex ("37 1B " + StringUtils.toHexStr (preampType) + SYSEX_ZERO_PADDING);
    }


    /**
     * Set the (digital) pre-amp 1 gain (only Push 3).
     */
    public void sendPreamp1Gain ()
    {
        // The gain in steps of two (1dB = 2) in the range of 0x00 (20dB) to 0x28 (no gain)
        final int preampGain = (PushConfiguration.PREAMP_GAIN_OPTIONS.length - 1 - this.configuration.getPreamp1Gain ()) * 2;
        this.sendSysex ("37 02 " + StringUtils.toHexStr (preampGain) + SYSEX_ZERO_PADDING);
    }


    /**
     * Set the (digital) pre-amp 2 gain (only Push 3).
     */
    public void sendPreamp2Gain ()
    {
        // The gain in steps of two (1dB = 2) in the range of 0x00 (20dB) to 0x28 (no gain)
        final int preampGain = (PushConfiguration.PREAMP_GAIN_OPTIONS.length - 1 - this.configuration.getPreamp2Gain ()) * 2;
        this.sendSysex ("37 03 " + StringUtils.toHexStr (preampGain) + SYSEX_ZERO_PADDING);
    }


    /**
     * Set the output configuration (only Push 3).
     */
    public void sendOutputConfiguration ()
    {
        // 0 = Headphones 1/2 - Speaker 1/2, 1 = Headphones 3/4 - Speaker 1/2, 2 = Headphones 1/2 -
        // Speaker 3/4
        final int audioOutputs = this.configuration.getAudioOutputs ();
        final int value = audioOutputs == 0 ? 0 : audioOutputs + 1;
        this.sendSysex ("37 11 " + StringUtils.toHexStr (value) + SYSEX_ZERO_PADDING);
    }


    /**
     * Send SysEx to the Push 2/3.
     *
     * @param parameters The parameters to send
     */
    public void sendSysex (final int [] parameters)
    {
        this.output.sendSysex (SYSEX_HEADER_TEXT + StringUtils.toHexStr (parameters) + "F7");
    }


    /**
     * Send SysEx to the Push 2/3.
     *
     * @param parameters The parameters to send
     */
    public void sendSysex (final String parameters)
    {
        this.output.sendSysex (SYSEX_HEADER_TEXT + parameters + " F7");
    }


    /**
     * Send SysEx to the Push 1.
     *
     * @param parameters The parameters to send
     */
    public void sendSysExPush1 (final String parameters)
    {
        this.output.sendSysex (SYSEX_HEADER_TEXT_PUSH1 + parameters + " F7");
    }


    /**
     * Get the pad sensitivity curve for the Push 2.
     *
     * @return The curve with 128 entries
     */
    public int [] createPadSensitivityCurvePush2 ()
    {
        final int sensitivity = this.configuration.getPadSensitivityPush2 ();
        final int gain = this.configuration.getPadGainPush2 ();
        final int dynamics = this.configuration.getPadDynamicsPush2 ();
        if (this.currentPadSensitivityPush2 == sensitivity && this.currentPadGainPush2 == gain && this.currentPadDynamicsPush2 == dynamics)
            return this.currentCurve;
        this.currentPadSensitivityPush2 = sensitivity;
        this.currentPadGainPush2 = gain;
        this.currentPadDynamicsPush2 = dynamics;

        final int minw = 160;
        final int maxw = MAXW[sensitivity];
        final int minv = MINV[gain];
        final int maxv = MAXV[gain];
        final double [] result = calculatePointsPush2 (ALPHA[dynamics]);
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
                final double [] bez = bezierPush2 (wnorm, t, p1x, p1y, p2x, p2y);
                final double b = bez[0];
                t = bez[1];
                final double velonorm = gammaFunc (b, GAMMA[gain]);
                velocity = minv + velonorm * (maxv - minv);
            }
            curve[index] = Math.clamp (Math.round (velocity), 1, 127);
        }

        this.currentCurve = curve;
        return curve;
    }


    private static double [] bezierPush2 (final double x, final double t, final double p1x, final double p1y, final double p2x, final double p2y)
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


    private static double [] calculatePointsPush2 (final double alpha)
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


    private static void add7L5M (final int [] array, final int index, final int value)
    {
        array[index] = value & 127;
        array[index + 1] = value >> 7 & 31;
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
        {
            this.handleDeviceInquiryResponse (deviceInquiry);
            return;
        }

        // Color palette entry message?
        if (this.configuration.isPushModern () && isPush2Data (byteData))
            this.colorPalette.handleColorPaletteMessage (byteData);
    }


    private static boolean isPush2Data (final int [] data)
    {
        if (data.length + 1 < SYSEX_HEADER_BYTES.length)
            return false;

        for (int i = 0; i < SYSEX_HEADER_BYTES.length; i++)
        {
            if (SYSEX_HEADER_BYTES[i] != data[i])
                return false;
        }

        return data[data.length - 1] == 0xF7;
    }


    /**
     * Handle the response of a device inquiry.
     *
     * @param deviceInquiry The parsed response
     */
    private void handleDeviceInquiryResponse (final DeviceInquiry deviceInquiry)
    {
        if (this.configuration.isPushModern ())
        {
            final int [] unspecifiedData = deviceInquiry.getUnspecifiedData ();
            if (unspecifiedData.length < 10)
                return;

            this.majorVersion = unspecifiedData[0];
            this.minorVersion = unspecifiedData[1];
            this.buildNumber = unspecifiedData[2] + (unspecifiedData[3] << 7);
            this.serialNumber = unspecifiedData[4] + (unspecifiedData[5] << 7) + (unspecifiedData[6] << 14) + (unspecifiedData[7] << 21) + (unspecifiedData[8] << 28);
            this.boardRevision = unspecifiedData[9];
        }
        else
        {
            final int [] data = deviceInquiry.getData ();
            if (data.length != 35)
                return;

            this.majorVersion = data[10] + data[9] * 10;
            this.minorVersion = data[12] + data[11] * 10;
            this.buildNumber = 0;
            this.serialNumber = 0;
            this.boardRevision = 0;
        }
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
     * Request the full color palette.
     */
    public void updateColorPalette ()
    {
        this.colorPalette.updatePalette ();
    }


    /**
     * Update MPE on/off state depending on selected view and MPE setting (only Push 3).
     */
    public void updateMPE ()
    {
        final boolean mpeEnabled = this.isMPEEnabled ();
        final INoteInput input = this.input.getDefaultNoteInput ();
        input.enableMPE (mpeEnabled);
        this.sendMPEActive (mpeEnabled);
        this.rebindGrid ();
    }


    private boolean isMPEEnabled ()
    {
        return this.getViewManager ().getActive () instanceof IExpressionView && this.configuration.isMPEEnabled ();
    }


    /**
     * Update the MPE pitch-bend range (only Push 3).
     */
    public void updateMPEPitchbendRange ()
    {
        final int mpePitchBendRange = this.configuration.getMPEPitchBendRange ();
        this.input.getDefaultNoteInput ().setMPEPitchBendSensitivity (mpePitchBendRange);
        this.output.sendMPEPitchbendRange (AbstractMidiOutput.ZONE_1, mpePitchBendRange);
    }
}
