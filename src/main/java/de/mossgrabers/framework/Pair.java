// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework;

/**
 * Helper class for storing a pair of values.
 *
 * @param <T1> The type of the first value
 * @param <T2> The type of the second value
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Pair<T1, T2>
{
    private T1 key;
    private T2 value;


    /**
     * Constructor.
     *
     * @param key The first value
     * @param value The second value
     */
    public Pair (final T1 key, final T2 value)
    {
        this.key = key;
        this.value = value;
    }


    /**
     * Get the first value.
     *
     * @return The first value.
     */
    public T1 getKey ()
    {
        return this.key;
    }


    /**
     * Get the second value.
     *
     * @return The second value.
     */
    public T2 getValue ()
    {
        return this.value;
    }
}
