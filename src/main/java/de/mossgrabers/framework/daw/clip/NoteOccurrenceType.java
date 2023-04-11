// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.clip;

/**
 * The different types of note occurrences.
 *
 * @author Jürgen Moßgraber
 */
public enum NoteOccurrenceType
{
    /** Always mode. */
    ALWAYS("Always"),
    /** On first mode. */
    FIRST("On First"),
    /** Never first mode. */
    NOT_FIRST("Never First"),
    /** With previous mode. */
    PREV("With Previous"),
    /** Without previous mode. */
    NOT_PREV("Without Previous"),
    /** With previous channel. */
    PREV_CHANNEL("With Prev Channel"),
    /** Without previous channel. */
    NOT_PREV_CHANNEL("Without Prev Channel"),
    /** With previous key mode. */
    PREV_KEY("With Prev Key"),
    /** Without previous key mode. */
    NOT_PREV_KEY("Without Prev Key"),
    /** Fill on mode. */
    FILL("Fill On"),
    /** Fill off mode. */
    NOT_FILL("Fill Off");


    private final String name;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     */
    private NoteOccurrenceType (final String name)
    {
        this.name = name;
    }


    /**
     * Get the name.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Lookup an note occurrence mode.
     *
     * @param value The value of the mode
     * @return The mode
     */
    public static NoteOccurrenceType lookup (final String value)
    {
        return NoteOccurrenceType.valueOf (value);
    }


    /**
     * Lookup an note occurrence mode.
     *
     * @param name The name of the mode
     * @return The mode
     */
    public static NoteOccurrenceType lookupByName (final String name)
    {
        final NoteOccurrenceType [] values = NoteOccurrenceType.values ();
        for (final NoteOccurrenceType mode: values)
        {
            if (mode.getName ().equals (name))
                return mode;
        }
        return values[0];
    }
}
