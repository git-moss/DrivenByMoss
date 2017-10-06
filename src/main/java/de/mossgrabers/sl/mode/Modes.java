// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.mode;

/**
 * Static mode IDs and some helper functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Modes
{
    /** Pick length of new clips. */
    public static final Integer MODE_FIXED         = 0;
    /** Show/hide different frames. */
    public static final Integer MODE_FRAME         = 1;
    /** Execute different functions. */
    public static final Integer MODE_FUNCTIONS     = 2;
    /** Edit master track. */
    public static final Integer MODE_MASTER        = 3;
    /** Edit play options. */
    public static final Integer MODE_PLAY_OPTIONS  = 4;
    /** Start scenes. */
    public static final Integer MODE_SESSION       = 5;
    /** Single Track editing mode. */
    public static final Integer MODE_TRACK         = 6;
    /** Track parameters which can be toggled. */
    public static final Integer MODE_TRACK_TOGGLES = 7;
    /** Select a view for a track. */
    public static final Integer MODE_VIEW_SELECT   = 8;
    /** Edit volume of all tracks. */
    public static final Integer MODE_VOLUME        = 9;
    /** Edit device parameters. */
    public static final Integer MODE_PARAMS        = 10;
    /** The preset browser. */
    public static final Integer MODE_BROWSER       = 11;


    /**
     * Private due to utility class.
     */
    private Modes ()
    {
        // Intentionally empty
    }
}
