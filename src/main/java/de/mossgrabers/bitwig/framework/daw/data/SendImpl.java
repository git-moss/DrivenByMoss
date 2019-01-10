// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.data.ISend;

import com.bitwig.extension.controller.api.Send;


/**
 * Encapsulates the data of a send.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendImpl extends ParameterImpl implements ISend
{
    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param send The send
     * @param index The index of the send
     */
    public SendImpl (final IValueChanger valueChanger, final Send send, final int index)
    {
        super (valueChanger, send, index);
    }
}
