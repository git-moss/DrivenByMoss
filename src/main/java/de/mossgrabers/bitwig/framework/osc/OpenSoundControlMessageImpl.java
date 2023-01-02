// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.osc;

import de.mossgrabers.framework.osc.IOpenSoundControlMessage;

import com.bitwig.extension.api.opensoundcontrol.OscMessage;

import java.util.List;


/**
 * Data class for storing the values of an OSC message.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OpenSoundControlMessageImpl implements IOpenSoundControlMessage
{
    private final String  address;
    private final List<?> values;


    /**
     * Constructor.
     *
     * @param message Bitwig implementation of a message
     */
    public OpenSoundControlMessageImpl (final OscMessage message)
    {
        this (message.getAddressPattern (), message.getArguments ());
    }


    /**
     * Constructor.
     *
     * @param address The OSC address
     * @param values The values
     */
    public OpenSoundControlMessageImpl (final String address, final List<?> values)
    {
        this.address = address;
        this.values = values;
    }


    /** {@inheritDoc} */
    @Override
    public String getAddress ()
    {
        return this.address;
    }


    /** {@inheritDoc} */
    @Override
    public Object [] getValues ()
    {
        return this.values == null ? new Object [0] : this.values.toArray ();
    }
}
