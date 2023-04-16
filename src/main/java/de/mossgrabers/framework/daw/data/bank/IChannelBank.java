// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.IChannel;


/**
 * Interface to a channel bank.
 *
 * @param <T> The specific item type of the bank
 *
 * @author Jürgen Moßgraber
 */
public interface IChannelBank<T extends IChannel> extends IBank<T>
{
    /**
     * Get the color ID of the current track.
     *
     * @return The color ID
     */
    String getSelectedChannelColorEntry ();


    /**
     * Get the scene bank.
     *
     * @return The scene bank.
     */
    ISceneBank getSceneBank ();


    /**
     * Set indication for all clips of the size of the number of tracks and scenes.
     *
     * @param enable True to enable
     */
    void setIndication (final boolean enable);


    /**
     * Check if there is a send at the given index, which can be edited.
     *
     * @param sendIndex The index of the send
     * @return True if there is a send to edit
     */
    boolean canEditSend (int sendIndex);


    /**
     * DAWs which can put different sends in a slot can return here a name to be displayed for a
     * slot.
     *
     * @param sendIndex The index of the send
     * @return The name to display
     */
    String getEditSendName (int sendIndex);
}