// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * A arpeggiator mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ArpeggiatorMode
{
    private final int    index;
    private final String name;
    private final String value;


    /**
     * Constructor.
     *
     * @param index The index of the mode
     * @param name The name of the mode
     * @param value The value of the mode
     */
    public ArpeggiatorMode (final int index, final String name, final String value)
    {
        this.index = index;
        this.name = name;
        this.value = value;
    }


    /**
     * Get the index.
     *
     * @return The index
     */
    public int getIndex ()
    {
        return this.index;
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
     * Get the value.
     *
     * @return The value
     */
    public String getValue ()
    {
        return this.value;
    }
}
