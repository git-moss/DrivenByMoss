// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;


/**
 * Get a number of parameters. This implementation provides the selected channels volume, panorama
 * and send parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ChannelParameterProvider extends AbstractChannelParameterProvider
{
    /**
     * Constructor.
     *
     * @param bank The bank from which to get the parameters
     */
    public ChannelParameterProvider (final IChannelBank<? extends IChannel> bank)
    {
        super (bank);
    }


    /**
     * Constructor.
     *
     * @param model Uses the current channel bank from this model to get the parameters
     */
    public ChannelParameterProvider (final IModel model)
    {
        super (model);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter get (final int index)
    {
        final IChannel selectedTrack = this.getBank ().getSelectedItem ();
        return selectedTrack == null ? EmptyParameter.INSTANCE : this.getInternal (index, selectedTrack);
    }


    /**
     * Get the parameter.
     *
     * @param index The index
     * @param selectedChannel The selected channel, not null
     * @return The parameter
     */
    private IParameter getInternal (final int index, final IChannel selectedChannel)
    {
        switch (index)
        {
            case 0:
                return selectedChannel.getVolumeParameter ();

            case 1:
                return selectedChannel.getPanParameter ();

            default:
                return this.handleSends (index, selectedChannel);
        }
    }


    /**
     * Get a send parameter.
     *
     * @param index The index
     * @param selectedChannel The selected channel, not null
     * @return The parameter
     */
    private IParameter handleSends (final int index, final IChannel selectedChannel)
    {
        final ISendBank sendBank = selectedChannel.getSendBank ();
        if (this.model.isEffectTrackBankActive ())
            return EmptyParameter.INSTANCE;
        try
        {
            return this.getSend (index - 2, sendBank);
        }
        catch (final IndexOutOfBoundsException ex)
        {
            return EmptyParameter.INSTANCE;
        }
    }


    /**
     * Get a send parameter.
     *
     * @param sendIndex The index of the send
     * @param sendBank The send bank
     * @return The send parameter
     */
    protected ISend getSend (final int sendIndex, final ISendBank sendBank)
    {
        return sendBank.getItem (sendIndex);
    }
}
