// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import com.bitwig.extension.controller.api.CursorTrack;


/**
 * Interface to a track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ITrackBank extends IChannelBank
{
    /**
     * Selects the first child if this is a group track.
     */
    void selectChildren ();


    /**
     * Selects the parent track if any (track must be inside a group).
     */
    void selectParent ();


    /**
     * Changes the value of a send.
     *
     * @param index The index of the track
     * @param sendIndex The index of the send
     * @param value The current value
     */
    void changeSend (final int index, final int sendIndex, final int value);


    /**
     * Set the value of a send.
     *
     * @param index The index of the track
     * @param sendIndex The index of the send
     * @param value The new value
     */
    void setSend (final int index, final int sendIndex, final double value);


    /**
     * Reset the value of a send to its default value.
     *
     * @param index The index of the track
     * @param sendIndex The index of the send
     */
    void resetSend (final int index, final int sendIndex);


    /**
     * Signal the automation touch of a send.
     *
     * @param index The index of the track
     * @param sendIndex The index of the send
     * @param isBeingTouched True if touched
     */
    void touchSend (final int index, final int sendIndex, final boolean isBeingTouched);


    /**
     * Set the indication of a send.
     *
     * @param index The index of the track
     * @param sendIndex The index of the send
     * @param indicate True if send is active for editing
     */
    void setSendIndication (final int index, final int sendIndex, final boolean indicate);


    // TODO: Remove this function
    /**
     * Get the encapsulated cursor track.
     *
     * @return The cursor track
     */
    CursorTrack getCursorTrack ();
}