// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.TrackImpl;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.observer.NoteObserver;

import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;


/**
 * A track bank of all instrument and audio tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackBankImpl extends AbstractTrackBankImpl
{
    // TODO Requires API 9
    // private BooleanValue isTopGroup;

    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param bank The Bitwig track bank
     * @param cursorTrack The cursor track
     * @param rootGroup The root track
     * @param numTracks The number of tracks in a bank page
     * @param numScenes The number of scenes in a bank page
     * @param numSends The number of sends in a bank page
     */
    public TrackBankImpl (final IHost host, final IValueChanger valueChanger, final TrackBank bank, final CursorTrack cursorTrack, final Track rootGroup, final int numTracks, final int numScenes, final int numSends)
    {
        super (host, valueChanger, cursorTrack, bank, numTracks, numScenes, numSends);

        // TODO Requires API 9
        // this.isTopGroup = this.bank.getItemAt (0).createParentTrack (0, 0).createEqualsValue
        // (rootGroup);
        // this.isTopGroup.markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectParent ()
    {
        this.cursorTrack.selectParent ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasParent ()
    {
        // TODO Requires API 9
        return true; // !this.isTopGroup.get ();
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
    public void addNoteObserver (final NoteObserver observer)
    {
        for (int i = 0; i < this.getPageSize (); i++)
            ((TrackImpl) this.getItem (i)).addNoteObserver (observer);
    }
}