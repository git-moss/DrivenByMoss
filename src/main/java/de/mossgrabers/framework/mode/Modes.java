// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Static mode IDs and some helper functions.
 *
 * @author Jürgen Moßgraber
 */
public enum Modes
{
    /** Single Track editing mode. */
    TRACK,
    /** Edit details of one track. */
    TRACK_DETAILS,
    /** Edit volume of all tracks. */
    VOLUME,
    /** Edit panning of all tracks. */
    PAN,
    /** Edit cross-fader settings of all tracks. */
    CROSSFADER,
    /** Edit cross-fader setting A of all tracks. */
    CROSSFADE_MODE_A,
    /** Edit cross-fader setting B of all tracks. */
    CROSSFADE_MODE_B,
    /** Mode to arm tracks for recording. */
    REC_ARM,
    /** Mode to select tracks. */
    TRACK_SELECT,
    /** Mode to mute tracks. */
    MUTE,
    /** Mode to solo tracks. */
    SOLO,
    /** Mode to stop clips. */
    STOP_CLIP,

    /** Edit Send 1 of all tracks. */
    SEND1,
    /** Edit Send 2 of all tracks. */
    SEND2,
    /** Edit Send 3 of all tracks. */
    SEND3,
    /** Edit Send 4 of all tracks. */
    SEND4,
    /** Edit Send 5 of all tracks. */
    SEND5,
    /** Edit Send 6 of all tracks. */
    SEND6,
    /** Edit Send 7 of all tracks. */
    SEND7,
    /** Edit Send 8 of all tracks. */
    SEND8,
    /** Edit Sends of all tracks. */
    SEND,

    /** Edit master track. */
    MASTER,
    /** Edit master track (temporary). */
    MASTER_TEMP,

    /** Edit device parameters. */
    DEVICE_PARAMS,
    /** Edit device parameters of the EQ. */
    EQ_DEVICE_PARAMS,
    /** Edit device parameters of the first instrument. */
    INSTRUMENT_DEVICE_PARAMS,
    /** Edit device slot chains. */
    DEVICE_CHAINS,
    /** Edit project parameters. */
    PROJECT_PARAMETERS,
    /** Edit track parameters. */
    TRACK_PARAMETERS,

    /** Edit layer parameters. */
    DEVICE_LAYER,
    /** Edit volume of all layers. */
    DEVICE_LAYER_VOLUME,
    /** Edit panning of all layers. */
    DEVICE_LAYER_PAN,
    /** Edit Send 1 of all layers. */
    DEVICE_LAYER_SEND1,
    /** Edit Send 2 of all layers. */
    DEVICE_LAYER_SEND2,
    /** Edit Send 3 of all layers. */
    DEVICE_LAYER_SEND3,
    /** Edit Send 4 of all layers. */
    DEVICE_LAYER_SEND4,
    /** Edit Send 5 of all layers. */
    DEVICE_LAYER_SEND5,
    /** Edit Send 6 of all layers. */
    DEVICE_LAYER_SEND6,
    /** Edit Send 7 of all layers. */
    DEVICE_LAYER_SEND7,
    /** Edit Send 8 of all layers. */
    DEVICE_LAYER_SEND8,
    /** Edit layer details. */
    DEVICE_LAYER_DETAILS,
    /** Layer mute. */
    DEVICE_LAYER_MUTE,
    /** Layer solo. */
    DEVICE_LAYER_SOLO,

    /** Browser mode. */
    BROWSER,

    /** Edit clip parameters. */
    CLIP,
    /** Edit note parameters. */
    NOTE,

    /** Show/hide different frames. */
    FRAME,
    /** Groove edit mode. */
    GROOVE,
    /** Edit accent parameters. */
    ACCENT,
    /** Scale configuration. */
    SCALES,
    /** Scale layout mode. */
    SCALE_LAYOUT,
    /** Pick length of new clips. */
    FIXED,
    /** Edit ribbon parameters. */
    RIBBON,
    /** Select a view for a track. */
    VIEW_SELECT,
    /** Edit automation parameters. */
    AUTOMATION,
    /** Transport mode. */
    TRANSPORT,
    /** Configuration mode. */
    CONFIGURATION,
    /** Setup mode. */
    SETUP,
    /** Info mode. */
    INFO,
    /** Project mode. */
    PROJECT,
    /** Audio configuration mode. */
    AUDIO,
    /** Select of session views. */
    SESSION_VIEW_SELECT,
    /** Session mode. */
    SESSION,
    /** Markers mode. */
    MARKERS,
    /** Repeat note length mode. */
    REPEAT_NOTE,
    /** Execute different functions. */
    FUNCTIONS,
    /** Edit play options. */
    PLAY_OPTIONS,
    /** Play cursor position mode. */
    POSITION,
    /** Tempo mode. */
    TEMPO,
    /** Loop start mode. */
    LOOP_START,
    /** Loop length mode. */
    LOOP_LENGTH,
    /** A user mode. */
    USER,
    /** A mode that does nothing. */
    DUMMY,
    /** A mode to select options for adding a track. */
    ADD_TRACK,
    /** A drum sequencer mode. */
    DRUM_SEQUENCER,
    /** A note sequencer mode. */
    NOTE_SEQUENCER;


