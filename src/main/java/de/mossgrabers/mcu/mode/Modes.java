// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.mode;

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
    public static final Integer       MODE_TRACK         = Integer.valueOf (0);
    /** Edit volume of all tracks. */
    public static final Integer       MODE_VOLUME        = Integer.valueOf (2);
    /** Edit panorama of all tracks. */
    public static final Integer       MODE_PAN           = Integer.valueOf (4);
    /** Edit Send 1 of all tracks. */
    public static final Integer       MODE_SEND1         = Integer.valueOf (5);
    /** Edit Send 2 of all tracks. */
    public static final Integer       MODE_SEND2         = Integer.valueOf (6);
    /** Edit Send 3 of all tracks. */
    public static final Integer       MODE_SEND3         = Integer.valueOf (7);
    /** Edit Send 4 of all tracks. */
    public static final Integer       MODE_SEND4         = Integer.valueOf (8);
    /** Edit Send 5 of all tracks. */
    public static final Integer       MODE_SEND5         = Integer.valueOf (9);
    /** Edit Send 6 of all tracks. */
    public static final Integer       MODE_SEND6         = Integer.valueOf (10);
    /** Edit Send 7 of all tracks. */
    public static final Integer       MODE_SEND7         = Integer.valueOf (11);
    /** Edit Send 8 of all tracks. */
    public static final Integer       MODE_SEND8         = Integer.valueOf (12);

    /** Edit master track. */
    public static final Integer       MODE_MASTER        = Integer.valueOf (14);

    /** Edit device parameters. */
    public static final Integer       MODE_DEVICE_PARAMS = Integer.valueOf (16);

    /** Browser mode. */
    public static final Integer       MODE_BROWSER       = Integer.valueOf (30);

    private static final Set<Integer> TRACK_MODES        = new HashSet<> ();

    static
    {
        TRACK_MODES.add (MODE_TRACK);
        TRACK_MODES.add (MODE_VOLUME);
        TRACK_MODES.add (MODE_PAN);
        TRACK_MODES.add (MODE_SEND1);
        TRACK_MODES.add (MODE_SEND2);
        TRACK_MODES.add (MODE_SEND3);
        TRACK_MODES.add (MODE_SEND4);
        TRACK_MODES.add (MODE_SEND5);
        TRACK_MODES.add (MODE_SEND6);
        TRACK_MODES.add (MODE_SEND7);
        TRACK_MODES.add (MODE_SEND8);
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
}
