// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.controller;

import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolConfiguration;
import de.mossgrabers.controller.ni.kontrol.mkii.TrackType;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * The Komplete Kontrol MkII control surface.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolProtocolControlSurface extends AbstractControlSurface<KontrolProtocolConfiguration>
{
    /** Command to initialize the protocol handshake (and acknowledge). */
    public static final int  CMD_HELLO                            = 0x01;
    /** Command to stop the protocol. */
    public static final int  CMD_GOODBYE                          = 0x02;

    /** The play button. */
    public static final int  KONTROL_PLAY                         = 0x10;
    /** The restart button (Shift+Play). No LED. */
    public static final int  KONTROL_RESTART                      = 0x11;
    /** The record button. */
    public static final int  KONTROL_RECORD                       = 0x12;
    /** The count-in button (Shift+Rec). */
    public static final int  KONTROL_COUNT_IN                     = 0x13;
    /** The stop button. */
    public static final int  KONTROL_STOP                         = 0x14;
    /** The clear button. */
    public static final int  KONTROL_CLEAR                        = 0x15;
    /** The loop button. */
    public static final int  KONTROL_LOOP                         = 0x16;
    /** The metro button. */
    public static final int  KONTROL_METRO                        = 0x17;
    /** The tempo button. No LED. */
    public static final int  KONTROL_TAP_TEMPO                    = 0x18;

    /** The undo button. */
    public static final int  KONTROL_UNDO                         = 0x20;
    /** The redo button (Shift+Undo). */
    public static final int  KONTROL_REDO                         = 0x21;
    /** The quantize button. */
    public static final int  KONTROL_QUANTIZE                     = 0x22;
    /** The auto button. */
    public static final int  KONTROL_AUTOMATION                   = 0x23;

    /** Track navigation. */
    public static final int  KONTROL_NAVIGATE_TRACKS              = 0x30;
    /** Track bank navigation. */
    public static final int  KONTROL_NAVIGATE_BANKS               = 0x31;
    /** Clip navigation. */
    public static final int  KONTROL_NAVIGATE_CLIPS               = 0x32;

    /** Transport navigation. */
    public static final int  KONTROL_NAVIGATE_MOVE_TRANSPORT      = 0x34;
    /** Loop navigation. */
    public static final int  KONTROL_NAVIGATE_MOVE_LOOP           = 0x35;

    /** Track available (actually the type the track, see TrackType). */
    public static final int  KONTROL_TRACK_AVAILABLE              = 0x40;
    /** Name of the Komplete plugin ID on the track, if exists. */
    public static final int  KONTROL_TRACK_INSTANCE               = 0x41;
    /** Select a track. */
    public static final int  KONTROL_TRACK_SELECTED               = 0x42;
    /** Mute a track. */
    public static final int  KONTROL_TRACK_MUTE                   = 0x43;
    /** Solo a track. */
    public static final int  KONTROL_TRACK_SOLO                   = 0x44;
    /** Arm a track. */
    public static final int  KONTROL_TRACK_RECARM                 = 0x45;
    /** Volume of a track. */
    public static final int  KONTROL_TRACK_VOLUME_TEXT            = 0x46;
    /** Panorama of a track. */
    public static final int  KONTROL_TRACK_PAN_TEXT               = 0x47;
    /** Name of a track. */
    public static final int  KONTROL_TRACK_NAME                   = 0x48;
    /** VU of a track. */
    public static final int  KONTROL_TRACK_VU                     = 0x49;
    /** Tracl muted by solo. */
    public static final int  KONTROL_TRACK_MUTED_BY_SOLO          = 0x4A;

    /** Change the volume of a track 0x50 - 0x57. */
    public static final int  KONTROL_TRACK_VOLUME                 = 0x50;
    /** Change the panorama of a track 0x58 - 0x5F. */
    public static final int  KONTROL_TRACK_PAN                    = 0x58;

    /** Play the currently selected clip. */
    public static final int  KONTROL_PLAY_SELECTED_CLIP           = 0x60;
    /** Stop the clip playing on the currently selected track. */
    public static final int  KONTROL_STOP_CLIP                    = 0x61;
    /** Start the currently selected scene. */
    public static final int  KONTROL_PLAY_SCENE                   = 0x62;
    /** Record Session button pressed. */
    public static final int  KONTROL_RECORD_SESSION               = 0x63;
    /** Increase/decrease volume of selected track. */
    public static final int  KONTROL_CHANGE_SELECTED_TRACK_VOLUME = 0x64;
    /** Increase/decrease pan of selected track. */
    public static final int  KONTROL_CHANGE_SELECTED_TRACK_PAN    = 0x65;
    /** Toggle mute of the selected track / Selected track muted. */
    public static final int  KONTROL_SELECTED_TRACK_MUTE          = 0x66;
    /** Toggle solo of the selected track / Selected track soloed. */
    public static final int  KONTROL_SELECTED_TRACK_SOLO          = 0x67;
    /** Selected track available. */
    public static final int  KONTROL_SELECTED_TRACK_AVAILABLE     = 0x68;
    /** Selected track muted by solo. */
    public static final int  KONTROL_SELECTED_TRACK_MUTED_BY_SOLO = 0x69;

    private final int        requiredVersion;
    private int              protocolVersion                      = KontrolProtocol.MAX_VERSION;
    private final ValueCache valueCache                           = new ValueCache ();
    private final Object     cacheLock                            = new Object ();
    private final Object     handshakeLock                        = new Object ();
    private boolean          isConnectedToNIHIA                   = false;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     * @param version The version number of the NIHIA protocol to request
     */
    public KontrolProtocolControlSurface (final IHost host, final ColorManager colorManager, final KontrolProtocolConfiguration configuration, final IMidiOutput output, final IMidiInput input, final int version)
    {
        super (host, configuration, colorManager, output, input, null, 800, 300);

        this.requiredVersion = version;
        this.defaultMidiChannel = 15;
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        super.internalShutdown ();

        synchronized (this.handshakeLock)
        {
            // Stop flush
            this.isConnectedToNIHIA = false;

            for (int i = 0; i < 8; i++)
                this.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_AVAILABLE, TrackType.EMPTY, i);

            this.sendCommand (KontrolProtocolControlSurface.CMD_GOODBYE, 0);
        }
    }


    /**
     * Returns true if the handshake with the Native Instruments Host Integration was successfully
     * executed.
     *
     * @return True if connected to the NIHIA
     */
    public boolean isConnectedToNIHIA ()
    {
        synchronized (this.handshakeLock)
        {
            return this.isConnectedToNIHIA;
        }
    }


    /**
     * Initialize the handshake with the NIHIA.
     */
    public void initHandshake ()
    {
        this.sendCommand (CMD_HELLO, this.requiredVersion);
    }


    /**
     * Call if the handshake response was successfully received from the NIHIA.
     *
     * @param protocol The protocol version
     */
    public void handshakeSuccess (final int protocol)
    {
        synchronized (this.handshakeLock)
        {
            this.setProtocolVersion (protocol);

            // Initial flush of the whole DAW state...
            this.clearCache ();

            this.isConnectedToNIHIA = true;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int cc, final int value)
    {
        this.sendCommand (cc, value);
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
        this.sendKontrolTrackSysEx (stateID, value, track, StringUtils.fixASCII (info).chars ().toArray ());
    }


    /** {@inheritDoc} */
    @Override
    public void clearCache ()
    {
        synchronized (this.cacheLock)
        {
            this.valueCache.clearCache ();
        }

        super.clearCache ();
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
        synchronized (this.cacheLock)
        {
            if (this.valueCache.store (track, stateID, value, info))
                return;
        }

        final int [] data = new int [3 + info.length];
        data[0] = stateID;
        data[1] = value;
        data[2] = track;
        for (int i = 0; i < info.length; i++)
            data[3 + i] = info[i];

        this.output.sendSysex ("F0 00 21 09 00 00 44 43 01 00 " + StringUtils.toHexStr (data) + "F7");
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


    /**
     * Caches the values of the system exclusive values.
     */
    private static class ValueCache
    {
        private final List<List<int []>> cache = new ArrayList<> (8);


        /**
         * Constructor.
         */
        public ValueCache ()
        {
            this.clearCache ();
        }


        /**
         * Clear the cache.
         */
        public final void clearCache ()
        {
            for (int i = 0; i < 8; i++)
            {
                final List<int []> e = new ArrayList<> (128);
                for (int j = 0; j < 128; j++)
                    e.add (new int [0]);
                this.cache.add (e);
            }
        }


        /**
         * Stores the value and data in the cache for the track and stateID.
         *
         * @param track The track number
         * @param stateID The state id
         * @param value The value
         * @param data Further data
         * @return False if cache was updated otherwise the given value and data are already stored
         */
        public boolean store (final int track, final int stateID, final int value, final int [] data)
        {
            final List<int []> trackItem = this.cache.get (track);
            final int [] values = trackItem.get (stateID);

            final int [] newValues = new int [1 + data.length];
            newValues[0] = value;
            if (data.length > 0)
                System.arraycopy (data, 0, newValues, 1, data.length);

            if (Arrays.equals (values, newValues))
                return true;

            trackItem.set (stateID, newValues);
            return false;
        }
    }
}