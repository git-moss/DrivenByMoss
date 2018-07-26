// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;

import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.TrackBank;


/**
 * A track bank of all instrument and audio tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackBankImpl extends AbstractTrackBankImpl
{
    private CursorTrack cursorTrack;


    /**
     * Constructor.
     * 
     * @param host The host
     * @param valueChanger The value changer
     * @param bank The Bitwig track bank
     * @param cursorTrack The cursor track
     * @param numTracks The number of tracks in a bank page
     * @param numScenes The number of scenes in a bank page
     * @param numSends The number of sends in a bank page
     */
    public TrackBankImpl (final IHost host, final IValueChanger valueChanger, final TrackBank bank, final CursorTrack cursorTrack, final int numTracks, final int numScenes, final int numSends)
    {
        super (host, valueChanger, bank, numTracks, numScenes, numSends);

        this.cursorTrack = cursorTrack;
    }


    /** {@inheritDoc} */
    @Override
    public void selectChildren ()
    {
        this.cursorTrack.selectFirstChild ();
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
        // TODO API extension required - https://github.com/teotigraphix/Framework4Bitwig/issues/205
        return true;
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
}