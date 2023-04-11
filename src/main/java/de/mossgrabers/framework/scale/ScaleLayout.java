// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.scale;

/**
 * Layouts that can be applied to a scale by using an 8x8 pad grid.
 *
 * @author Jürgen Moßgraber
 */
public enum ScaleLayout
{
    /** Upwards in fourth steps. */
    FOURTH_UP("4th ^"),
    /** To the right in fourth steps. */
    FOURTH_RIGHT("4th >"),
    /** Upwards in third steps. */
    THIRD_UP("3rd ^"),
    /** To the right in third steps. */
    THIRD_RIGHT("3rd >"),
    /** Sequential up in fourth steps. */
    SEQUENT_UP("Seqent ^"),
    /** Sequential to the right in fourth steps. */
    SEQUENT_RIGHT("Seqent >"),
    /** Upwards in eighth steps. */
    EIGHT_UP("8th ^"),
    /** To the right in eighth steps. */
    EIGHT_RIGHT("8th >"),
    /** Eighth steps centered upwards. */
    EIGHT_UP_CENTER("8th ^ centered"),
    /** Eighth steps centered to the right. */
    EIGHT_RIGHT_CENTER("8th > centered"),
    /** Upward by ~five steps and rightward by two steps. (dx = 2, dy = 5) */
    STAGGERED_UP("Staggered ^"),
    /** Rightward by ~five steps and upward by two steps. (dx = 5, dy = 2) */
    STAGGERED_RIGHT("Staggered >");


    private static final String [] scaleLayoutNames;
    static
    {
        final ScaleLayout [] values = ScaleLayout.values ();
        scaleLayoutNames = new String [values.length];
        for (int i = 0; i < values.length; i++)
            scaleLayoutNames[i] = values[i].name;
    }

    private String name;


    /**
     * Constructor.
     *
     * @param name The name of the color
     */
    ScaleLayout (final String name)
    {
        this.name = name;
    }


    /**
     * Get the name of the scale layout.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Get the names of all scale layouts.
     *
     * @return The names of all scales
     */
    public static String [] getNames ()
    {
        return scaleLayoutNames;
    }


    /**
     * Get a scale layout by its name.
     *
     * @param name The name of the layout
     * @return The layout or null if it does not exist
     */
    public static ScaleLayout getByName (final String name)
    {
        for (final ScaleLayout layout: ScaleLayout.values ())
        {
            if (layout.getName ().equals (name))
                return layout;
        }
        return null;
    }
}
