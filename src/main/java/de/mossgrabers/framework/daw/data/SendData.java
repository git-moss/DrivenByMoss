// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import com.bitwig.extension.controller.api.Parameter;


/**
 * Encapsulates the data of a send.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendData extends ParameterData
{
    private int index;


    /**
     * Constructor.
     *
     * @param parameter The parameter
     * @param maxParameterValue The maximum number for values (range is 0 till maxParameterValue-1)
     * @param index The index of the send
     */
    public SendData (final Parameter parameter, final int maxParameterValue, final int index)
    {
        super (parameter, maxParameterValue);
        this.index = index;
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
}
