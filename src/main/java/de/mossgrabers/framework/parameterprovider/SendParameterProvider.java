// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;


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
     * Constructor. Provides all sends of the currently selected track.
     *
     * @param model Uses the current track bank from this model to get the parameters
     */
    public SendParameterProvider (final IModel model)
    {
        this (model, -1);
    }


    /**
     * Constructor. Provides one send parameter of all tracks.
     *
     * @param model Uses the current track bank from this model to get the parameters
     * @param sendIndex The index of the send to provide
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
        if (this.sendIndex == -1)
        {
            final ITrack track = this.model.getCurrentTrackBank ().getSelectedItem ();
            if (track == null)
                return EmptyParameter.INSTANCE;
            final ISendBank sendBank = track.getSendBank ();
            return sendBank.getItemCount () > 0 ? sendBank.getItem (index) : EmptyParameter.INSTANCE;
        }

        final ISendBank sendBank = this.getChannel (index).getSendBank ();
        return sendBank.getItemCount () == 0 ? EmptyParameter.INSTANCE : sendBank.getItem (this.sendIndex);
    }
}
