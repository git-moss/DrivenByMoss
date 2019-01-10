// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import java.util.HashSet;
import java.util.Set;


/**
 * Static view IDs.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Views
{
    /** View for playing notes. */
    public static final Integer       VIEW_PLAY      = Integer.valueOf (0);
    /** View for changing track volumes. */
    public static final Integer       VIEW_VOLUME    = Integer.valueOf (1);
    /** View for changing track panoramas. */
    public static final Integer       VIEW_PAN       = Integer.valueOf (2);
    /** View for changing track sends. */
    public static final Integer       VIEW_SENDS     = Integer.valueOf (3);
    /** View for browsing. */
    public static final Integer       VIEW_BROWSER   = Integer.valueOf (4);
    /** View for shift options (only MkII). */
    public static final Integer       VIEW_SHIFT     = Integer.valueOf (5);
    /** View for editing remote parameters. */
    public static final Integer       VIEW_DEVICE    = Integer.valueOf (6);
    /** View for a session grid with clips. */
    public static final Integer       VIEW_SESSION   = Integer.valueOf (7);
    /** View for a sequencer. */
    public static final Integer       VIEW_SEQUENCER = Integer.valueOf (8);
    /** View for raindrops sequencer. */
    public static final Integer       VIEW_RAINDROPS = Integer.valueOf (9);
    /** View for playing drums and sequencing. */
    public static final Integer       VIEW_DRUM      = Integer.valueOf (10);
    /** View for drum sequencing with 4 sounds. */
    public static final Integer       VIEW_DRUM4     = Integer.valueOf (11);
    /** View for drum sequencing with 8 sounds. */
    public static final Integer       VIEW_DRUM8     = Integer.valueOf (12);
    /** View for drum playing with 64 pads. */
    public static final Integer       VIEW_DRUM64    = Integer.valueOf (13);
    /** View for piano layout playing. */
    public static final Integer       VIEW_PIANO     = Integer.valueOf (14);

    private static final Set<Integer> NOTE_MODES     = new HashSet<> ();

    static
    {
        NOTE_MODES.add (VIEW_PLAY);
        NOTE_MODES.add (VIEW_PIANO);
        NOTE_MODES.add (VIEW_DRUM);
        NOTE_MODES.add (VIEW_DRUM4);
        NOTE_MODES.add (VIEW_DRUM8);
        NOTE_MODES.add (VIEW_DRUM64);
        NOTE_MODES.add (VIEW_SEQUENCER);
        NOTE_MODES.add (VIEW_RAINDROPS);
    }


    /**
     * Private due to utility class.
     */
    private Views ()
    {
        // Intentionally empty
    }


    /**
     * Returns true if the given view ID is one of the note views.
     *
     * @param viewId The view ID to test
     * @return True if it is a note view
     */
    public static boolean isNoteView (final Integer viewId)
    {
        return NOTE_MODES.contains (viewId);
    }
}
