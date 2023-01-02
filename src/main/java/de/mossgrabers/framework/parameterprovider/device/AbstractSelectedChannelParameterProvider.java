// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.device;

import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.observer.IItemSelectionObserver;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.parameter.IParameter;

import java.util.Optional;


/**
 * Get a number of parameters. This implementation provides the selected channels volume, panorama
 * and send parameters.
 *
 * @param <B> The type of the bank
 * @param <C> The type of the banks' item
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AbstractSelectedChannelParameterProvider<B extends IChannelBank<C>, C extends IChannel> extends AbstractChannelParameterProvider<B, C> implements IItemSelectionObserver
{
    /**
     * Constructor.
     *
     * @param bank The bank from which to get the parameters
     */
    public AbstractSelectedChannelParameterProvider (final B bank)
    {
        super (bank);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter get (final int index)
    {
        final Optional<? extends IChannel> selectedTrack = this.bank.getSelectedItem ();
        return selectedTrack.isEmpty () ? EmptyParameter.INSTANCE : this.getInternal (index, selectedTrack.get ());
    }


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        super.addParametersObserver (observer);

        this.bank.addSelectionObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        super.removeParametersObserver (observer);

        if (!this.hasObservers ())
            this.bank.removeSelectionObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    public void call (final int index, final boolean isSelected)
    {
        this.notifyParametersObservers ();
    }


    /**
     * Get the parameter.
     *
     * @param index The index
     * @param selectedChannel The selected channel, not null
     * @return The parameter
     */
    protected IParameter getInternal (final int index, final IChannel selectedChannel)
    {
        switch (index)
        {
            case 0:
                return selectedChannel.getVolumeParameter ();

            case 1:
                return selectedChannel.getPanParameter ();

            default:
                return this.handleSends (index - 2, selectedChannel);
        }
    }


    /**
     * Get a send parameter.
     *
     * @param sendIndex The index of the send
     * @param selectedChannel The selected channel, not null
     * @return The parameter
     */
    protected IParameter handleSends (final int sendIndex, final IChannel selectedChannel)
    {
        try
        {
            return this.getSend (sendIndex, selectedChannel.getSendBank ());
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
