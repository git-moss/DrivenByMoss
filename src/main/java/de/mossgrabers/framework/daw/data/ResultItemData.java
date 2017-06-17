// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import com.bitwig.extension.controller.api.ClipLauncherSlot;
import com.bitwig.extension.controller.api.ColorValue;


/**
 * Encapsulates the data of a result item.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ResultItemData
{
    private final int              index;
    private final ClipLauncherSlot slot;


    /**
     * Constructor.
     *
     * @param slot The slot
     * @param index The index of the slot
     */
    public ResultItemData (final ClipLauncherSlot slot, final int index)
    {
        this.index = index;
        this.slot = slot;

        slot.exists ().markInterested ();
        slot.name ().markInterested ();
        slot.hasContent ().markInterested ();
        slot.color ().markInterested ();

        // States
        slot.isPlaying ().markInterested ();
        slot.isPlaybackQueued ().markInterested ();
        slot.isRecording ().markInterested ();
        slot.isRecordingQueued ().markInterested ();
        slot.isSelected ().markInterested ();
        slot.isStopQueued ().markInterested ();
    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
    public void enableObservers (final boolean enable)
    {
        this.slot.exists ().setIsSubscribed (enable);
        this.slot.name ().setIsSubscribed (enable);
        this.slot.hasContent ().setIsSubscribed (enable);
        this.slot.color ().setIsSubscribed (enable);
        this.slot.isPlaying ().setIsSubscribed (enable);
        this.slot.isPlaybackQueued ().setIsSubscribed (enable);
        this.slot.isRecording ().setIsSubscribed (enable);
        this.slot.isRecordingQueued ().setIsSubscribed (enable);
        this.slot.isSelected ().setIsSubscribed (enable);
        this.slot.isStopQueued ().setIsSubscribed (enable);
    }


    /**
     * Get the index.
     *
     * @return The index
     */
    public int getIndex ()
    {
        return this.index;
    }


    /**
     * Does the slot exist?
     *
     * @return True if it exists
     */
    public boolean doesExist ()
    {
        return this.slot.exists ().get ();
    }


    /**
     * get the name of the slot.
     *
     * @return The name of the slot
     */
    public String getName ()
    {
        return this.slot.name ().get ();
    }


    /**
     * Is the slot selected?
     *
     * @return True if selected
     */
    public boolean isSelected ()
    {
        return this.slot.isSelected ().get ();
    }


    /**
     * Does the slot have content?
     *
     * @return True if it has content
     */
    public boolean hasContent ()
    {
        return this.slot.hasContent ().get ();
    }


    /**
     * Is the slot recording?
     *
     * @return True if it is recording
     */
    public boolean isRecording ()
    {
        return this.slot.isRecording ().get ();
    }


    /**
     * Is the slot playing?
     *
     * @return True if the slot is playing
     */
    public boolean isPlaying ()
    {
        return this.slot.isPlaying ().get ();
    }


    /**
     * True if the slot is queued for playback or recording.
     *
     * @return True if the slot is queued for playback or recording.
     */
    public boolean isQueued ()
    {
        return this.slot.isPlaybackQueued ().get () || this.slot.isRecordingQueued ().get ();
    }


    /**
     * Get the color of the slot.
     *
     * @return The color
     */
    public double [] getColor ()
    {
        final ColorValue color = this.slot.color ();
        return new double []
        {
            color.red (),
            color.green (),
            color.blue ()
        };
    }
}
