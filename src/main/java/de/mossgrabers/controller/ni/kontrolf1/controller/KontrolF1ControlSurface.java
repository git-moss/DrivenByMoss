// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrolf1.controller;

import de.mossgrabers.controller.ni.kontrolf1.KontrolF1Configuration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * The Beatstep control surface.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings("javadoc")
public class KontrolF1ControlSurface extends AbstractControlSurface<KontrolF1Configuration>
{
    // public static final int BEATSTEP_SHIFT      = 7;

    // public static final int BEATSTEP_KNOB_1     = 20;
    // public static final int BEATSTEP_KNOB_2     = 21;
    // public static final int BEATSTEP_KNOB_3     = 22;
    // public static final int BEATSTEP_KNOB_4     = 23;
    // public static final int BEATSTEP_KNOB_5     = 24;
    // public static final int BEATSTEP_KNOB_6     = 25;
    // public static final int BEATSTEP_KNOB_7     = 26;
    // public static final int BEATSTEP_KNOB_8     = 27;
    // public static final int BEATSTEP_KNOB_9     = 30;
    // public static final int BEATSTEP_KNOB_10    = 31;
    // public static final int BEATSTEP_KNOB_11    = 32;
    // public static final int BEATSTEP_KNOB_12    = 33;
    // public static final int BEATSTEP_KNOB_13    = 34;
    // public static final int BEATSTEP_KNOB_14    = 35;
    // public static final int BEATSTEP_KNOB_15    = 36;
    // public static final int BEATSTEP_KNOB_16    = 37;
    // public static final int BEATSTEP_KNOB_MAIN  = 40;

    // public static final int BEATSTEP_PRO_STEP1  = 50;
    // public static final int BEATSTEP_PRO_STEP2  = 51;
    // public static final int BEATSTEP_PRO_STEP3  = 52;
    // public static final int BEATSTEP_PRO_STEP4  = 53;
    // public static final int BEATSTEP_PRO_STEP5  = 54;
    // public static final int BEATSTEP_PRO_STEP6  = 55;
    // public static final int BEATSTEP_PRO_STEP7  = 56;
    // public static final int BEATSTEP_PRO_STEP8  = 57;
    // public static final int BEATSTEP_PRO_STEP9  = 58;
    // public static final int BEATSTEP_PRO_STEP10 = 59;
    // public static final int BEATSTEP_PRO_STEP11 = 60;
    // public static final int BEATSTEP_PRO_STEP12 = 61;
    // public static final int BEATSTEP_PRO_STEP13 = 62;
    // public static final int BEATSTEP_PRO_STEP14 = 63;
    // public static final int BEATSTEP_PRO_STEP15 = 64;
    // public static final int BEATSTEP_PRO_STEP16 = 65;

    // public static final int BEATSTEP_PAD_1      = 0x70;
    // public static final int BEATSTEP_PAD_2      = 0x71;
    // public static final int BEATSTEP_PAD_3      = 0x72;
    // public static final int BEATSTEP_PAD_4      = 0x73;
    // public static final int BEATSTEP_PAD_5      = 0x74;
    // public static final int BEATSTEP_PAD_6      = 0x75;
    // public static final int BEATSTEP_PAD_7      = 0x76;
    // public static final int BEATSTEP_PAD_8      = 0x77;
    // public static final int BEATSTEP_PAD_9      = 0x78;
    // public static final int BEATSTEP_PAD_10     = 0x79;
    // public static final int BEATSTEP_PAD_11     = 0x7A;
    // public static final int BEATSTEP_PAD_12     = 0x7B;
    // public static final int BEATSTEP_PAD_13     = 0x7C;
    // public static final int BEATSTEP_PAD_14     = 0x7D;
    // public static final int BEATSTEP_PAD_15     = 0x7E;
    // public static final int BEATSTEP_PAD_16     = 0x7F;

    // static final String     SYSEX_HEADER        = "F0 00 20 6B 7F 42 02 00 10 ";
    // static final String     SYSEX_END           = "F7";

    // private boolean         isShift;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     */
    public KontrolF1ControlSurface (final IHost host, final ColorManager colorManager, final KontrolF1Configuration configuration, final IMidiOutput output, final IMidiInput input)
    {
        // super (host, configuration, colorManager, output, input, new BeatstepPadGrid (colorManager, output), 800, 314);
        super (host, configuration, colorManager, output, input, null, 800, 314);
    }


}