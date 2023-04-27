// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.esi.xjam.controller;

import de.mossgrabers.controller.esi.xjam.XjamConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;


/**
 * The Kontrol 1 surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XjamControlSurface extends AbstractControlSurface<XjamConfiguration>
{
    // Continuous

    /** Bank 1 - Encoder 1. */
    public static final int BANK1_ENCODER_1 = 1;
    /** Bank 1 - Encoder 2. */
    public static final int BANK1_ENCODER_2 = 2;
    /** Bank 1 - Encoder 3. */
    public static final int BANK1_ENCODER_3 = 3;
    /** Bank 1 - Encoder 4. */
    public static final int BANK1_ENCODER_4 = 4;
    /** Bank 1 - Encoder 5. */
    public static final int BANK1_ENCODER_5 = 5;
    /** Bank 1 - Encoder 6. */
    public static final int BANK1_ENCODER_6 = 6;

    /** Bank 2 - Encoder 1. */
    public static final int BANK2_ENCODER_1 = 7;
    /** Bank 2 - Encoder 2. */
    public static final int BANK2_ENCODER_2 = 8;
    /** Bank 2 - Encoder 3. */
    public static final int BANK2_ENCODER_3 = 9;
    /** Bank 2 - Encoder 4. */
    public static final int BANK2_ENCODER_4 = 10;
    /** Bank 2 - Encoder 5. */
    public static final int BANK2_ENCODER_5 = 11;
    /** Bank 2 - Encoder 6. */
    public static final int BANK2_ENCODER_6 = 12;

    /** Bank 3 - Encoder 1. */
    public static final int BANK3_ENCODER_1 = 13;
    /** Bank 3 - Encoder 2. */
    public static final int BANK3_ENCODER_2 = 14;
    /** Bank 3 - Encoder 3. */
    public static final int BANK3_ENCODER_3 = 15;
    /** Bank 3 - Encoder 4. */
    public static final int BANK3_ENCODER_4 = 16;
    /** Bank 3 - Encoder 5. */
    public static final int BANK3_ENCODER_5 = 17;
    /** Bank 3 - Encoder 6. */
    public static final int BANK3_ENCODER_6 = 18;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param input The MIDI input
     */
    public XjamControlSurface (final IHost host, final ColorManager colorManager, final XjamConfiguration configuration, final IMidiInput input)
    {
        super (0, host, configuration, colorManager, null, input, null, null, 800, 300);
    }
}