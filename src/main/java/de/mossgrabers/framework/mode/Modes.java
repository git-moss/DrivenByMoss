// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import java.util.HashSet;
import java.util.Set;


/**
 * Static mode IDs and some helper functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Modes
{
    /** Single Track editing mode. */
    public static final Integer       MODE_TRACK                = Integer.valueOf (0);
    /** Edit details of one track. */
    public static final Integer       MODE_TRACK_DETAILS        = Integer.valueOf (1);
    /** Edit volume of all tracks. */
    public static final Integer       MODE_VOLUME               = Integer.valueOf (2);
    /** Edit crossfader settings of all tracks. */
    public static final Integer       MODE_CROSSFADER           = Integer.valueOf (3);
    /** Edit panorama of all tracks. */
    public static final Integer       MODE_PAN                  = Integer.valueOf (4);
    /** Edit Send 1 of all tracks. */
    public static final Integer       MODE_SEND1                = Integer.valueOf (5);
    /** Edit Send 2 of all tracks. */
    public static final Integer       MODE_SEND2                = Integer.valueOf (6);
    /** Edit Send 3 of all tracks. */
    public static final Integer       MODE_SEND3                = Integer.valueOf (7);
    /** Edit Send 4 of all tracks. */
    public static final Integer       MODE_SEND4                = Integer.valueOf (8);
    /** Edit Send 5 of all tracks. */
    public static final Integer       MODE_SEND5                = Integer.valueOf (9);
    /** Edit Send 6 of all tracks. */
    public static final Integer       MODE_SEND6                = Integer.valueOf (10);
    /** Edit Send 7 of all tracks. */
    public static final Integer       MODE_SEND7                = Integer.valueOf (11);
    /** Edit Send 8 of all tracks. */
    public static final Integer       MODE_SEND8                = Integer.valueOf (12);
    /** Edit Sends of all tracks. */
    public static final Integer       MODE_SEND                 = Integer.valueOf (13);
    /** Mode to arm tracks for recording. */
    public static final Integer       MODE_REC_ARM              = Integer.valueOf (14);
    /** Mode to select tracks. */
    public static final Integer       MODE_TRACK_SELECT         = Integer.valueOf (15);
    /** Mode to mute tracks. */
    public static final Integer       MODE_MUTE                 = Integer.valueOf (16);
    /** Mode to solo tracks. */
    public static final Integer       MODE_SOLO                 = Integer.valueOf (17);
    /** Mode to stop clips. */
    public static final Integer       MODE_STOP_CLIP            = Integer.valueOf (18);

    /** Edit master track. */
    public static final Integer       MODE_MASTER               = Integer.valueOf (19);
    /** Edit master track (temporary). */
    public static final Integer       MODE_MASTER_TEMP          = Integer.valueOf (20);

    /** Edit device parameters. */
    public static final Integer       MODE_DEVICE_PARAMS        = Integer.valueOf (21);
    /** Edit layer parameters. */
    public static final Integer       MODE_DEVICE_LAYER         = Integer.valueOf (22);
    /** Edit volume of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_VOLUME  = Integer.valueOf (23);
    /** Edit panorama of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_PAN     = Integer.valueOf (24);
    /** Edit Send 1 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND1   = Integer.valueOf (25);
    /** Edit Send 2 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND2   = Integer.valueOf (26);
    /** Edit Send 3 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND3   = Integer.valueOf (27);
    /** Edit Send 4 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND4   = Integer.valueOf (28);
    /** Edit Send 5 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND5   = Integer.valueOf (29);
    /** Edit Send 6 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND6   = Integer.valueOf (30);
    /** Edit Send 7 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND7   = Integer.valueOf (31);
    /** Edit Send 8 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND8   = Integer.valueOf (32);
    /** Edit Sends of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND    = Integer.valueOf (33);
    /** Edit layer details. */
    public static final Integer       MODE_DEVICE_LAYER_DETAILS = Integer.valueOf (34);

    /** Browser mode. */
    public static final Integer       MODE_BROWSER              = Integer.valueOf (35);

    /** Edit clip parameters. */
    public static final Integer       MODE_CLIP                 = Integer.valueOf (36);
    /** Edit note parameters. */
    public static final Integer       MODE_NOTE                 = Integer.valueOf (37);

    /** Show/hide different frames. */
    public static final Integer       MODE_FRAME                = Integer.valueOf (38);
    /** Groove edit mode. */
    public static final Integer       MODE_GROOVE               = Integer.valueOf (39);
    /** Edit accent parameters. */
    public static final Integer       MODE_ACCENT               = Integer.valueOf (40);
    /** Scale configuration. */
    public static final Integer       MODE_SCALES               = Integer.valueOf (41);
    /** Scale layout mode. */
    public static final Integer       MODE_SCALE_LAYOUT         = Integer.valueOf (42);
    /** Pick length of new clips. */
    public static final Integer       MODE_FIXED                = Integer.valueOf (43);
    /** Edit ribbon parameters. */
    public static final Integer       MODE_RIBBON               = Integer.valueOf (44);
    /** Select a view for a track. */
    public static final Integer       MODE_VIEW_SELECT          = Integer.valueOf (45);
    /** Edit automation parameters. */
    public static final Integer       MODE_AUTOMATION           = Integer.valueOf (46);
    /** Transport mode. */
    public static final Integer       MODE_TRANSPORT            = Integer.valueOf (47);
    /** Configuration mode. */
    public static final Integer       MODE_CONFIGURATION        = Integer.valueOf (48);
    /** Setup mode. */
    public static final Integer       MODE_SETUP                = Integer.valueOf (49);
    /** Info mode. */
    public static final Integer       MODE_INFO                 = Integer.valueOf (50);
    /** Select of session views. */
    public static final Integer       MODE_SESSION_VIEW_SELECT  = Integer.valueOf (51);
    /** Session mode. */
    public static final Integer       MODE_SESSION              = Integer.valueOf (52);
    /** Markers mode. */
    public static final Integer       MODE_MARKERS              = Integer.valueOf (53);
    /** Repeat note length mode. */
    public static final Integer       MODE_REPEAT_NOTE          = Integer.valueOf (54);
    /** Execute different functions. */
    public static final Integer       MODE_FUNCTIONS            = Integer.valueOf (55);
    /** Edit play options. */
    public static final Integer       MODE_PLAY_OPTIONS         = Integer.valueOf (56);
    /** Play cursor position mode. */
    public static final Integer       MODE_POSITION             = Integer.valueOf (57);
    /** Tempo mode. */
    public static final Integer       MODE_TEMPO                = Integer.valueOf (58);

    private static final Set<Integer> TRACK_MODES               = new HashSet<> ();
    private static final Set<Integer> LAYER_MODES               = new HashSet<> ();
    private static final Set<Integer> SEND_MODES                = new HashSet<> ();

    static
    {
        SEND_MODES.add (MODE_SEND1);
        SEND_MODES.add (MODE_SEND2);
        SEND_MODES.add (MODE_SEND3);
        SEND_MODES.add (MODE_SEND4);
        SEND_MODES.add (MODE_SEND5);
        SEND_MODES.add (MODE_SEND6);
        SEND_MODES.add (MODE_SEND7);
        SEND_MODES.add (MODE_SEND8);
        SEND_MODES.add (MODE_SEND);

        TRACK_MODES.add (MODE_TRACK);
        TRACK_MODES.add (MODE_VOLUME);
        TRACK_MODES.add (MODE_PAN);
        TRACK_MODES.add (MODE_CROSSFADER);
        TRACK_MODES.add (MODE_TRACK_DETAILS);
        TRACK_MODES.add (MODE_REC_ARM);
        TRACK_MODES.add (MODE_TRACK_SELECT);
        TRACK_MODES.add (MODE_MUTE);
        TRACK_MODES.add (MODE_SOLO);
        TRACK_MODES.add (MODE_STOP_CLIP);
        TRACK_MODES.addAll (SEND_MODES);

        LAYER_MODES.add (MODE_DEVICE_LAYER);
        LAYER_MODES.add (MODE_DEVICE_LAYER_VOLUME);
        LAYER_MODES.add (MODE_DEVICE_LAYER_PAN);
        LAYER_MODES.add (MODE_DEVICE_LAYER_SEND1);
        LAYER_MODES.add (MODE_DEVICE_LAYER_SEND2);
        LAYER_MODES.add (MODE_DEVICE_LAYER_SEND3);
        LAYER_MODES.add (MODE_DEVICE_LAYER_SEND4);
        LAYER_MODES.add (MODE_DEVICE_LAYER_SEND5);
        LAYER_MODES.add (MODE_DEVICE_LAYER_SEND6);
        LAYER_MODES.add (MODE_DEVICE_LAYER_SEND7);
        LAYER_MODES.add (MODE_DEVICE_LAYER_SEND8);
        LAYER_MODES.add (MODE_DEVICE_LAYER_SEND);
        LAYER_MODES.add (MODE_DEVICE_LAYER_DETAILS);
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
    public static boolean isSendMode (final Integer modeId)
    {
        return SEND_MODES.contains (modeId);
    }


    /**
     * Returns true if the given mode ID is one of the track modes.
     *
     * @param modeId The mode ID to test
     * @return True if it is a track mode
     */
    public static boolean isTrackMode (final Integer modeId)
    {
        return TRACK_MODES.contains (modeId);
    }


    /**
     * Returns true if the given mode ID is one of the device layer modes.
     *
     * @param modeId The mode ID to test
     * @return True if it is a device layer mode
     */
    public static boolean isLayerMode (final Integer modeId)
    {
        return LAYER_MODES.contains (modeId);
    }


    /**
     * Returns true if the given mode ID is one of the device modes.
     *
     * @param modeId The mode ID to test
     * @return True if it is a device mode
     */
    public static boolean isDeviceMode (final Integer modeId)
    {
        return LAYER_MODES.contains (modeId) || MODE_DEVICE_PARAMS.equals (modeId);
    }
}
