// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs.controller;

import de.mossgrabers.controller.akai.acvs.ACVSConfiguration;
import de.mossgrabers.controller.akai.acvs.ACVSDevice;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Optional;


/**
 * The control surface for Akai devices which support the ACVS protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class ACVSControlSurface extends AbstractControlSurface<ACVSConfiguration>
{
    /** Expect a pong every 5 seconds. */
    private static final int    HEARTBEAT_TIMEOUT                = 5000;

    /** Send a ping every 3 seconds. */
    private static final int    PING_INTERVAL                    = 3000;

    ///////////////////////////////////////////////////////////////////
    // Commands sent from the MPC/Force Touch display on channel 0x00
    ///////////////////////////////////////////////////////////////////

    // 0-7
    public static final int     NOTE_TRACK1_SELECT               = 0;

    // 16-23
    public static final int     NOTE_TRACK1_STOP                 = 16;

    // 24-87
    public static final int     NOTE_CLIP1_LAUNCH                = 24;

    // 88-95
    public static final int     NOTE_SCENE1                      = 88;

    public static final int     NOTE_SOLO                        = 0;
    public static final int     NOTE_MUTE                        = 1;
    public static final int     NOTE_CUE                         = 2;
    public static final int     NOTE_CROSSFADER                  = 4;
    public static final int     NOTE_REC_ARM                     = 5;

    public static final int     NOTE_TOGGLE_DEVICE               = 0;
    public static final int     NOTE_PREV_DEVICE                 = 1;
    public static final int     NOTE_NEXT_DEVICE                 = 2;
    public static final int     NOTE_PREV_BANK                   = 3;
    public static final int     NOTE_NEXT_BANK                   = 4;

    ///////////////////////////////////////////////////////////////////
    // Additional commands sent from Touch display on channel 0x0A
    ///////////////////////////////////////////////////////////////////

    public static final int     NOTE_METRONOME                   = 0;
    public static final int     NOTE_CAPTURE_MIDI                = 1;
    public static final int     NOTE_ABLETON_LINK                = 2;
    public static final int     NOTE_ARRANGE_OVERDUB             = 3;
    public static final int     NOTE_ARRANGER_AUTOMATION_ARM     = 4;
    public static final int     NOTE_LOOP_SWITCH                 = 5;
    public static final int     NOTE_LAUNCH_QUANTIZE             = 6;
    public static final int     NOTE_TOGGLE_ARRANGE_SESSION      = 7;
    public static final int     NOTE_FOLLOW                      = 8;
    public static final int     NOTE_CLIP_DEV_VIEW               = 9;
    public static final int     NOTE_DEVICE_LOCK                 = 10;
    public static final int     NOTE_DETAILED_VIEW               = 11;
    public static final int     NOTE_NUDGE_DOWN                  = 12;
    public static final int     NOTE_NUDGE_UP                    = 13;
    public static final int     NOTE_DELETE                      = 14;
    public static final int     NOTE_QUANTIZE_INTERVAL           = 15;
    public static final int     NOTE_QUANTIZE                    = 16;
    public static final int     NOTE_DOUBLE                      = 17;
    public static final int     NOTE_NEW                         = 18;
    public static final int     NOTE_BACK_TO_ARRANGEMENT         = 19;
    public static final int     NOTE_STOP_ALL_CLIPS              = 20;
    public static final int     NOTE_INSERT_SCENE                = 21;
    public static final int     NOTE_ARRANGE_RECORD              = 22;
    public static final int     NOTE_TOGGLE_CLIP_SCENE_LAUNCH    = 23;

    public static final int     CC_PLAY_POSITION                 = 0;
    public static final int     CC_MOVE_LOOP                     = 1;
    public static final int     CC_LOOP_LENGTH                   = 2;

    ///////////////////////////////////////////////////////////////////
    // Commands sent from MPC buttons on channel 0x0C
    ///////////////////////////////////////////////////////////////////

    public static final int     NOTE_MPC_LAUNCH_CLIP_OR_SCENE1   = 0;

    public static final int     NOTE_MPC_BANK_A                  = 64;
    public static final int     NOTE_MPC_BANK_B                  = 65;
    public static final int     NOTE_MPC_BANK_C                  = 66;
    public static final int     NOTE_MPC_BANK_D                  = 67;
    public static final int     NOTE_MPC_NOTE_REPEAT             = 68;
    public static final int     NOTE_MPC_FULL_LEVEL              = 69;
    public static final int     NOTE_MPC_16_LEVEL                = 70;
    public static final int     NOTE_MPC_ERASE                   = 71;
    public static final int     NOTE_MPC_SHIFT                   = 72;
    public static final int     NOTE_MPC_MAIN                    = 73;
    public static final int     NOTE_MPC_UNDO                    = 74;
    public static final int     NOTE_MPC_COPY                    = 75;
    public static final int     NOTE_MPC_TAP                     = 76;
    public static final int     NOTE_MPC_REC                     = 77;
    public static final int     NOTE_MPC_OVERDUB                 = 78;
    public static final int     NOTE_MPC_STOP                    = 79;
    public static final int     NOTE_MPC_PLAY                    = 80;
    public static final int     NOTE_MPC_PLAY_START              = 81;

    public static final int     NOTE_MPC_CURSOR_UP               = 106;
    public static final int     NOTE_MPC_CURSOR_DOWN             = 107;
    public static final int     NOTE_MPC_CURSOR_LEFT             = 108;
    public static final int     NOTE_MPC_CURSOR_RIGHT            = 109;

    // Only MPC-X
    public static final int     NOTE_MPC_LEFT                    = 82;
    public static final int     NOTE_MPC_RIGHT                   = 83;

    ///////////////////////////////////////////////////////////////////
    // Commands sent from Force buttons on channel 0x0C
    ///////////////////////////////////////////////////////////////////

    // 0-7
    public static final int     NOTE_FORCE_TRACK_SELECT1         = 0;
    // 8-15
    public static final int     NOTE_FORCE_TRACK_ASSIGN1         = 8;
    // 16-79
    public static final int     NOTE_FORCE_LAUNCH_CLIP_OR_SCENE1 = 16;
    // 80-87
    public static final int     NOTE_FORCE_LAUNCH_SCENE1         = 80;
    public static final int     NOTE_FORCE_MASTER                = 88;
    public static final int     NOTE_FORCE_STOP_ALL_CLIPS        = 89;
    public static final int     NOTE_FORCE_LAUNCH                = 91;
    public static final int     NOTE_FORCE_NOTE                  = 92;
    public static final int     NOTE_FORCE_STEP_SEQ              = 93;
    public static final int     NOTE_FORCE_CLIP_SELECT           = 94;
    public static final int     NOTE_FORCE_EDIT                  = 95;
    public static final int     NOTE_FORCE_COPY                  = 96;
    public static final int     NOTE_FORCE_DELETE                = 97;
    public static final int     NOTE_FORCE_ARP                   = 98;
    public static final int     NOTE_FORCE_TAP_TEMPO             = 99;
    public static final int     NOTE_FORCE_MUTE                  = 100;
    public static final int     NOTE_FORCE_SOLO                  = 101;
    public static final int     NOTE_FORCE_REC_ARM               = 102;
    public static final int     NOTE_FORCE_CLIP_STOP             = 103;
    public static final int     NOTE_FORCE_PLAY                  = 104;
    public static final int     NOTE_FORCE_STOP                  = 105;
    public static final int     NOTE_FORCE_REC                   = 106;
    public static final int     NOTE_FORCE_UNDO                  = 107;
    public static final int     NOTE_FORCE_LOAD                  = 108;
    public static final int     NOTE_FORCE_SAVE                  = 109;
    public static final int     NOTE_FORCE_MATRIX                = 110;
    public static final int     NOTE_FORCE_CLIP                  = 111;
    public static final int     NOTE_FORCE_MIXER                 = 112;
    public static final int     NOTE_FORCE_NAVIGATE              = 113;
    public static final int     NOTE_FORCE_SHIFT                 = 114;
    public static final int     NOTE_FORCE_CURSOR_UP             = 115;
    public static final int     NOTE_FORCE_CURSOR_DOWN           = 116;
    public static final int     NOTE_FORCE_CURSOR_LEFT           = 117;
    public static final int     NOTE_FORCE_CURSOR_RIGHT          = 118;
    public static final int     NOTE_FORCE_ASSIGN_A              = 119;
    public static final int     NOTE_FORCE_ASSIGN_B              = 120;

    public static final int     CC_VOLUME                        = 0;
    public static final int     CC_PAN                           = 1;
    public static final int     CC_SEND1_LEVEL                   = 3;
    public static final int     CC_SEND2_LEVEL                   = 4;
    public static final int     CC_SEND3_LEVEL                   = 5;
    public static final int     CC_SEND4_LEVEL                   = 6;

    public static final int     CC_PARAM1_VALUE                  = 0;

    private long                lastPong                         = 0;
    private ITextMessageHandler textMessageHandler               = null;
    private final ModeManager   trackModeManager                 = new ModeManager ();
    private boolean             isShutdown                       = false;


    /**
     * Constructor.
     *
     * @param acvsDevice The specific ACVS device
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     */
    public ACVSControlSurface (final IHost host, final ColorManager colorManager, final ACVSConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, new ACVSMidiOutput (output, configuration.getACVSActiveDevice ()), input, null, 100, 100);

        this.addTextDisplay (new ACVSDisplay (this.host, this.getOutput ()));

        this.input.setSysexCallback (this::handleSysEx);
    }


    /**
     * Set a handler for value changes received as text via system exclusive.
     *
     * @param textMessageHandler The handler
     */
    public void setITextMessageHandler (final ITextMessageHandler textMessageHandler)
    {
        this.textMessageHandler = textMessageHandler;
    }


    /**
     * Start pinging the device.
     */
    public void sendPing ()
    {
        if (this.isShutdown)
            return;

        if (System.currentTimeMillis () - this.lastPong > HEARTBEAT_TIMEOUT && this.lastPong > 0)
        {
            this.lastPong = 0;
            this.host.println ("Disconnected.");
        }

        ((ACVSMidiOutput) this.output).sendPing ();
        this.host.scheduleTask (this::sendPing, PING_INTERVAL);
    }


    /**
     * Handle incoming system exclusive data.
     *
     * @param dataStr The data formatted as a string
     */
    private void handleSysEx (final String dataStr)
    {
        final int [] data = StringUtils.fromHexStr (dataStr);
        final ACVSMidiOutput acvsMidiOutput = this.getOutput ();

        final Optional<ACVSMessage> messageOpt = acvsMidiOutput.getMessageContent (data);
        if (messageOpt.isEmpty ())
        {
            this.host.error ("Unknown System Exclusive message: " + dataStr);
            return;
        }

        final ACVSMessage acvsMessage = messageOpt.get ();
        final int messageID = acvsMessage.getMessageID ();
        switch (messageID)
        {
            case ACVSMidiOutput.MESSAGE_ID_PONG:
                final ACVSDevice acvsDevice = this.configuration.getACVSActiveDevice ();
                final ACVSDevice newAcvsDevice = acvsMessage.getACVSDevice ();
                if (acvsDevice != newAcvsDevice)
                {
                    this.configuration.setACVSActiveDevice (newAcvsDevice);
                    this.host.println ("Switching to " + newAcvsDevice.getName ());
                    this.host.restart ();
                    return;
                }

                final long now = System.currentTimeMillis ();
                if (now - this.lastPong > HEARTBEAT_TIMEOUT)
                {
                    this.forceFlush ();
                    this.host.println ("Connected to " + this.configuration.getACVSActiveDevice ().getName ());
                }
                this.lastPong = now;
                break;

            case ACVSMidiOutput.MESSAGE_ID_TEXT:
                if (this.textMessageHandler != null)
                {
                    final int [] content = acvsMessage.getContent ();
                    final int itemID = content[0] << 8 + content[1];
                    final StringBuilder sb = new StringBuilder ();
                    for (int i = 4; i < content.length; i++)
                        sb.append (Character.valueOf ((char) content[i]));
                    this.textMessageHandler.handleTextMessage (itemID, sb.toString ());
                }
                break;

            default:
                this.host.error ("Unknown ACVS System Exclusive message: " + messageID);
                break;
        }
    }


    /**
     * Get the cast ACVS output.
     *
     * @return The output
     */
    private ACVSMidiOutput getOutput ()
    {
        return (ACVSMidiOutput) this.output;
    }


    /**
     * Get the track mode manager.
     *
     * @return The track mode manager
     */
    public ModeManager getTrackModeManager ()
    {
        return this.trackModeManager;
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        this.isShutdown = true;
        this.host.println ("Stop sending ping.");

        final ACVSDisplay d = (ACVSDisplay) this.getDisplay ();

        d.setScreenItem (ScreenItem.TRACK_NUMBER_OF_SCENES, 0);
        for (int trackIndex = 0; trackIndex < 8; trackIndex++)
            d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_TYPE, trackIndex), 0);

        final boolean isMPC = !this.configuration.isActiveACVSDevice (ACVSDevice.FORCE);
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                final int position = row * 8 + col;
                if (isMPC)
                {
                    d.setScreenItem (ScreenItem.get (ScreenItem.MPC_PAD1_STATE, position), 0);
                    d.setScreenItem (ScreenItem.get (ScreenItem.MPC_PAD1_COLOR, position), 0);
                }
                else
                {
                    d.setScreenItem (ScreenItem.get (ScreenItem.FORCE_PAD1_STATE, position), 0);
                    d.setScreenItem (ScreenItem.get (ScreenItem.FORCE_PAD1_COLOR, position), 0);
                }
            }
        }

        super.internalShutdown ();
    }
}