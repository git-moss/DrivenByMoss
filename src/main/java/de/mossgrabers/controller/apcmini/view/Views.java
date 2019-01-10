// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apcmini.view;

/**
 * Static view IDs.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Views
{
    /** View for playing notes. */
    public static final Integer VIEW_PLAY      = Integer.valueOf (0);
    /** View for a session grid with clips. */
    public static final Integer VIEW_SESSION   = Integer.valueOf (1);
    /** View for a sequencer. */
    public static final Integer VIEW_SEQUENCER = Integer.valueOf (2);
    /** View for playing drums and sequencing. */
    public static final Integer VIEW_DRUM      = Integer.valueOf (3);
    /** View for raindrops sequencer. */
    public static final Integer VIEW_RAINDROPS = Integer.valueOf (4);
    /** View for shift. */
    public static final Integer VIEW_SHIFT     = Integer.valueOf (5);
    /** View for shift. */
    public static final Integer VIEW_BROWSER   = Integer.valueOf (6);


    /**
     * Private due to utility class.
     */
    private Views ()
    {
        // Intentionally empty
    }
}
