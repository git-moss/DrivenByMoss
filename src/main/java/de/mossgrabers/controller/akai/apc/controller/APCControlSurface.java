// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.controller;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;

import java.util.Arrays;


/**
 * The APC 1 and APC 2 control surface.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings("javadoc")
public class APCControlSurface extends AbstractControlSurface<APCConfiguration>
{
    // MIDI Notes
    public static final int     APC_BUTTON_RECORD_ARM          = 0x30;
    public static final int     APC_BUTTON_SOLO                = 0x31;
    public static final int     APC_BUTTON_ACTIVATOR           = 0x32;
    public static final int     APC_BUTTON_TRACK_SELECTION     = 0x33;
    public static final int     APC_BUTTON_CLIP_STOP           = 0x34;
    public static final int     APC_BUTTON_CLIP_LAUNCH_1       = 0x35;
    public static final int     APC_BUTTON_CLIP_LAUNCH_2       = 0x36;
    public static final int     APC_BUTTON_CLIP_LAUNCH_3       = 0x37;
    public static final int     APC_BUTTON_CLIP_LAUNCH_4       = 0x38;
    public static final int     APC_BUTTON_CLIP_LAUNCH_5       = 0x39;
    public static final int     APC_BUTTON_CLIP_TRACK          = 0x3A;
    public static final int     APC_BUTTON_DEVICE_ON_OFF       = 0x3B;
    public static final int     APC_BUTTON_DEVICE_LEFT         = 0x3C;
    public static final int     APC_BUTTON_DEVICE_RIGHT        = 0x3D;
    public static final int     APC_BUTTON_DETAIL_VIEW         = 0x3E;
    public static final int     APC_BUTTON_REC_QUANT           = 0x3F;
    public static final int     APC_BUTTON_MIDI_OVERDUB        = 0x40;
    public static final int     APC_BUTTON_METRONOME           = 0x41;
    public static final int     APC_BUTTON_A_B                 = 0x42;         // mkII
    public static final int     APC_BUTTON_MASTER              = 0x50;
    public static final int     APC_BUTTON_STOP_ALL_CLIPS      = 0x51;
    public static final int     APC_BUTTON_SCENE_LAUNCH_1      = 0x52;
    public static final int     APC_BUTTON_SCENE_LAUNCH_2      = 0x53;
    public static final int     APC_BUTTON_SCENE_LAUNCH_3      = 0x54;
    public static final int     APC_BUTTON_SCENE_LAUNCH_4      = 0x55;
    public static final int     APC_BUTTON_SCENE_LAUNCH_5      = 0x56;
    public static final int     APC_BUTTON_PAN                 = 0x57;
    public static final int     APC_BUTTON_SEND_A              = 0x58;
    public static final int     APC_BUTTON_SEND_B              = 0x59;
    public static final int     APC_BUTTON_SEND_C              = 0x5A;
    public static final int     APC_BUTTON_PLAY                = 0x5B;
    public static final int     APC_BUTTON_STOP                = 0x5C;
    public static final int     APC_BUTTON_RECORD              = 0x5D;
    public static final int     APC_BUTTON_UP                  = 0x5E;
    public static final int     APC_BUTTON_DOWN                = 0x5F;
    public static final int     APC_BUTTON_RIGHT               = 0x60;
    public static final int     APC_BUTTON_LEFT                = 0x61;
    public static final int     APC_BUTTON_SHIFT               = 0x62;
    public static final int     APC_BUTTON_TAP_TEMPO           = 0x63;
    public static final int     APC_BUTTON_NUDGE_PLUS          = 0x64;
    public static final int     APC_BUTTON_NUDGE_MINUS         = 0x65;
    public static final int     APC_BUTTON_SESSION             = 0x66;         // mkII
    public static final int     APC_BUTTON_BANK                = 0x67;         // mkII

    // MIDI CC
    public static final int     APC_KNOB_TRACK_LEVEL           = 0x07;
    public static final int     APC_KNOB_TEMPO                 = 0x0D;         // mkII
    public static final int     APC_KNOB_MASTER_LEVEL          = 0x0E;
    public static final int     APC_KNOB_CROSSFADER            = 0x0F;
    public static final int     APC_KNOB_DEVICE_KNOB_1         = 0x10;
    public static final int     APC_KNOB_DEVICE_KNOB_2         = 0x11;
    public static final int     APC_KNOB_DEVICE_KNOB_3         = 0x12;
    public static final int     APC_KNOB_DEVICE_KNOB_4         = 0x13;
    public static final int     APC_KNOB_DEVICE_KNOB_5         = 0x14;
    public static final int     APC_KNOB_DEVICE_KNOB_6         = 0x15;
    public static final int     APC_KNOB_DEVICE_KNOB_7         = 0x16;
    public static final int     APC_KNOB_DEVICE_KNOB_8         = 0x17;
    public static final int     APC_KNOB_DEVICE_KNOB_LED_1     = 0x18;
    public static final int     APC_KNOB_DEVICE_KNOB_LED_2     = 0x19;
    public static final int     APC_KNOB_DEVICE_KNOB_LED_3     = 0x1A;
    public static final int     APC_KNOB_DEVICE_KNOB_LED_4     = 0x1B;
    public static final int     APC_KNOB_DEVICE_KNOB_LED_5     = 0x1C;
    public static final int     APC_KNOB_DEVICE_KNOB_LED_6     = 0x1D;
    public static final int     APC_KNOB_DEVICE_KNOB_LED_7     = 0x1E;
    public static final int     APC_KNOB_DEVICE_KNOB_LED_8     = 0x1F;
    public static final int     APC_KNOB_CUE_LEVEL             = 0x2F;
    public static final int     APC_KNOB_TRACK_KNOB_1          = 0x30;
    public static final int     APC_KNOB_TRACK_KNOB_2          = 0x31;
    public static final int     APC_KNOB_TRACK_KNOB_3          = 0x32;
    public static final int     APC_KNOB_TRACK_KNOB_4          = 0x33;
    public static final int     APC_KNOB_TRACK_KNOB_5          = 0x34;
    public static final int     APC_KNOB_TRACK_KNOB_6          = 0x35;
    public static final int     APC_KNOB_TRACK_KNOB_7          = 0x36;
    public static final int     APC_KNOB_TRACK_KNOB_8          = 0x37;
    public static final int     APC_KNOB_TRACK_KNOB_LED_MODE_1 = 0x38;
    public static final int     APC_KNOB_TRACK_KNOB_LED_MODE_2 = 0x39;
    public static final int     APC_KNOB_TRACK_KNOB_LED_MODE_3 = 0x3A;
    public static final int     APC_KNOB_TRACK_KNOB_LED_MODE_4 = 0x3B;
    public static final int     APC_KNOB_TRACK_KNOB_LED_MODE_5 = 0x3C;
    public static final int     APC_KNOB_TRACK_KNOB_LED_MODE_6 = 0x3D;
    public static final int     APC_KNOB_TRACK_KNOB_LED_MODE_7 = 0x3E;
    public static final int     APC_KNOB_TRACK_KNOB_LED_MODE_8 = 0x3F;
    public static final int     APC_FOOTSWITCH_1               = 0x40;
    public static final int     APC_FOOTSWITCH_2               = 0x43;

    private static final String ID_APC_40                      = "73";
    private static final String ID_APC_40_MKII                 = "29";

    public static final int     LED_MODE_SINGLE                = 1;
    public static final int     LED_MODE_VOLUME                = 2;
    public static final int     LED_MODE_PAN                   = 3;

    private final boolean       isMkII;
    private final int []        knobCache                      = new int [128];


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     * @param isMkII True if is mkII
     */
    public APCControlSurface (final IHost host, final ColorManager colorManager, final APCConfiguration configuration, final IMidiOutput output, final IMidiInput input, final boolean isMkII)
    {
        super (host, configuration, colorManager, output, input, new APCPadGrid (colorManager, output, isMkII), 800, isMkII ? 480 : 640);

        this.isMkII = isMkII;

        Arrays.fill (this.knobCache, -1);

        // Set Mode 2
        this.output.sendSysex ("F0 47 7F " + (isMkII ? ID_APC_40_MKII : ID_APC_40) + " 60 00 04 41 08 02 01 F7");
    }


    /**
     * Get if it is the MkII model.
     *
     * @return True if it is the MkII model otherwise it is MkI
     */
    public boolean isMkII ()
    {
        return this.isMkII;
    }


    /**
     * Set an LED ring on the device. Values are cached and only sent if changed.
     *
     * @param knob The knobs CC value
     * @param value The value for the LED ring
     */
    public void setLED (final int knob, final int value)
    {
        if (this.knobCache[knob] == value)
            return;
        this.knobCache[knob] = value;
        this.scheduleTask ( () -> {

            if (this.knobCache[knob] == value)
                this.output.sendCC (knob, value);

        }, 100);
    }
}