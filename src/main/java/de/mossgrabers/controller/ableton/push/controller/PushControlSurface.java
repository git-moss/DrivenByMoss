// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.controller;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.DeviceInquiry;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.List;


/**
 * The Push 1 and Push 2 control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushControlSurface extends AbstractControlSurface<PushConfiguration>
{
    // @formatter:off
    /** The names for the dynamic curves. */
    public static final List<String>  PUSH_PAD_CURVES_NAME     = List.of (
        "Linear",
        "Log 1 (Default)",
        "Log 2",
        "Log 3",
        "Log 4",
        "Log 5"
    );

    /** The names for the pad thresholds. */
    public static final List<String>  PUSH_PAD_THRESHOLDS_NAME = List.of (
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
    );
    // @formatter:off

    /** The tap button. */
    public static final int        PUSH_BUTTON_TAP               = 3;
    /** The metronome button. */
    public static final int        PUSH_BUTTON_METRONOME         = 9;
    /** The small knob 1. */
    public static final int        PUSH_SMALL_KNOB1              = 14;
    /** The small knob 2. */
    public static final int        PUSH_SMALL_KNOB2              = 15;
    /** The button 1 in row 1. */
    public static final int        PUSH_BUTTON_ROW1_1            = 20;
    /** The button 2 in row 1. */
    public static final int        PUSH_BUTTON_ROW1_2            = 21;
    /** The button 3 in row 1. */
    public static final int        PUSH_BUTTON_ROW1_3            = 22;
    /** The button 4 in row 1. */
    public static final int        PUSH_BUTTON_ROW1_4            = 23;
    /** The button 5 in row 1. */
    public static final int        PUSH_BUTTON_ROW1_5            = 24;
    /** The button 6 in row 1. */
    public static final int        PUSH_BUTTON_ROW1_6            = 25;
    /** The button 7 in row 1. */
    public static final int        PUSH_BUTTON_ROW1_7            = 26;
    /** The button 8 in row 1. */
    public static final int        PUSH_BUTTON_ROW1_8            = 27;
    /** The master button. */
    public static final int        PUSH_BUTTON_MASTER            = 28;
    /** The clip stop button. */
    public static final int        PUSH_BUTTON_STOP_CLIP         = 29;
    /** The setup button - only Push 2. */
    public static final int        PUSH_BUTTON_SETUP             = 30;
    /** The layout button - only Push 2. */
    public static final int        PUSH_BUTTON_LAYOUT            = 31;
    /** The convert button - only Push 2. */
    public static final int        PUSH_BUTTON_CONVERT           = 35;
    /** The scene 1 button. */
    public static final int        PUSH_BUTTON_SCENE1            = 36;                    // 1/4
    /** The scene 2 button. */
    public static final int        PUSH_BUTTON_SCENE2            = 37;
    /** The scene 3 button. */
    public static final int        PUSH_BUTTON_SCENE3            = 38;
    /** The scene 4 button. */
    public static final int        PUSH_BUTTON_SCENE4            = 39;
    /** The scene 5 button. */
    public static final int        PUSH_BUTTON_SCENE5            = 40;                    // ...
    /** The scene 6 button. */
    public static final int        PUSH_BUTTON_SCENE6            = 41;
    /** The scene 7 button. */
    public static final int        PUSH_BUTTON_SCENE7            = 42;
    /** The scene 8 button. */
    public static final int        PUSH_BUTTON_SCENE8            = 43;                    // 1/32T
    /** The cursor left button. */
    public static final int        PUSH_BUTTON_LEFT              = 44;
    /** The cursor right button. */
    public static final int        PUSH_BUTTON_RIGHT             = 45;
    /** The cursor up button. */
    public static final int        PUSH_BUTTON_UP                = 46;
    /** The cursor down button. */
    public static final int        PUSH_BUTTON_DOWN              = 47;
    /** The select button. */
    public static final int        PUSH_BUTTON_SELECT            = 48;
    /** The shift button. */
    public static final int        PUSH_BUTTON_SHIFT             = 49;
    /** The note button. */
    public static final int        PUSH_BUTTON_NOTE              = 50;
    /** The session button. */
    public static final int        PUSH_BUTTON_SESSION           = 51;
    /** The add effect button. */
    public static final int        PUSH_BUTTON_ADD_EFFECT        = 52;
    /** The add track button. */
    public static final int        PUSH_BUTTON_ADD_TRACK         = 53;
    /** The octave down button. */
    public static final int        PUSH_BUTTON_OCTAVE_DOWN       = 54;
    /** The octave up button. */
    public static final int        PUSH_BUTTON_OCTAVE_UP         = 55;
    /** The repeat button. */
    public static final int        PUSH_BUTTON_REPEAT            = 56;
    /** The accent button. */
    public static final int        PUSH_BUTTON_ACCENT            = 57;
    /** The scales button. */
    public static final int        PUSH_BUTTON_SCALES            = 58;
    /** The user mode button. */
    public static final int        PUSH_BUTTON_USER_MODE         = 59;
    /** The mute button. */
    public static final int        PUSH_BUTTON_MUTE              = 60;
    /** The solo button. */
    public static final int        PUSH_BUTTON_SOLO              = 61;
    /** The device left button. */
    public static final int        PUSH_BUTTON_DEVICE_LEFT       = 62;
    /** The device right button. */
    public static final int        PUSH_BUTTON_DEVICE_RIGHT      = 63;
    /** The footswitch 1. */
    public static final int        PUSH_FOOTSWITCH1              = 64;
    /** The footswitch 2. */
    public static final int        PUSH_FOOTSWITCH2              = 69;
    /** The knob 1. */
    public static final int        PUSH_KNOB1                    = 71;
    /** The knob 2. */
    public static final int        PUSH_KNOB2                    = 72;
    /** The knob 3. */
    public static final int        PUSH_KNOB3                    = 73;
    /** The knob 4. */
    public static final int        PUSH_KNOB4                    = 74;
    /** The knob 5. */
    public static final int        PUSH_KNOB5                    = 75;
    /** The knob 6. */
    public static final int        PUSH_KNOB6                    = 76;
    /** The knob 7. */
    public static final int        PUSH_KNOB7                    = 77;
    /** The knob 8. */
    public static final int        PUSH_KNOB8                    = 78;
    /** The knob 9 - master knob. */
    public static final int        PUSH_KNOB9                    = 79;
    /** The play button. */
    public static final int        PUSH_BUTTON_PLAY              = 85;
    /** The record button. */
    public static final int        PUSH_BUTTON_RECORD            = 86;
    /** The new button. */
    public static final int        PUSH_BUTTON_NEW               = 87;
    /** The duplicate button. */
    public static final int        PUSH_BUTTON_DUPLICATE         = 88;
    /** The automation button. */
    public static final int        PUSH_BUTTON_AUTOMATION        = 89;
    /** The fixed length button. */
    public static final int        PUSH_BUTTON_FIXED_LENGTH      = 90;
    /** The second row button 1. */
    public static final int        PUSH_BUTTON_ROW2_1            = 102;
    /** The second row button 2. */
    public static final int        PUSH_BUTTON_ROW2_2            = 103;
    /** The second row button 3. */
    public static final int        PUSH_BUTTON_ROW2_3            = 104;
    /** The second row button 4. */
    public static final int        PUSH_BUTTON_ROW2_4            = 105;
    /** The second row button 5. */
    public static final int        PUSH_BUTTON_ROW2_5            = 106;
    /** The second row button 6. */
    public static final int        PUSH_BUTTON_ROW2_6            = 107;
    /** The second row button 7. */
    public static final int        PUSH_BUTTON_ROW2_7            = 108;
    /** The second row button 8. */
    public static final int        PUSH_BUTTON_ROW2_8            = 109;
    /** The device button. */
    public static final int        PUSH_BUTTON_DEVICE            = 110;
    /** The browse button. */
    public static final int        PUSH_BUTTON_BROWSE            = 111;
    /** The track / mix button. */
    public static final int        PUSH_BUTTON_TRACK             = 112;
    /** The clip button. */
    public static final int        PUSH_BUTTON_CLIP              = 113;
    /** The volume button - only Push 1. */
    public static final int        PUSH_BUTTON_VOLUME            = 114;
    /** The pan/send button - only Push 1. */
    public static final int        PUSH_BUTTON_PAN_SEND          = 115;
    /** The quantize button. */
    public static final int        PUSH_BUTTON_QUANTIZE          = 116;
    /** The double button. */
    public static final int        PUSH_BUTTON_DOUBLE            = 117;
    /** The delete button. */
    public static final int        PUSH_BUTTON_DELETE            = 118;
    /** The undo button. */
    public static final int        PUSH_BUTTON_UNDO              = 119;

    /** The note sent when touching knob 1. */
    public static final int        PUSH_KNOB1_TOUCH              = 0;
    /** The note sent when touching knob 2. */
    public static final int        PUSH_KNOB2_TOUCH              = 1;
    /** The note sent when touching knob 3. */
    public static final int        PUSH_KNOB3_TOUCH              = 2;
    /** The note sent when touching knob 4. */
    public static final int        PUSH_KNOB4_TOUCH              = 3;
    /** The note sent when touching knob 5. */
    public static final int        PUSH_KNOB5_TOUCH              = 4;
    /** The note sent when touching knob 6. */
    public static final int        PUSH_KNOB6_TOUCH              = 5;
    /** The note sent when touching knob 7. */
    public static final int        PUSH_KNOB7_TOUCH              = 6;
    /** The note sent when touching knob 8. */
    public static final int        PUSH_KNOB8_TOUCH              = 7;
    /** The note sent when touching the master knob. */
    public static final int        PUSH_KNOB9_TOUCH              = 8;
    /** The note sent when touching the small knob 1. */
    public static final int        PUSH_SMALL_KNOB1_TOUCH        = 10;
    /** The note sent when touching the small knob 2. */
    public static final int        PUSH_SMALL_KNOB2_TOUCH        = 9;

    /** The MIDI note which is sent when touching the ribbon. */
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

    private static final int []    SYSEX_HEADER                  =
    {
        0xF0,
        0x00,
        0x21,
        0x1D,
        0x01,
        0x01
    };

    private final PaletteEntry []  colorPalette                  = new PaletteEntry [128];
    private boolean                colorPaletteHasUpdate         = false;

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
     * @param output The MIDI output
     * @param input The MIDI input
     */
    public PushControlSurface (final IHost host, final ColorManager colorManager, final PushConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, new PadGridImpl (colorManager, output), 200, 156);

        for (int i = 0; i < this.colorPalette.length; i++)
            this.colorPalette[i] = new PaletteEntry (PushColorManager.getPaletteColorRGB (i));

        this.input.setSysexCallback (this::handleSysEx);
    }


    /**
     * Get the name of the selected pad threshold.
     *
     * @return The name of the selected pad threshold
     */
    public String getSelectedPadThreshold ()
    {
        return PUSH_PAD_THRESHOLDS_NAME.get (this.configuration.getPadThreshold ());
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
                case PUSH_RIBBON_DISCRETE:
                    status = 9;
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
     * Sets the Push 1/2 pads aftertouch either to poly or channel pressure.
     *
     * @param isPolyPressure Set poly pressure if true otherwise channel pressure
     */
    public void sendPressureMode (final boolean isPolyPressure)
    {
        if (this.configuration.isPush2 ())
            this.output.sendSysex ("F0 00 21 1D 01 01 1E 0" + (isPolyPressure ? "1" : "0") + " F7");
        else
            this.output.sendSysex ("F0 47 7F 15 5C 00 01 0" + (isPolyPressure ? "0" : "1") + " F7");
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
            for (int i = 0; i < PAD_VELOCITY_CURVE_CHUNK_SIZE; i++)
                args[i + 2] = velocities[index + i];
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
        this.output.sendSysex ("F0 00 21 1D 01 01 " + StringUtils.toHexStr (parameters) + "F7");
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
        if (this.configuration.isPush2 () && isPush2Data (byteData) && PaletteEntry.isValid (byteData))
            this.handleColorPaletteMessage (byteData);
    }


    private static boolean isPush2Data (final int [] data)
    {
        if (data.length + 1 < SYSEX_HEADER.length)
            return false;

        for (int i = 0; i < SYSEX_HEADER.length; i++)
        {
            if (SYSEX_HEADER[i] != data[i])
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
        final int [] revisionLevel = deviceInquiry.getUnspecifiedData ();

        if (this.configuration.isPush2 ())
        {
            if (revisionLevel.length != 10)
                return;

            this.majorVersion = revisionLevel[0];
            this.minorVersion = revisionLevel[1];
            this.buildNumber = revisionLevel[2] + (revisionLevel[3] << 7);
            this.serialNumber = revisionLevel[4] + (revisionLevel[5] << 7) + (revisionLevel[6] << 14) + (revisionLevel[7] << 21) + (revisionLevel[8] << 28);
            this.boardRevision = revisionLevel[9];
        }
        else
        {
            if (revisionLevel.length != 24)
                return;

            this.majorVersion = revisionLevel[0];
            this.minorVersion = revisionLevel[2] + revisionLevel[1] * 10;
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
        // Retrieve the first, all others are requested after the previous one was received
        this.sendColorPaletteRequest (0);
    }


    /**
     * Handle a color palette message.
     *
     * @param data The message data
     */
    private void handleColorPaletteMessage (final int [] data)
    {
        synchronized (this.colorPalette)
        {
            final int index = data[7];

            // Is an update necessary?
            if (this.colorPalette[index].requiresUpdate (data))
            {
                this.colorPaletteHasUpdate = true;

                if (this.colorPalette[index].hasMaxNumberOfWriteRetriesReached ())
                {
                    // Cancel the whole process
                    this.host.error ("Failed writing color palette entry #" + index + ". Gave up after " + PaletteEntry.MAX_NUMBER_OF_RETRIES + " retries. Check selected MIDI in-/outputs.");
                    return;
                }

                this.colorPalette[index].incWriteRetries ();
                this.sendPush2SysEx (this.colorPalette[index].createUpdateMessage (index));
            }
            else
            {
                this.colorPalette[index].setOK ();

                final int retries = this.colorPalette[index].getWriteRetries ();
                if (retries > 1)
                    this.host.println ("Success writing color palette entry #" + index + " after " + retries + " attempts.");
            }

            if (index < 127)
            {
                this.sendColorPaletteRequest (index + 1);
                return;
            }
        }

        // Re-apply the color palette, if necessary
        if (this.colorPaletteHasUpdate)
        {
            this.host.scheduleTask ( () -> this.output.sendSysex ("F0 00 21 1D 01 01 05 F7"), 1000);

            // Request all values again to confirm it was written
            this.sendColorPaletteRequest (0);
        }
    }


    /**
     * Send a request to the Push 2 to send the values of an entry of the current color palette.
     *
     * @param paletteEntry The index of the entry 0-127
     */
    private void sendColorPaletteRequest (final int paletteEntry)
    {
        synchronized (this.colorPalette)
        {
            this.sendPush2SysEx (new int []
            {
                0x04,
                paletteEntry
            });
            this.colorPalette[paletteEntry].incReadRetries ();
        }

        // If there was no answer after 1s, retry...
        this.scheduleTask ( () -> {

            synchronized (this.colorPalette)
            {
                if (!this.colorPalette[paletteEntry].requiresRead ())
                    return;

                if (this.colorPalette[paletteEntry].hasMaxNumberOfReadRetriesReached ())
                {
                    this.host.error ("Failed reading color palette entry #" + paletteEntry + ". Gave up after " + PaletteEntry.MAX_NUMBER_OF_RETRIES + " retries. Check selected MIDI in-/outputs.");
                    return;
                }

                this.host.println ("Resending color palette entry #" + paletteEntry + " request.");
                this.sendColorPaletteRequest (paletteEntry);
            }

        }, 1000);
    }
}