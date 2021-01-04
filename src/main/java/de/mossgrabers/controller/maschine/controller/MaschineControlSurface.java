// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.controller;

import de.mossgrabers.controller.maschine.Maschine;
import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.command.trigger.MaschineStopCommand;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * The NI Maschine control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class MaschineControlSurface extends AbstractControlSurface<MaschineConfiguration>
{
    // Midi CC
    public static final int TOUCHSTRIP        = 1;
    public static final int TOUCHSTRIP_TOUCH  = 2;

    public static final int ENCODER           = 7;
    public static final int ENCODER_PUSH      = 8;
    public static final int ENCODER_TOUCH     = 9;

    /** Mode buttons are from CC 22 to 29. */
    public static final int MODE_BUTTON_1     = 22;
    public static final int MODE_BUTTON_2     = 23;
    public static final int MODE_BUTTON_3     = 24;
    public static final int MODE_BUTTON_4     = 25;
    public static final int MODE_BUTTON_5     = 26;
    public static final int MODE_BUTTON_6     = 27;
    public static final int MODE_BUTTON_7     = 28;
    public static final int MODE_BUTTON_8     = 29;

    public static final int CURSOR_UP         = 30;
    public static final int CURSOR_RIGHT      = 31;
    public static final int CURSOR_DOWN       = 32;
    public static final int CURSOR_LEFT       = 33;

    public static final int GROUP             = 34;
    public static final int AUTO              = 35;
    public static final int LOCK              = 36;
    public static final int NOTE_REPEAT       = 37;

    public static final int PROJECT           = 38;
    public static final int FAVORITES         = 39;
    public static final int BROWSER           = 40;

    public static final int CHANNEL           = 41;
    public static final int ARRANGER          = 42;
    public static final int MIXER             = 43;

    public static final int VOLUME            = 44;
    public static final int PLUGIN            = 45;
    public static final int SWING             = 46;
    public static final int SAMPLING          = 47;
    public static final int TEMPO             = 48;

    public static final int PITCH             = 49;
    public static final int MOD               = 50;
    public static final int PERFORM           = 51;
    public static final int NOTES             = 52;

    public static final int RESTART           = 53;
    public static final int ERASE             = 54;
    public static final int TAP_METRO         = 55;
    public static final int FOLLOW            = 56;
    public static final int PLAY              = 57;
    public static final int REC               = 58;
    public static final int STOP              = 59;

    /** Mode button touch events are from CC 60 to 67. */
    public static final int MODE_KNOB_TOUCH_1 = 60;

    /** Mode buttons are from CC 70 to 77. */
    public static final int MODE_KNOB_1       = 70;

    public static final int FIXED_VEL         = 80;
    public static final int PAD_MODE          = 81;
    public static final int KEYBOARD          = 82;
    public static final int CHORDS            = 84;
    public static final int STEP              = 83;
    public static final int SCENE             = 85;
    public static final int PATTERN           = 86;
    public static final int EVENTS            = 87;
    public static final int VARIATION         = 88;
    public static final int DUPLICATE         = 89;
    public static final int SELECT            = 90;
    public static final int SOLO              = 91;
    public static final int MUTE              = 92;

    /** Banks are from CC 100 to 107. */
    public static final int BANK_1            = 100;

    public static final int PAGE_LEFT         = 110;
    public static final int PAGE_RIGHT        = 111;

    private final Maschine  maschine;
    private int             ribbonValue       = -1;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param maschine The maschine description
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
     */
    public MaschineControlSurface (final IHost host, final ColorManager colorManager, final Maschine maschine, final MaschineConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, new MaschinePadGrid (colorManager, output), 800, maschine.getHeight ());

        this.maschine = maschine;
    }


    /**
     * Signal that the stop function should not be called on button release.
     */
    public void setStopConsumed ()
    {
        ((MaschineStopCommand) this.getButton (ButtonID.STOP).getCommand ()).setConsumed ();
    }


    /** {@inheritDoc} */
    @Override
    protected void flushHardware ()
    {
        super.flushHardware ();

        ((MaschinePadGrid) this.padGrid).flush ();
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int channel, final int cc, final int state)
    {
        if (cc == MODE_BUTTON_7)
            this.output.sendNoteEx (channel, cc, state);
        else
            this.output.sendCCEx (channel, cc, state);
    }


    /**
     * Set the display value of the ribbon on the controller.
     *
     * @param value The value to set
     */
    public void setRibbonValue (final int value)
    {
        if (this.ribbonValue == value)
            return;
        this.ribbonValue = value;
        this.output.sendCC (1, value);
    }


    /**
     * Get the Maschine object.
     *
     * @return The Maschine object
     */
    public Maschine getMaschine ()
    {
        return this.maschine;
    }
}