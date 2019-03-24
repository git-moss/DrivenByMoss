// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.controller;

import de.mossgrabers.controller.maschine.mikro.mk3.MaschineMikroMk3Configuration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * The NI Maschine Mikro Mk3 control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class MaschineMikroMk3ControlSurface extends AbstractControlSurface<MaschineMikroMk3Configuration>
{
    // Midi CC
    public static final int    MIKRO_3_TOUCHSTRIP   = 1;

    public static final int    MIKRO_3_ENCODER      = 7;
    public static final int    MIKRO_3_ENCODER_PUSH = 8;

    public static final int    MIKRO_3_GROUP        = 34;
    public static final int    MIKRO_3_AUTO         = 35;
    public static final int    MIKRO_3_LOCK         = 36;
    public static final int    MIKRO_3_NOTE_REPEAT  = 37;

    public static final int    MIKRO_3_PROJECT      = 38;
    public static final int    MIKRO_3_FAVORITES    = 39;
    public static final int    MIKRO_3_BROWSER      = 40;

    public static final int    MIKRO_3_VOLUME       = 44;
    public static final int    MIKRO_3_PLUGIN       = 45;
    public static final int    MIKRO_3_SWING        = 46;
    public static final int    MIKRO_3_SAMPLING     = 47;
    public static final int    MIKRO_3_TEMPO        = 48;

    public static final int    MIKRO_3_PITCH        = 49;
    public static final int    MIKRO_3_MOD          = 50;
    public static final int    MIKRO_3_PERFORM      = 51;
    public static final int    MIKRO_3_NOTES        = 52;

    public static final int    MIKRO_3_RESTART      = 53;
    public static final int    MIKRO_3_ERASE        = 54;
    public static final int    MIKRO_3_TAP_METRO    = 55;
    public static final int    MIKRO_3_FOLLOW       = 56;
    public static final int    MIKRO_3_PLAY         = 57;
    public static final int    MIKRO_3_REC          = 58;
    public static final int    MIKRO_3_STOP         = 59;

    public static final int    MIKRO_3_FIXED_VEL    = 80;
    public static final int    MIKRO_3_PAD_MODE     = 81;
    public static final int    MIKRO_3_KEYBOARD     = 82;
    public static final int    MIKRO_3_CHORDS       = 84;
    public static final int    MIKRO_3_STEP         = 83;
    public static final int    MIKRO_3_SCENE        = 85;
    public static final int    MIKRO_3_PATTERN      = 86;
    public static final int    MIKRO_3_EVENTS       = 87;
    public static final int    MIKRO_3_VARIATION    = 88;
    public static final int    MIKRO_3_DUPLICATE    = 89;
    public static final int    MIKRO_3_SELECT       = 90;
    public static final int    MIKRO_3_SOLO         = 91;
    public static final int    MIKRO_3_MUTE         = 92;

    public static final int [] MIKRO_3_BUTTONS_ALL  =
    {
        MIKRO_3_ENCODER_PUSH,
        MIKRO_3_GROUP,
        MIKRO_3_AUTO,
        MIKRO_3_LOCK,
        MIKRO_3_NOTE_REPEAT,
        MIKRO_3_PROJECT,
        MIKRO_3_FAVORITES,
        MIKRO_3_BROWSER,
        MIKRO_3_VOLUME,
        MIKRO_3_PLUGIN,
        MIKRO_3_SWING,
        MIKRO_3_SAMPLING,
        MIKRO_3_TEMPO,
        MIKRO_3_PITCH,
        MIKRO_3_MOD,
        MIKRO_3_PERFORM,
        MIKRO_3_NOTES,
        MIKRO_3_RESTART,
        MIKRO_3_ERASE,
        MIKRO_3_TAP_METRO,
        MIKRO_3_FOLLOW,
        MIKRO_3_PLAY,
        MIKRO_3_REC,
        MIKRO_3_STOP,
        MIKRO_3_FIXED_VEL,
        MIKRO_3_PAD_MODE,
        MIKRO_3_KEYBOARD,
        MIKRO_3_CHORDS,
        MIKRO_3_STEP,
        MIKRO_3_SCENE,
        MIKRO_3_PATTERN,
        MIKRO_3_EVENTS,
        MIKRO_3_VARIATION,
        MIKRO_3_DUPLICATE,
        MIKRO_3_SELECT,
        MIKRO_3_SOLO,
        MIKRO_3_MUTE
    };

    public static final int    MIKRO_3_STATE_OFF    = 0;
    public static final int    MIKRO_3_STATE_ON     = 127;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
     */
    public MaschineMikroMk3ControlSurface (final IHost host, final ColorManager colorManager, final MaschineMikroMk3Configuration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, new PadGridImpl (colorManager, output), MIKRO_3_BUTTONS_ALL);

        // Sadly, the Shift button does not send a value
        this.shiftButtonId = -1;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleCC (final int channel, final int cc, final int value)
    {
        if (cc == MaschineMikroMk3ControlSurface.MIKRO_3_ENCODER)
        {
            // Change absolute into relative value

            this.getOutput ().sendCC (MaschineMikroMk3ControlSurface.MIKRO_3_ENCODER, 63);

            final int relativeValue = value - 63;
            super.handleCC (channel, cc, relativeValue);

            return;
        }

        if (cc == MaschineMikroMk3ControlSurface.MIKRO_3_TOUCHSTRIP)
        {
            super.handleCC (channel, cc, value);
            return;
        }

        // All buttons are toggle buttons (first press sends 127, second 0)
        // Therefore, turn any received message into a proper button press and release
        super.handleCC (channel, cc, 127);
        super.handleCC (channel, cc, 0);

        this.clearButtonCache (cc);
    }


    /** {@inheritDoc} */
    @Override
    public void setButton (final int button, final int state)
    {
        this.output.sendCC (button, state);
    }


    /** {@inheritDoc} */
    @Override
    public void setButtonEx (final int button, final int channel, final int state)
    {
        this.output.sendCCEx (channel, button, state);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleGridNote (final int note, final int velocity)
    {
        super.handleGridNote (this.pads.translateToGrid (note), velocity);
    }
}