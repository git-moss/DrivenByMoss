// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.daw.data;

import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;

import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.Track;


/**
 * The data of a track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackImpl extends ChannelImpl implements ITrack
{
    protected Track  track;
    private ISlot [] slots;


    /**
     * Constructor.
     *
     * @param track The track
     * @param maxParameterValue The maximum parameter value, remove when clipping bug is fixed
     * @param index The index of the track in the page
     * @param numSends The number of sends of a bank
     * @param numScenes The number of scenes of a bank
     */
    public TrackImpl (final Track track, final int maxParameterValue, final int index, final int numSends, final int numScenes)
    {
        super (track, maxParameterValue, index, numSends);

        this.track = track;

        if (track == null)
            return;

        track.trackType ().markInterested ();
        track.position ().markInterested ();
        track.isGroup ().markInterested ();
        track.arm ().markInterested ();
        track.monitor ().markInterested ();
        track.autoMonitor ().markInterested ();
        track.crossFadeMode ().markInterested ();
        track.canHoldNoteData ().markInterested ();
        track.canHoldAudioData ().markInterested ();
        track.isStopped ().markInterested ();

        this.slots = new SlotImpl [numScenes];
        final ClipLauncherSlotBank cs = track.clipLauncherSlotBank ();
        for (int i = 0; i < numScenes; i++)
            this.slots[i] = new SlotImpl (cs.getItemAt (i), i);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        this.track.trackType ().setIsSubscribed (enable);
        this.track.position ().setIsSubscribed (enable);
        this.track.isGroup ().setIsSubscribed (enable);
        this.track.arm ().setIsSubscribed (enable);
        this.track.monitor ().setIsSubscribed (enable);
        this.track.autoMonitor ().setIsSubscribed (enable);
        this.track.crossFadeMode ().setIsSubscribed (enable);
        this.track.canHoldNoteData ().setIsSubscribed (enable);
        this.track.canHoldAudioData ().setIsSubscribed (enable);
        this.track.isStopped ().setIsSubscribed (enable);

        for (final ISlot slot: this.slots)
            slot.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public String getType ()
    {
        return this.track.trackType ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return this.track.position ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isGroup ()
    {
        return this.track.isGroup ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecArm ()
    {
        return this.track.arm ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMonitor ()
    {
        return this.track.monitor ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAutoMonitor ()
    {
        return this.track.autoMonitor ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canHoldNotes ()
    {
        return this.track.canHoldNoteData ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canHoldAudioData ()
    {
        return this.track.canHoldAudioData ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getCrossfadeMode ()
    {
        return this.track.crossFadeMode ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public ISlot [] getSlots ()
    {
        return this.slots;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlaying ()
    {
        return !this.track.isStopped ().get ();
    }
}
