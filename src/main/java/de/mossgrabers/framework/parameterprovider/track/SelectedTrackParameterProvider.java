// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.track;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.daw.data.empty.EmptySend;
import de.mossgrabers.framework.observer.IItemSelectionObserver;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.parameter.IParameter;

import java.util.Optional;


/**
 * Get a number of parameters. This implementation provides the selected tracks' volume, panorama
 * and send parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectedTrackParameterProvider extends AbstractTrackParameterProvider implements IItemSelectionObserver
{
    /**
     * Constructor.
     *
     * @param bank The bank from which to get the parameters
     */
    public SelectedTrackParameterProvider (final ITrackBank bank)
    {
        super (bank);
    }


    /**
     * Constructor. Use for track modes.
     *
     * @param model Uses the current channel bank from this model to get the parameters
     */
    public SelectedTrackParameterProvider (final IModel model)
    {
        super (model);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter get (final int index)
    {
        final Optional<ITrack> selectedTrack = this.bank.getSelectedItem ();
        return selectedTrack.isEmpty () ? EmptyParameter.INSTANCE : this.getInternal (index, selectedTrack.get ());
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ColorEx> getColor (final int index)
    {
        final Optional<ITrack> selectedTrack = this.bank.getSelectedItem ();
        if (selectedTrack.isEmpty ())
            return Optional.empty ();

        final ITrack channel = selectedTrack.get ();
        if (index < 2)
            return Optional.of (channel.getColor ());

        return Optional.ofNullable (this.getSend (index - 2, channel).getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        super.addParametersObserver (observer);

        if (this.model == null)
        {
            this.bank.addSelectionObserver (this);
            return;
        }

        this.model.getTrackBank ().addSelectionObserver (this);
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            effectTrackBank.addSelectionObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        super.removeParametersObserver (observer);

        if (this.hasObservers ())
            return;

        if (this.model == null)
        {
            this.bank.removeSelectionObserver (this);
            return;
        }

        this.model.getTrackBank ().removeSelectionObserver (this);
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            effectTrackBank.removeSelectionObserver (this);
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
                return this.getSend (index - 2, selectedChannel);
        }
    }


    /**
     * Get a send parameter.
     *
     * @param sendIndex The index of the send
     * @param selectedChannel The selected channel, not null
     * @return The parameter
     */
    protected ISend getSend (final int sendIndex, final IChannel selectedChannel)
    {
        final ISendBank sendBank = selectedChannel.getSendBank ();
        if (this.model != null && this.model.isEffectTrackBankActive ())
            return EmptySend.INSTANCE;
        try
        {
            return this.getSend (sendIndex, sendBank);
        }
        catch (final IndexOutOfBoundsException ex)
        {
            return EmptySend.INSTANCE;
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
