// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode;

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
    public static final Integer       MODE_TRACK                = 0;
    /** Edit details of one track. */
    public static final Integer       MODE_TRACK_DETAILS        = 1;
    /** Edit volume of all tracks. */
    public static final Integer       MODE_VOLUME               = 2;
    /** Edit crossfader settings of all tracks. */
    public static final Integer       MODE_CROSSFADER           = 3;
    /** Edit panorama of all tracks. */
    public static final Integer       MODE_PAN                  = 4;
    /** Edit Send 1 of all tracks. */
    public static final Integer       MODE_SEND1                = 5;
    /** Edit Send 2 of all tracks. */
    public static final Integer       MODE_SEND2                = 6;
    /** Edit Send 3 of all tracks. */
    public static final Integer       MODE_SEND3                = 7;
    /** Edit Send 4 of all tracks. */
    public static final Integer       MODE_SEND4                = 8;
    /** Edit Send 5 of all tracks. */
    public static final Integer       MODE_SEND5                = 9;
    /** Edit Send 6 of all tracks. */
    public static final Integer       MODE_SEND6                = 10;
    /** Edit Send 7 of all tracks. */
    public static final Integer       MODE_SEND7                = 11;
    /** Edit Send 8 of all tracks. */
    public static final Integer       MODE_SEND8                = 12;
    /** Edit Sends of all tracks. */
    public static final Integer       MODE_SEND                 = 13;

    /** Edit master track. */
    public static final Integer       MODE_MASTER               = 14;
    /** Edit master track (temporary). */
    public static final Integer       MODE_MASTER_TEMP          = 15;

    /** Edit device parameters. */
    public static final Integer       MODE_DEVICE_PARAMS        = 16;
    /** Edit layer parameters. */
    public static final Integer       MODE_DEVICE_LAYER         = 17;
    /** Edit volume of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_VOLUME  = 18;
    /** Edit panorama of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_PAN     = 19;
    /** Edit Send 1 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND1   = 20;
    /** Edit Send 2 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND2   = 21;
    /** Edit Send 3 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND3   = 22;
    /** Edit Send 4 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND4   = 23;
    /** Edit Send 5 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND5   = 24;
    /** Edit Send 6 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND6   = 25;
    /** Edit Send 7 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND7   = 26;
    /** Edit Send 8 of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND8   = 27;
    /** Edit Sends of all layers. */
    public static final Integer       MODE_DEVICE_LAYER_SEND    = 28;
    /** Edit layer details. */
    public static final Integer       MODE_DEVICE_LAYER_DETAILS = 29;

    /** Browser mode. */
    public static final Integer       MODE_BROWSER              = 30;

    /** Edit clip parameters. */
    public static final Integer       MODE_CLIP                 = 31;
    /** Edit note parameters. */
    public static final Integer       MODE_NOTE                 = 32;

    /** Show/hide different frames. */
    public static final Integer       MODE_FRAME                = 33;
    /** Groove edit mode. */
    public static final Integer       MODE_GROOVE               = 35;
    /** Edit accent parameters. */
    public static final Integer       MODE_ACCENT               = 36;
    /** Scale configuration. */
    public static final Integer       MODE_SCALES               = 37;
    /** Scale layout mode. */
    public static final Integer       MODE_SCALE_LAYOUT         = 38;
    /** Pick length of new clips. */
    public static final Integer       MODE_FIXED                = 39;
    /** Edit ribbon parameters. */
    public static final Integer       MODE_RIBBON               = 40;
    /** Select a view for a track. */
    public static final Integer       MODE_VIEW_SELECT          = 41;
    /** Edit automation parameters. */
    public static final Integer       MODE_AUTOMATION           = 42;
    /** Transport mode. */
    public static final Integer       MODE_TRANSPORT            = 43;
    /** Configuration mode (Push 1). */
    public static final Integer       MODE_CONFIGURATION        = 44;
    /** Setup mode (Push 2). */
    public static final Integer       MODE_SETUP                = 45;
    /** Info mode (Push 2). */
    public static final Integer       MODE_INFO                 = 46;
    /** Select of session views. */
    public static final Integer       MODE_SESSION_VIEW_SELECT  = 47;

    private static final Set<Integer> TRACK_MODES               = new HashSet<> ();
    private static final Set<Integer> LAYER_MODES               = new HashSet<> ();

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
