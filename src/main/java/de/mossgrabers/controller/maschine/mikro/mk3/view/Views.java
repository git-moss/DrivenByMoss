// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.view;

/**
 * Static view IDs.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Views
{
    /** View for playing notes. */
    public static final Integer VIEW_PLAY         = Integer.valueOf (0);
    /** View for playing drums. */
    public static final Integer VIEW_DRUM         = Integer.valueOf (1);

    /** View for Track selection. */
    public static final Integer VIEW_TRACK_SELECT = Integer.valueOf (2);
    /** View for soloing tracks. */
    public static final Integer VIEW_TRACK_SOLO   = Integer.valueOf (3);
    /** View for muting tracks. */
    public static final Integer VIEW_TRACK_MUTE   = Integer.valueOf (4);

    /** View for starting scenes. */
    public static final Integer VIEW_SCENE        = Integer.valueOf (5);
    /** View for starting clips. */
    public static final Integer VIEW_CLIP         = Integer.valueOf (6);

    /** View for selecting parameters. */
    public static final Integer VIEW_PARAMETER    = Integer.valueOf (7);


    /**
     * Private due to utility class.
     */
    private Views ()
    {
        // Intentionally empty
    }
}
