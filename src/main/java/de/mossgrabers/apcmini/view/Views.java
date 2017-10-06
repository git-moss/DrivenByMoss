// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apcmini.view;

/**
 * Static view IDs.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Views
{
    /** View for playing notes. */
    public static final Integer VIEW_PLAY      = 0;
    /** View for a session grid with clips. */
    public static final Integer VIEW_SESSION   = 1;
    /** View for a sequencer. */
    public static final Integer VIEW_SEQUENCER = 2;
    /** View for playing drums and sequencing. */
    public static final Integer VIEW_DRUM      = 3;
    /** View for raindrops sequencer. */
    public static final Integer VIEW_RAINDROPS = 4;
    /** View for shift. */
    public static final Integer VIEW_SHIFT     = 5;
    /** View for shift. */
    public static final Integer VIEW_BROWSER   = 6;


    /**
     * Private due to utility class.
     */
    private Views ()
    {
        // Intentionally empty
    }
}
