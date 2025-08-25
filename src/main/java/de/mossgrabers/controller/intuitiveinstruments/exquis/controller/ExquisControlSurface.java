// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * A control surface which supports the Intuitive Instruments Exquis controller.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisControlSurface extends AbstractControlSurface<ExquisConfiguration>
{
    /** The IDs of the four knobs. */
    public static final List<ContinuousID> KNOBS                     = ContinuousID.createSequentialList (ContinuousID.KNOB1, 4);

    private static final int               CMD_START_POS             = 5;
    private static final int []            SYSEX_HEADER              =
    {
        0xF0,
        0x00,
        0x21,
        0x7E,
        0x7F
    };

    /** The touch-strip part 1. */
    public static final int                TOUCHSTRIP_P1             = 0x50;
    /** The touch-strip part 2. */
    public static final int                TOUCHSTRIP_P2             = 0x51;
    /** The touch-strip part 3. */
    public static final int                TOUCHSTRIP_P3             = 0x52;
    /** The touch-strip part 4. */
    public static final int                TOUCHSTRIP_P4             = 0x53;
    /** The touch-strip part 5. */
    public static final int                TOUCHSTRIP_P5             = 0x54;
    /** The touch-strip part 6. */
    public static final int                TOUCHSTRIP_P6             = 0x55;

    /** The touch-strip. */
    public static final int                TOUCHSTRIP                = 0x5A;

    /** The record button. */
    public static final int                BUTTON_RECORD             = 0x66;
    /** The repeat button. */
    public static final int                BUTTON_REPEAT             = 0x67;
    /** The session button. */
    public static final int                BUTTON_SESSION            = 0x68;
    /** The play/stop button. */
    public static final int                BUTTON_PLAY_STOP          = 0x69;

    /** The down button. */
    public static final int                BUTTON_DOWN               = 0x6A;
    /** The up button. */
    public static final int                BUTTON_UP                 = 0x6B;
    /** The left button. */
    public static final int                BUTTON_LEFT               = 0x6C;
    /** The right button. */
    public static final int                BUTTON_RIGHT              = 0x6D;

    /** The first knob. Other knobs are 0x6F, 0x70 and 0x71. */
    public static final int                FIRST_KNOB                = 0x6E;

    /** The 1st knob button. */
    public static final int                BUTTON_KNOB1              = 0x72;
    /** The 2nd knob button. */
    public static final int                BUTTON_KNOB2              = 0x73;
    /** The 3rd knob button. */
    public static final int                BUTTON_KNOB3              = 0x74;
    /** The 4th knob button. */
    public static final int                BUTTON_KNOB4              = 0x75;

    /** Turn off developer mode. */
    public static final int                DEV_MODE_OFF              = 0x00;
    /** Take over knobs and non-configuration buttons. */
    public static final int                DEV_MODE_PLAY_MODE        = 0x22;
    /** Take over control elements. */
    public static final int                DEV_MODE_FULL             = 0x3F;

    /** Configure the developer mode. */
    public static final int                CMD_DEVELOPER_MODE        = 0x00;
    /** Use/configure a custom scale. */
    public static final int                CMD_USE_CUSTOM_SCALE_LIST = 0x01;
    /** Update the color palette. */
    public static final int                CMD_COLOR_PALETTE         = 0x02;
    /** Refresh of the LED display */
    public static final int                CMD_REFRESH               = 0x03;
    /** Set the color of one or more LEDs. */
    public static final int                CMD_SET_LED_COLOR         = 0x04;
    /** Get/Set the tempo. */
    public static final int                CMD_TEMPO                 = 0x05;
    /** Get/Set the root note. */
    public static final int                CMD_ROOT_NOTE             = 0x06;
    /** Get/set the scale number. */
    public static final int                CMD_SCALE_NUMBER          = 0x07;
    /** Configure a custom scale. */
    public static final int                CMD_CUSTOM_SCALE          = 0x08;
    /** Retrieve the current layout and the MIDI settings. */
    public static final int                CMD_SNAPSHOT              = 0x09;
    /** Get the non developer mode CC numbers configured by users. */
    public static final int                CMD_ENCODERS_SNAPSHOT     = 0x0A;

    private final ISysexCallback           callback;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     * @param padGrid The pads if any, may be null
     * @param callback Callback for commands received via system exclusive messages
     */
    public ExquisControlSurface (final IHost host, final ColorManager colorManager, final ExquisConfiguration configuration, final IMidiOutput output, final IMidiInput input, final IPadGrid padGrid, final ISysexCallback callback)
    {
        super (host, configuration, colorManager, output, input, padGrid, 100, 200);

        ((ExquisPadGrid) padGrid).setSurface (this);

        this.callback = callback;

        this.input.setSysexCallback (this::handleSysEx);
    }


    /**
     * Send a developer mode configuration command to the device.
     *
     * @param configurationMask The mask, use the predefined constants in this class
     */
    public void configureDeveloperMode (final int configurationMask)
    {
        this.sendSysex (CMD_DEVELOPER_MODE, new byte []
        {
            (byte) configurationMask
        });
    }


    /**
     * Set the color of a LED.
     *
     * @param ledID The ID of the LED
     * @param color The color
     */
    public void setLED (final int ledID, final ColorEx color)
    {
        this.setLED (ledID, color, 0);
    }


    /**
     * Set the color of a LED.
     *
     * @param ledID The ID of the LED
     * @param color The color
     * @param fx A blink effect
     */
    public void setLED (final int ledID, final ColorEx color, final int fx)
    {
        final int [] intRGB127 = color.toIntRGB127 ();
        final byte [] params = new byte []
        {
            (byte) ledID,
            (byte) intRGB127[0],
            (byte) intRGB127[1],
            (byte) intRGB127[2],
            (byte) fx
        };

        this.sendSysex (CMD_SET_LED_COLOR, params);
    }


    /**
     * Send the current tempo to the device if it has changed.
     *
     * @param tempo The tempo, it will be clipped to an integer in [20..240]
     */
    public void updateTempo (final int tempo)
    {
        final byte [] params =
        {
            // Shift right by 7 bits for the remaining MSB
            (byte) (tempo >> 7),
            // Make sure LSB is in the range 0 to 127 - mask with 0111 1111 (127)
            (byte) (tempo & 0x7F)
        };

        this.sendSysex (CMD_TEMPO, params);
    }


    /**
     * Send the updated root note to the device.
     *
     * @param scaleOffset The index of the note in the scale (0-11)
     */
    public void updateRootNote (final int scaleOffset)
    {
        this.sendSysex (CMD_ROOT_NOTE, new byte []
        {
            (byte) scaleOffset
        });
    }


    /**
     * Send the updated scale to the device.
     *
     * @param scaleIndex The index of the scale [0..13]
     */
    public void updateScale (final int scaleIndex)
    {
        this.sendSysex (CMD_SCALE_NUMBER, new byte []
        {
            (byte) scaleIndex
        });
    }


    /**
     * Send a byte array to the CTRL output of the Electra.One.
     *
     * @param command The command bytes
     * @param content The content bytes
     */
    private void sendSysex (final int command, final byte [] content)
    {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream ())
        {
            for (final int element: SYSEX_HEADER)
                out.write (element);
            out.write (command);
            out.write (content);
            out.write ((byte) 0xF7);
            this.output.sendSysex (out.toByteArray ());
        }
        catch (final IOException ex)
        {
            this.host.error ("Could send sysex to Exquis.", ex);
        }
    }


    /**
     * Handle incoming system exclusive data. Messages are split up in chunks of 1024 bytes! This
     * method concatenates and stores the parts until the full message is received and then hands it
     * to the processing.
     *
     * @param dataStr The data
     */
    private void handleSysEx (final String dataStr)
    {
        final int [] data = StringUtils.fromHexStr (dataStr);
        if (Arrays.compareUnsigned (SYSEX_HEADER, 0, SYSEX_HEADER.length, data, 0, SYSEX_HEADER.length) != 0)
            return;

        switch (data[CMD_START_POS])
        {
            case CMD_REFRESH:
                this.forceFlush ();
                break;

            case CMD_TEMPO:
                final int tempo = (data[CMD_START_POS + 1] << 7) + data[CMD_START_POS + 2];
                this.callback.updateTempo (tempo);
                break;

            case CMD_ROOT_NOTE:
                final String noteName = Scales.NOTE_NAMES.get (data[CMD_START_POS + 1]);
                this.configuration.setScaleBase (noteName);
                break;

            case CMD_SCALE_NUMBER:
                this.configuration.setScale (ExquisConfiguration.EXQUISE_SCALES[data[CMD_START_POS + 1]]);
                break;

            default:
                // Other commands are not used...
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int cc, final int value)
    {
        this.setLED (cc, this.colorManager.getColor (value, null));
    }


    /** {@inheritDoc} */
    @Override
    protected void handleMidi (final int status, final int data1, final int data2)
    {
        // Ignore unused CCs...
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        this.configureDeveloperMode (DEV_MODE_OFF);
    }


    /** {@inheritDoc} */
    @Override
    protected void flushHardware ()
    {
        // Don't flush when developer mode is already deactivated!
        if (!this.isShuttingDown)
            super.flushHardware ();
    }
}