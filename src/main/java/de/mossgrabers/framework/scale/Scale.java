// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.scale;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Several scales and their intervals.
 *
 * @author Jürgen Moßgraber
 */
public enum Scale
{
    // @formatter:off

    /** The Major scale. */
    MAJOR("Major", new int []                          { 0, 2, 4, 5, 7, 9, 11 }),

    /** The Minor scale. */
    MINOR("Minor", new int []                          { 0, 2, 3, 5, 7, 8, 10 }),

    /** The Dorian scale. */
    DORIAN("Dorian", new int []                        { 0, 2, 3, 5, 7, 9, 10 }),

    /** The Mixolydian scale. */
    MIXOLYDIAN("Mixolydian", new int []                { 0, 2, 4, 5, 7, 9, 10 }),

    /** The Lydian scale. */
    LYDIAN("Lydian", new int []                        { 0, 2, 4, 6, 7, 9, 11 }),

    /** The Phrygian scale. */
    PHRYGIAN("Phrygian", new int []                    { 0, 1, 3, 5, 7, 8, 10 }),

    /** The Locrian scale. */
    LOCRIAN("Locrian", new int []                      { 0, 1, 3, 5, 6, 8, 10 }),

    /** The Whole Tone scale. */
    WHOLE_TONE("Whole Tone", new int []                { 0, 2, 4, 6, 8, 10 }),

    /** The Half-whole diminished scale. */
    HALF_WHOLE("Half-whole Dim.", new int []           { 0, 1, 3, 4, 6, 7, 9, 10 }),

    /** The Whole-half scale. */
    WHOLE_HALF("Whole-half Dim.", new int []           { 0, 2, 3, 5, 6, 8, 9, 11 }),

    /** The Minor Blues scale. */
    MINOR_BLUES("Minor Blues", new int []              { 0, 3, 5, 6, 7, 10 }),

    /** The Minor Pentatonic scale. */
    MINOR_PENTATONIC("Minor Pentatonic", new int []    { 0, 3, 5, 7, 10 }),

    /** The Major Pentatonic scale. */
    MAJOR_PENTATONIC("Major Pentatonic", new int []    { 0, 2, 4, 7, 9 }),

    /** The Harmonic Minor scale. */
    HARMONIC_MINOR("Harmonic Minor", new int []        { 0, 2, 3, 5, 7, 8, 11 }),

    /** The Harmonic Major scale. */
    HARMONIC_MAJOR("Harmonic Major", new int []        { 0, 2, 4, 5, 7, 8, 11 }),

    /** The Dorian #4 scale. */
    DORIAN_NR_4("Dorian #4", new int []                { 0, 2, 3, 6, 7, 9, 10 }),

    /** The Phrygian Dominant scale. */
    PHRYGIAN_DOMINANT("Phrygian Dominant", new int []  { 0, 1, 4, 5, 7, 8, 10 }),

    /** The Melodic Minor scale. */
    MELODIC_MINOR("Melodic Minor", new int []          { 0, 2, 3, 5, 7, 9, 11 }),

    /** The Lydian Augmented scale. */
    LYDIAN_AUGMENTED("Lydian Augmented", new int []    { 0, 2, 4, 6, 8, 9, 11 }),

    /** The Lydian Dominant scale. */
    LYDIAN_DOMINANT("Lydian Dominant", new int []      { 0, 2, 4, 6, 7, 9, 10 }),

    /** The Super Locrian scale. */
    SUPER_LOCRIAN("Super Locrian", new int []          { 0, 1, 3, 4, 6, 8, 10 }),

    /** The Spanish scale. */
    SPANISH("Spanish", new int []                      { 0, 1, 4, 5, 7, 9, 10 }),

    /** The 8-Tone Spanish scale. */
    TONE8_SPANISH("8-Tone Spanish", new int []         { 0, 1, 3, 4, 5, 6, 8, 10 }),

    /** The Bhairav scale. */
    BHAIRAV("Bhairav", new int []                      { 0, 1, 4, 5, 7, 8, 11 }),

    /** The Hungarian Minor scale. */
    HUNGARIAN_MINOR("Hungarian Minor", new int []      { 0, 2, 3, 6, 7, 8, 11 }),

