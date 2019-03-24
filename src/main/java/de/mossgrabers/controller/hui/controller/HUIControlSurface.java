// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.hui.controller;

import de.mossgrabers.controller.hui.HUIConfiguration;
import de.mossgrabers.framework.command.continuous.FaderAbsoluteCommand;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.continuous.PlayPositionCommand;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;

import java.util.Arrays;


/**
 * A control surface which supports the HUI protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class HUIControlSurface extends AbstractControlSurface<HUIConfiguration>
{
    public static final int         HUI_FADER1                   = 0;
    public static final int         HUI_SELECT1                  = 1;
    public static final int         HUI_MUTE1                    = 2;
    public static final int         HUI_SOLO1                    = 3;
    public static final int         HUI_AUTO1                    = 4;
    public static final int         HUI_VSELECT1                 = 5;
    public static final int         HUI_INSERT1                  = 6;
    public static final int         HUI_ARM1                     = 7;

    public static final int         HUI_FADER2                   = 8;
    public static final int         HUI_SELECT2                  = 9;
    public static final int         HUI_MUTE2                    = 10;
    public static final int         HUI_SOLO2                    = 11;
    public static final int         HUI_AUTO2                    = 12;
    public static final int         HUI_VSELECT2                 = 13;
    public static final int         HUI_INSERT2                  = 14;
    public static final int         HUI_ARM2                     = 15;

    public static final int         HUI_FADER3                   = 16;
    public static final int         HUI_SELECT3                  = 17;
    public static final int         HUI_MUTE3                    = 18;
    public static final int         HUI_SOLO3                    = 19;
    public static final int         HUI_AUTO3                    = 20;
    public static final int         HUI_VSELECT3                 = 21;
    public static final int         HUI_INSERT3                  = 22;
    public static final int         HUI_ARM3                     = 23;

    public static final int         HUI_FADER4                   = 24;
    public static final int         HUI_SELECT4                  = 25;
    public static final int         HUI_MUTE4                    = 26;
    public static final int         HUI_SOLO4                    = 27;
    public static final int         HUI_AUTO4                    = 28;
    public static final int         HUI_VSELECT4                 = 29;
    public static final int         HUI_INSERT4                  = 30;
    public static final int         HUI_ARM4                     = 31;

    public static final int         HUI_FADER5                   = 32;
    public static final int         HUI_SELECT5                  = 33;
    public static final int         HUI_MUTE5                    = 34;
    public static final int         HUI_SOLO5                    = 35;
    public static final int         HUI_AUTO5                    = 36;
    public static final int         HUI_VSELECT5                 = 37;
    public static final int         HUI_INSERT5                  = 38;
    public static final int         HUI_ARM5                     = 39;

    public static final int         HUI_FADER6                   = 40;
    public static final int         HUI_SELECT6                  = 41;
    public static final int         HUI_MUTE6                    = 42;
    public static final int         HUI_SOLO6                    = 43;
    public static final int         HUI_AUTO6                    = 44;
    public static final int         HUI_VSELECT6                 = 45;
    public static final int         HUI_INSERT6                  = 46;
    public static final int         HUI_ARM6                     = 47;

    public static final int         HUI_FADER7                   = 48;
    public static final int         HUI_SELECT7                  = 49;
    public static final int         HUI_MUTE7                    = 50;
    public static final int         HUI_SOLO7                    = 51;
    public static final int         HUI_AUTO7                    = 52;
    public static final int         HUI_VSELECT7                 = 53;
    public static final int         HUI_INSERT7                  = 54;
    public static final int         HUI_ARM7                     = 55;

    public static final int         HUI_FADER8                   = 56;
    public static final int         HUI_SELECT8                  = 57;
    public static final int         HUI_MUTE8                    = 58;
    public static final int         HUI_SOLO8                    = 59;
    public static final int         HUI_AUTO8                    = 60;
    public static final int         HUI_VSELECT8                 = 61;
    public static final int         HUI_INSERT8                  = 62;
    public static final int         HUI_ARM8                     = 63;

    public static final int         HUI_KEY_CTRL_CLT             = 64;
    public static final int         HUI_KEY_SHIFT_AD             = 65;
    public static final int         HUI_KEY_EDITMODE             = 66;
    public static final int         HUI_KEY_UNDO                 = 67;
    public static final int         HUI_KEY_ALT_FINE             = 68;
    public static final int         HUI_KEY_OPTION_A             = 69;
    public static final int         HUI_KEY_EDITTOOL             = 70;
    public static final int         HUI_KEY_SAVE                 = 71;

    public static final int         HUI_WINDOW_MIX               = 72;
    public static final int         HUI_WINDOW_EDIT              = 73;
    public static final int         HUI_WINDOW_TRANSPRT          = 74;
    public static final int         HUI_WINDOW_MEM_LOC           = 75;
    public static final int         HUI_WINDOW_STATUS            = 76;
    public static final int         HUI_WINDOW_ALT               = 77;

    public static final int         HUI_CHANL_LEFT               = 80;
    public static final int         HUI_BANK_LEFT                = 81;
    public static final int         HUI_CHANL_RIGHT              = 82;
    public static final int         HUI_BANK_RIGHT               = 83;

    public static final int         HUI_ASSIGN1_OUTPUT           = 88;
    public static final int         HUI_ASSIGN1_INPUT            = 89;
    public static final int         HUI_ASSIGN1_PAN              = 90;
    public static final int         HUI_ASSIGN1_SEND_E           = 91;
    public static final int         HUI_ASSIGN1_SEND_D           = 92;
    public static final int         HUI_ASSIGN1_SEND_C           = 93;
    public static final int         HUI_ASSIGN1_SEND_B           = 94;
    public static final int         HUI_ASSIGN1_SEND_A           = 95;

    public static final int         HUI_ASSIGN2_ASSIGN           = 96;
    public static final int         HUI_ASSIGN2_DEFAULT          = 97;
    public static final int         HUI_ASSIGN2_SUSPEND          = 98;
    public static final int         HUI_ASSIGN2_SHIFT            = 99;
    public static final int         HUI_ASSIGN2_MUTE             = 100;
    public static final int         HUI_ASSIGN2_BYPASS           = 101;
    public static final int         HUI_ASSIGN2_RECRDYAL         = 102;

    public static final int         HUI_CURSOR_DOWN              = 104;
    public static final int         HUI_CURSOR_LEFT              = 105;
    public static final int         HUI_CURSOR_MODE              = 106;
    public static final int         HUI_CURSOR_RIGHT             = 107;
    public static final int         HUI_CURSOR_UP                = 108;
    public static final int         HUI_WHEEL_SCRUB              = 109;
    public static final int         HUI_WHEEL_SHUTTLE            = 110;

    public static final int         HUI_TRANSPORT_TALKBACK       = 112;
    public static final int         HUI_TRANSPORT_REWIND         = 113;
    public static final int         HUI_TRANSPORT_FAST_FWD       = 114;
    public static final int         HUI_TRANSPORT_STOP           = 115;
    public static final int         HUI_TRANSPORT_PLAY           = 116;
    public static final int         HUI_TRANSPORT_RECORD         = 117;

    public static final int         HUI_TRANSPORT_RETURN_TO_ZERO = 120;
    public static final int         HUI_TRANSPORT_TO_END         = 121;
    public static final int         HUI_TRANSPORT_ON_LINE        = 122;
    public static final int         HUI_TRANSPORT_LOOP           = 123;
    public static final int         HUI_TRANSPORT_QICK_PUNCH     = 124;

    public static final int         HUI_TRANSPORT_AUDITION       = 128;
    public static final int         HUI_TRANSPORT_PRE            = 129;
    public static final int         HUI_TRANSPORT_IN             = 130;
    public static final int         HUI_TRANSPORT_OUT            = 131;
    public static final int         HUI_TRANSPORT_POST           = 132;

    public static final int         HUI_CONTROL_ROOM_INPUT_3     = 136;
    public static final int         HUI_CONTROL_ROOM_INPUT_2     = 137;
    public static final int         HUI_CONTROL_ROOM_INPUT_1     = 138;
    public static final int         HUI_CONTROL_ROOM_MUTE        = 139;
    public static final int         HUI_CONTROL_ROOM_DISCRETE    = 140;

    public static final int         HUI_CONTROL_ROOM_OUTPUT_3    = 144;
    public static final int         HUI_CONTROL_ROOM_OUTPUT_2    = 145;
    public static final int         HUI_CONTROL_ROOM_OUTPUT_1    = 146;
    public static final int         HUI_CONTROL_ROOM_DIM         = 147;
    public static final int         HUI_CONTROL_ROOM_MONO        = 148;

    public static final int         HUI_NUM_0                    = 152;
    public static final int         HUI_NUM_1                    = 153;
    public static final int         HUI_NUM_4                    = 154;
    public static final int         HUI_NUM_2                    = 155;
    public static final int         HUI_NUM_5                    = 156;
    public static final int         HUI_NUM_DOT                  = 157;
    public static final int         HUI_NUM_3                    = 158;
    public static final int         HUI_NUM_6                    = 159;

    public static final int         HUI_NUM_ENTER                = 160;
    public static final int         HUI_NUM_PLUS                 = 161;

    public static final int         HUI_NUM_7                    = 168;
    public static final int         HUI_NUM_8                    = 169;
    public static final int         HUI_NUM_9                    = 170;
    public static final int         HUI_NUM_MINUS                = 171;
    public static final int         HUI_NUM_CLR                  = 172;
    public static final int         HUI_NUM_SET                  = 173;
    public static final int         HUI_NUM_DIV                  = 174;
    public static final int         HUI_NUM_MULT                 = 175;

    public static final int         HUI_TIMECODE                 = 176;
    public static final int         HUI_FEET                     = 177;
    public static final int         HUI_BEATS                    = 178;
    public static final int         HUI_RUDESOLO                 = 179;

    public static final int         HUI_AUTO_ENABLE_PLUG_IN      = 184;
    public static final int         HUI_AUTO_ENABLE_PAN          = 185;
    public static final int         HUI_AUTO_ENABLE_FADER        = 186;
    public static final int         HUI_AUTO_ENABLE_SENDMUTE     = 187;
    public static final int         HUI_AUTO_ENABLE_SEND         = 188;
    public static final int         HUI_AUTO_ENABLE_MUTE         = 189;

    public static final int         HUI_AUTO_MODE_TRIM           = 192;
    public static final int         HUI_AUTO_MODE_LATCH          = 193;
    public static final int         HUI_AUTO_MODE_READ           = 194;
    public static final int         HUI_AUTO_MODE_OFF            = 195;
    public static final int         HUI_AUTO_MODE_WRITE          = 196;
    public static final int         HUI_AUTO_MODE_TOUCH          = 197;

    public static final int         HUI_STATUS_PHASE             = 200;
    public static final int         HUI_STATUS_MONITOR           = 201;
    public static final int         HUI_STATUS_AUTO              = 202;
    public static final int         HUI_STATUS_SUSPEND           = 203;
    public static final int         HUI_STATUS_CREATE            = 204;
    public static final int         HUI_STATUS_GROUP             = 205;

    public static final int         HUI_EDIT_PASTE               = 208;
    public static final int         HUI_EDIT_CUT                 = 209;
    public static final int         HUI_EDIT_CAPTURE             = 210;
    public static final int         HUI_EDIT_DELETE              = 211;
    public static final int         HUI_EDIT_COPY                = 212;
    public static final int         HUI_EDIT_SEPARATE            = 213;

    public static final int         HUI_F1                       = 216;
    public static final int         HUI_F2                       = 217;
    public static final int         HUI_F3                       = 218;
    public static final int         HUI_F4                       = 219;
    public static final int         HUI_F5                       = 220;
    public static final int         HUI_F6                       = 221;
    public static final int         HUI_F7                       = 222;
    public static final int         HUI_F8_ESC                   = 223;

    public static final int         HUI_DSP_EDIT_INS_PARA        = 224;
    public static final int         HUI_DSP_EDIT_ASSIGN          = 225;
    public static final int         HUI_DSP_EDIT_SELECT_1        = 226;
    public static final int         HUI_DSP_EDIT_SELECT_2        = 227;
    public static final int         HUI_DSP_EDIT_SELECT_3        = 228;
    public static final int         HUI_DSP_EDIT_SELECT_4        = 229;
    public static final int         HUI_DSP_EDIT_BYPASS          = 230;
    public static final int         HUI_DSP_EDIT_COMPARE         = 231;

    public static final int         HUI_FS_RLAY1                 = 232;
    public static final int         HUI_FS_RLAY2                 = 233;
    public static final int         HUI_CLICK                    = 234;
    public static final int         HUI_BEEP                     = 235;

    private static final int []     HUI_BUTTONS_ALL              =
    {
        HUI_FADER1,
        HUI_SELECT1,
        HUI_MUTE1,
        HUI_SOLO1,
        HUI_AUTO1,
        HUI_VSELECT1,
        HUI_INSERT1,
        HUI_ARM1,
        HUI_FADER2,
        HUI_SELECT2,
        HUI_MUTE2,
        HUI_SOLO2,
        HUI_AUTO2,
        HUI_VSELECT2,
        HUI_INSERT2,
        HUI_ARM2,
        HUI_FADER3,
        HUI_SELECT3,
        HUI_MUTE3,
        HUI_SOLO3,
        HUI_AUTO3,
        HUI_VSELECT3,
        HUI_INSERT3,
        HUI_ARM3,
        HUI_FADER4,
        HUI_SELECT4,
        HUI_MUTE4,
        HUI_SOLO4,
        HUI_AUTO4,
        HUI_VSELECT4,
        HUI_INSERT4,
        HUI_ARM4,
        HUI_FADER5,
        HUI_SELECT5,
        HUI_MUTE5,
        HUI_SOLO5,
        HUI_AUTO5,
        HUI_VSELECT5,
        HUI_INSERT5,
        HUI_ARM5,
        HUI_FADER6,
        HUI_SELECT6,
        HUI_MUTE6,
        HUI_SOLO6,
        HUI_AUTO6,
        HUI_VSELECT6,
        HUI_INSERT6,
        HUI_ARM6,
        HUI_FADER7,
        HUI_SELECT7,
        HUI_MUTE7,
        HUI_SOLO7,
        HUI_AUTO7,
        HUI_VSELECT7,
        HUI_INSERT7,
        HUI_ARM7,
        HUI_FADER8,
        HUI_SELECT8,
        HUI_MUTE8,
        HUI_SOLO8,
        HUI_AUTO8,
        HUI_VSELECT8,
        HUI_INSERT8,
        HUI_ARM8,
        HUI_KEY_CTRL_CLT,
        HUI_KEY_SHIFT_AD,
        HUI_KEY_EDITMODE,
        HUI_KEY_UNDO,
        HUI_KEY_ALT_FINE,
        HUI_KEY_OPTION_A,
        HUI_KEY_EDITTOOL,
        HUI_KEY_SAVE,
        HUI_WINDOW_MIX,
        HUI_WINDOW_EDIT,
        HUI_WINDOW_TRANSPRT,
        HUI_WINDOW_MEM_LOC,
        HUI_WINDOW_STATUS,
        HUI_WINDOW_ALT,
        HUI_CHANL_LEFT,
        HUI_BANK_LEFT,
        HUI_CHANL_RIGHT,
        HUI_BANK_RIGHT,
        HUI_ASSIGN1_OUTPUT,
        HUI_ASSIGN1_INPUT,
        HUI_ASSIGN1_PAN,
        HUI_ASSIGN1_SEND_E,
        HUI_ASSIGN1_SEND_D,
        HUI_ASSIGN1_SEND_C,
        HUI_ASSIGN1_SEND_B,
        HUI_ASSIGN1_SEND_A,
        HUI_ASSIGN2_ASSIGN,
        HUI_ASSIGN2_DEFAULT,
        HUI_ASSIGN2_SUSPEND,
        HUI_ASSIGN2_SHIFT,
        HUI_ASSIGN2_MUTE,
        HUI_ASSIGN2_BYPASS,
        HUI_ASSIGN2_RECRDYAL,
        HUI_CURSOR_DOWN,
        HUI_CURSOR_LEFT,
        HUI_CURSOR_MODE,
        HUI_CURSOR_RIGHT,
        HUI_CURSOR_UP,
        HUI_WHEEL_SCRUB,
        HUI_WHEEL_SHUTTLE,
        HUI_TRANSPORT_TALKBACK,
        HUI_TRANSPORT_REWIND,
        HUI_TRANSPORT_FAST_FWD,
        HUI_TRANSPORT_STOP,
        HUI_TRANSPORT_PLAY,
        HUI_TRANSPORT_RECORD,
        HUI_TRANSPORT_RETURN_TO_ZERO,
        HUI_TRANSPORT_TO_END,
        HUI_TRANSPORT_ON_LINE,
        HUI_TRANSPORT_LOOP,
        HUI_TRANSPORT_QICK_PUNCH,
        HUI_TRANSPORT_AUDITION,
        HUI_TRANSPORT_PRE,
        HUI_TRANSPORT_IN,
        HUI_TRANSPORT_OUT,
        HUI_TRANSPORT_POST,
        HUI_CONTROL_ROOM_INPUT_3,
        HUI_CONTROL_ROOM_INPUT_2,
        HUI_CONTROL_ROOM_INPUT_1,
        HUI_CONTROL_ROOM_MUTE,
        HUI_CONTROL_ROOM_DISCRETE,
        HUI_CONTROL_ROOM_OUTPUT_3,
        HUI_CONTROL_ROOM_OUTPUT_2,
        HUI_CONTROL_ROOM_OUTPUT_1,
        HUI_CONTROL_ROOM_DIM,
        HUI_CONTROL_ROOM_MONO,
        HUI_NUM_0,
        HUI_NUM_1,
        HUI_NUM_4,
        HUI_NUM_2,
        HUI_NUM_5,
        HUI_NUM_DOT,
        HUI_NUM_3,
        HUI_NUM_6,
        HUI_NUM_ENTER,
        HUI_NUM_PLUS,
        HUI_NUM_7,
        HUI_NUM_8,
        HUI_NUM_9,
        HUI_NUM_MINUS,
        HUI_NUM_CLR,
        HUI_NUM_SET,
        HUI_NUM_DIV,
        HUI_NUM_MULT,
        HUI_TIMECODE,
        HUI_FEET,
        HUI_BEATS,
        HUI_RUDESOLO,
        HUI_AUTO_ENABLE_PLUG_IN,
        HUI_AUTO_ENABLE_PAN,
        HUI_AUTO_ENABLE_FADER,
        HUI_AUTO_ENABLE_SENDMUTE,
        HUI_AUTO_ENABLE_SEND,
        HUI_AUTO_ENABLE_MUTE,
        HUI_AUTO_MODE_TRIM,
        HUI_AUTO_MODE_LATCH,
        HUI_AUTO_MODE_READ,
        HUI_AUTO_MODE_OFF,
        HUI_AUTO_MODE_WRITE,
        HUI_AUTO_MODE_TOUCH,
        HUI_STATUS_PHASE,
        HUI_STATUS_MONITOR,
        HUI_STATUS_AUTO,
        HUI_STATUS_SUSPEND,
        HUI_STATUS_CREATE,
        HUI_STATUS_GROUP,
        HUI_EDIT_PASTE,
        HUI_EDIT_CUT,
        HUI_EDIT_CAPTURE,
        HUI_EDIT_DELETE,
        HUI_EDIT_COPY,
        HUI_EDIT_SEPARATE,
        HUI_F1,
        HUI_F2,
        HUI_F3,
        HUI_F4,
        HUI_F5,
        HUI_F6,
        HUI_F7,
        HUI_F8_ESC,
        HUI_DSP_EDIT_INS_PARA,
        HUI_DSP_EDIT_ASSIGN,
        HUI_DSP_EDIT_SELECT_1,
        HUI_DSP_EDIT_SELECT_2,
        HUI_DSP_EDIT_SELECT_3,
        HUI_DSP_EDIT_SELECT_4,
        HUI_DSP_EDIT_BYPASS,
        HUI_DSP_EDIT_COMPARE
    };

    private static final boolean [] HUI_BUTTON_UPDATE;
    static
    {
        HUI_BUTTON_UPDATE = new boolean [128];
        Arrays.fill (HUI_BUTTON_UPDATE, false);
    }

    public static final int                                                    KNOB_LED_MODE_OFF        = -1;
    public static final int                                                    KNOB_LED_MODE_SINGLE_DOT = 0;
    public static final int                                                    KNOB_LED_MODE_BOOST_CUT  = 1;
    public static final int                                                    KNOB_LED_MODE_WRAP       = 2;
    public static final int                                                    KNOB_LED_MODE_SPREAD     = 3;

    private HUISegmentDisplay                                                  segmentDisplay;
    private HUIMainDisplay                                                     mainDisplay;
    private int []                                                             knobValues               = new int [8];

    // The currently selected zone (area of a group of buttons)
    private int                                                                zone;

    @SuppressWarnings("unchecked")
    private final FaderAbsoluteCommand<HUIControlSurface, HUIConfiguration> [] faderCommands            = new FaderAbsoluteCommand [8];
    @SuppressWarnings("unchecked")
    private final KnobRowModeCommand<HUIControlSurface, HUIConfiguration> []   knobCommands             = new KnobRowModeCommand [8];
    private final PlayPositionCommand<HUIControlSurface, HUIConfiguration>     playPositionCommand;

    private final int []                                                       faderHiValues            = new int [8];


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
     * @param model The model
     */
    public HUIControlSurface (final IHost host, final ColorManager colorManager, final HUIConfiguration configuration, final IMidiOutput output, final IMidiInput input, final IModel model)
    {
        super (host, configuration, colorManager, output, input, null, HUI_BUTTONS_ALL);

        this.shiftButtonId = HUI_KEY_SHIFT_AD;
        this.selectButtonId = HUI_KEY_OPTION_A;
        this.leftButtonId = HUI_CURSOR_LEFT;
        this.rightButtonId = HUI_CURSOR_RIGHT;
        this.upButtonId = HUI_CURSOR_UP;
        this.downButtonId = HUI_CURSOR_DOWN;

        Arrays.fill (this.knobValues, -1);

        for (int i = 0; i < 8; i++)
        {
            this.faderCommands[i] = new FaderAbsoluteCommand<> (i, model, this);
            this.knobCommands[i] = new KnobRowModeCommand<> (i, model, this);
        }
        this.playPositionCommand = new PlayPositionCommand<> (model, this);
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        super.shutdown ();

        this.segmentDisplay.shutdown ();
        this.mainDisplay.shutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public void setButton (final int button, final int state)
    {
        // Select the zone
        this.output.sendCC (0x0C, button / 8);
        // Turn on / off button
        this.output.sendCC (0x2C, (state > 0 ? 0x40 : 0x00) + button % 8);
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
        int v = 0;

        if (knobLEDMode != KNOB_LED_MODE_OFF)
        {
            final int rescale = (int) Math.round (value * 10.0 / maxValue);
            v = 0x10 * knobLEDMode + rescale + 1;
        }

        if (this.knobValues[index] == v)
            return;

        this.knobValues[index] = v;
        this.output.sendCC (0x10 + index, v);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleMidi (final int status, final int data1, final int data2)
    {
        final int code = status & 0xF0;
        if (code != 0xB0)
            return;

        switch (data1)
        {
            // Move fader hi-byte
            case 0x00:
            case 0x01:
            case 0x02:
            case 0x03:
            case 0x04:
            case 0x05:
            case 0x06:
            case 0x07:
                this.faderHiValues[data1] = data2;
                break;

            case 0x0d:
                this.playPositionCommand.execute (data2);
                break;

            // Button zone selection
            case 0x0F:
                this.zone = data2;
                break;

            // Move fader lo-byte
            case 0x20:
            case 0x21:
            case 0x22:
            case 0x23:
            case 0x24:
            case 0x25:
            case 0x26:
            case 0x27:
                final int chnl = data1 - 0x20;
                final int value = (this.faderHiValues[chnl] << 7) + data2;
                this.faderCommands[chnl].execute (value);
                break;

            // Button port up/down (a button in the selected row)
            case 0x2F:
                final boolean isDown = data2 >= 0x40;
                final int cc = this.zone * 8 + data2 % 8;
                if (this.isButton (cc))
                    this.handleCC (0, cc, isDown ? 127 : 0);
                break;

            case 0x40:
            case 0x41:
            case 0x42:
            case 0x43:
            case 0x44:
            case 0x45:
            case 0x46:
            case 0x47:
                final int channel = data1 - 0x40;
                this.knobCommands[channel].execute (data2);
                break;

            default:
                this.host.println ("Unhandled midi CC: " + data1);
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


    /**
     * Set the main display.
     *
     * @param mainDisplay The main display
     */
    public void setMainDisplay (final HUIMainDisplay mainDisplay)
    {
        this.mainDisplay = mainDisplay;
    }


    /**
     * Get the main display.
     *
     * @return The main display
     */
    public HUIMainDisplay getMainDisplay ()
    {
        return this.mainDisplay;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isGridNote (final int note)
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    protected void scheduledFlush ()
    {
        super.scheduledFlush ();

        if (this.mainDisplay != null)
            this.mainDisplay.flush ();
    }
}