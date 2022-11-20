// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.controller;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;

import java.util.Arrays;
import java.util.List;


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

    // CC: 41-43 increase, 1-3 decrease
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

    public static final String      SYSEX_HDR             = "F0 00 00 66 14 ";
    public static final int         MCU_SYSEX_CMD_DISPLAY = 0x12;

    private static final boolean [] MCU_BUTTON_UPDATE;
    static
    {
        MCU_BUTTON_UPDATE = new boolean [128];
        Arrays.fill (MCU_BUTTON_UPDATE, false);
    }

    public static final int               VUMODE_LED               = 1;
    public static final int               VUMODE_OFF               = 2;
    public static final int               VUMODE_LED_AND_LCD       = 3;
    public static final int               VUMODE_LCD               = 4;

    public static final int               KNOB_LED_MODE_SINGLE_DOT = 0;
    public static final int               KNOB_LED_MODE_BOOST_CUT  = 1;
    public static final int               KNOB_LED_MODE_WRAP       = 2;
    public static final int               KNOB_LED_MODE_SPREAD     = 3;

    private int                           activeVuMode             = VUMODE_LED;
    private final int []                  knobValues               = new int [8];
    private byte []                       currentDisplayColors     = new byte [8];

    private final List<MCUControlSurface> surfaces;
    private final int                     extenderOffset;
    private final boolean                 isMainDevice;


    /**
     * Constructor.
     *
     * @param surfaces All surfaces to be able to check for status keys like Shift.
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     * @param extenderOffset The channel/bank offset if multiple extenders are used
     * @param isMainDevice True if it is the main MCU controller (and not an extender)
     */
    public MCUControlSurface (final List<MCUControlSurface> surfaces, final IHost host, final ColorManager colorManager, final MCUConfiguration configuration, final IMidiOutput output, final IMidiInput input, final int extenderOffset, final boolean isMainDevice)
    {
        super (surfaces.size (), host, configuration, colorManager, output, input, null, isMainDevice ? 1000 : 600, 1000);

        this.surfaces = surfaces;
        this.extenderOffset = extenderOffset;
        this.isMainDevice = isMainDevice;

        Arrays.fill (this.knobValues, -1);
        Arrays.fill (this.currentDisplayColors, (byte) -1);
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        final IMidiOutput output = this.getMidiOutput ();
        for (int i = 0; i < 8; i++)
        {
            output.sendChannelAftertouch (0x10 * i, 0);
            output.sendPitchbend (i, 0, 0);
        }
        output.sendChannelAftertouch (1, 0, 0);
        output.sendChannelAftertouch (1, 0x10, 0);
        output.sendPitchbend (8, 0, 0);

        final ColorEx [] colors = new ColorEx [8];
        for (int i = 0; i < 8; i++)
            colors[i] = ColorEx.WHITE;
        this.sendDisplayColor (colors);

        super.internalShutdown ();
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
        // value = 0 turns the LEDs off, value range is 1-11, center is 6
        int rescale = (int) Math.round (value * 11.0 / maxValue);
        if (value > 0 && rescale == 0)
            rescale = 1;
        int v = knobLEDMode << 4;
        v += rescale;

        if (this.knobValues[index] == v)
            return;

        this.knobValues[index] = v;
        this.output.sendCC (0x30 + index, v);
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


    // LEDs on/LEDs and VU-meter on display on/VU-meter on display on/all off
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

        // The MCU changes the VU-meter mode when receiving the corresponding system exclusive
        // message
        final IMidiOutput out = this.getMidiOutput ();
        switch (this.activeVuMode)
        {
            case VUMODE_LED:
                for (int i = 0; i < 8; i++)
                {
                    // resets the LEDs (and VU-meters on the display?)
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


    /**
     * Sets the display back-light colors of a X-Touch (Extender).
     *
     * @param colors The colors to set, must be exactly 8
     */
    public void sendDisplayColor (final ColorEx [] colors)
    {
        if (!this.configuration.hasDisplayColors ())
            return;

        ColorEx [] cs = colors;
        if (this.isMainDevice && this.getTextDisplay ().isNotificationActive ())
        {
            cs = new ColorEx [8];
            Arrays.fill (cs, ColorEx.WHITE);
        }

        final byte [] displayColors = new byte [8];
        for (int i = 0; i < 8; i++)
            displayColors[i] = toIndex (cs[i] == null ? ColorEx.BLACK : cs[i]);

        if (Arrays.compare (displayColors, this.currentDisplayColors) == 0)
            return;

        this.currentDisplayColors = displayColors;

        final byte [] sysexMessage = new byte [15];
        sysexMessage[0] = (byte) 0xF0;
        sysexMessage[1] = 0x00;
        sysexMessage[2] = 0x00;
        sysexMessage[3] = 0x66;
        sysexMessage[4] = this.isMainDevice ? (byte) 0x14 : (byte) 0x15;
        sysexMessage[5] = 0x72;
        for (int i = 0; i < 8; i++)
            sysexMessage[6 + i] = displayColors[i];
        sysexMessage[14] = (byte) 0xF7;
        this.getMidiOutput ().sendSysex (sysexMessage);
    }


    /**
     * Convert a RGB color to a color index (0-7).
     * <ul>
     * <li>0 - black : 0, 0, 0
     * <li>1 - blue : 0, 0, 1
     * <li>2 - green : 0, 1, 0
     * <li>3 - cyan : 0, 1, 1
     * <li>4 - red : 1, 0, 0
     * <li>5 - pink : 1, 0, 1
     * <li>6 - yellow: 1, 1, 0
     * <li>7 - white : 1, 1, 1
     * </ul>
     *
     * @param color The color to transform
     * @return The color index
     */
    private static byte toIndex (final ColorEx color)
    {
        final double red = color.getRed ();
        final double green = color.getGreen ();
        final double blue = color.getBlue ();
        if (red < 0.5 && green < 0.5 && blue < 0.5 && (red > 0 || green > 0 || blue > 0))
        {
            // Use white for dim colors
            return 7;
        }

        final int hasRed = red >= 0.5 ? 1 : 0;
        final int hasGreen = green >= 0.5 ? 1 : 0;
        final int hasBlue = blue >= 0.5 ? 1 : 0;
        return (byte) (hasBlue * 4 + hasGreen * 2 + hasRed);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPressed (final ButtonID buttonID)
    {
        // Check on all MCU surfaces for state button presses

        for (final MCUControlSurface surface: this.surfaces)
        {
            if (surface.isSinglePressed (buttonID))
                return true;
        }
        return false;
    }


    private boolean isSinglePressed (final ButtonID buttonID)
    {
        return super.isPressed (buttonID);
    }


    /**
     * Returns true if this is the 'most right device'.
     *
     * @return True if this is the 'most right device'
     */
    public boolean isLastDevice ()
    {
        return this.surfaceID == this.surfaces.size () - 1;
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