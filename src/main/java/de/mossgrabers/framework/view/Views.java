// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import java.util.ArrayList;
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
public enum Views
{
    /** View for playing notes. */
    PLAY,
    /** View for playing chords. */
    CHORDS,
    /** View for a session grid with clips. */
    SESSION,
    /** View for a sequencer. */
    SEQUENCER,
    /** View for playing drums and sequencing. */
    DRUM,
    /** View for raindrops sequencer. */
    RAINDROPS,
    /** View for playing in piano keyboard style. */
    PIANO,
    /** View sending program changes. */
    PRG_CHANGE,
    /** View for editing the clip length. */
    CLIP,
    /** View for drum sequencing with 4 sounds. */
    DRUM4,
    /** View for drum sequencing with 8 sounds. */
    DRUM8,
    /** View for drum playing with 64 pads. */
    DRUM64,
    /** View for selecting a color. */
    COLOR,
    /** View for playing scenes. */
    SCENE_PLAY,
    /** View for the poly sequencer. */
    POLY_SEQUENCER,

    /** View for browsing. */
    BROWSER,
    /** View for changing track parameters. */
    TRACK,
    /** View for changing track volumes. */
    TRACK_VOLUME,
    /** View for changing track panoramas. */
    TRACK_PAN,
    /** View for changing track sends. */
    TRACK_SENDS,
    /** View for Track selection. */
    TRACK_SELECT,
    /** View for soloing tracks. */
    TRACK_SOLO,
    /** View for muting tracks. */
    TRACK_MUTE,
    /** View for editing remote parameters. */
    DEVICE,

    /** View for controlling values. */
    CONTROL,
    /** View for shift options. */
    SHIFT,
    /** View for user view. */
    USER,
    /** View for note repeat options. */
    REPEAT_NOTE,
    /** View for editing note parameters. */
    NOTE_EDIT_VIEW,
    /** View for mixing. */
    MIX,

    /** View for changing the tempo. */
    TEMPO,
    /** View for changing the shuffle. */
    SHUFFLE,
    /** View for changing project settings. */
    PROJECT,

    /** To block functionality. */
    DUMMY1,
    /** To block functionality. */
    DUMMY2,
    /** To block functionality. */
    DUMMY3,
    /** To block functionality. */
    DUMMY4,
    /** To block functionality. */
    DUMMY5,
    /** To block functionality. */
    DUMMY6;


    /** The name of the play view. */
    public static final String              NAME_PLAY           = "Play";
    /** The name of the chords view. */
    public static final String              NAME_CHORDS         = "Chords";
    /** The name of the piano view. */
    public static final String              NAME_PIANO          = "Piano";
    /** The name of the drum view. */
    public static final String              NAME_DRUM           = "Drum";
    /** The name of the drum4 view. */
    public static final String              NAME_DRUM4          = "Drum 4";
    /** The name of the drum 8 view. */
    public static final String              NAME_DRUM8          = "Drum 8";
    /** The name of the drum 64 view. */
    public static final String              NAME_DRUM64         = "Drum 64";
    /** The name of the scene play view. */
    public static final String              NAME_SCENE_PLAY     = "Scene Play";
    /** The name of the sequencer view. */
    public static final String              NAME_SEQUENCER      = "Sequencer";
    /** The name of the raindrops view. */
    public static final String              NAME_RAINDROPS      = "Raindrop";
    /** The name of the poly sequencer view. */
    public static final String              NAME_POLY_SEQUENCER = "Poly Seq.";
    /** The name of the browser view. */
    public static final String              NAME_BROWSER        = "Browser";

    private static final Map<String, Views> NOTE_VIEW_NAMES     = new HashMap<> ();
    private static final List<Views>        NOTE_VIEWS          = new ArrayList<> ();
    private static final Set<Views>         SEQUENCER_VIEWS     = new HashSet<> ();
    private static final Set<Views>         SESSION_VIEWS       = new HashSet<> ();

