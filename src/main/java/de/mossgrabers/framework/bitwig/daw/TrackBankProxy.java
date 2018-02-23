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
}