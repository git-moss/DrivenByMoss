// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.daw.data;

import de.mossgrabers.framework.daw.data.ISend;

import com.bitwig.extension.controller.api.Parameter;


/**
 * Encapsulates the data of a send.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendImpl extends ParameterImpl implements ISend
{
    private int index;


    /**
     * Constructor.
     *
     * @param parameter The parameter
     * @param maxParameterValue The maximum number for values (range is 0 till maxParameterValue-1)
     * @param index The index of the send
     */
    public SendImpl (final Parameter parameter, final int maxParameterValue, final int index)
    {
        super (parameter, maxParameterValue);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.index;
    }
}
