// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.Track;


/**
 * The data of a track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackData extends ChannelData
{
    protected Track     track;
    private SlotData [] slots;


    /**
     * Constructor.
     *
     * @param track The track
     * @param maxParameterValue The maximum paraeter value, remove when clipping bug is fixed
     * @param index The index of the track in the page
     * @param numSends The number of sends of a bank
     * @param numScenes The number of scenes of a bank
     */
    public TrackData (final Track track, final int maxParameterValue, final int index, final int numSends, final int numScenes)
    {
        super (track, maxParameterValue, index, numSends);

        this.track = track;

        if (track == null)
            return;

        track.trackType ().markInterested ();
        track.position ().markInterested ();
        track.isGroup ().markInterested ();
        track.getArm ().markInterested ();
        track.getMonitor ().markInterested ();
        track.getAutoMonitor ().markInterested ();
        track.getCrossFadeMode ().markInterested ();
        track.getCanHoldNoteData ().markInterested ();
        track.getCanHoldAudioData ().markInterested ();
        track.isStopped ().markInterested ();

        this.slots = new SlotData [numScenes];
        final ClipLauncherSlotBank cs = track.clipLauncherSlotBank ();
        for (int i = 0; i < numScenes; i++)
            this.slots[i] = new SlotData (cs.getItemAt (i), i);
    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        this.track.trackType ().setIsSubscribed (enable);
        this.track.position ().setIsSubscribed (enable);
        this.track.isGroup ().setIsSubscribed (enable);
        this.track.getArm ().setIsSubscribed (enable);
        this.track.getMonitor ().setIsSubscribed (enable);
        this.track.getAutoMonitor ().setIsSubscribed (enable);
        this.track.getCrossFadeMode ().setIsSubscribed (enable);
        this.track.getCanHoldNoteData ().setIsSubscribed (enable);
        this.track.getCanHoldAudioData ().setIsSubscribed (enable);
        this.track.isStopped ().setIsSubscribed (enable);

        for (final SlotData slot: this.slots)
            slot.enableObservers (enable);
    }


    /**
     * Get the type of the track.
     *
     * @return The type
     */
    public String getType ()
    {
        return this.track.trackType ().get ();
    }


    /**
     * Get the position of the track in all tracks.
     *
     * @return The position
     */
    public int getPosition ()
    {
        return this.track.position ().get ();
    }


    /**
     * Is the track a group?
     *
     * @return True if the track is a group
     */
    public boolean isGroup ()
    {
        return this.track.isGroup ().get ();
    }


    /**
     * True if armed for recording.
     *
     * @return True if armed for recording.
     */
    public boolean isRecarm ()
    {
        return this.track.getArm ().get ();
    }


    /**
     * True if monitoring is on.
     *
     * @return True if monitoring is on.
     */
    public boolean isMonitor ()
    {
        return this.track.getMonitor ().get ();
    }


    /**
     * True if auto monitoring is on.
     *
     * @return True if auto monitoring is on.
     */
    public boolean isAutoMonitor ()
    {
        return this.track.getAutoMonitor ().get ();
    }


    /**
     * Returns true if the track can hold note data.
     *
     * @return True if the track can hold note data.
     */
    public boolean canHoldNotes ()
    {
        return this.track.getCanHoldNoteData ().get ();
    }


    /**
     * Returns true if the track can hold audio data.
     *
     * @return True if the track can hold audio data.
     */
    public boolean canHoldAudioData ()
    {
        return this.track.getCanHoldAudioData ().get ();
    }


    /**
     * Get the crossfade mode (A, B, AB).
     *
     * @return The crossfade mode
     */
    public String getCrossfadeMode ()
    {
        return this.track.getCrossFadeMode ().get ();
    }


    /**
     * Get the clip slots of the track.
     *
     * @return The clip slots
     */
    public SlotData [] getSlots ()
    {
        return this.slots;
    }


    /**
     * Returns true if a clip is playing on the track.
     *
     * @return True if a clip is playing on the track.
     */
    public boolean isPlaying ()
    {
        return !this.track.isStopped ().get ();
    }
}
