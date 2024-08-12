// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

import java.util.Arrays;
import java.util.List;

import de.mossgrabers.framework.scale.Scales;


/**
 * Identifies the names of chords from a list of notes.
 *
 * @author Jürgen Moßgraber
 */
public class ChordIdentifier
{
    private static final Chord [] CHORDS = new Chord []
    {
        new Chord ("C Major", 0, 4, 7),
        new Chord ("C# Major", 1, 5, 8),
        new Chord ("D Major", 2, 6, 9),
        new Chord ("D# Major", 3, 7, 10),
        new Chord ("E Major", 4, 8, 11),
        new Chord ("F Major", 0, 5, 9),
        new Chord ("F# Major", 1, 6, 10),
        new Chord ("G Major", 2, 7, 11),
        new Chord ("G# Major", 0, 3, 8),
        new Chord ("A Major", 1, 4, 9),
        new Chord ("A# Major", 2, 5, 10),
        new Chord (" B Major", 3, 6, 11),

        new Chord ("C Minor", 0, 3, 7),
        new Chord ("C# Minor", 1, 4, 8),
        new Chord ("D Minor", 2, 5, 9),
        new Chord ("D# Minor", 3, 6, 10),
        new Chord ("E Minor", 4, 7, 11),
        new Chord ("F Minor", 0, 5, 8),
        new Chord ("F# Minor", 1, 6, 9),
        new Chord ("G Minor", 2, 7, 10),
        new Chord ("G# Minor", 3, 8, 11),
        new Chord ("A Minor", 0, 4, 9),
        new Chord ("A# Minor", 1, 5, 10),
        new Chord ("B Minor", 2, 6, 11),

        new Chord ("C Diminished", 0, 3, 6),
        new Chord ("C# Diminished", 1, 4, 7),
        new Chord ("D Diminished", 2, 5, 8),
        new Chord ("D# Diminished", 3, 6, 9),
        new Chord ("E Diminished", 4, 7, 10),
        new Chord ("F Diminished", 5, 8, 11),
        new Chord ("F# Diminished", 0, 6, 9),
        new Chord ("G Diminished", 1, 7, 10),
        new Chord ("G# Diminished", 2, 8, 11),
        new Chord ("A Diminished", 0, 3, 9),
        new Chord ("A# Diminished", 1, 4, 10),
        new Chord ("B Diminished", 2, 5, 11),

        new Chord ("C Augmented", 0, 4, 8),
        new Chord ("C# Augmented", 1, 5, 9),
        new Chord ("D Augmented", 2, 6, 10),
        new Chord ("D# Augmented", 3, 7, 11),
        new Chord ("E Augmented", 0, 4, 8),
        new Chord ("F Augmented", 1, 5, 9),
        new Chord ("F# Augmented", 2, 6, 10),
        new Chord ("G Augmented", 3, 7, 11),
        new Chord ("G# Augmented", 0, 4, 8),
        new Chord ("A Augmented", 1, 5, 9),
        new Chord ("A# Augmented", 2, 6, 10),
        new Chord ("B Augmented", 3, 7, 11),

        new Chord ("C Major 7th", 0, 4, 7, 11),
        new Chord ("C# Major 7th", 0, 1, 5, 8),
        new Chord ("D Major 7th", 1, 2, 6, 9),
        new Chord ("D# Major 7th", 2, 3, 7, 10),
        new Chord ("E Major 7th", 3, 4, 8, 11),
        new Chord ("F Major 7th", 0, 4, 5, 9),
        new Chord ("F# Major 7th", 1, 5, 6, 10),
        new Chord ("G Major 7th", 2, 6, 7, 11),
        new Chord ("G# Major 7th", 0, 3, 7, 8),
        new Chord ("A Major 7th", 1, 4, 8, 9),
        new Chord ("A# Major 7th", 2, 5, 9, 10),
        new Chord ("B Major 7th", 3, 6, 10, 11),

        new Chord ("C Minor 7th", 0, 3, 7, 10),
        new Chord ("C# Minor 7th", 1, 4, 8, 11),
        new Chord ("D Minor 7th", 0, 2, 5, 9),
        new Chord ("D# Minor 7th", 1, 3, 6, 10),
        new Chord ("E Minor 7th", 2, 4, 7, 11),
        new Chord ("F Minor 7th", 0, 3, 5, 8),
        new Chord ("F# Minor 7th", 1, 4, 6, 9),
        new Chord ("G Minor 7th", 2, 5, 7, 10),
        new Chord ("G# Minor 7th", 3, 6, 8, 11),
        new Chord ("A Minor 7th", 0, 4, 7, 9),
        new Chord ("A# Minor 7th", 1, 5, 8, 10),
        new Chord ("B Minor 7th", 2, 6, 9, 11),

        new Chord ("C Dominant 7th", 0, 4, 7, 10),
        new Chord ("C# Dominant 7th", 1, 5, 8, 11),
        new Chord ("D Dominant 7th", 0, 2, 6, 9),
        new Chord ("D# Dominant 7th", 1, 3, 7, 10),
        new Chord ("E Dominant 7th", 2, 4, 8, 11),
        new Chord ("F Dominant 7th", 0, 3, 5, 9),
        new Chord ("F# Dominant 7th", 1, 4, 6, 10),
        new Chord ("G Dominant 7th", 2, 5, 7, 11),
        new Chord ("G# Dominant 7th", 0, 3, 6, 8),
        new Chord ("A Dominant 7th", 1, 4, 7, 9),
        new Chord ("A# Dominant 7th", 2, 5, 8, 10),
        new Chord ("B Dominant 7th", 3, 6, 9, 11),

        new Chord ("C Sus2", 0, 2, 7),
        new Chord ("C# Sus2", 1, 3, 8),
        new Chord ("D Sus2", 2, 4, 9),
        new Chord ("D# Sus2", 3, 5, 10),
        new Chord ("E Sus2", 4, 6, 11),
        new Chord ("F Sus2", 0, 5, 7),
        new Chord ("F# Sus2", 1, 6, 8),
        new Chord ("G Sus2", 2, 7, 9),
        new Chord ("G# Sus2", 3, 8, 10),
        new Chord ("A Sus2", 4, 9, 11),
        new Chord ("A# Sus2", 0, 5, 10),
        new Chord ("B Sus2", 1, 6, 11),

        new Chord ("C Sus4", 0, 5, 7),
        new Chord ("C# Sus4", 1, 6, 8),
        new Chord ("D Sus4", 2, 7, 9),
        new Chord ("D# Sus4", 3, 8, 10),
        new Chord ("E Sus4", 4, 9, 11),
        new Chord ("F Sus4", 0, 5, 10),
        new Chord ("F# Sus4", 1, 6, 11),
        new Chord ("G Sus4", 0, 2, 7),
        new Chord ("G# Sus4", 1, 3, 8),
        new Chord ("A Sus4", 2, 4, 9),
        new Chord ("A# Sus4", 3, 5, 10),
        new Chord ("B Sus4", 4, 6, 11),

        new Chord ("C Major Add6", 0, 4, 7, 9),
        new Chord ("C# Major Add6", 1, 5, 8, 10),
        new Chord ("D Major Add6", 2, 6, 9, 11),
        new Chord ("D# Major Add6", 0, 3, 7, 10),
        new Chord ("E Major Add6", 1, 4, 8, 11),
        new Chord ("F Major Add6", 0, 2, 5, 9),
        new Chord ("F# Major Add6", 1, 3, 6, 10),
        new Chord ("G Major Add6", 2, 4, 7, 11),
        new Chord ("G# Major Add6", 0, 3, 5, 8),
        new Chord ("A Major Add6", 1, 4, 6, 9),
        new Chord ("A# Major Add6", 2, 5, 7, 10),
        new Chord ("B Major Add6", 3, 6, 8, 11),

        new Chord ("C Minor Add6", 0, 3, 7, 9),
        new Chord ("C# Minor Add6", 1, 4, 8, 10),
        new Chord ("D Minor Add6", 2, 5, 9, 11),
        new Chord ("D# Minor Add6", 0, 3, 6, 10),
        new Chord ("E Minor Add6", 1, 4, 7, 11),
        new Chord ("F Minor Add6", 0, 2, 5, 8),
        new Chord ("F# Minor Add6", 1, 3, 6, 9),
        new Chord ("G Minor Add6", 2, 4, 7, 10),
        new Chord ("G# Minor Add6", 3, 5, 8, 11),
        new Chord ("A Minor Add6", 0, 4, 6, 9),
        new Chord ("A# Minor Add6", 1, 5, 7, 10),
        new Chord ("B Minor Add6", 2, 6, 8, 11),

        new Chord ("C Major Add9", 0, 2, 4, 7),
        new Chord ("C# Major Add9", 1, 3, 5, 8),
        new Chord ("D Major Add9", 2, 4, 6, 9),
        new Chord ("D# Major Add9", 3, 5, 7, 10),
        new Chord ("E Major Add9", 4, 6, 8, 11),
        new Chord ("F Major Add9", 0, 5, 7, 9),
        new Chord ("F# Major Add9", 1, 6, 8, 10),
        new Chord ("G Major Add9", 2, 7, 9, 11),
        new Chord ("G# Major Add9", 0, 3, 8, 10),
        new Chord ("A Major Add9", 1, 4, 9, 11),
        new Chord ("A# Major Add9", 0, 2, 5, 10),
        new Chord ("B Major Add9", 1, 3, 6, 11),

        new Chord ("C Minor Add9", 0, 2, 3, 7),
        new Chord ("C# Minor Add9", 1, 3, 4, 8),
        new Chord ("D Minor Add9", 2, 4, 5, 9),
        new Chord ("D# Minor Add9", 3, 5, 6, 10),
        new Chord ("E Minor Add9", 4, 6, 7, 11),
        new Chord ("F Minor Add9", 0, 5, 7, 8),
        new Chord ("F# Minor Add9", 1, 6, 8, 9),
        new Chord ("G Minor Add9", 2, 7, 9, 10),
        new Chord ("G# Minor Add9", 3, 8, 10, 11),
        new Chord ("A Minor Add9", 0, 4, 9, 11),
        new Chord ("A# Minor Add9", 0, 1, 5, 10),
        new Chord ("B Minor Add9", 1, 2, 6, 11),

        new Chord ("C Major Add11", 0, 4, 5, 7),
        new Chord ("C# Major Add11", 1, 5, 6, 8),
        new Chord ("D Major Add11", 2, 6, 7, 9),
        new Chord ("D# Major Add11", 3, 7, 8, 10),
        new Chord ("E Major Add11", 4, 8, 9, 11),
        new Chord ("F Major Add11", 0, 5, 9, 10),
        new Chord ("F# Major Add11", 1, 6, 10, 11),
        new Chord ("G Major Add11", 0, 2, 7, 11),
        new Chord ("G# Major Add11", 0, 1, 3, 8),
        new Chord ("A Major Add11", 1, 2, 4, 9),
        new Chord ("A# Major Add11", 2, 3, 5, 10),
        new Chord ("B Major Add11", 3, 4, 6, 11),

        new Chord ("C Minor Add11", 0, 3, 5, 7),
        new Chord ("C# Minor Add11", 1, 4, 6, 8),
        new Chord ("D Minor Add11", 2, 5, 7, 9),
        new Chord ("D# Minor Add11", 3, 6, 8, 10),
        new Chord ("E Minor Add11", 4, 7, 9, 11),
        new Chord ("F Minor Add11", 0, 5, 8, 10),
        new Chord ("F# Minor Add11", 1, 6, 9, 11),
        new Chord ("G Minor Add11", 0, 2, 7, 10),
        new Chord ("G# Minor Add11", 1, 3, 8, 11),
        new Chord ("A Minor Add11", 0, 2, 4, 9),
        new Chord ("A# Minor Add11", 1, 3, 5, 10),
        new Chord ("B Minor Add11", 2, 4, 6, 11)
    };


