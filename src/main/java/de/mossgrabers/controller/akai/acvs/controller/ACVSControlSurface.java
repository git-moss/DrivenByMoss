// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs.controller;

import de.mossgrabers.controller.akai.acvs.ACVSConfiguration;
import de.mossgrabers.controller.akai.acvs.ACVSDevice;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The Akai MPC control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class ACVSControlSurface extends AbstractControlSurface<ACVSConfiguration>
{
    /** Expect a pong every 5 seconds. */
    private static final int    HEARTBEAT_TIMEOUT                 = 5000;

    /** Send a ping every 3 seconds. */
    private static final int    PING_INTERVAL                     = 3000;

    // Commands sent from the MPC/Force Touch display on channel 0x00

    // 0-7
    public static final int     NOTE_TRACK1_SELECT                = 0;

    // 16-23
    public static final int     NOTE_TRACK1_STOP                  = 16;

    // 24-87
    public static final int     NOTE_CLIP1_LAUNCH                 = 24;

    // 88-95
    public static final int     NOTE_SCENE1                       = 88;

    public static final int     NOTE_SOLO                         = 0;
    public static final int     NOTE_MUTE                         = 1;
    public static final int     NOTE_CUE                          = 2;
    public static final int     NOTE_CROSSFADER                   = 4;
    public static final int     NOTE_REC_ARM                      = 5;

    public static final int     NOTE_TOGGLE_DEVICE                = 0;
    public static final int     NOTE_PREV_DEVICE                  = 1;
    public static final int     NOTE_NEXT_DEVICE                  = 2;
    public static final int     NOTE_PREV_BANK                    = 3;
    public static final int     NOTE_NEXT_BANK                    = 4;

    // Commands sent from MPC Touch display on channel 0x0A
    public static final int     NOTE_MPC_METRONOME                = 0;
    public static final int     NOTE_MPC_CAPTURE_MIDI             = 1;
    public static final int     NOTE_MPC_ABLETON_LINK             = 2;
    public static final int     NOTE_MPC_ARRANGE_OVERDUB          = 3;
    public static final int     NOTE_MPC_ARRANGER_AUTOMATION_ARM  = 4;
    public static final int     NOTE_MPC_LOOP_SWITCH              = 5;
    public static final int     NOTE_MPC_LAUNCH_QUANTIZE          = 6;
    public static final int     NOTE_MPC_TOGGLE_ARRANGE_SESSION   = 7;
    public static final int     NOTE_MPC_FOLLOW                   = 8;
    public static final int     NOTE_MPC_CLIP_DEV_VIEW            = 9;
    public static final int     NOTE_MPC_DEVICE_LOCK              = 10;
    public static final int     NOTE_MPC_DETAILED_VIEW            = 11;
    public static final int     NOTE_MPC_NUDGE_DOWN               = 12;
    public static final int     NOTE_MPC_NUDGE_UP                 = 13;
    public static final int     NOTE_MPC_DELETE                   = 14;
    public static final int     NOTE_MPC_QUANTIZE_INTERVAL        = 15;
    public static final int     NOTE_MPC_QUANTIZE                 = 16;
    public static final int     NOTE_MPC_DOUBLE                   = 17;
    public static final int     NOTE_MPC_NEW                      = 18;
    public static final int     NOTE_MPC_BACK_TO_ARRANGEMENT      = 19;
    public static final int     NOTE_MPC_STOP_ALL_CLIPS           = 20;
    public static final int     NOTE_MPC_INSERT_SCENE             = 21;
    public static final int     NOTE_MPC_ARRANGE_RECORD           = 22;
    public static final int     NOTE_MPC_TOGGLE_CLIP_SCENE_LAUNCH = 23;

    // Commands sent from MPC buttons on channel 0x0C
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE1        = 0;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE2        = 1;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE3        = 2;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE4        = 3;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE5        = 4;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE6        = 5;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE7        = 6;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE8        = 7;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE9        = 8;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE10       = 9;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE11       = 10;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE12       = 11;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE13       = 12;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE14       = 13;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE15       = 14;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE16       = 15;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE17       = 16;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE18       = 17;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE19       = 18;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE20       = 19;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE21       = 20;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE22       = 21;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE23       = 22;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE24       = 23;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE25       = 24;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE26       = 25;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE27       = 26;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE28       = 27;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE29       = 28;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE30       = 29;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE31       = 30;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE32       = 31;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE33       = 32;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE34       = 33;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE35       = 34;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE36       = 35;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE37       = 36;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE38       = 37;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE39       = 38;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE40       = 39;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE41       = 40;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE42       = 41;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE43       = 42;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE44       = 43;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE45       = 44;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE46       = 45;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE47       = 46;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE48       = 47;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE49       = 48;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE50       = 49;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE51       = 50;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE52       = 51;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE53       = 52;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE54       = 53;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE55       = 54;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE56       = 55;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE57       = 56;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE58       = 57;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE59       = 58;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE60       = 59;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE61       = 60;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE62       = 61;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE63       = 62;
    public static final int     NOTE_LAUNCH_CLIP_OR_SCENE64       = 63;

    public static final int     NOTE_MPC_BANK_A                   = 64;
    public static final int     NOTE_MPC_BANK_B                   = 65;
    public static final int     NOTE_MPC_BANK_C                   = 66;
    public static final int     NOTE_MPC_BANK_D                   = 67;
    public static final int     NOTE_MPC_NOTE_REPEAT              = 68;
    public static final int     NOTE_MPC_FULL_LEVEL               = 69;
    public static final int     NOTE_MPC_16_LEVEL                 = 70;
    public static final int     NOTE_MPC_ERASE                    = 71;
    public static final int     NOTE_MPC_SHIFT                    = 72;
    public static final int     NOTE_MPC_MAIN                     = 73;
    public static final int     NOTE_MPC_UNDO                     = 74;
    public static final int     NOTE_MPC_COPY                     = 75;
    public static final int     NOTE_MPC_TAP                      = 76;
    public static final int     NOTE_MPC_REC                      = 77;
    public static final int     NOTE_MPC_OVERDUB                  = 78;
    public static final int     NOTE_MPC_STOP                     = 79;
    public static final int     NOTE_MPC_PLAY                     = 80;
    public static final int     NOTE_MPC_PLAY_START               = 81;

    public static final int     NOTE_MPC_CURSOR_UP                = 106;
    public static final int     NOTE_MPC_CURSOR_DOWN              = 107;
    public static final int     NOTE_MPC_CURSOR_LEFT              = 108;
    public static final int     NOTE_MPC_CURSOR_RIGHT             = 109;

    // Only MPC-X
    public static final int     NOTE_MPC_LEFT                     = 82;
    public static final int     NOTE_MPC_RIGHT                    = 83;

    public static final int     CC_VOLUME                         = 0;
    public static final int     CC_PAN                            = 1;
    public static final int     CC_SEND1_LEVEL                    = 3;
    public static final int     CC_SEND2_LEVEL                    = 4;
    public static final int     CC_SEND3_LEVEL                    = 5;
    public static final int     CC_SEND4_LEVEL                    = 6;

    public static final int     CC_PARAM1_VALUE                   = 0;

    private final ACVSDevice    acvsDevice;

    private long                lastPong                          = 0;
    private ITextMessageHandler textMessageHandler                = null;


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
    public ACVSControlSurface (final ACVSDevice acvsDevice, final IHost host, final ColorManager colorManager, final ACVSConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, new ACVSMidiOutput (acvsDevice, output), input, null, 100, 100);

        this.acvsDevice = acvsDevice;

        this.addTextDisplay (new ACVSDisplay (this.host, (ACVSMidiOutput) this.output));

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
        final ACVSMidiOutput acvsMidiOutput = (ACVSMidiOutput) this.output;

        final int [] content = acvsMidiOutput.getMessageContent (data);
        if (content.length == 0)
        {
            this.host.error ("Unknown System Exclusive message: " + dataStr);
            return;
        }

        switch (content[0])
        {
            case ACVSMidiOutput.MESSAGE_ID_PONG:
                final long now = System.currentTimeMillis ();
                if (now - this.lastPong > HEARTBEAT_TIMEOUT)
                {
                    this.forceFlush ();
                    this.host.println ("Connected.");
                }
                this.lastPong = now;
                break;

            case ACVSMidiOutput.MESSAGE_ID_TEXT:
                if (this.textMessageHandler != null)
                {
                    final int itemID = content[1] << 8 + content[2];
                    final StringBuilder sb = new StringBuilder ();
                    for (int i = 5; i < content.length; i++)
                        sb.append (Character.valueOf ((char) content[i]));
                    this.textMessageHandler.handleTextMessage (itemID, sb.toString ());
                }
                break;

            default:
                this.host.error ("Unknown ACVS System Exclusive message: " + content[0]);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int channel, final int cc, final int value)
    {
        this.output.sendNoteEx (channel, cc, value);
    }


    /**
     * Get the ACVS device.
     *
     * @return The specific ACVS device
     */
    public ACVSDevice getAcvsDevice ()
    {
        return this.acvsDevice;
    }
}