    /** The name of the Track mode. */
    public static final String              NAME_TRACK              = "Track";
    /** The name of the Volume mode. */
    public static final String              NAME_VOLUME             = "Volume";
    /** The name of the Panning mode. */
    public static final String              NAME_PANNING            = "Panning";
    /** The name of the Sends mode. */
    public static final String              NAME_SENDS              = "Sends";
    /** The name of the Send 1 mode. */
    public static final String              NAME_SEND1              = "Send 1";
    /** The name of the Send 2 mode. */
    public static final String              NAME_SEND2              = "Send 2";
    /** The name of the Send 3 mode. */
    public static final String              NAME_SEND3              = "Send 3";
    /** The name of the Send 4 mode. */
    public static final String              NAME_SEND4              = "Send 4";
    /** The name of the Send 5 mode. */
    public static final String              NAME_SEND5              = "Send 5";
    /** The name of the Send 6 mode. */
    public static final String              NAME_SEND6              = "Send 6";
    /** The name of the Send 7 mode. */
    public static final String              NAME_SEND7              = "Send 7";
    /** The name of the Send 8 mode. */
    public static final String              NAME_SEND8              = "Send 8";
    /** The name of the Cross-fade mode. */
    public static final String              NAME_CROSSFADE          = "Crossfade";
    /** The name of the Parameters mode. */
    public static final String              NAME_PARAMETERS         = "Parameters";
    /** The name of the Project Parameters mode. */
    public static final String              NAME_PROJECT_PARAMETERS = "Project Parameters";
    /** The name of the Track Parameters mode. */
    public static final String              NAME_TRACK_PARAMETERS   = "Track Parameters";
    /** The name of the Equalizer mode. */
    public static final String              NAME_EQUALIZER          = "Equalizer";
    /** The name of the Instrument parameters mode. */
    public static final String              NAME_INSTRUMENT_PARAMS  = "Instrument Parameters";
    /** The name of the Layer mode. */
    public static final String              NAME_LAYER              = "Layer";
    /** The name of the Layer Volume mode. */
    public static final String              NAME_LAYER_VOLUME       = "Layer Volume";
    /** The name of the Layer Panning mode. */
    public static final String              NAME_LAYER_PANNING      = "Layer Panning";
    /** The name of the Layer Sends mode. */
    public static final String              NAME_LAYER_SENDS        = "Layer Sends";
    /** The name of the Layer Send 1 mode. */
    public static final String              NAME_LAYER_SEND1        = "Layer Send 1";
    /** The name of the Layer Send 2 mode. */
    public static final String              NAME_LAYER_SEND2        = "Layer Send 2";
    /** The name of the Layer Send 3 mode. */
    public static final String              NAME_LAYER_SEND3        = "Layer Send 3";
    /** The name of the Layer Send 4 mode. */
    public static final String              NAME_LAYER_SEND4        = "Layer Send 4";
    /** The name of the Layer Send 5 mode. */
    public static final String              NAME_LAYER_SEND5        = "Layer Send 5";
    /** The name of the Layer Send 6 mode. */
    public static final String              NAME_LAYER_SEND6        = "Layer Send 6";
    /** The name of the Layer Send 7 mode. */
    public static final String              NAME_LAYER_SEND7        = "Layer Send 7";
    /** The name of the Layer Send 8 mode. */
    public static final String              NAME_LAYER_SEND8        = "Layer Send 8";
    /** The name of the Automation mode. */
    public static final String              NAME_AUTOMATION         = "Automation";
    /** The name of the Markers mode. */
    public static final String              NAME_MARKERS            = "Markers";

    private static final Set<Modes>         TRACK_MODES             = EnumSet.range (Modes.TRACK, Modes.STOP_CLIP);
    private static final Set<Modes>         LAYER_MODES             = EnumSet.range (Modes.DEVICE_LAYER, Modes.DEVICE_LAYER_DETAILS);
    private static final Set<Modes>         SEND_MODES              = EnumSet.range (Modes.SEND1, Modes.SEND8);
    private static final Set<Modes>         LAYER_SEND_MODES        = EnumSet.range (Modes.DEVICE_LAYER_SEND1, Modes.DEVICE_LAYER_SEND8);
    private static final Set<Modes>         MIX_MODES               = EnumSet.range (Modes.TRACK, Modes.SEND);
    private static final Set<Modes>         MASTER_MODES            = EnumSet.of (Modes.MASTER, Modes.MASTER_TEMP, Modes.FRAME);

    private static final Map<String, Modes> MODE_NAMES              = new HashMap<> ();

