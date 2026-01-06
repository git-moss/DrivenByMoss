// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.graphics.canvas.component.LabelComponent;
import de.mossgrabers.framework.graphics.canvas.component.LabelComponent.LabelLayout;


/**
 * The OXI One control surface.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneControlSurface extends AbstractControlSurface<OxiOneConfiguration>
{
    /** Knob 1: the velocity knob. */
    public static final int                    KNOB1_VELOCITY      = 0;
    /** Knob 2: the octave knob. */
    public static final int                    KNOB2_OCTAVE        = 1;
    /** Knob 3: the filter knob touch. */
    public static final int                    KNOB3_GATE          = 2;
    /** Knob 4: the resonance knob. */
    public static final int                    KNOB4_MODULATION    = 3;

    /** The Arpeggiator button. */
    public static final int                    BUTTON_ARP          = 0;
    /** The 4th sequencer track button. */
    public static final int                    BUTTON_SEQUENCER4   = 1;
    /** The 16/left button. */
    public static final int                    BUTTON_16_LEFT      = 2;
    /** The End button. */
    public static final int                    BUTTON_END          = 3;

    /** The keyboard/preview button. */
    public static final int                    BUTTON_KEYBOARD     = 4;
    /** The 3rd sequencer track button. */
    public static final int                    BUTTON_SEQUENCER3   = 5;
    /** The 32/up button. */
    public static final int                    BUTTON_32_UP        = 6;
    /** The Init/x2 button. */
    public static final int                    BUTTON_INIT         = 7;

    /** The arranger button. */
    public static final int                    BUTTON_ARRANGER     = 8;
    /** The 2nd sequencer track button. */
    public static final int                    BUTTON_SEQUENCER2   = 9;
    /** The 48/down button. */
    public static final int                    BUTTON_48_DOWN      = 10;
    /** The Save button. */
    public static final int                    BUTTON_SAVE         = 11;

    /** The back button. */
    public static final int                    BUTTON_BACK         = 12;
    /** The 1st sequencer track button. */
    public static final int                    BUTTON_SEQUENCER1   = 13;
    /** The 64/right button. */
    public static final int                    BUTTON_64_RIGHT     = 14;
    /** The Load button. */
    public static final int                    BUTTON_LOAD         = 15;

    /** The encoder 1 button. */
    public static final int                    BUTTON_ENCODER1     = 16;
    /** The Shift button. */
    public static final int                    BUTTON_SHIFT        = 17;
    /** The MOD button. */
    public static final int                    BUTTON_MOD          = 18;
    /** The Copy button. */
    public static final int                    BUTTON_COPY         = 19;

    /** The encoder 2 button. */
    public static final int                    BUTTON_ENCODER2     = 20;
    /** The Stop button. */
    public static final int                    BUTTON_STOP         = 21;
    /** The Division button. */
    public static final int                    BUTTON_DIVISION     = 22;
    /** The Paste button. */
    public static final int                    BUTTON_PASTE        = 23;

    /** The encoder 3 button. */
    public static final int                    BUTTON_ENCODER3     = 24;
    /** The Play button. */
    public static final int                    BUTTON_PLAY         = 25;
    /** The LFO button. */
    public static final int                    BUTTON_LFO          = 26;
    /** The Undo button. */
    public static final int                    BUTTON_UNDO         = 27;

    /** The encoder 4 button. */
    public static final int                    BUTTON_ENCODER4     = 28;
    /** The Record button. */
    public static final int                    BUTTON_REC          = 29;
    /** The Step Chord button. */
    public static final int                    BUTTON_STEP_CHORD   = 30;
    /** The Random button. */
    public static final int                    BUTTON_RANDOM       = 31;

    /** The Mute button. */
    public static final int                    BUTTON_MUTE         = 32;

    private static final int                   LEDS_BACK           = 0;
    private static final int                   LEDS_CONFIG         = 1;
    private static final int                   LEDS_ARRANGER_SHOW  = 2;
    private static final int                   LEDS_ARRANGER_STATE = 3;
    private static final int                   LEDS_KEYBOARD       = 4;
    private static final int                   LEDS_PREVIEW        = 5;
    private static final int                   LEDS_ARP            = 6;
    private static final int                   LEDS_ARP_HOLD       = 7;
    private static final int                   LEDS_SHIFT          = 8;
    private static final int                   LEDS_STOP           = 9;
    private static final int                   LEDS_SEQ_1          = 10;
    private static final int                   LEDS_REC            = 11;
    private static final int                   LEDS_PLAY           = 12;
    private static final int                   LEDS_SEQ_3          = 13;
    private static final int                   LEDS_NUDGE          = 14;
    private static final int                   LEDS_SYNC           = 15;
    private static final int                   LEDS_SEQ_1_SEL      = 16;
    private static final int                   LEDS_SEQ_2          = 17;
    private static final int                   LEDS_SEQ_2_SEL      = 18;
    private static final int                   LEDS_MUTE           = 19;
    private static final int                   LEDS_LOAD           = 20;
    private static final int                   LEDS_SEQ_4          = 21;
    private static final int                   LEDS_SEQ_4_SEL      = 22;
    private static final int                   LEDS_SEQ_3_SEL      = 23;
    private static final int                   LEDS_SAVE           = 24;
    private static final int                   LEDS_CLEAR          = 25;
    private static final int                   LEDS_DUPLICATE      = 26;
    private static final int                   LEDS_PASTE          = 27;
    private static final int                   LEDS_COPY           = 28;
    private static final int                   LEDS_UNDO           = 29;
    private static final int                   LEDS_RANDOM         = 30;
    private static final int                   LEDS_REDO           = 31;
    private static final int                   LEDS_RANDOM2        = 32;
    private static final int                   LEDS_INIT           = 33;
    private static final int                   LEDS_X2             = 34;
    private static final int                   LEDS_END            = 35;
    private static final int                   LEDS_2              = 36;
    private static final int                   LEDS_MOD            = 37;
    private static final int                   LEDS_DIVISION       = 38;
    private static final int                   LEDS_FOLLOW         = 39;
    private static final int                   LEDS_LFO            = 40;
    private static final int                   LEDS_CONDENSE       = 41;
    private static final int                   LEDS_CVOUT          = 42;
    private static final int                   LEDS_STEP_CHORD     = 43;
    private static final int                   LEDS_EXPAND         = 44;
    private static final int                   LEDS_16             = 45;
    private static final int                   LEDS_LEFT           = 46;
    private static final int                   LEDS_32             = 47;
    private static final int                   LEDS_UP             = 48;
    private static final int                   LEDS_48             = 49;
    private static final int                   LEDS_RIGHT          = 50;
    private static final int                   LEDS_64             = 51;
    private static final int                   LEDS_DOWN           = 52;
    private static final int                   LEDS_NO_LED         = 53;
    private static final int                   LEDS_PLAY2          = 54;

    private static final List<Integer>         NORMAL_LEDS         = new ArrayList<> ();
    private static final List<Integer>         SHIFTED_LEDS        = new ArrayList<> ();
    private static final Map<Integer, Integer> BUTTON_LEDS         = new HashMap<> ();
    private static final Map<Integer, Integer> SHIFTED_BUTTON_LEDS = new HashMap<> ();

    static
    {
        NORMAL_LEDS.add (Integer.valueOf (LEDS_BACK));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_ARRANGER_SHOW));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_KEYBOARD));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_ARP_HOLD));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_STOP));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_SEQ_1));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_SEQ_2));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_SEQ_3));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_SEQ_4));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_PLAY));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_MUTE));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_PASTE));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_DUPLICATE));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_UNDO));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_RANDOM));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_INIT));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_END));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_MOD));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_DIVISION));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_LFO));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_STEP_CHORD));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_16));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_32));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_48));
        NORMAL_LEDS.add (Integer.valueOf (LEDS_64));

        SHIFTED_LEDS.add (Integer.valueOf (LEDS_CONFIG));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_ARRANGER_STATE));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_PREVIEW));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_ARP));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_SYNC));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_SEQ_1_SEL));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_SEQ_2_SEL));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_SEQ_3_SEL));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_SEQ_4_SEL));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_PLAY2));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_NUDGE));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_CLEAR));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_COPY));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_REDO));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_RANDOM2));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_X2));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_2));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_FOLLOW));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_EXPAND));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_CVOUT));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_CONDENSE));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_LEFT));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_UP));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_RIGHT));
        SHIFTED_LEDS.add (Integer.valueOf (LEDS_DOWN));

        BUTTON_LEDS.put (Integer.valueOf (BUTTON_SHIFT), Integer.valueOf (LEDS_SHIFT));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_BACK), Integer.valueOf (LEDS_BACK));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_STOP), Integer.valueOf (LEDS_STOP));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_REC), Integer.valueOf (LEDS_REC));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_PLAY), Integer.valueOf (LEDS_PLAY));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_UNDO), Integer.valueOf (LEDS_UNDO));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_LOAD), Integer.valueOf (LEDS_LOAD));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_SAVE), Integer.valueOf (LEDS_SAVE));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_COPY), Integer.valueOf (LEDS_COPY));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_PASTE), Integer.valueOf (LEDS_PASTE));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_16_LEFT), Integer.valueOf (LEDS_16));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_32_UP), Integer.valueOf (LEDS_32));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_48_DOWN), Integer.valueOf (LEDS_48));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_64_RIGHT), Integer.valueOf (LEDS_64));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_SEQUENCER1), Integer.valueOf (LEDS_SEQ_1));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_SEQUENCER2), Integer.valueOf (LEDS_SEQ_2));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_SEQUENCER3), Integer.valueOf (LEDS_SEQ_3));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_SEQUENCER4), Integer.valueOf (LEDS_SEQ_4));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_MUTE), Integer.valueOf (LEDS_MUTE));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_ARRANGER), Integer.valueOf (LEDS_ARRANGER_SHOW));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_KEYBOARD), Integer.valueOf (LEDS_KEYBOARD));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_ARP), Integer.valueOf (LEDS_ARP));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_MOD), Integer.valueOf (LEDS_MOD));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_LFO), Integer.valueOf (LEDS_LFO));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_STEP_CHORD), Integer.valueOf (LEDS_STEP_CHORD));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_DIVISION), Integer.valueOf (LEDS_DIVISION));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_INIT), Integer.valueOf (LEDS_INIT));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_END), Integer.valueOf (LEDS_END));
        BUTTON_LEDS.put (Integer.valueOf (BUTTON_RANDOM), Integer.valueOf (LEDS_RANDOM));

        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_SHIFT), Integer.valueOf (LEDS_SHIFT));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_STOP), Integer.valueOf (LEDS_SYNC));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_REC), Integer.valueOf (LEDS_NO_LED));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_PLAY), Integer.valueOf (LEDS_PLAY2));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_UNDO), Integer.valueOf (LEDS_REDO));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_COPY), Integer.valueOf (LEDS_DUPLICATE));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_16_LEFT), Integer.valueOf (LEDS_LEFT));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_32_UP), Integer.valueOf (LEDS_UP));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_48_DOWN), Integer.valueOf (LEDS_DOWN));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_64_RIGHT), Integer.valueOf (LEDS_RIGHT));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_SEQUENCER1), Integer.valueOf (LEDS_SEQ_1_SEL));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_SEQUENCER2), Integer.valueOf (LEDS_SEQ_2_SEL));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_SEQUENCER3), Integer.valueOf (LEDS_SEQ_3_SEL));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_SEQUENCER4), Integer.valueOf (LEDS_SEQ_4_SEL));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_MUTE), Integer.valueOf (LEDS_NUDGE));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_KEYBOARD), Integer.valueOf (LEDS_PREVIEW));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_ARP), Integer.valueOf (LEDS_ARP_HOLD));
        SHIFTED_BUTTON_LEDS.put (Integer.valueOf (BUTTON_RANDOM), Integer.valueOf (LEDS_RANDOM2));
    }

    private static final byte [] ENTER_REMOTE_MODE =
    {
        (byte) 0xF0,
        0x00,
        0x21,
        0x5B,
        0x00,
        0x01,
        0x06,
        0x55,
        (byte) 0xF7
    };

    private static final byte [] EXIT_REMOTE_MODE  =
    {
        (byte) 0xF0,
        0x00,
        0x21,
        0x5B,
        0x00,
        0x01,
        0x00,
        (byte) 0xF7
    };

    private static final String  ACKNOWLEDGE       = "f000215b00010653f7";

    private final boolean []     buttonStates      = new boolean [55];

    private final byte []        ledUpdate         =
    {
        (byte) 0xF0,
        0x00,
        0x21,
        0x5B,
        0x00,
        0x01,
        0x02,
        0x00,
        0x00,
        0x00,
        0x00,
        (byte) 0xF7
    };


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     */
    public OxiOneControlSurface (final IHost host, final ColorManager colorManager, final OxiOneConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, new OxiOnePadGrid (colorManager, output), 290, 140);

        this.input.setSysexCallback (this::handleSysEx);
    }


    /**
     * Enable the MIDI remote mode on the OXI One.
     */
    public void enterRemoteMode ()
    {
        this.output.sendSysex (ENTER_REMOTE_MODE);
    }


    /**
     * Disable the MIDI remote mode on the OXI One.
     */
    public void exitRemoteMode ()
    {
        this.output.sendSysex (EXIT_REMOTE_MODE);
    }


    /** {@inheritDoc} */
    @Override
    protected void flushHardware ()
    {
        super.flushHardware ();

        ((OxiOnePadGrid) this.padGrid).flush ();
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        final IGraphicDisplay display = this.getGraphicsDisplay ();
        display.addElement (new LabelComponent ("Goodbye", null, ColorEx.BLACK, false, false, LabelLayout.PLAIN));
        display.send ();

        this.exitRemoteMode ();

        super.internalShutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public OxiOnePadGrid getPadGrid ()
    {
        return (OxiOnePadGrid) this.padGrid;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isDeletePressed ()
    {
        // Use mode button as delete button for resetting knobs
        final boolean pressed = this.isPressed (ButtonID.BANK_RIGHT);
        if (pressed)
            this.setTriggerConsumed (ButtonID.BANK_RIGHT);
        return pressed;
    }


    /**
     * Update all buttons which have 2 states depending on the state of the shift button.
     */
    public void updateFunctionButtonLEDs ()
    {
        final boolean isShifted = this.isShiftPressed ();
        for (int i = 0; i < this.buttonStates.length; i++)
        {
            if (NORMAL_LEDS.contains (Integer.valueOf (i)) && this.buttonStates[i])
                this.updateLED (i, !isShifted);
            else if (SHIFTED_LEDS.contains (Integer.valueOf (i)) && this.buttonStates[i])
                this.updateLED (i, isShifted);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int cc, final int value)
    {
        // There are different MIDI CCs for the LEDs depending on the state of the Shift button!
        final Integer integerCC = Integer.valueOf (cc);

        final Integer normalLedCC = BUTTON_LEDS.get (integerCC);
        final Integer shiftedLedCC = SHIFTED_BUTTON_LEDS.get (integerCC);
        if (normalLedCC == null)
            return;

        // Store the state for updating the buttons with a double LED when the Shift button is used
        // 1st bit represents the state of the shifted LED (on/off)
        final int intNormalLedCCValue = normalLedCC.intValue ();
        this.buttonStates[intNormalLedCCValue] = (value & 1) > 0;
        // 2nd bit represents the state of the shifted LED (on/off)
        if (shiftedLedCC != null)
            this.buttonStates[shiftedLedCC.intValue ()] = (value & 2) > 0;

        if (this.isShiftPressed ())
        {
            if (shiftedLedCC != null)
                this.updateLED (shiftedLedCC.intValue (), this.buttonStates[shiftedLedCC.intValue ()]);
        }
        else
            this.updateLED (intNormalLedCCValue, this.buttonStates[intNormalLedCCValue]);
    }


    /**
     * Update an LED on the device.
     *
     * @param ledID The ID of the LED
     * @param enabled True to turn on the LED otherwise turn it off
     */
    private void updateLED (final int ledID, final boolean enabled)
    {
        this.ledUpdate[7] = (byte) (ledID / 16);
        this.ledUpdate[8] = (byte) (ledID % 16);
        this.ledUpdate[10] = (byte) (enabled ? 1 : 0);
        this.output.sendSysex (this.ledUpdate);
    }


    /**
     * Handle incoming system exclusive messages.
     *
     * @param data The data of the system exclusive message
     */
    private void handleSysEx (final String data)
    {
        if (ACKNOWLEDGE.equalsIgnoreCase (data))
            this.scheduleTask (this::forceFlush, 3000);
    }
}