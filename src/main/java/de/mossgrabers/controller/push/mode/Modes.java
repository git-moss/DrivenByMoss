// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

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

    /** Edit master track. */
    public static final Integer       MODE_MASTER               = Integer.valueOf (14);
    /** Edit master track (temporary). */
    public static final Integer       MODE_MASTER_TEMP          = Integer.valueOf (15);

    /** Edit device parameters. */
    public static final Integer       MODE_DEVICE_PARAMS        = Integer.valueOf (16);
    /** Edit layer parameters. */
    public static final Integer       MODE_DEVICE_LAYER         = Integer.valueOf (17);
    /** Edit volume of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_VOLUME  = Integer.valueOf (18);
    /** Edit panorama of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_PAN     = Integer.valueOf (19);
    /** Edit Send 1 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND1   = Integer.valueOf (20);
    /** Edit Send 2 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND2   = Integer.valueOf (21);
    /** Edit Send 3 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND3   = Integer.valueOf (22);
    /** Edit Send 4 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND4   = Integer.valueOf (23);
    /** Edit Send 5 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND5   = Integer.valueOf (24);
    /** Edit Send 6 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND6   = Integer.valueOf (25);
    /** Edit Send 7 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND7   = Integer.valueOf (26);
    /** Edit Send 8 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND8   = Integer.valueOf (27);
    /** Edit Sends of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND    = Integer.valueOf (28);
    /** Edit layer details. */
    public static final Integer       MODE_DEVICE_LAYER_DETAILS = Integer.valueOf (29);

    /** Browser mode. */
    public static final Integer       MODE_BROWSER              = Integer.valueOf (30);

    /** Edit clip parameters. */
    public static final Integer       MODE_CLIP                 = Integer.valueOf (31);
    /** Edit note parameters. */
    public static final Integer       MODE_NOTE                 = Integer.valueOf (32);

    /** Show/hide different frames. */
    public static final Integer       MODE_FRAME                = Integer.valueOf (33);
    /** Groove edit mode. */
    public static final Integer       MODE_GROOVE               = Integer.valueOf (35);
    /** Edit accent parameters. */
    public static final Integer       MODE_ACCENT               = Integer.valueOf (36);
    /** Scale configuration. */
    public static final Integer       MODE_SCALES               = Integer.valueOf (37);
    /** Scale layout mode. */
    public static final Integer       MODE_SCALE_LAYOUT         = Integer.valueOf (38);
    /** Pick length of new clips. */
    public static final Integer       MODE_FIXED                = Integer.valueOf (39);
    /** Edit ribbon parameters. */
    public static final Integer       MODE_RIBBON               = Integer.valueOf (40);
    /** Select a view for a track. */
    public static final Integer       MODE_VIEW_SELECT          = Integer.valueOf (41);
    /** Edit automation parameters. */
    public static final Integer       MODE_AUTOMATION           = Integer.valueOf (42);
    /** Transport mode. */
    public static final Integer       MODE_TRANSPORT            = Integer.valueOf (43);
    /** Configuration mode (Push 1). */
    public static final Integer       MODE_CONFIGURATION        = Integer.valueOf (44);
    /** Setup mode (Push 2). */
    public static final Integer       MODE_SETUP                = Integer.valueOf (45);
    /** Info mode (Push 2). */
    public static final Integer       MODE_INFO                 = Integer.valueOf (46);
    /** Select of session views. */
    public static final Integer       MODE_SESSION_VIEW_SELECT  = Integer.valueOf (47);
    /** Session mode. */
    public static final Integer       MODE_SESSION              = Integer.valueOf (48);
    /** Markers mode. */
    public static final Integer       MODE_MARKERS              = Integer.valueOf (49);
    /** Repeat note length mode. */
    public static final Integer       MODE_REPEAT_NOTE          = Integer.valueOf (50);

    private static final Set<Integer> TRACK_MODES               = new HashSet<> ();
    private static final Set<Integer> LAYER_MODES               = new HashSet<> ();
    /** All modes. */
    public static final Set<Integer>  ALL_MODES                 = new HashSet<> ();

    static
    {
        TRACK_MODES.add (MODE_TRACK);
        TRACK_MODES.add (MODE_VOLUME);
        TRACK_MODES.add (MODE_PAN);
        TRACK_MODES.add (MODE_CROSSFADER);
        TRACK_MODES.add (MODE_SEND1);
        TRACK_MODES.add (MODE_SEND2);
        TRACK_MODES.add (MODE_SEND3);
        TRACK_MODES.add (MODE_SEND4);
        TRACK_MODES.add (MODE_SEND5);
        TRACK_MODES.add (MODE_SEND6);
        TRACK_MODES.add (MODE_SEND7);
        TRACK_MODES.add (MODE_SEND8);
        TRACK_MODES.add (MODE_SEND);
        TRACK_MODES.add (MODE_TRACK_DETAILS);

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

        ALL_MODES.add (MODE_TRACK);
        ALL_MODES.add (MODE_TRACK_DETAILS);
        ALL_MODES.add (MODE_VOLUME);
        ALL_MODES.add (MODE_CROSSFADER);
        ALL_MODES.add (MODE_PAN);
        ALL_MODES.add (MODE_SEND1);
        ALL_MODES.add (MODE_SEND2);
        ALL_MODES.add (MODE_SEND3);
        ALL_MODES.add (MODE_SEND4);
        ALL_MODES.add (MODE_SEND5);
        ALL_MODES.add (MODE_SEND6);
        ALL_MODES.add (MODE_SEND7);
        ALL_MODES.add (MODE_SEND8);
        ALL_MODES.add (MODE_SEND);
        ALL_MODES.add (MODE_MASTER);
        ALL_MODES.add (MODE_MASTER_TEMP);
        ALL_MODES.add (MODE_DEVICE_PARAMS);
        ALL_MODES.add (MODE_DEVICE_LAYER);
        ALL_MODES.add (MODE_DEVICE_LAYER_VOLUME);
        ALL_MODES.add (MODE_DEVICE_LAYER_PAN);
        ALL_MODES.add (MODE_DEVICE_LAYER_SEND1);
        ALL_MODES.add (MODE_DEVICE_LAYER_SEND2);
        ALL_MODES.add (MODE_DEVICE_LAYER_SEND3);
        ALL_MODES.add (MODE_DEVICE_LAYER_SEND4);
        ALL_MODES.add (MODE_DEVICE_LAYER_SEND5);
        ALL_MODES.add (MODE_DEVICE_LAYER_SEND6);
        ALL_MODES.add (MODE_DEVICE_LAYER_SEND7);
        ALL_MODES.add (MODE_DEVICE_LAYER_SEND8);
        ALL_MODES.add (MODE_DEVICE_LAYER_SEND);
        ALL_MODES.add (MODE_DEVICE_LAYER_DETAILS);
        ALL_MODES.add (MODE_BROWSER);
        ALL_MODES.add (MODE_CLIP);
        ALL_MODES.add (MODE_NOTE);
        ALL_MODES.add (MODE_FRAME);
        ALL_MODES.add (MODE_GROOVE);
        ALL_MODES.add (MODE_ACCENT);
        ALL_MODES.add (MODE_SCALES);
        ALL_MODES.add (MODE_SCALE_LAYOUT);
        ALL_MODES.add (MODE_FIXED);
        ALL_MODES.add (MODE_RIBBON);
        ALL_MODES.add (MODE_VIEW_SELECT);
        ALL_MODES.add (MODE_AUTOMATION);
        ALL_MODES.add (MODE_TRANSPORT);
        ALL_MODES.add (MODE_CONFIGURATION);
        ALL_MODES.add (MODE_SETUP);
        ALL_MODES.add (MODE_INFO);
        ALL_MODES.add (MODE_SESSION_VIEW_SELECT);
        ALL_MODES.add (MODE_SESSION);
        ALL_MODES.add (MODE_MARKERS);
        ALL_MODES.add (MODE_REPEAT_NOTE);
    }


    /**
     * Private due to utility class.
     */
    private Modes ()
    {
        // Intentionally empty
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
