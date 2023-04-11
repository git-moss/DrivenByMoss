// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

/**
 * Helper class for storing a pair of values.
 *
 * @param <T1> The type of the first value
 * @param <T2> The type of the second value
 *
 * @author Jürgen Moßgraber
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
     * Constructor.
     */
    public Pair ()
    {
        // Intentionally empty
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


    /**
     * Set both the key and value.
     *
     * @param key The key
     * @param value The value
     */
    public void set (final T1 key, final T2 value)
    {
        this.key = key;
        this.value = value;
    }


    /**
     * Set the key value.
     *
     * @param key The key
     */
    public void setKey (final T1 key)
    {
        this.key = key;
    }


    /**
     * Set the value.
     *
     * @param value The value
     */
    public void setValue (final T2 value)
    {
        this.value = value;
    }
}
