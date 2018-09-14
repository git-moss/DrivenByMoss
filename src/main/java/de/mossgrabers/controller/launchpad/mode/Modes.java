// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.mode;

/**
 * Static mode IDs and some helper functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Modes
{
    /** Mode to arm tracks for recording. */
    public static final Integer MODE_REC_ARM      = Integer.valueOf (0);
    /** Mode to select tracks. */
    public static final Integer MODE_TRACK_SELECT = Integer.valueOf (1);
    /** Mode to mute tracks. */
    public static final Integer MODE_MUTE         = Integer.valueOf (2);
    /** Mode to solo tracks. */
    public static final Integer MODE_SOLO         = Integer.valueOf (3);
    /** Mode for changing track volumes. */
    public static final Integer MODE_VOLUME       = Integer.valueOf (4);
    /** Mode for changing track panoramas. */
    public static final Integer MODE_PAN          = Integer.valueOf (5);
    /** Mode for changing track sends. */
    public static final Integer MODE_SENDS        = Integer.valueOf (6);
    /** Mode to stop clips. */
    public static final Integer MODE_STOP_CLIP    = Integer.valueOf (7);


    /**
     * Private due to utility class.
     */
    private Modes ()
    {
        // Intentionally empty
    }
}
