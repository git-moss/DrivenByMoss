// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.ApplicationImpl;
import de.mossgrabers.bitwig.framework.daw.data.CursorTrackImpl;
import de.mossgrabers.bitwig.framework.daw.data.TrackImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.observer.INoteObserver;

import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;


/**
 * A track bank of all instrument and audio tracks.
 *
 * @author Jürgen Moßgraber
 */
public class TrackBankImpl extends AbstractTrackBankImpl
{
    /**
     * Constructor.
     *
     * @param host The host
     * @param application The application
     * @param valueChanger The value changer
     * @param bank The Bitwig track bank
     * @param cursorTrack The cursor track
     * @param rootGroup The root track
     * @param numTracks The number of tracks in a bank page
     * @param numScenes The number of scenes in a bank page
     * @param numSends The number of sends in a bank page
     */
    public TrackBankImpl (final IHost host, final ApplicationImpl application, final IValueChanger valueChanger, final TrackBank bank, final CursorTrackImpl cursorTrack, final Track rootGroup, final int numTracks, final int numScenes, final int numSends)
    {
        super (host, valueChanger, bank, cursorTrack, rootGroup, application, numTracks, numScenes, numSends);
    }


    /** {@inheritDoc} */
    @Override
    public void selectParent ()
    {
        ((CursorTrack) this.cursorTrack.getTrack ()).selectParent ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasParent ()
    {
        return this.getItem (0).hasParent ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canEditSend (final int sendIndex)
    {
        return this.getItem (0).getSendBank ().getItem (sendIndex).doesExist ();
    }


    /** {@inheritDoc} */
    @Override
    public String getEditSendName (final int sendIndex)
    {
        return this.getItem (0).getSendBank ().getItem (sendIndex).getName ();
    }


    /** {@inheritDoc} */
    @Override
    public void addNoteObserver (final INoteObserver observer)
    {
        for (int i = 0; i < this.getPageSize (); i++)
            ((TrackImpl) this.getItem (i)).addNoteObserver (observer);
    }
}