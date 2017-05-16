// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.controller.ValueChanger;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorTrack;


/**
 * A track bank of all instrument and audio tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackBankProxy extends AbstractTrackBankProxy
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


    /**
     * Selects the first child if this is a group track.
     */
    public void selectChildren ()
    {
        this.cursorTrack.selectFirstChild ();
    }


    /**
     * Selects the parent track if any (track must be inside a group).
     */
    public void selectParent ()
    {
        this.cursorTrack.selectParent ();
    }


    /**
     * Changes the value of a send.
     *
     * @param index The index of the track
     * @param sendIndex The index of the send
     * @param value The current value
     */
    public void changeSend (final int index, final int sendIndex, final int value)
    {
        final Double newValue = Double.valueOf (this.valueChanger.calcKnobSpeed (value));
        this.trackBank.getChannel (index).sendBank ().getItemAt (sendIndex).inc (newValue, Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Set the value of a send.
     *
     * @param index The index of the track
     * @param sendIndex The index of the send
     * @param value The new value
     */
    public void setSend (final int index, final int sendIndex, final double value)
    {
        this.getTrack (index).getSends ()[sendIndex].setValue (value);
    }


    /**
     * Reset the value of a send to its default value.
     *
     * @param index The index of the track
     * @param sendIndex The index of the send
     */
    public void resetSend (final int index, final int sendIndex)
    {
        this.trackBank.getChannel (index).sendBank ().getItemAt (sendIndex).reset ();
    }


    /**
     * Signal the automation touch of a send.
     *
     * @param index The index of the track
     * @param sendIndex The index of the send
     * @param isBeingTouched True if touched
     */
    public void touchSend (final int index, final int sendIndex, final boolean isBeingTouched)
    {
        this.trackBank.getChannel (index).sendBank ().getItemAt (sendIndex).touch (isBeingTouched);
    }


    /**
     * Set the indication of a send.
     *
     * @param index The index of the track
     * @param sendIndex The index of the send
     * @param indicate True if send is active for editing
     */
    public void setSendIndication (final int index, final int sendIndex, final boolean indicate)
    {
        this.trackBank.getChannel (index).sendBank ().getItemAt (sendIndex).setIndication (indicate);
    }


    /**
     * Get the encapsulated cursor track.
     *
     * @return The cursor track
     */
    public CursorTrack getCursorTrack ()
    {
        return this.cursorTrack;
    }
}