// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.daw;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.daw.ITrackBank;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorTrack;


/**
 * A track bank of all instrument and audio tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackBankProxy extends AbstractTrackBankProxy implements ITrackBank
{
    private CursorTrack cursorTrack;


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param cursorTrack The cursor track
     * @param numTracks The number of tracks in a bank page
     * @param numScenes The number of scenes in a bank page
     * @param numSends The number of sends in a bank page
     * @param hasFlatTrackList True if group navigation should not be supported, instead all tracks
     *            are flat
     */
    public TrackBankProxy (final ControllerHost host, final ValueChanger valueChanger, final CursorTrack cursorTrack, final int numTracks, final int numScenes, final int numSends, final boolean hasFlatTrackList)
    {
        super (valueChanger, numTracks, numScenes, numSends);

        this.cursorTrack = cursorTrack;

        if (hasFlatTrackList)
        {
            this.trackBank = host.createMainTrackBank (numTracks, numSends, numScenes);
            this.trackBank.followCursorTrack (cursorTrack);
        }
        else
            this.trackBank = this.cursorTrack.createSiblingsTrackBank (numTracks, numSends, numScenes, false, false);

        this.init ();
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
    public void changeSend (final int index, final int sendIndex, final int value)
    {
        final Double newValue = Double.valueOf (this.valueChanger.calcKnobSpeed (value));
        this.trackBank.getChannel (index).sendBank ().getItemAt (sendIndex).inc (newValue, Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setSend (final int index, final int sendIndex, final double value)
    {
        this.getTrack (index).getSends ()[sendIndex].setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetSend (final int index, final int sendIndex)
    {
        this.trackBank.getChannel (index).sendBank ().getItemAt (sendIndex).reset ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchSend (final int index, final int sendIndex, final boolean isBeingTouched)
    {
        this.trackBank.getChannel (index).sendBank ().getItemAt (sendIndex).touch (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setSendIndication (final int index, final int sendIndex, final boolean indicate)
    {
        this.trackBank.getChannel (index).sendBank ().getItemAt (sendIndex).setIndication (indicate);
    }
}