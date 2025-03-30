// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.melbourne.rotocontrol.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import de.mossgrabers.controller.melbourne.rotocontrol.RotoControlConfiguration;
import de.mossgrabers.controller.melbourne.rotocontrol.mode.RotoControlDisplay;
import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The control surface for the Roto Control.
 *
 * @author Jürgen Moßgraber
 */
public class RotoControlControlSurface extends AbstractControlSurface<RotoControlConfiguration>
{
    /** The first knob. */
    public static final int           KNOB_FIRST            = 12;

    /** The first button. */
    public static final int           BUTTON_FIRST          = 20;

    /** The play button. */
    public static final int           BUTTON_PLAY           = 28;
    /** The stop button. */
    public static final int           BUTTON_STOP           = 29;
    /** The record button. */
    public static final int           BUTTON_REC            = 30;
    /** The session record button. */
    public static final int           BUTTON_SESSION_REC    = 31;
    /** The loop button. */
    public static final int           BUTTON_LOOP           = 32;
    /** The punch in button. */
    public static final int           BUTTON_PUNCH_IN       = 33;
    /** The punch out button. */
    public static final int           BUTTON_PUNCH_OUT      = 34;
    /** The automation button. */
    public static final int           BUTTON_AUTOMATION     = 35;
    /** The rewind button. */
    public static final int           BUTTON_REWIND         = 36;
    /** The forward button. */
    public static final int           BUTTON_FORWARD        = 37;

    private static final Set<Integer> TRANSPORT_LED_BUTTONS = new HashSet<> ();
    static
    {
        TRANSPORT_LED_BUTTONS.add (Integer.valueOf (BUTTON_PLAY));
        TRANSPORT_LED_BUTTONS.add (Integer.valueOf (BUTTON_STOP));
        TRANSPORT_LED_BUTTONS.add (Integer.valueOf (BUTTON_REC));
        TRANSPORT_LED_BUTTONS.add (Integer.valueOf (BUTTON_SESSION_REC));
        TRANSPORT_LED_BUTTONS.add (Integer.valueOf (BUTTON_LOOP));
        TRANSPORT_LED_BUTTONS.add (Integer.valueOf (BUTTON_PUNCH_IN));
        TRANSPORT_LED_BUTTONS.add (Integer.valueOf (BUTTON_PUNCH_OUT));
        TRANSPORT_LED_BUTTONS.add (Integer.valueOf (BUTTON_AUTOMATION));
    }

    private final IMessageCallback                callback;
    private final int []                          lastCCValues = new int [32];
    private final Map<Integer, ContinuousCommand> commands     = new HashMap<> ();
    private final RotoControlDisplay              rotoDisplay;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     * @param callback The callback for commands received via system exclusive
     * @param model The model
     */
    public RotoControlControlSurface (final IHost host, final ColorManager colorManager, final RotoControlConfiguration configuration, final IMidiOutput output, final IMidiInput input, final IMessageCallback callback, final IModel model)
    {
        super (host, configuration, colorManager, output, input, null, 100, 100);

        this.callback = callback;
        this.rotoDisplay = new RotoControlDisplay (this, model);

        this.input.setSysexCallback (this::handleSysEx);
    }


    /**
     * Workaround until sequenced CC are working in Bitwig.
     *
     * @param cc The base CC to assign the command to
     * @param command The command to trigger
     */
    public void setHiResContinuousCommand (final int cc, final ContinuousCommand command)
    {
        this.commands.put (Integer.valueOf (cc), command);
    }


    /**
     * Send the startup command to the ROTO Control.
     */
    public void sendStartupCommand ()
    {
        this.sendSysex (RotoControlMessage.GENERAL, RotoControlMessage.TR_DAW_STARTED);
    }


    /**
     * Fully update the roto-control display state.
     */
    public void flushRotoDisplay ()
    {
        this.rotoDisplay.flushTrackDisplay ();
        this.rotoDisplay.flushDeviceDisplay ();
    }


    /** {@inheritDoc} */
    @Override
    protected void flushHardware ()
    {
        this.rotoDisplay.updateTrackDisplay ();
        this.rotoDisplay.updateDeviceDisplay ();

        super.flushHardware ();
    }


    /** {@inheritDoc} */
    @Override
    protected void handleCC (final int channel, final int data1, final int data2)
    {
        if (channel != 15)
            return;

        if (data1 < 32)
        {
            if (this.commands.get (Integer.valueOf (data1)) != null)
            {
                // Store the MSB
                this.lastCCValues[data1] = data2;
                return;
            }
        }
        else if (data1 < 64)
        {
            final ContinuousCommand command = this.commands.get (Integer.valueOf (data1 - 32));
            if (command != null)
            {
                // LSB arrived as well, handle the command
                command.execute (this.lastCCValues[data1 - 32] * 128 + data2);
                return;
            }
        }

        super.handleCC (channel, data1, data2);
    }


    /**
     * Handle incoming system exclusive data.
     *
     * @param dataStr The data formatted as a string
     */
    private void handleSysEx (final String dataStr)
    {
        final int [] data = StringUtils.fromHexStr (dataStr);

        final Optional<RotoControlMessage> messageOpt = RotoControlMessage.getMessageContent (data);
        if (messageOpt.isEmpty ())
        {
            this.host.error ("Unknown Roto Control System Exclusive message: " + dataStr);
            return;
        }

        this.callback.handle (messageOpt.get ());
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int cc, final int value)
    {
        if (TRANSPORT_LED_BUTTONS.contains (Integer.valueOf (cc)))
            this.callback.updateTransportStatus ();
        else
            super.setTrigger (bindType, channel, cc, value);
    }


    /**
     * Send a command to the ROTO control via system exclusive.
     *
     * @param messageType The type of the message
     * @param messageSubType The sub-type of the message
     */
    public void sendSysex (final int messageType, final int messageSubType)
    {
        this.sendSysex (messageType, messageSubType, (int []) null);
    }


    /**
     * Send a command to the ROTO control via system exclusive.
     *
     * @param messageType The type of the message
     * @param messageSubType The sub-type of the message
     * @param value The data content of the message which is just one value
     */
    public void sendSysex (final int messageType, final int messageSubType, final int value)
    {
        this.sendSysex (messageType, messageSubType, new int []
        {
            value
        });
    }


    /**
     * Send a command to the ROTO control via system exclusive.
     *
     * @param messageType The type of the message
     * @param messageSubType The sub-type of the message
     * @param content The data content of the message
     */
    public void sendSysex (final int messageType, final int messageSubType, final int [] content)
    {
        this.output.sendSysex (new RotoControlMessage (messageType, messageSubType, content).createMessage ());
    }


    /**
     * Send a command to the ROTO control via system exclusive.
     *
     * @param messageType The type of the message
     * @param messageSubType The sub-type of the message
     * @param content The data content of the message
     */
    public void sendSysex (final int messageType, final int messageSubType, final byte [] content)
    {
        this.output.sendSysex (new RotoControlMessage (messageType, messageSubType, content).createMessage ());
    }
}