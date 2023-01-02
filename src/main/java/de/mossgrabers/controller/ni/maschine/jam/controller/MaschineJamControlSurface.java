// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.controller;

import de.mossgrabers.controller.ni.maschine.Maschine;
import de.mossgrabers.controller.ni.maschine.core.AbstractMaschineControlSurface;
import de.mossgrabers.controller.ni.maschine.core.controller.MaschinePadGrid;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * The NI Maschine Jam control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class MaschineJamControlSurface extends AbstractMaschineControlSurface<MaschineJamConfiguration>
{
    // MIDI CC
    public static final int             FOOTSWITCH_TIP     = 3;
    public static final int             FOOTSWITCH_RING    = 4;

    public static final int             FADER1             = 8;
    public static final int             FADER2             = 9;
    public static final int             FADER3             = 10;
    public static final int             FADER4             = 11;
    public static final int             FADER5             = 12;
    public static final int             FADER6             = 13;
    public static final int             FADER7             = 14;
    public static final int             FADER8             = 15;

    public static final int             FADER_TOUCH1       = 20;
    public static final int             FADER_TOUCH2       = 21;
    public static final int             FADER_TOUCH3       = 22;
    public static final int             FADER_TOUCH4       = 23;
    public static final int             FADER_TOUCH5       = 24;
    public static final int             FADER_TOUCH6       = 25;
    public static final int             FADER_TOUCH7       = 26;
    public static final int             FADER_TOUCH8       = 27;

    public static final int             SONG               = 30;

    public static final int             STEP               = 31;
    public static final int             PAD_MODE           = 32;
    public static final int             CLEAR              = 33;
    public static final int             DUPLICATE          = 34;
    public static final int             NOTE_REPEAT        = 35;

    public static final int             MACRO              = 36;
    public static final int             LEVEL              = 37;
    public static final int             AUX                = 38;
    public static final int             CONTROL            = 39;
    public static final int             AUTO               = 40;

    public static final int             NAV_UP             = 41;
    public static final int             NAV_DOWN           = 42;
    public static final int             NAV_LEFT           = 43;
    public static final int             NAV_RIGHT          = 44;

    public static final int             SELECT             = 50;
    public static final int             SWING              = 51;
    public static final int             TUNE               = 52;
    public static final int             LOCK               = 53;
    public static final int             NOTES              = 54;
    public static final int             PERFORM            = 55;
    public static final int             BROWSE             = 56;

    public static final int             KNOB_TURN          = 57;
    public static final int             KNOB_PUSH          = 58;
    public static final int             KNOB_TOUCH         = 59;

    public static final int             IN                 = 60;
    public static final int             HEADPHONE          = 61;
    public static final int             MASTER             = 62;
    public static final int             GROUP              = 63;

    public static final int             STRIP_LEFT         = 64;
    public static final int             STRIP_RIGHT        = 65;

    public static final int             FOOTSWITCH         = 66;

    public static final int             GROUP_A            = 70;
    public static final int             GROUP_B            = 71;
    public static final int             GROUP_C            = 72;
    public static final int             GROUP_D            = 73;
    public static final int             GROUP_E            = 74;
    public static final int             GROUP_F            = 75;
    public static final int             GROUP_G            = 76;
    public static final int             GROUP_H            = 77;

    public static final int             SCENE1             = 80;
    public static final int             SCENE2             = 81;
    public static final int             SCENE3             = 82;
    public static final int             SCENE4             = 83;
    public static final int             SCENE5             = 84;
    public static final int             SCENE6             = 85;
    public static final int             SCENE7             = 86;
    public static final int             SCENE8             = 87;

    public static final int             PLAY               = 90;
    public static final int             RECORD             = 91;
    public static final int             LEFT               = 92;
    public static final int             RIGHT              = 93;
    public static final int             TEMPO              = 94;
    public static final int             GRID               = 95;
    public static final int             SOLO               = 96;
    public static final int             MUTE               = 97;

    private static final int            STRIP_VALUE_OFFSET = 11;

    private static final byte []        STRIP_SETUP        =
    {
        (byte) 0xF0,
        0x00,
        0x21,
        0x09,
        0x15,
        0x00,
        0x4D,
        0x50,
        0x00,
        0x01,
        0x05,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        (byte) 0xF7
    };

    private static final byte []        STRIP_DUAL_VALUE   =
    {
        (byte) 0xF0,
        0x00,
        0x21,
        0x09,
        0x15,
        0x00,
        0x4d,
        0x50,
        0x00,
        0x01,
        0x04,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        (byte) 0xF7
    };

    private static final FaderConfig [] EMPTY              = new FaderConfig [8];

    static
    {
        for (int i = 0; i < EMPTY.length; i++)
            EMPTY[i] = new FaderConfig (FaderConfig.TYPE_SINGLE, 0, 0);
    }

    private final FaderConfig [] currentFaderConfigs = new FaderConfig [8];


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param maschine The maschine description
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     */
    public MaschineJamControlSurface (final IHost host, final ColorManager colorManager, final MaschineJamConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, Maschine.JAM, output, input, new MaschinePadGrid (colorManager, output, 8, 8), 800, 800);

        for (int i = 0; i < this.currentFaderConfigs.length; i++)
            this.currentFaderConfigs[i] = new FaderConfig (-1, -1, -1);
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
    protected void internalShutdown ()
    {
        this.setupFaders (EMPTY);

        super.internalShutdown ();
    }


    /**
     * Configure the color and position of the touch faders. The color and type are configured via a
     * system exclusive message. The value is set via MIDI CC. In dual mode there is a dot and a
     * strip, the dot is always white and set via a system exclusive message.
     *
     * @param configs The configuration of the 8 faders
     */
    public void setupFaders (final FaderConfig [] configs)
    {
        final byte [] stripSetup = STRIP_SETUP.clone ();
        final byte [] stripDualValues = STRIP_DUAL_VALUE.clone ();

        boolean hasSetupChanged = false;
        boolean hasDualValueChanged = false;

        for (int i = 0; i < 8; i++)
        {
            final FaderConfig faderConfig = configs[i];
            final FaderConfig currentFaderConfig = this.currentFaderConfigs[i];

            final boolean isDual = faderConfig.getType () == FaderConfig.TYPE_DUAL;

            if (faderConfig.getType () != currentFaderConfig.getType () || faderConfig.getColor () != currentFaderConfig.getColor ())
                hasSetupChanged = true;
            final int pos = i * 2;
            stripSetup[STRIP_VALUE_OFFSET + pos] = (byte) faderConfig.getType ();
            stripSetup[STRIP_VALUE_OFFSET + 1 + pos] = (byte) faderConfig.getColor ();

            // Update value?
            if (isDual && (faderConfig.getDualValue () != currentFaderConfig.getDualValue () || hasSetupChanged))
                hasDualValueChanged = true;

            if (faderConfig.getValue () != currentFaderConfig.getValue () || hasSetupChanged)
                this.output.sendCC (8 + i, faderConfig.getValue ());

            stripDualValues[STRIP_VALUE_OFFSET + i] = (byte) faderConfig.getDualValue ();

            this.currentFaderConfigs[i] = faderConfig;
        }

        // Setup needs to be updated
        if (hasSetupChanged)
            this.output.sendSysex (stripSetup);
        // Is dual mode active and a value has changed?
        if (hasDualValueChanged)
            this.output.sendSysex (stripDualValues);
    }
}