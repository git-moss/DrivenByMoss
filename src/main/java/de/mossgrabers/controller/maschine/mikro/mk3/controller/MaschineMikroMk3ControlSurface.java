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
    public static final int MIKRO_3_TOUCHSTRIP   = 1;

    public static final int MIKRO_3_ENCODER      = 7;
    public static final int MIKRO_3_ENCODER_PUSH = 8;

    public static final int MIKRO_3_GROUP        = 34;
    public static final int MIKRO_3_AUTO         = 35;
    public static final int MIKRO_3_LOCK         = 36;
    public static final int MIKRO_3_NOTE_REPEAT  = 37;

    public static final int MIKRO_3_PROJECT      = 38;
    public static final int MIKRO_3_FAVORITES    = 39;
    public static final int MIKRO_3_BROWSER      = 40;

    public static final int MIKRO_3_VOLUME       = 44;
    public static final int MIKRO_3_PLUGIN       = 45;
    public static final int MIKRO_3_SWING        = 46;
    public static final int MIKRO_3_SAMPLING     = 47;
    public static final int MIKRO_3_TEMPO        = 48;

    public static final int MIKRO_3_PITCH        = 49;
    public static final int MIKRO_3_MOD          = 50;
    public static final int MIKRO_3_PERFORM      = 51;
    public static final int MIKRO_3_NOTES        = 52;

    public static final int MIKRO_3_RESTART      = 53;
    public static final int MIKRO_3_ERASE        = 54;
    public static final int MIKRO_3_TAP_METRO    = 55;
    public static final int MIKRO_3_FOLLOW       = 56;
    public static final int MIKRO_3_PLAY         = 57;
    public static final int MIKRO_3_REC          = 58;
    public static final int MIKRO_3_STOP         = 59;

    public static final int MIKRO_3_FIXED_VEL    = 80;
    public static final int MIKRO_3_PAD_MODE     = 81;
    public static final int MIKRO_3_KEYBOARD     = 82;
    public static final int MIKRO_3_CHORDS       = 84;
    public static final int MIKRO_3_STEP         = 83;
    public static final int MIKRO_3_SCENE        = 85;
    public static final int MIKRO_3_PATTERN      = 86;
    public static final int MIKRO_3_EVENTS       = 87;
    public static final int MIKRO_3_VARIATION    = 88;
    public static final int MIKRO_3_DUPLICATE    = 89;
    public static final int MIKRO_3_SELECT       = 90;
    public static final int MIKRO_3_SOLO         = 91;
    public static final int MIKRO_3_MUTE         = 92;


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
        super (host, configuration, colorManager, output, input, new PadGridImpl (colorManager, output, 4, 4, 36), 800, 440);
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int channel, final int cc, final int state)
    {
        this.output.sendCCEx (channel, cc, state);
    }
}