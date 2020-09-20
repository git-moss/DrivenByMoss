// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;


/**
 * Get a number of parameters. This implementation provides all volume parameters of the channels of
 * the current channel bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendParameterProvider extends AbstractChannelParameterProvider
{
    private final int sendIndex;


    /**
     * Constructor.
     *
     * @param model Uses the current track bank from this model to get the parameters
     * @param sendIndex The index of the send
     */
    public SendParameterProvider (final IModel model, final int sendIndex)
    {
        super (model);

        this.sendIndex = sendIndex;
    }


    /** {@inheritDoc} */
    @Override
    public IParameter get (final int index)
    {
        return this.getChannel (index).getSendBank ().getItem (this.sendIndex);
    }
}