    private static boolean                  isInitialised       = false;


    /**
     * Private due to utility class.
     */
    private Views ()
    {
        // Intentionally empty
    }


    /**
     * Initialize.
     */
    public static void init ()
    {
        if (isInitialised)
            return;
        isInitialised = true;

        NOTE_VIEWS.add (PLAY);
        NOTE_VIEWS.add (CHORDS);
        NOTE_VIEWS.add (PIANO);
        NOTE_VIEWS.add (DRUM64);
        NOTE_VIEW_NAMES.put (NAME_PLAY, PLAY);
        NOTE_VIEW_NAMES.put (NAME_CHORDS, CHORDS);
        NOTE_VIEW_NAMES.put (NAME_PIANO, PIANO);
        NOTE_VIEW_NAMES.put (NAME_DRUM64, DRUM64);

        NOTE_VIEWS.add (DRUM);
        NOTE_VIEWS.add (DRUM4);
        NOTE_VIEWS.add (DRUM8);
        NOTE_VIEW_NAMES.put (NAME_DRUM, DRUM);
        NOTE_VIEW_NAMES.put (NAME_DRUM4, DRUM4);
        NOTE_VIEW_NAMES.put (NAME_DRUM8, DRUM8);

        SEQUENCER_VIEWS.add (DRUM);
        SEQUENCER_VIEWS.add (DRUM4);
        SEQUENCER_VIEWS.add (DRUM8);

        NOTE_VIEWS.add (SEQUENCER);
        NOTE_VIEWS.add (RAINDROPS);
        NOTE_VIEWS.add (POLY_SEQUENCER);
        NOTE_VIEW_NAMES.put (NAME_SEQUENCER, SEQUENCER);
        NOTE_VIEW_NAMES.put (NAME_RAINDROPS, RAINDROPS);
        NOTE_VIEW_NAMES.put (NAME_POLY_SEQUENCER, POLY_SEQUENCER);

        SEQUENCER_VIEWS.add (SEQUENCER);
        SEQUENCER_VIEWS.add (RAINDROPS);
        SEQUENCER_VIEWS.add (POLY_SEQUENCER);

        SESSION_VIEWS.add (SESSION);
        SESSION_VIEWS.add (SCENE_PLAY);
    }


    /**
     * Returns true if the given view ID is one of the sequencer views.
     *
     * @param viewId The view ID to test
     * @return True if it is a sequencer view
     */
    public static boolean isSequencerView (final Views viewId)
    {
        return SEQUENCER_VIEWS.contains (viewId);
    }


    /**
     * Returns true if the given view ID is one of the session views.
     *
     * @param viewId The view ID to test
     * @return True if it is a session view
     */
    public static boolean isSessionView (final Views viewId)
    {
        return SESSION_VIEWS.contains (viewId);
    }


    /**
     * Returns true if the given view ID is one of the note views.
     *
     * @param viewId The view ID to test
     * @return True if it is a note view
     */
    public static boolean isNoteView (final Views viewId)
    {
        return NOTE_VIEWS.contains (viewId);
    }


    /**
     * Get the note view at the given index.
     *
     * @param name The name of the note view
     * @return The note view
     */
    public static Views getNoteView (final String name)
    {
        return NOTE_VIEW_NAMES.get (name);
    }


    /**
     * Get the note view name.
     *
     * @param view The view ID
     * @return The note view name
     */
    public static String getNoteViewName (final Views view)
    {
        for (final Map.Entry<String, Views> e: NOTE_VIEW_NAMES.entrySet ())
        {
            if (e.getValue () == view)
                return e.getKey ();
        }
        return "Missing view name";
    }


    /**
     * Get an offset view.
     *
     * @param view The base view
     * @param offset The offset
     * @return The offset view
     */
    public static Views get (final Views view, final int offset)
    {
        return Views.values ()[view.ordinal () + offset];
    }
}
