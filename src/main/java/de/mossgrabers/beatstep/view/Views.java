// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep.view;

/**
 * Static view IDs.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Views
{
    /** View for changing track parameters. */
    public static final Integer VIEW_TRACK     = 0;
    /** View for editing remote parameters. */
    public static final Integer VIEW_DEVICE    = 1;
    /** View for playing notes. */
    public static final Integer VIEW_PLAY      = 2;
    /** View for playing drums and sequencing. */
    public static final Integer VIEW_DRUM      = 3;
    /** View for a sequencer. */
    public static final Integer VIEW_SEQUENCER = 4;
    /** View for a session grid with clips. */
    public static final Integer VIEW_SESSION   = 5;
    /** View for browsing. */
    public static final Integer VIEW_BROWSER   = 6;
    /** View for shift options (only MkII). */
    public static final Integer VIEW_SHIFT     = 7;


    /**
     * Private due to utility class.
     */
    private Views ()
    {
        // Intentionally empty
    }
}
