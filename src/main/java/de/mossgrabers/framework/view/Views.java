// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.daw.IHost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Static view IDs.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Views
{
    /** The name of the play view. */
    public static final String                VIEW_NAME_PLAY           = "Play";
    /** The name of the piano view. */
    public static final String                VIEW_NAME_PIANO          = "Piano";
    /** The name of the drum view. */
    public static final String                VIEW_NAME_DRUM           = "Drum";
    /** The name of the drum4 view. */
    public static final String                VIEW_NAME_DRUM4          = "Drum 4";
    /** The name of the drum 8 view. */
    public static final String                VIEW_NAME_DRUM8          = "Drum 8";
    /** The name of the drum 64 view. */
    public static final String                VIEW_NAME_DRUM64         = "Drum 64";
    /** The name of the sequencer view. */
    public static final String                VIEW_NAME_SEQUENCER      = "Sequencer";
    /** The name of the raindrops view. */
    public static final String                VIEW_NAME_RAINDROPS      = "Raindrop";
    /** The name of the poly sequencer view. */
    public static final String                VIEW_NAME_POLY_SEQUENCER = "Poly Seq.";

    /** View for playing notes. */
    public static final Integer               VIEW_PLAY                = Integer.valueOf (0);
    /** View for a session grid with clips. */
    public static final Integer               VIEW_SESSION             = Integer.valueOf (1);
    /** View for a sequencer. */
    public static final Integer               VIEW_SEQUENCER           = Integer.valueOf (2);
    /** View for playing drums and sequencing. */
    public static final Integer               VIEW_DRUM                = Integer.valueOf (3);
    /** View for raindrops sequencer. */
    public static final Integer               VIEW_RAINDROPS           = Integer.valueOf (4);
    /** View for playing in piano keyboard style. */
    public static final Integer               VIEW_PIANO               = Integer.valueOf (5);
    /** View sending program changes. */
    public static final Integer               VIEW_PRG_CHANGE          = Integer.valueOf (6);
    /** View for editing the clip length. */
    public static final Integer               VIEW_CLIP                = Integer.valueOf (7);
    /** View for drum sequencing with 4 sounds. */
    public static final Integer               VIEW_DRUM4               = Integer.valueOf (8);
    /** View for drum sequencing with 8 sounds. */
    public static final Integer               VIEW_DRUM8               = Integer.valueOf (9);
    /** View for drum playing with 64 pads. */
    public static final Integer               VIEW_DRUM64              = Integer.valueOf (10);
    /** View for selecting a color. */
    public static final Integer               VIEW_COLOR               = Integer.valueOf (11);
    /** View for playing scenes. */
    public static final Integer               VIEW_SCENE_PLAY          = Integer.valueOf (12);
    /** View for the poly sequencer. */
    public static final Integer               VIEW_POLY_SEQUENCER      = Integer.valueOf (13);

    /** View for browsing. */
    public static final Integer               VIEW_BROWSER             = Integer.valueOf (14);
    /** View for changing track parameters. */
    public static final Integer               VIEW_TRACK               = Integer.valueOf (15);
    /** View for changing track volumes. */
    public static final Integer               VIEW_TRACK_VOLUME        = Integer.valueOf (16);
    /** View for changing track panoramas. */
    public static final Integer               VIEW_TRACK_PAN           = Integer.valueOf (17);
    /** View for changing track sends. */
    public static final Integer               VIEW_TRACK_SENDS         = Integer.valueOf (18);
    /** View for Track selection. */
    public static final Integer               VIEW_TRACK_SELECT        = Integer.valueOf (19);
    /** View for soloing tracks. */
    public static final Integer               VIEW_TRACK_SOLO          = Integer.valueOf (20);
    /** View for muting tracks. */
    public static final Integer               VIEW_TRACK_MUTE          = Integer.valueOf (21);
    /** View for editing remote parameters. */
    public static final Integer               VIEW_DEVICE              = Integer.valueOf (22);

    /** View for controlling values. */
    public static final Integer               VIEW_CONTROL             = Integer.valueOf (23);
    /** View for shift options. */
    public static final Integer               VIEW_SHIFT               = Integer.valueOf (24);

    private static final Map<String, Integer> NOTE_VIEW_NAMES          = new HashMap<> ();
    private static final List<Integer>        NOTE_VIEWS               = new ArrayList<> ();
    private static final Set<Integer>         SEQUENCER_VIEWS          = new HashSet<> ();
    private static final Set<Integer>         SESSION_VIEWS            = new HashSet<> ();

    private static boolean                    isInitialised            = false;


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
        NOTE_VIEWS.add (VIEW_DRUM64);
        NOTE_VIEW_NAMES.put (VIEW_NAME_PLAY, VIEW_PLAY);
        NOTE_VIEW_NAMES.put (VIEW_NAME_PIANO, VIEW_PIANO);
        NOTE_VIEW_NAMES.put (VIEW_NAME_DRUM64, VIEW_DRUM64);

        if (host.hasDrumDevice ())
        {
            NOTE_VIEWS.add (VIEW_DRUM);
            NOTE_VIEWS.add (VIEW_DRUM4);
            NOTE_VIEWS.add (VIEW_DRUM8);
            NOTE_VIEW_NAMES.put (VIEW_NAME_DRUM, VIEW_DRUM);
            NOTE_VIEW_NAMES.put (VIEW_NAME_DRUM4, VIEW_DRUM4);
            NOTE_VIEW_NAMES.put (VIEW_NAME_DRUM8, VIEW_DRUM8);

            SEQUENCER_VIEWS.add (VIEW_DRUM);
            SEQUENCER_VIEWS.add (VIEW_DRUM4);
            SEQUENCER_VIEWS.add (VIEW_DRUM8);
        }

        if (host.hasClips ())
        {
            NOTE_VIEWS.add (VIEW_SEQUENCER);
            NOTE_VIEWS.add (VIEW_RAINDROPS);
            NOTE_VIEW_NAMES.put (VIEW_NAME_SEQUENCER, VIEW_SEQUENCER);
            NOTE_VIEW_NAMES.put (VIEW_NAME_RAINDROPS, VIEW_RAINDROPS);

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
     * Returns true if the given view ID is one of the note views.
     *
     * @param viewId The view ID to test
     * @return True if it is a note view
     */
    public static boolean isNoteView (final Integer viewId)
    {
        return NOTE_VIEWS.contains (viewId);
    }


    /**
     * Get the note view names.
     *
     * @return The names
     */
    public static String [] getNoteViewNames ()
    {
        final String [] array = NOTE_VIEW_NAMES.keySet ().toArray (new String [NOTE_VIEW_NAMES.size ()]);
        Arrays.sort (array);
        return array;
    }


    /**
     * Get the note view at the given index.
     *
     * @param name The name of the note view
     * @return The note view
     */
    public static Integer getNoteView (final String name)
    {
        return NOTE_VIEW_NAMES.get (name);
    }
}
