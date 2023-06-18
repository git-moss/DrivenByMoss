// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.ApplicationImpl;
import de.mossgrabers.bitwig.framework.daw.data.CursorTrackImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.observer.INoteObserver;

import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;


/**
 * A track bank of all effect tracks.
 *
 * @author Jürgen Moßgraber
 */
public class EffectTrackBankImpl extends AbstractTrackBankImpl
{
    private final ITrackBank audioInstrumentTrackBank;


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param effectTrackBank The effect track bank
     * @param cursorTrack The cursor track assigned to this track bank
     * @param rootGroup The root track
     * @param application The application
     * @param numFxTracks The number of FX track of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param numSends The number of sends in a bank page
     * @param audioInstrumentTrackBank The track bank which monitors the audio and instrument tracks
     * @param numParamPages The number of project parameter pages
     * @param numParams The number of project parameters
     */
    public EffectTrackBankImpl (final IHost host, final IValueChanger valueChanger, final TrackBank effectTrackBank, final CursorTrackImpl cursorTrack, final Track rootGroup, final ApplicationImpl application, final int numFxTracks, final int numScenes, final int numSends, final int numParamPages, final int numParams, final ITrackBank audioInstrumentTrackBank)
    {
        super (host, valueChanger, effectTrackBank, cursorTrack, rootGroup, application, numFxTracks, numScenes, numSends);

        if (this.bank.isPresent ())
            this.bank.get ().followCursorTrack ((CursorTrack) cursorTrack.getTrack ());

        this.audioInstrumentTrackBank = audioInstrumentTrackBank;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        super.scrollTo (position - this.audioInstrumentTrackBank.getItemCount ());
    }


    /** {@inheritDoc} */
    @Override
    public void selectParent ()
    {
        // Effect bank is flat
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasParent ()
    {
        // Effect bank is flat
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void addNoteObserver (final INoteObserver observer)
    {
        // No notes on FX tracks
    }
}