// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.midi.mkii.controller;

import de.mossgrabers.controller.kontrol.midi.mkii.KontrolMkIIConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The Komplete Kontrol MkII control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolMkIIControlSurface extends AbstractControlSurface<KontrolMkIIConfiguration>
{
    /** Command to initialise the protocol handshake. */
    public static final int     CMD_HELLO                       = 0x01;
    /** Command to stop the protocol. */
    public static final int     CMD_GOODBYE                     = 0x02;

    /** The play button. */
    public static final int     KONTROL_BUTTON_PLAY             = 0x10;
    /** The restart button (Shift+Play). No LED. */
    public static final int     KONTROL_BUTTON_RESTART          = 0x11;
    /** The record button. */
    public static final int     KONTROL_BUTTON_RECORD           = 0x12;
    /** The count-in button (Shift+Rec). */
    public static final int     KONTROL_BUTTON_COUNT_IN         = 0x13;
    /** The stop button. */
    public static final int     KONTROL_BUTTON_STOP             = 0x14;
    /** The clear button. */
    public static final int     KONTROL_BUTTON_CLEAR            = 0x15;
    /** The loop button. */
    public static final int     KONTROL_BUTTON_LOOP             = 0x16;
    /** The metro button. */
    public static final int     KONTROL_BUTTON_METRO            = 0x17;
    /** The tempo button. No LED. */
    public static final int     KONTROL_BUTTON_TEMPO            = 0x18;
    /** The undo button. */
    public static final int     KONTROL_BUTTON_UNDO             = 0x20;
    /** The redo button (Shift+Undo). */
    public static final int     KONTROL_BUTTON_REDO             = 0x21;
    /** The quantize button. */
    public static final int     KONTROL_BUTTON_QUANTIZE         = 0x22;
    /** The auto button. */
    public static final int     KONTROL_BUTTON_AUTOMATION       = 0x23;

    /** Track navigation. */
    public static final int     KONTROL_NAVIGATE_TRACKS         = 0x30;
    /** Track bank navigation. */
    public static final int     KONTROL_NAVIGATE_BANKS          = 0x31;
    /** Clip navigation. */
    public static final int     KONTROL_NAVIGATE_CLIPS          = 0x32;
    /** Scene navigation. */
    public static final int     KONTROL_NAVIGATE_SCENES         = 0x33;

    /** Transport navigation. */
    public static final int     KONTROL_NAVIGATE_MOVE_TRANSPORT = 0x64; // TODO 0x34;
    /** Loop navigation. */
    public static final int     KONTROL_NAVIGATE_MOVE_LOOP      = 0x35;

    /** Select a track. */
    public static final int     KONTROL_BUTTON_SELECT           = 0x42;
    /** Mute a track. */
    public static final int     KONTROL_BUTTON_MUTE             = 0x43;
    /** Solo a track. */
    public static final int     KONTROL_BUTTON_SOLO             = 0x44;
    /** Arm a track. */
    public static final int     KONTROL_BUTTON_ARM              = 0x45;

    /** Change the volume of a track. */
    public static final int     KONTROL_KNOB_VOLUME             = 0x50;
    /** Change the panorama of a track. */
    public static final int     KONTROL_KNOB_PAN                = 0x58;

    private static final int [] KONTROL_BUTTONS_ALL             =
    {
        KONTROL_BUTTON_PLAY,
        KONTROL_BUTTON_RESTART,
        KONTROL_BUTTON_RECORD,
        KONTROL_BUTTON_COUNT_IN,
        KONTROL_BUTTON_STOP,
        KONTROL_BUTTON_CLEAR,
        KONTROL_BUTTON_LOOP,
        KONTROL_BUTTON_METRO,
        KONTROL_BUTTON_TEMPO,
        KONTROL_BUTTON_UNDO,
        KONTROL_BUTTON_REDO,
        KONTROL_BUTTON_QUANTIZE,
        KONTROL_BUTTON_AUTOMATION
    };

    private int                 protocolVersion                 = 1;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
     */
    public KontrolMkIIControlSurface (final IHost host, final ColorManager colorManager, final KontrolMkIIConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, null, KONTROL_BUTTONS_ALL);
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setButton (final int button, final int state)
    {
        this.sendCommand (button, state);
    }


    /**
     * Send a command to the Komplete Kontrol.
     *
     * @param command The command number
     * @param value The value
     */
    public void sendCommand (final int command, final int value)
    {
        this.output.sendCCEx (15, command, value);
    }


    /**
     * Send SysEx to the Kontrol.
     *
     * @param stateID The state ID (command)
     * @param value The value to send
     * @param track The track index (0-7)
     */
    public void sendKontrolTrackSysEx (final int stateID, final int value, final int track)
    {
        this.sendKontrolTrackSysEx (stateID, value, track, "");
    }


    /**
     * Send SysEx to the Kontrol.
     *
     * @param stateID The state ID (command)
     * @param value The value to send
     * @param track The track index (0-7)
     * @param info An info string
     */
    public void sendKontrolTrackSysEx (final int stateID, final int value, final int track, final String info)
    {
        final String asciiText = StringUtils.fixASCII (info);

        final int length = asciiText.length ();
        final int [] data = new int [3 + length];
        data[0] = stateID;
        data[1] = value;
        data[2] = track;
        for (int i = 0; i < length; i++)
            data[3 + i] = asciiText.charAt (i);
        this.sendKontrolSysEx (data);
    }


    /**
     * Send SysEx to the Kontrol.
     *
     * @param stateID The state ID (command)
     * @param value The value to send
     * @param track The track index (0-7)
     * @param info Further info data
     */
    public void sendKontrolTrackSysEx (final int stateID, final int value, final int track, final int [] info)
    {
        final int [] data = new int [3 + info.length];
        data[0] = stateID;
        data[1] = value;
        data[2] = track;
        for (int i = 0; i < info.length; i++)
            data[3 + i] = info[i];
        this.sendKontrolSysEx (data);
    }


    /**
     * Send SysEx to the Kontrol.
     *
     * @param parameters The parameters to send
     */
    public void sendKontrolSysEx (final int [] parameters)
    {
        this.output.sendSysex ("F0 00 21 09 00 00 44 43 01 00" + StringUtils.toHexStr (parameters) + "F7");
    }


    /** {@inheritDoc} */
    @Override
    protected void handleCC (final int channel, final int cc, final int value)
    {
        if (channel != 15)
            return;

        // Emulate a proper button press, NIHost only sends value 1
        if (this.isButton (cc))
        {
            super.handleCC (channel, cc, 127);
            super.handleCC (channel, cc, 0);
        }
        else
            super.handleCC (channel, cc, value);
    }


    /** {@inheritDoc} */
    @Override
    protected void checkButtonState (final int buttonID)
    {
        // No long presses on the Komplete Kontrol MkII
    }


    /**
     * Get the protocol number of the currently connected Komplete Kontrol.
     *
     * @return The protocol number
     */
    public int getProtocolVersion ()
    {
        return this.protocolVersion;
    }


    /**
     * Set the protocol number of the currently connected Komplete Kontrol.
     *
     * @param protocolVersion The protocol number
     */
    public void setProtocolVersion (final int protocolVersion)
    {
        this.protocolVersion = protocolVersion;
    }
}