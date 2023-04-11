// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.track;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.data.empty.EmptySend;
import de.mossgrabers.framework.observer.IItemSelectionObserver;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;

import java.util.Optional;


/**
 * Get a number of parameters. This implementation provides all send parameters of the channels of
 * the current channel bank.
 *
 * @author Jürgen Moßgraber
 */
public class SendParameterProvider extends AbstractTrackParameterProvider implements IItemSelectionObserver
{
    private final int sendIndex;
    private final int sendOffset;


    /**
     * Constructor. Provides one send parameter of all tracks.
     *
     * @param model Uses the current track bank from this model to get the parameters
     * @param sendIndex The index of the send to provide, set to -1 to provide all sends of the
     *            selected track
     * @param sendOffset If all sends are provided they can be offset by this value
     */
    public SendParameterProvider (final IModel model, final int sendIndex, final int sendOffset)
    {
        super (model);

        this.sendIndex = sendIndex;
        this.sendOffset = sendOffset;
    }


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        super.addParametersObserver (observer);

        if (this.sendIndex == -1)
            this.bank.addSelectionObserver (this);
        else
        {
            ITrackBank trackBank = this.model.getTrackBank ();
            for (int i = 0; i < trackBank.getPageSize (); i++)
                trackBank.getItem (i).getSendBank ().addPageObserver (this);
            trackBank = this.model.getEffectTrackBank ();
            if (trackBank != null)
            {
                for (int i = 0; i < trackBank.getPageSize (); i++)
                    trackBank.getItem (i).getSendBank ().addPageObserver (this);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        if (this.sendIndex == -1)
            this.bank.removeSelectionObserver (this);
        else
        {
            ITrackBank trackBank = this.model.getTrackBank ();
            for (int i = 0; i < trackBank.getPageSize (); i++)
                trackBank.getItem (i).getSendBank ().removePageObserver (this);
            trackBank = this.model.getEffectTrackBank ();
            if (trackBank != null)
            {
                for (int i = 0; i < trackBank.getPageSize (); i++)
                    trackBank.getItem (i).getSendBank ().removePageObserver (this);
            }
        }

        super.removeParametersObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public ISend get (final int index)
    {
        if (this.sendIndex == -1)
        {
            final Optional<ITrack> track = this.bank.getSelectedItem ();
            if (track.isEmpty ())
                return EmptySend.INSTANCE;
            final ISendBank sendBank = track.get ().getSendBank ();
            final int idx = this.sendOffset + index;
            return idx < sendBank.getItemCount () ? sendBank.getItem (idx) : EmptySend.INSTANCE;
        }

        final ISendBank sendBank = this.bank.getItem (index).getSendBank ();
        final int idx = this.sendOffset + this.sendIndex;
        return sendBank.getItemCount () == 0 ? EmptySend.INSTANCE : sendBank.getItem (idx);
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ColorEx> getColor (final int index)
    {
        return Optional.ofNullable (this.get (index).getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public void call (final int index, final boolean isSelected)
    {
        this.notifyParametersObservers ();
    }
}
