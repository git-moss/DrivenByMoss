// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import java.util.EnumSet;
import java.util.Set;


/**
 * Static mode IDs and some helper functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum Modes
{
    /** Single Track editing mode. */
    MODE_TRACK,
    /** Edit details of one track. */
    MODE_TRACK_DETAILS,
    /** Edit volume of all tracks. */
    MODE_VOLUME,
    /** Edit panorama of all tracks. */
    MODE_PAN,
    /** Edit crossfader settings of all tracks. */
    MODE_CROSSFADER,
    /** Mode to arm tracks for recording. */
    MODE_REC_ARM,
    /** Mode to select tracks. */
    MODE_TRACK_SELECT,
    /** Mode to mute tracks. */
    MODE_MUTE,
    /** Mode to solo tracks. */
    MODE_SOLO,
    /** Mode to stop clips. */
    MODE_STOP_CLIP,

    /** Edit Send 1 of all tracks. */
    MODE_SEND1,
    /** Edit Send 2 of all tracks. */
    MODE_SEND2,
    /** Edit Send 3 of all tracks. */
    MODE_SEND3,
    /** Edit Send 4 of all tracks. */
    MODE_SEND4,
    /** Edit Send 5 of all tracks. */
    MODE_SEND5,
    /** Edit Send 6 of all tracks. */
    MODE_SEND6,
    /** Edit Send 7 of all tracks. */
    MODE_SEND7,
    /** Edit Send 8 of all tracks. */
    MODE_SEND8,
    /** Edit Sends of all tracks. */
    MODE_SEND,

    /** Edit master track. */
    MODE_MASTER,
    /** Edit master track (temporary). */
    MODE_MASTER_TEMP,

    /** Edit device parameters. */
    MODE_DEVICE_PARAMS,

    /** Edit layer parameters. */
    MODE_DEVICE_LAYER,
    /** Edit volume of all layers. */
    MODE_DEVICE_LAYER_VOLUME,
    /** Edit panorama of all layers. */
    MODE_DEVICE_LAYER_PAN,
    /** Edit Send 1 of all layers. */
    MODE_DEVICE_LAYER_SEND1,
    /** Edit Send 2 of all layers. */
    MODE_DEVICE_LAYER_SEND2,
    /** Edit Send 3 of all layers. */
    MODE_DEVICE_LAYER_SEND3,
    /** Edit Send 4 of all layers. */
    MODE_DEVICE_LAYER_SEND4,
    /** Edit Send 5 of all layers. */
    MODE_DEVICE_LAYER_SEND5,
    /** Edit Send 6 of all layers. */
    MODE_DEVICE_LAYER_SEND6,
    /** Edit Send 7 of all layers. */
    MODE_DEVICE_LAYER_SEND7,
    /** Edit Send 8 of all layers. */
    MODE_DEVICE_LAYER_SEND8,
    /** Edit Sends of all layers. */
    MODE_DEVICE_LAYER_SEND,
    /** Edit layer details. */
    MODE_DEVICE_LAYER_DETAILS,

    /** Browser mode. */
    MODE_BROWSER,

    /** Edit clip parameters. */
    MODE_CLIP,
    /** Edit note parameters. */
    MODE_NOTE,

    /** Show/hide different frames. */
    MODE_FRAME,
    /** Groove edit mode. */
    MODE_GROOVE,
    /** Edit accent parameters. */
    MODE_ACCENT,
    /** Scale configuration. */
    MODE_SCALES,
    /** Scale layout mode. */
    MODE_SCALE_LAYOUT,
    /** Pick length of new clips. */
    MODE_FIXED,
    /** Edit ribbon parameters. */
    MODE_RIBBON,
    /** Select a view for a track. */
    MODE_VIEW_SELECT,
    /** Edit automation parameters. */
    MODE_AUTOMATION,
    /** Transport mode. */
    MODE_TRANSPORT,
    /** Configuration mode. */
    MODE_CONFIGURATION,
    /** Setup mode. */
    MODE_SETUP,
    /** Info mode. */
    MODE_INFO,
    /** Select of session views. */
    MODE_SESSION_VIEW_SELECT,
    /** Session mode. */
    MODE_SESSION,
    /** Markers mode. */
    MODE_MARKERS,
    /** Repeat note length mode. */
    MODE_REPEAT_NOTE,
    /** Execute different functions. */
    MODE_FUNCTIONS,
    /** Edit play options. */
    MODE_PLAY_OPTIONS,
    /** Play cursor position mode. */
    MODE_POSITION,
    /** Tempo mode. */
    MODE_TEMPO;

    private static final Set<Modes> TRACK_MODES = EnumSet.range (Modes.MODE_TRACK, Modes.MODE_STOP_CLIP);
    private static final Set<Modes> LAYER_MODES = EnumSet.range (Modes.MODE_DEVICE_LAYER, Modes.MODE_DEVICE_LAYER_DETAILS);
    private static final Set<Modes> SEND_MODES  = EnumSet.range (Modes.MODE_SEND1, Modes.MODE_SEND);

    static
    {
        TRACK_MODES.addAll (SEND_MODES);
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
     * Returns true if the given mode ID is one of the device modes.
     *
     * @param modeId The mode ID to test
     * @return True if it is a device mode
     */
    public static boolean isDeviceMode (final Modes modeId)
    {
        return LAYER_MODES.contains (modeId) || MODE_DEVICE_PARAMS == modeId;
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
