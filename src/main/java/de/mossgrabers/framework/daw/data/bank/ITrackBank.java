// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.bank;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.IDeviceMetadata;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.observer.IIndexedValueObserver;
import de.mossgrabers.framework.observer.INoteObserver;

import java.util.List;


/**
 * Interface to a track bank.
 *
 * @author Jürgen Moßgraber
 */
public interface ITrackBank extends IChannelBank<ITrack>
{
    /**
     * Toggles record arm on all tracks of the current bank page.
     */
    void toggleRecArm ();


    /**
     * Stop all playing clips. Alternative function to stop the playback of all clips, e.g. not
     * quantized, if true.
     *
     * @param isAlternative True, to execute the alternative function
     */
    void stop (boolean isAlternative);


    /**
     * Selects the parent track if any (track must be inside a group).
     */
    void selectParent ();


    /**
     * Returns true if there is a parent track.
     *
     * @return True if there is a parent track
     */
    boolean hasParent ();


    /**
     * Returns true if one of the clips of the current bank page is recording.
     *
     * @return True if one of the clips of the current bank page is recording
     */
    boolean isClipRecording ();


    /**
     * Add an observer for the items name.
     *
     * @param observer The observer to notify on a name change
     */
    void addNameObserver (IIndexedValueObserver<String> observer);


    /**
     * Add a note observer.
     *
     * @param observer The note observer
     */
    void addNoteObserver (final INoteObserver observer);


    /**
     * Adds a new channel to this channel bank. Creates a random name. Uses a random color for the
     * channel.
     *
     * @param type The type of channel to add
     */
    void addChannel (ChannelType type);


    /**
     * Adds a new channel to this channel bank. Uses a random color for the channel.
     *
     * @param type The type of channel to add
     * @param name The name of the channel, might be null
     */
    void addChannel (ChannelType type, String name);


    /**
     * Adds a new channel to this channel bank.
     *
     * @param type The type of channel to add
     * @param name The name of the channel, might be null
     * @param color The color of the channel, might be null
     */
    void addChannel (ChannelType type, String name, ColorEx color);


    /**
     * Adds a new channel to this channel bank.
     *
     * @param type The type of channel to add
     * @param name The name of the channel, might be null
     * @param devices The devices to add to the channel
     */
    void addChannel (ChannelType type, String name, List<IDeviceMetadata> devices);
}