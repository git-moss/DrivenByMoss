// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.controller;

import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolConfiguration;
import de.mossgrabers.controller.ni.kontrol.mkii.NIHIASysExCallback;
import de.mossgrabers.controller.ni.kontrol.mkii.TrackType;
import de.mossgrabers.controller.ni.kontrol.mkii.mode.ParamsMode;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;


/**
 * The Komplete Kontrol MkII control surface.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolProtocolControlSurface extends AbstractControlSurface<KontrolProtocolConfiguration>
{
    ///////////////////////////////////////////////////////////////////////////////
    // SYSEX

    /** Switches a parameter on or off on. */
    public static final int          SYSEX_SURFACE_CONFIGURATION      = 0x03;
    /** New for v4. */
    public static final int          SYSEX_IDENTITY                   = 0x07;
    /** New for v4. */
    public static final int          SYSEX_SET_TEMPO                  = 0x19;

    /** Track available (actually the type the track, see TrackType). */
    public static final int          SYSEX_TRACK_AVAILABLE            = 0x40;
    /** Name of the Komplete-plugin ID on the track, if exists. */
    public static final int          SYSEX_TRACK_INSTANCE             = 0x41;
    /** Select a track. */
    public static final int          SYSEX_TRACK_SELECTED             = 0x42;
    /** Mute a track. */
    public static final int          SYSEX_TRACK_MUTE                 = 0x43;
    /** Solo a track. */
    public static final int          SYSEX_TRACK_SOLO                 = 0x44;
    /** Arm a track. */
    public static final int          SYSEX_TRACK_RECARM               = 0x45;
    /** Volume of a track. */
    public static final int          SYSEX_TRACK_VOLUME_TEXT          = 0x46;
    /** Panning of a track. */
    public static final int          SYSEX_TRACK_PAN_TEXT             = 0x47;
    /** Name of a track. */
    public static final int          SYSEX_TRACK_NAME                 = 0x48;
    /** VU of a track. */
    public static final int          SYSEX_TRACK_VU                   = 0x49;
    /** Track muted by solo. */
    public static final int          SYSEX_TRACK_MUTED_BY_SOLO        = 0x4A;
    /** New for v4: Track color. */
    public static final int          SYSEX_TRACK_COLOR                = 0x4B;

    /** New for v4: The index of the selected plug-in of the selected track. */
    public static final int          SYSEX_PLUGIN_SELECTED_PLUGIN     = 0x70;
    /** New for v4: The names of all available plug-ins of the selected track. */
    public static final int          SYSEX_PLUGIN_CHAIN_INFO          = 0x71;
    /** New for v4: Set the names of one of the 8 parameters of the page. */
    public static final int          SYSEX_PLUGIN_PARAM_DISPLAY_NAME  = 0x72;
    /** New for v4: Set the value as text of one of the 8 parameters of the page. */
    public static final int          SYSEX_PLUGIN_PARAM_DISPLAY_VALUE = 0x73;
    /** New for v4: Set the selected parameter page. */
    public static final int          SYSEX_PLUGIN_SELECTED_PARAM_PAGE = 0x74;
    /** New for v4: Set the name of the parameter page. */
    public static final int          SYSEX_PLUGIN_PAGE_NAME           = 0x75;
    /** New for v4: Set the name of the selected preset, if any. */
    public static final int          SYSEX_PLUGIN_SELECTED_PRESET     = 0x76;

    ///////////////////////////////////////////////////////////////////////////////
    // MIDI CC

    /** Command to initialize the protocol handshake (and acknowledge). */
    public static final int          CC_HELLO                         = 0x01;
    /** Command to stop the protocol. */
    public static final int          CC_GOODBYE                       = 0x02;

    /** New for v4: The Shift button. */
    public static final int          CC_SHIFT                         = 0x04;
    /** New for v4: Mode selection: Mixer/Plug-ins. */
    public static final int          CC_MODE_SELECT                   = 0x05;
    /** New for v4: Use SysEx parameter updates. */
    public static final int          CC_USE_SYSEX_PARAM_UPDATES       = 0x06;

    /** The play button. */
    public static final int          CC_PLAY                          = 0x10;
    /** The restart button (Shift+Play). No LED. */
    public static final int          CC_RESTART                       = 0x11;
    /** The record button. */
    public static final int          CC_RECORD                        = 0x12;
    /** The count-in button (Shift+Rec). */
    public static final int          CC_COUNT_IN                      = 0x13;
    /** The stop button. */
    public static final int          CC_STOP                          = 0x14;
    /** The clear button. */
    public static final int          CC_CLEAR                         = 0x15;
    /** The loop button. */
    public static final int          CC_LOOP                          = 0x16;
    /** The metro button. */
    public static final int          CC_METRO                         = 0x17;
    /** The tempo button. No LED. */
    public static final int          CC_TAP_TEMPO                     = 0x18;

    /** The undo button. */
    public static final int          CC_UNDO                          = 0x20;
    /** The redo button (Shift+Undo). */
    public static final int          CC_REDO                          = 0x21;
    /** The quantize button. */
    public static final int          CC_QUANTIZE                      = 0x22;
    /** The auto button. */
    public static final int          CC_AUTOMATION                    = 0x23;

    /** Track navigation. */
    public static final int          CC_NAVIGATE_TRACKS               = 0x30;
    /** Track bank navigation. */
    public static final int          CC_NAVIGATE_BANKS                = 0x31;
    /** Clip navigation. */
    public static final int          CC_NAVIGATE_CLIPS                = 0x32;

    /** Transport navigation. */
    public static final int          CC_NAVIGATE_MOVE_TRANSPORT       = 0x34;
    /** Loop navigation. */
    public static final int          CC_NAVIGATE_MOVE_LOOP            = 0x35;
    /** New for v4: Browser navigation. */
    public static final int          CC_NAVIGATE_PRESETS              = 0x36;

    /** Change the volume of a track 0x50 - 0x57. */
    public static final int          CC_TRACK_VOLUME                  = 0x50;
    /** Change the panning of a track 0x58 - 0x5F. */
    public static final int          CC_TRACK_PAN                     = 0x58;

    /** Play the currently selected clip. */
    public static final int          CC_PLAY_SELECTED_CLIP            = 0x60;
    /** Stop the clip playing on the currently selected track. */
    public static final int          CC_STOP_CLIP                     = 0x61;
    /** Start the currently selected scene. */
    public static final int          CC_PLAY_SCENE                    = 0x62;
    /** Record Session button pressed. */
    public static final int          CC_RECORD_SESSION                = 0x63;
    /** Increase/decrease volume of selected track. */
    public static final int          CC_CHANGE_SELECTED_TRACK_VOLUME  = 0x64;
    /** Increase/decrease pan of selected track. */
    public static final int          CC_CHANGE_SELECTED_TRACK_PAN     = 0x65;
    /** Toggle mute of the selected track / Selected track muted. */
    public static final int          CC_SELECTED_TRACK_MUTE           = 0x66;
    /** Toggle solo of the selected track / Selected track soloed. */
    public static final int          CC_SELECTED_TRACK_SOLO           = 0x67;
    /** Selected track available. */
    public static final int          CC_SELECTED_TRACK_AVAILABLE      = 0x68;
    /** Selected track muted by solo. */
    public static final int          CC_SELECTED_TRACK_MUTED_BY_SOLO  = 0x69;

    /** First parameter has changed. */
    public static final int          CC_PARAM_VALUE_CHANGE            = 0x70;

    private static final double      TEN_NS_PER_MINUTE                = 60e8;

    private static final byte []     NHIA_SYSEX_HEADER                = new byte []
    {
        (byte) 0xF0,
        0x00,
        0x21,
        0x09,
        0x00,
        0x00,
        0x44,
        0x43,
        0x01,
        0x00
    };

    private final int                requiredVersion;
    private int                      protocolVersion                  = KontrolProtocol.MAX_VERSION;
    private final NIHIASysExCallback sysexCallback;

    private final Object             cacheLock                        = new Object ();
    private final ValueCache         valueCache                       = new ValueCache ();
    private double                   cachedTempo                      = 0;
    private final Object             handshakeLock                    = new Object ();
    private boolean                  isConnectedToNIHIA               = false;
    private final int []                   ccValueCache                     = new int [255];


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     * @param sysexCallback Callback for value changes received via SysEx
     * @param version The version number of the NIHIA protocol to request
     */
    public KontrolProtocolControlSurface (final IHost host, final ColorManager colorManager, final KontrolProtocolConfiguration configuration, final IMidiOutput output, final IMidiInput input, final NIHIASysExCallback sysexCallback, final int version)
    {
        super (host, configuration, colorManager, output, input, null, 800, 300);

        this.requiredVersion = version;
        this.defaultMidiChannel = 15;
        this.sysexCallback = sysexCallback;

        input.setSysexCallback (this::handleSysEx);
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
                this.sendKontrolSysEx (KontrolProtocolControlSurface.SYSEX_TRACK_AVAILABLE, TrackType.EMPTY, i);

            this.sendCommand (KontrolProtocolControlSurface.CC_GOODBYE, 0);
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
        this.sendCommand (CC_HELLO, this.requiredVersion);
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

        this.sysexCallback.sendDAWInfo ();
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
        if (this.ccValueCache[command] == value)
            return;
        this.ccValueCache[command] = value;
        this.output.sendCCEx (15, command, value);
    }


    /**
     * Send SysEx to the Kontrol.
     *
     * @param stateID The state ID (command)
     * @param value The value to send
     * @param index The index (0-7) e.g. of a track
     * @param doCache Cache the value if true
     */
    public void sendKontrolSysEx (final int stateID, final int value, final int index, final boolean doCache)
    {
        this.sendKontrolSysEx (stateID, value, index, "", doCache);
    }


    /**
     * Send SysEx to the Kontrol. Does cache the value.
     *
     * @param stateID The state ID (command)
     * @param value The value to send
     * @param index The index (0-7) e.g. of a track
     */
    public void sendKontrolSysEx (final int stateID, final int value, final int index)
    {
        this.sendKontrolSysEx (stateID, value, index, "", true);
    }


    /**
     * Send SysEx to the Kontrol.
     *
     * @param stateID The state ID (command)
     * @param value The value to send
     * @param index The index (0-7) e.g. of a track
     * @param info An info string
     * @param doCache Cache the value if true
     */
    public void sendKontrolSysEx (final int stateID, final int value, final int index, final String info, final boolean doCache)
    {
        this.sendKontrolSysEx (stateID, value, index, StringUtils.fixASCII (info).chars ().toArray (), doCache);
    }


    /**
     * Send SysEx to the Kontrol. Does cache the value.
     *
     * @param stateID The state ID (command)
     * @param value The value to send
     * @param index The index (0-7) e.g. of a track
     * @param info An info string
     */
    public void sendKontrolSysEx (final int stateID, final int value, final int index, final String info)
    {
        this.sendKontrolSysEx (stateID, value, index, StringUtils.fixASCII (info).chars ().toArray (), true);
    }


    /**
     * Send SysEx to the Kontrol.
     *
     * @param stateID The state ID (command)
     * @param value The value to send
     * @param index The index (0-7) e.g. of a track
     * @param info Further info data
     * @param doCache Cache the value if true
     */
    public void sendKontrolSysEx (final int stateID, final int value, final int index, final int [] info, final boolean doCache)
    {
        synchronized (this.cacheLock)
        {
            final boolean isPresent = this.valueCache.store (stateID, index, value, info);
            if (doCache && isPresent)
                return;
        }

        final byte [] data = new byte [3 + info.length];
        data[0] = (byte) stateID;
        data[1] = (byte) value;
        data[2] = (byte) index;
        for (int i = 0; i < info.length; i++)
            data[3 + i] = (byte) info[i];

        this.sendNHIASysEx (data);
    }


    /**
     * Send SysEx to the Kontrol. Does cache the value.
     *
     * @param stateID The state ID (command)
     * @param value The value to send
     * @param index The index (0-7) e.g. of a track
     * @param info Further info data
     */
    public void sendKontrolSysEx (final int stateID, final int value, final int index, final int [] info)
    {
        this.sendKontrolSysEx (stateID, value, index, info, true);
    }


    /**
     * Send some global values like tempo to the Kontrol.
     *
     * @param model The data model
     */
    public void sendGlobalValues (final IModel model)
    {
        if (this.protocolVersion < 4)
            return;

        this.sendTempo (model.getTransport ().getTempo (), false);

        // This needs to be sent all the time to activate the PLUG-IN button!
        ((ParamsMode) this.getModeManager ().get (Modes.DEVICE_PARAMS)).updateAvailableDevices ();
    }


    /**
     * Send the tempo to the Kontrol.
     *
     * @param tempo The tempo
     * @param ignoreCache Clears the cache and resends always
     */
    public void sendTempo (final double tempo, final boolean ignoreCache)
    {
        synchronized (this.cacheLock)
        {
            if (!ignoreCache && Double.compare (tempo, this.cachedTempo) == 0)
                return;
            this.cachedTempo = tempo;
        }

        final byte [] data = new byte [8];
        data[0] = SYSEX_SET_TEMPO;
        data[1] = 0;
        data[2] = 0;
        final long nsPerMinute = (long) (TEN_NS_PER_MINUTE / tempo);
        for (int i = 0; i < 5; i++)
            data[3 + i] = (byte) (nsPerMinute >> i * 7 & 0x7F);

        this.sendNHIASysEx (data);
    }


    /**
     * Sends information about the connected DAW.
     *
     * @param versionMajor The major version of the DAW
     * @param versionMinor The minor version of the DAW
     * @param dawName The name of the DAW
     */
    public void sendDAWInfo (final int versionMajor, final int versionMinor, final String dawName)
    {
        final int [] array = StringUtils.fixASCII (dawName).chars ().toArray ();
        final byte [] data = new byte [3 + array.length];
        data[0] = SYSEX_IDENTITY;
        data[1] = (byte) versionMajor;
        data[2] = (byte) versionMinor;
        for (int i = 0; i < array.length; i++)
            data[3 + i] = (byte) array[i];
        this.sendNHIASysEx (data);
    }


    /** {@inheritDoc} */
    @Override
    public void clearCache ()
    {
        synchronized (this.cacheLock)
        {
            this.valueCache.clearCache ();
            Arrays.fill (this.ccValueCache, -1);
        }

        super.clearCache ();
    }


    private void sendNHIASysEx (final byte [] data)
    {
        super.sendSysex (NHIA_SYSEX_HEADER, data);
    }


    /**
     * Handle incoming system exclusive messages.
     *
     * @param data The data of the system exclusive message
     */
    private void handleSysEx (final String data)
    {
        final int [] byteData = StringUtils.fromHexStr (data);
        if (!startsWithPrefix (byteData))
        {
            this.host.error (String.format ("Unused sysex command: %s", data));
            return;
        }

        switch (byteData[10])
        {
            case SYSEX_SET_TEMPO:
                long nsPerMinute = 0;
                for (int i = 0; i < 5; i++)
                    nsPerMinute |= (long) byteData[13 + i] << i * 7;
                final double tempo = TEN_NS_PER_MINUTE / nsPerMinute;
                final long roundedTo2Fractions = Math.round (tempo * 100.0);
                this.sysexCallback.setTempo (roundedTo2Fractions / 100.0);
                break;

            case SYSEX_PLUGIN_SELECTED_PLUGIN:
                this.sysexCallback.selectDevice (byteData[12]);
                break;

            default:
                this.host.error (String.format ("Unused NHIA sysex command: %02X", Integer.valueOf (byteData[10])));
                break;
        }
    }


    private static boolean startsWithPrefix (final int [] byteData)
    {
        if (byteData.length < NHIA_SYSEX_HEADER.length)
            return false;
        for (int i = 0; i < NHIA_SYSEX_HEADER.length; i++)
        {
            // & 0xFF ensures the integer is compared as an unsigned byte
            if ((byteData[i] & 0xFF) != (NHIA_SYSEX_HEADER[i] & 0xFF))
                return false;
        }
        return true;
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
        private final Map<Integer, Map<Integer, int []>> cache                 = new TreeMap<> ();

        private int                                      numParameterPages     = -1;
        private int                                      selectedParameterPage = -1;


        /**
         * Clear the cache.
         */
        public final void clearCache ()
        {
            this.cache.clear ();
        }


        /**
         * Stores the value and data in the cache for the 2 given keys.
         *
         * @param key1 The first key for caching
         * @param key2 The second key for caching
         * @param value The value
         * @param data Further data
         * @return False if cache was updated otherwise the given value and data are already stored
         */
        public boolean store (final int key1, final int key2, final int value, final int [] data)
        {
            // Cache does not work for this since the value is the page size which is not different
            // for each page (the index)!
            if (key1 == SYSEX_PLUGIN_SELECTED_PARAM_PAGE)
            {
                if (key2 != this.selectedParameterPage || value != this.numParameterPages)
                {
                    this.selectedParameterPage = key2;
                    this.numParameterPages = value;
                    return false;
                }
                return true;
            }

            final Map<Integer, int []> item = this.cache.computeIfAbsent (Integer.valueOf (key1), key -> new TreeMap<> ());
            final int [] values = item.get (Integer.valueOf (key2));

            final int [] newValues = new int [1 + data.length];
            newValues[0] = value;
            if (data.length > 0)
                System.arraycopy (data, 0, newValues, 1, data.length);

            if (Arrays.equals (values, newValues))
                return true;

            item.put (Integer.valueOf (key2), newValues);
            return false;
        }
    }
}