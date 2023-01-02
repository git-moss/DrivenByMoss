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
    public static final int BANK1_ENCODER_1 = 10;
    /** Bank 1 - Encoder 2. */
    public static final int BANK1_ENCODER_2 = 11;
    /** Bank 1 - Encoder 3. */
    public static final int BANK1_ENCODER_3 = 12;
    /** Bank 1 - Encoder 4. */
    public static final int BANK1_ENCODER_4 = 13;
    /** Bank 1 - Encoder 5. */
    public static final int BANK1_ENCODER_5 = 14;
    /** Bank 1 - Encoder 6. */
    public static final int BANK1_ENCODER_6 = 15;

    /** Bank 2 - Encoder 1. */
    public static final int BANK2_ENCODER_1 = 16;
    /** Bank 2 - Encoder 2. */
    public static final int BANK2_ENCODER_2 = 17;
    /** Bank 2 - Encoder 3. */
    public static final int BANK2_ENCODER_3 = 18;
    /** Bank 2 - Encoder 4. */
    public static final int BANK2_ENCODER_4 = 19;
    /** Bank 2 - Encoder 5. */
    public static final int BANK2_ENCODER_5 = 20;
    /** Bank 2 - Encoder 6. */
    public static final int BANK2_ENCODER_6 = 21;

    /** Bank 3 - Encoder 1. */
    public static final int BANK3_ENCODER_1 = 22;
    /** Bank 3 - Encoder 2. */
    public static final int BANK3_ENCODER_2 = 23;
    /** Bank 3 - Encoder 3. */
    public static final int BANK3_ENCODER_3 = 24;
    /** Bank 3 - Encoder 4. */
    public static final int BANK3_ENCODER_4 = 25;
    /** Bank 3 - Encoder 5. */
    public static final int BANK3_ENCODER_5 = 26;
    /** Bank 3 - Encoder 6. */
    public static final int BANK3_ENCODER_6 = 27;


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


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        // TODO

        super.internalShutdown ();
    }


    /**
     * Send the LED status to the device.
     */
    public void updateButtonLEDs ()
    {
        // TODO
    }


    /** {@inheritDoc} */
    @Override
    protected void updateViewControls ()
    {
        super.updateViewControls ();
        this.updateButtonLEDs ();
    }


    /** {@inheritDoc} */
    @Override
    protected void handleCC (final int data1, final int data2)
    {
        if (data1 != 1)
            super.handleCC (data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    protected void handlePitchbend (final int data1, final int data2)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected void handleNoteOff (final int data1, final int data2)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected void handleNoteOn (final int data1, final int data2)
    {
        // Intentionally empty
    }
}