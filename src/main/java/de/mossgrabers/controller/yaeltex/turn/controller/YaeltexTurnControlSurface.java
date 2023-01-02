// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.yaeltex.turn.controller;

import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;

import java.util.Arrays;


/**
 * The Yaeltex Turn control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class YaeltexTurnControlSurface extends AbstractControlSurface<YaeltexTurnConfiguration>
{
    // Zero based!
    public static final int MIDI_CHANNEL_MAIN          = 15;
    public static final int MIDI_CHANNEL_SET_COLOR     = 13;
    public static final int MIDI_CHANNEL_SET_INTENSITY = 12;

    // MIDI Notes on MIDI channel 16
    public static final int BUTTON_CLIPS               = 8;
    public static final int BUTTON_USR                 = 9;
    public static final int BUTTON_PLAY                = 10;
    public static final int BUTTON_REC                 = 11;
    public static final int BUTTON_LOOP                = 12;
    public static final int BUTTON_LEFT                = 13;
    public static final int BUTTON_SELECT              = 14;
    public static final int BUTTON_SHIFT               = 15;

    public static final int BUTTON_SESSION             = 0;
    public static final int BUTTON_TRK                 = 1;
    public static final int BUTTON_STOP                = 2;
    public static final int BUTTON_OVERDUB             = 3;
    public static final int BUTTON_TAP_TEMPO           = 4;
    public static final int BUTTON_RIGHT               = 5;
    public static final int BUTTON_UP                  = 6;
    public static final int BUTTON_DOWN                = 7;

    // MIDI Notes on MIDI channel 15
    public static final int BUTTON_ROW1_1              = 70;
    public static final int BUTTON_ROW2_1              = 86;
    public static final int BUTTON_ROW3_1              = 102;
    public static final int BUTTON_ROW4_1              = 110;

    // Digital knobs from CC00 to CC31 on MIDI channel 16
    public static final int KNOB_DIGITAL_ROW1          = 0;

    // Analog knobs from CC32 to CC63 on MIDI channel 16
    public static final int KNOB_ANALOG_ROW1           = 32;

    // Faders from CC64 to CC71 on MIDI channel 16
    public static final int FADER1                     = 64;

    public static final int KNOB_ANALOG_TEMPO          = 72;
    public static final int KNOB_ANALOG_A_B            = 73;
    public static final int KNOB_ANALOG_CUE            = 74;
    public static final int KNOB_ANALOG_MASTER         = 75;

    private final int []    knobCache                  = new int [128];
    private final int []    knobColorCache             = new int [128];


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     */
    public YaeltexTurnControlSurface (final IHost host, final ColorManager colorManager, final YaeltexTurnConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, new YaeltexTurnPadGrid (colorManager, output), 700, 860);

        this.defaultMidiChannel = MIDI_CHANNEL_MAIN;

        Arrays.fill (this.knobCache, -1);
        Arrays.fill (this.knobColorCache, -1);
    }


    /** {@inheritDoc} */
    @Override
    protected void flushHardware ()
    {
        super.flushHardware ();

        ((YaeltexTurnPadGrid) this.padGrid).flush ();
    }


    /**
     * Set an LED ring on the device. Values are cached and only sent if changed.
     *
     * @param knob The knobs CC value
     * @param value The value for the LED ring
     * @param color The index of the color for the LED ring
     */
    public void setLED (final int knob, final int value, final int color)
    {
        if (this.knobCache[knob] != value)
        {
            this.knobCache[knob] = value;
            this.output.sendCCEx (MIDI_CHANNEL_MAIN, knob, value);
        }

        if (this.knobColorCache[knob] != color)
        {
            this.knobColorCache[knob] = color;
            this.output.sendCCEx (MIDI_CHANNEL_SET_COLOR, knob, color);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        super.internalShutdown ();

        for (int i = 0; i < 32; i++)
            this.setLED (KNOB_DIGITAL_ROW1 + i, 0, YaeltexTurnColorManager.BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int cc, final int value)
    {
        if (bindType == BindType.CC || channel != YaeltexTurnControlSurface.MIDI_CHANNEL_MAIN)
            super.setTrigger (bindType, channel, cc, value);
        else
        {
            // Little trick to identify values which need an intensity
            final boolean isLow = value >= 128;
            this.output.sendNoteEx (YaeltexTurnControlSurface.MIDI_CHANNEL_MAIN, cc, isLow ? value - 128 : value);
            this.output.sendNoteEx (YaeltexTurnControlSurface.MIDI_CHANNEL_SET_INTENSITY, cc, isLow ? 0 : 127);
        }
    }
}