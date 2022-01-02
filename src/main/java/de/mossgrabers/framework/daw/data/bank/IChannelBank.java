// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.IChannel;


/**
 * Interface to a channel bank.
 *
 * @param <T> The specific item type of the bank
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
     * Stop all playing clips.
     */
    void stop ();


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
}