    static
    {
        TRACK_MODES.addAll (SEND_MODES);

        MODE_NAMES.put (NAME_TRACK, TRACK);
        MODE_NAMES.put (NAME_VOLUME, VOLUME);
        MODE_NAMES.put (NAME_PANNING, PAN);
        MODE_NAMES.put (NAME_SEND1, SEND1);
        MODE_NAMES.put (NAME_SEND2, SEND2);
        MODE_NAMES.put (NAME_SEND3, SEND3);
        MODE_NAMES.put (NAME_SEND4, SEND4);
        MODE_NAMES.put (NAME_SEND5, SEND5);
        MODE_NAMES.put (NAME_SEND6, SEND6);
        MODE_NAMES.put (NAME_SEND7, SEND7);
        MODE_NAMES.put (NAME_SEND8, SEND8);
        MODE_NAMES.put (NAME_LAYER, DEVICE_LAYER);
        MODE_NAMES.put (NAME_LAYER_VOLUME, DEVICE_LAYER_VOLUME);
        MODE_NAMES.put (NAME_LAYER_PANNING, DEVICE_LAYER_PAN);
        MODE_NAMES.put (NAME_LAYER_SEND1, DEVICE_LAYER_SEND1);
        MODE_NAMES.put (NAME_LAYER_SEND2, DEVICE_LAYER_SEND2);
        MODE_NAMES.put (NAME_LAYER_SEND3, DEVICE_LAYER_SEND3);
        MODE_NAMES.put (NAME_LAYER_SEND4, DEVICE_LAYER_SEND4);
        MODE_NAMES.put (NAME_LAYER_SEND5, DEVICE_LAYER_SEND5);
        MODE_NAMES.put (NAME_LAYER_SEND6, DEVICE_LAYER_SEND6);
        MODE_NAMES.put (NAME_LAYER_SEND7, DEVICE_LAYER_SEND7);
        MODE_NAMES.put (NAME_LAYER_SEND8, DEVICE_LAYER_SEND8);
        MODE_NAMES.put (NAME_PARAMETERS, DEVICE_PARAMS);
        MODE_NAMES.put (NAME_PROJECT_PARAMETERS, PROJECT_PARAMETERS);
        MODE_NAMES.put (NAME_TRACK_PARAMETERS, TRACK_PARAMETERS);
        MODE_NAMES.put (NAME_EQUALIZER, EQ_DEVICE_PARAMS);
        MODE_NAMES.put (NAME_INSTRUMENT_PARAMS, INSTRUMENT_DEVICE_PARAMS);
        MODE_NAMES.put (NAME_MARKERS, MARKERS);
    }


    /**
     * Private due to utility class.
     */
    private Modes ()
    {
        // Intentionally empty
    }


    /**
     * Returns true if the given mode ID is one of the send modes.
     *
     * @param modeId The mode ID to test
     * @return True if it is a send mode
     */
    public static boolean isSendMode (final Modes modeId)
    {
        return SEND_MODES.contains (modeId);
    }


    /**
     * Returns true if the given mode ID is one of the track modes.
     *
     * @param modeId The mode ID to test
     * @return True if it is a track mode
     */
    public static boolean isTrackMode (final Modes modeId)
    {
        return TRACK_MODES.contains (modeId);
    }


    /**
     * Returns true if the given mode ID is one of the mix modes.
     *
     * @param modeId The mode ID to test
     * @return True if it is a mix mode
     */
    public static boolean isMixMode (final Modes modeId)
    {
        return MIX_MODES.contains (modeId);
    }


    /**
     * Returns true if the given mode ID is one of the device layer modes.
     *
     * @param modeId The mode ID to test
     * @return True if it is a device layer mode
     */
    public static boolean isLayerMode (final Modes modeId)
    {
        return LAYER_MODES.contains (modeId);
    }


    /**
     * Returns true if the given mode ID is one of the layer send modes.
     *
     * @param modeId The mode ID to test
     * @return True if it is a layer send mode
     */
    public static boolean isLayerSendMode (final Modes modeId)
    {
        return LAYER_SEND_MODES.contains (modeId);
    }


    /**
     * Returns true if the given mode ID is one of the master modes.
     *
     * @param modeId The mode ID to test
     * @return True if it is a master mode
     */
    public static boolean isMasterMode (final Modes modeId)
    {
        return MASTER_MODES.contains (modeId);
    }


    /**
     * Returns true if the given mode ID is one of the device modes.
     *
     * @param modeId The mode ID to test
     * @return True if it is a device mode
     */
    public static boolean isDeviceMode (final Modes modeId)
    {
        return LAYER_MODES.contains (modeId) || DEVICE_PARAMS == modeId;
    }


    /**
     * Get the mode by its' name.
     *
     * @param name The name of the note view
     * @return The note view
     */
    public static Modes getModeByName (final String name)
    {
        return MODE_NAMES.get (name);
    }


    /**
     * Get the mode name.
     *
     * @param mode The mode ID
     * @return The mode name
     */
    public static String getModeName (final Modes mode)
    {
        for (final Map.Entry<String, Modes> e: MODE_NAMES.entrySet ())
        {
            if (e.getValue () == mode)
                return e.getKey ();
        }
        return "Missing mode name";
    }


    /**
     * Get an offset mode.
     *
     * @param mode The base mode
     * @param offset The offset
     * @return The offset mode
     */
    public static Modes get (final Modes mode, final int offset)
    {
        return Modes.values ()[mode.ordinal () + offset];
    }
}