    /**
     * Returns the name of the chord for the given notes.
     * 
     * @param notes The notes for which to identify the chord
     * @return The name of the chord or null if the notes contains less than 3 notes or is not
     *         known.
     */
    public static String identifyChord (final List<Integer> notes)
    {
        // Reduce notes to their pitch classes (0-11)
        final int [] pitchClasses = notes.stream ().map (note -> Integer.valueOf (note.intValue () % 12)).distinct ().sorted ().mapToInt (Integer::intValue).filter (num -> num != -1).toArray ();
        if (pitchClasses.length >= 3 || pitchClasses.length <= 4)
        {
            for (final Chord chord: CHORDS)
            {
                if (chord.doesMatch (pitchClasses))
                    return chord.name;
            }
        }

        if (pitchClasses.length == 0)
            return null;

        final StringBuilder sb = new StringBuilder ();
        for (int i = 0; i < pitchClasses.length; i++)
        {
            if (sb.length () > 0)
                sb.append (", ");
            sb.append (Scales.NOTE_NAMES.get (pitchClasses[i]));
        }
        return sb.toString ();
    }


    private static class Chord
    {
        private final String name;
        private final int [] notes;


        Chord (final String name, final int note1, final int note2, final int note3)
        {
            this.name = name;
            this.notes = new int []
            {
                note1,
                note2,
                note3
            };
        }


        Chord (final String name, final int note1, final int note2, final int note3, final int note4)
        {
            this.name = name;
            this.notes = new int []
            {
                note1,
                note2,
                note3,
                note4
            };
        }


        public boolean doesMatch (final int [] pitchClasses)
        {
            return pitchClasses.length == this.notes.length && Arrays.compare (this.notes, pitchClasses) == 0;
        }
    }
}