    /** The Hirojoshi scale. */
    HIROJOSHI("Hirojoshi", new int []                  { 0, 2, 3, 7, 8 }),

    /** The In-Sen scale. */
    IN_SEN("In-Sen", new int []                        { 0, 1, 5, 7, 10 }),

    /** The Iwato scale. */
    IWATO("Iwato", new int []                          { 0, 1, 5, 6, 10 }),

    /** The Kumoi scale. */
    KUMOI("Kumoi", new int []                          { 0, 2, 3, 7, 9 }),

    /** The Pelog scale. */
    PELOG("Pelog", new int []                          { 0, 1, 3, 4, 7, 8 }),

    /** The Pelog Selisir scale. */
    PELOG_SELISIR("Pelog Selisir", new int []          { 0, 1, 3, 7, 8 }),

    /** The Pelog Tembung scale. */
    PELOG_TEMBUNG("Pelog Tembung", new int []          { 0, 1, 5, 7, 8 }),

    /** The Messiaen 3 scale. */
    MESSIAEN_3("Messiaen 3", new int []                { 0, 2, 3, 4, 6, 7, 8, 10, 11 }),

    /** The Messiaen 4 scale. */
    MESSIAEN_4("Messiaen 4", new int []                { 0, 1, 2, 5, 6, 7, 8, 11 }),

    /** The Messiaen 5 scale. */
    MESSIAEN_5("Messiaen 5", new int []                { 0, 1, 5, 6, 7, 11 }),

    /** The Messiaen 6 scale. */
    MESSIAEN_6("Messiaen 6", new int []                { 0, 2, 4, 5, 6, 8, 10, 11 }),

    /** The Messiaen 7 scale. */
    MESSIAEN_7("Messiaen 7", new int []                { 0, 1, 2, 3, 5, 6, 7, 8, 9, 11 });

    // @formatter:on


    private String                      name;
    private int []                      intervals;
    private final Set<Integer>          scaleKeys    = new HashSet<> (7);
    private final Map<Integer, Integer> indexInScale = new HashMap<> (7);

    private static final String []      SCALE_NAMES;
    static
    {
        final Scale [] values = Scale.values ();
        SCALE_NAMES = new String [values.length];
        for (int i = 0; i < values.length; i++)
            SCALE_NAMES[i] = values[i].name;
    }


    /**
     * Constructor.
     *
     * @param name The name of the scale
     * @param intervals The intervals of the scale
     */
    Scale (final String name, final int [] intervals)
    {
        this.name = name;
        this.intervals = intervals;

        for (int i = 0; i < this.intervals.length; i++)
        {
            final Integer key = Integer.valueOf (this.intervals[i]);
            this.scaleKeys.add (key);
            this.indexInScale.put (key, Integer.valueOf (i));
        }
    }


    /**
     * Get the name of the scale.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Get the intervals of the scale.
     *
     * @return The intervals
     */
    public int [] getIntervals ()
    {
        return this.intervals;
    }


    /**
     * Tests if the given note is in the scale.
     *
     * @param key The note in the octave to test (0-11)
     * @return True if it is in the scale
     */
    public boolean isInScale (final int key)
    {
        return this.scaleKeys.contains (Integer.valueOf (key));
    }


    /**
     * Get the index of the note in the scale.
     *
     * @param note A note 0-127
     * @return The index of the note in the scale, returns -1 if it is out of the scale
     */
    public int getIndexInScale (final int note)
    {
        final Integer index = this.indexInScale.get (Integer.valueOf (note % 12));
        return index == null ? -1 : index.intValue ();
    }


    /**
     * Get the names of all scales.
     *
     * @return The names of all scales
     */
    public static String [] getNames ()
    {
        return SCALE_NAMES;
    }


    /**
     * Get a scale layout by its name.
     *
     * @param name The name of the layout
     * @return The layout or null if it does not exist
     */
    public static Scale getByName (final String name)
    {
        for (final Scale scale: Scale.values ())
        {
            if (scale.getName ().equals (name))
                return scale;
        }
        return null;
    }
}
