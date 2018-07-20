// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.TrackImpl;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;

import com.bitwig.extension.controller.api.TrackBank;


/**
 * An abstract track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackBankImpl extends AbstractChannelBank<TrackBank, ITrack> implements ITrackBank
{
    /**
     * Constructor.
     *
     * @param bank The bank to encapsulate
     * @param valueChanger The value changer
     * @param numTracks The number of tracks of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param numSends The number of sends of a bank page
     */
    public AbstractTrackBankImpl (final TrackBank bank, final IValueChanger valueChanger, final int numTracks, final int numScenes, final int numSends)
    {
        super (bank, valueChanger, numTracks, numScenes, numSends);

        this.initItems ();

        final int pageSize = this.getPageSize ();

        this.bank.cursorIndex ().addValueObserver (index -> {
            for (int i = 0; i < pageSize; i++)
            {
                final boolean isSelected = index == i;
                if (this.items.get (i).isSelected () != isSelected)
                    this.handleBankTrackSelection (i, isSelected);
            }
        });

        this.sceneBank = new SceneBankImpl (this.bank.sceneBank (), this.numScenes);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);
        this.sceneBank.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        for (int index = 0; index < this.getPageSize (); index++)
            this.bank.getItemAt (index).clipLauncherSlotBank ().setIndication (enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isClipRecording ()
    {
        for (int t = 0; t < this.getPageSize (); t++)
        {
            for (int s = 0; s < this.numScenes; s++)
            {
                if (this.items.get (t).getSlotBank ().getItem (s).isRecording ())
                    return true;
            }
        }
        return false;
    }


    /**
     * Create all track data and setup observers.
     */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new TrackImpl (this.bank.getItemAt (i), this.valueChanger, i, this.numSends, this.numScenes));
    }


    /**
     * Handles track changes. Notifies all track change observers.
     *
     * @param index The index of the newly de-/selected track
     * @param isSelected True if selected
     */
    private void handleBankTrackSelection (final int index, final boolean isSelected)
    {
        this.getItem (index).setSelected (isSelected);
        this.notifySelectionObservers (index, isSelected);
    }
}