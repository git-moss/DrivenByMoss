// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.framework.daw.IHost;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Static view IDs.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Views
{
    /** The name of the play view. */
    public static final String         VIEW_NAME_PLAY           = "Play";
    /** The name of the piano view. */
    public static final String         VIEW_NAME_PIANO          = "Piano";
    /** The name of the drum view. */
    public static final String         VIEW_NAME_DRUM           = "Drum";
    /** The name of the drum4 view. */
    public static final String         VIEW_NAME_DRUM4          = "Drum 4";
    /** The name of the drum 8 view. */
    public static final String         VIEW_NAME_DRUM8          = "Drum 8";
    /** The name of the drum 64 view. */
    public static final String         VIEW_NAME_DRUM64         = "Drum 64";
    /** The name of the sequencer view. */
    public static final String         VIEW_NAME_SEQUENCER      = "Sequencer";
    /** The name of the raindrops view. */
    public static final String         VIEW_NAME_RAINDROPS      = "Raindrop";
    /** The name of the poly sequencer view. */
    public static final String         VIEW_NAME_POLY_SEQUENCER = "Poly Seq.";

    /** View for playing notes. */
    public static final Integer        VIEW_PLAY                = Integer.valueOf (0);
    /** View for a session grid with clips. */
    public static final Integer        VIEW_SESSION             = Integer.valueOf (1);
    /** View for a sequencer. */
    public static final Integer        VIEW_SEQUENCER           = Integer.valueOf (2);
    /** View for playing drums and sequencing. */
    public static final Integer        VIEW_DRUM                = Integer.valueOf (3);
    /** View for raindrops sequencer. */
    public static final Integer        VIEW_RAINDROPS           = Integer.valueOf (4);
    /** View for playing in piano keyboard style. */
    public static final Integer        VIEW_PIANO               = Integer.valueOf (5);
    /** View sending program changes. */
    public static final Integer        VIEW_PRG_CHANGE          = Integer.valueOf (6);
    /** View for editing the clip length. */
    public static final Integer        VIEW_CLIP                = Integer.valueOf (7);
    /** View for drum sequencing with 4 sounds. */
    public static final Integer        VIEW_DRUM4               = Integer.valueOf (8);
    /** View for drum sequencing with 8 sounds. */
    public static final Integer        VIEW_DRUM8               = Integer.valueOf (9);
    /** View for drum playing with 64 pads. */
    public static final Integer        VIEW_DRUM64              = Integer.valueOf (10);
    /** View for selecting a color. */
    public static final Integer        VIEW_COLOR               = Integer.valueOf (11);
    /** View for playing scenes. */
    public static final Integer        VIEW_SCENE_PLAY          = Integer.valueOf (12);
    /** View for the poly sequencer. */
    public static final Integer        VIEW_POLY_SEQUENCER      = Integer.valueOf (13);

    private static final List<String>  NOTE_VIEW_NAMES          = new ArrayList<> ();
    private static final List<Integer> NOTE_VIEWS               = new ArrayList<> ();
    private static final Set<Integer>  SEQUENCER_VIEWS          = new HashSet<> ();
    private static final Set<Integer>  SESSION_VIEWS            = new HashSet<> ();

    private static boolean             isInitialised            = false;


    /**
     * Private due to utility class.
     */
    private Views ()
    {
        // Intentionally empty
    }


    /**
     * Initialise.
     *
     * @param host The DAW host
     */
    public static void init (final IHost host)
    {
        if (isInitialised)
            return;
        isInitialised = true;

        NOTE_VIEWS.add (VIEW_PLAY);
        NOTE_VIEWS.add (VIEW_PIANO);
        NOTE_VIEW_NAMES.add (VIEW_NAME_PLAY);
        NOTE_VIEW_NAMES.add (VIEW_NAME_PIANO);

        if (host.hasDrumDevice ())
        {
            NOTE_VIEWS.add (VIEW_DRUM);
            NOTE_VIEWS.add (VIEW_DRUM4);
            NOTE_VIEWS.add (VIEW_DRUM8);
            NOTE_VIEWS.add (VIEW_DRUM64);
            NOTE_VIEW_NAMES.add (VIEW_NAME_DRUM);
            NOTE_VIEW_NAMES.add (VIEW_NAME_DRUM4);
            NOTE_VIEW_NAMES.add (VIEW_NAME_DRUM8);
            NOTE_VIEW_NAMES.add (VIEW_NAME_DRUM64);

            SEQUENCER_VIEWS.add (VIEW_DRUM);
            SEQUENCER_VIEWS.add (VIEW_DRUM4);
            SEQUENCER_VIEWS.add (VIEW_DRUM8);
        }

        if (host.hasClips ())
        {
            NOTE_VIEWS.add (VIEW_SEQUENCER);
            NOTE_VIEWS.add (VIEW_RAINDROPS);
            NOTE_VIEW_NAMES.add (VIEW_NAME_SEQUENCER);
            NOTE_VIEW_NAMES.add (VIEW_NAME_RAINDROPS);

            SEQUENCER_VIEWS.add (VIEW_SEQUENCER);
            SEQUENCER_VIEWS.add (VIEW_RAINDROPS);
            SEQUENCER_VIEWS.add (VIEW_POLY_SEQUENCER);

            SESSION_VIEWS.add (VIEW_SESSION);
            SESSION_VIEWS.add (VIEW_SCENE_PLAY);
        }
    }


    /**
     * Returns true if the given view ID is one of the sequencer views.
     *
     * @param viewId The view ID to test
     * @return True if it is a sequencer view
     */
    public static boolean isSequencerView (final Integer viewId)
    {
        return SEQUENCER_VIEWS.contains (viewId);
    }


    /**
     * Returns true if the given view ID is one of the session views.
     *
     * @param viewId The view ID to test
     * @return True if it is a session view
     */
    public static boolean isSessionView (final Integer viewId)
    {
        return SESSION_VIEWS.contains (viewId);
    }


    /**
     * Get the note view names.
     *
     * @return The names
     */
    public static String [] getNoteViewNames ()
    {
        return NOTE_VIEW_NAMES.toArray (new String [NOTE_VIEW_NAMES.size ()]);
    }


    /**
     * Get the note view at the given index.
     *
     * @param index An index
     * @return The note view
     */
    public static Integer getNoteView (final int index)
    {
        return NOTE_VIEWS.get (index);
    }
}
