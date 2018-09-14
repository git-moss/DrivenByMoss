// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.osc.protocol;

import java.util.List;


/**
 * Data class for storing the values of an OSC message.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OscMessageData
{
    private final String       address;
    private final List<Object> values;


    /**
     * Constructor.
     *
     * @param address The OSC address
     * @param values The values
     */
    public OscMessageData (final String address, final List<Object> values)
    {
        this.address = address;
        this.values = values;
    }


    /**
     * Get the OSC address.
     *
     * @return The OSC address
     */
    public String getAddress ()
    {
        return this.address;
    }


    /**
     * Get the values array.
     *
     * @return The values array
     */
    public Object [] getValues ()
    {
        return this.values == null ? new Object [0] : this.values.toArray ();
    }
}
