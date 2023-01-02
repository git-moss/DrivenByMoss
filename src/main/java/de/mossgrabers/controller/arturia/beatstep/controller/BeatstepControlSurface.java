// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.controller;

import de.mossgrabers.controller.arturia.beatstep.BeatstepConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The Beatstep 1 and Beatstep 2 control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class BeatstepControlSurface extends AbstractControlSurface<BeatstepConfiguration>
{
    public static final int BEATSTEP_SHIFT      = 7;

    public static final int BEATSTEP_KNOB_1     = 20;
    public static final int BEATSTEP_KNOB_2     = 21;
    public static final int BEATSTEP_KNOB_3     = 22;
    public static final int BEATSTEP_KNOB_4     = 23;
    public static final int BEATSTEP_KNOB_5     = 24;
    public static final int BEATSTEP_KNOB_6     = 25;
    public static final int BEATSTEP_KNOB_7     = 26;
    public static final int BEATSTEP_KNOB_8     = 27;
    public static final int BEATSTEP_KNOB_9     = 30;
    public static final int BEATSTEP_KNOB_10    = 31;
    public static final int BEATSTEP_KNOB_11    = 32;
    public static final int BEATSTEP_KNOB_12    = 33;
    public static final int BEATSTEP_KNOB_13    = 34;
    public static final int BEATSTEP_KNOB_14    = 35;
    public static final int BEATSTEP_KNOB_15    = 36;
    public static final int BEATSTEP_KNOB_16    = 37;
    public static final int BEATSTEP_KNOB_MAIN  = 40;

    public static final int BEATSTEP_PRO_STEP1  = 50;
    public static final int BEATSTEP_PRO_STEP2  = 51;
    public static final int BEATSTEP_PRO_STEP3  = 52;
    public static final int BEATSTEP_PRO_STEP4  = 53;
    public static final int BEATSTEP_PRO_STEP5  = 54;
    public static final int BEATSTEP_PRO_STEP6  = 55;
    public static final int BEATSTEP_PRO_STEP7  = 56;
    public static final int BEATSTEP_PRO_STEP8  = 57;
    public static final int BEATSTEP_PRO_STEP9  = 58;
    public static final int BEATSTEP_PRO_STEP10 = 59;
    public static final int BEATSTEP_PRO_STEP11 = 60;
    public static final int BEATSTEP_PRO_STEP12 = 61;
    public static final int BEATSTEP_PRO_STEP13 = 62;
    public static final int BEATSTEP_PRO_STEP14 = 63;
    public static final int BEATSTEP_PRO_STEP15 = 64;
    public static final int BEATSTEP_PRO_STEP16 = 65;

    public static final int BEATSTEP_PAD_1      = 0x70;
    public static final int BEATSTEP_PAD_2      = 0x71;
    public static final int BEATSTEP_PAD_3      = 0x72;
    public static final int BEATSTEP_PAD_4      = 0x73;
    public static final int BEATSTEP_PAD_5      = 0x74;
    public static final int BEATSTEP_PAD_6      = 0x75;
    public static final int BEATSTEP_PAD_7      = 0x76;
    public static final int BEATSTEP_PAD_8      = 0x77;
    public static final int BEATSTEP_PAD_9      = 0x78;
    public static final int BEATSTEP_PAD_10     = 0x79;
    public static final int BEATSTEP_PAD_11     = 0x7A;
    public static final int BEATSTEP_PAD_12     = 0x7B;
    public static final int BEATSTEP_PAD_13     = 0x7C;
    public static final int BEATSTEP_PAD_14     = 0x7D;
    public static final int BEATSTEP_PAD_15     = 0x7E;
    public static final int BEATSTEP_PAD_16     = 0x7F;

    static final String     SYSEX_HEADER        = "F0 00 20 6B 7F 42 02 00 10 ";
    static final String     SYSEX_END           = "F7";

    private boolean         isShift;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     */
    public BeatstepControlSurface (final IHost host, final ColorManager colorManager, final BeatstepConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, new BeatstepPadGrid (colorManager, output), 800, 314);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleGridNote (final ButtonEvent event, final int note, final int velocity)
    {
        super.handleGridNote (event, note, velocity);

        if (event == ButtonEvent.UP)
        {
            // Red LED is turned off on button release, restore the correct color
            final LightInfo lightInfo = this.padGrid.getLightInfo (note);
            ((BeatstepPadGrid) this.padGrid).lightPad (note, lightInfo.getColor ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShiftPressed ()
    {
        return this.isShift;
    }
}