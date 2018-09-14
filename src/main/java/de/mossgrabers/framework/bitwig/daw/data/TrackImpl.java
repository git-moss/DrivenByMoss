// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.daw.data;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;

import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.Track;

import java.util.ArrayList;
import java.util.List;


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
     * @param valueChanger The valueChanger
     * @param index The index of the track in the page
     * @param numSends The number of sends of a bank
     * @param numScenes The number of scenes of a bank
     */
    public TrackImpl (final Track track, final ValueChanger valueChanger, final int index, final int numSends, final int numScenes)
    {
        super (track, valueChanger, index, numSends);

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
            this.slots[i] = new SlotImpl (cs, cs.getItemAt (i), i);
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
    public void setRecArm (final boolean value)
    {
        this.track.arm ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleRecArm ()
    {
        this.track.arm ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMonitor ()
    {
        return this.track.monitor ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setMonitor (final boolean value)
    {
        this.track.monitor ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMonitor ()
    {
        this.track.monitor ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAutoMonitor ()
    {
        return this.track.autoMonitor ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setAutoMonitor (final boolean value)
    {
        this.track.autoMonitor ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleAutoMonitor ()
    {
        this.track.autoMonitor ().toggle ();
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
    public void changeCrossfadeModeAsNumber (final int control)
    {
        this.setCrossfadeModeAsNumber (this.valueChanger.changeValue (control, this.getCrossfadeModeAsNumber (), 1, 3));
    }


    /** {@inheritDoc} */
    @Override
    public void setCrossfadeMode (final String mode)
    {
        this.track.crossFadeMode ().set (mode);
    }


    /** {@inheritDoc} */
    @Override
    public int getCrossfadeModeAsNumber ()
    {
        switch (this.getCrossfadeMode ())
        {
            case "A":
                return 0;
            case "AB":
                return 1;
            case "B":
                return 2;
            default:
                // Not possible
                break;
        }
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void setCrossfadeModeAsNumber (final int modeValue)
    {
        this.setCrossfadeMode (modeValue == 0 ? "A" : modeValue == 1 ? "AB" : "B");
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCrossfadeMode ()
    {
        switch (this.getCrossfadeMode ())
        {
            case "A":
                this.setCrossfadeMode ("B");
                break;
            case "B":
                this.setCrossfadeMode ("AB");
                break;
            case "AB":
                this.setCrossfadeMode ("A");
                break;
            default:
                // Not possible
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getNumSlots ()
    {
        return this.slots.length;
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getSlot (final int slotIndex)
    {
        return this.slots[slotIndex];
    }


    /** {@inheritDoc} */
    @Override
    public ISlot [] getSelectedSlots ()
    {
        final List<ISlot> selection = new ArrayList<> ();
        for (final ISlot slot: this.slots)
        {
            if (slot.isSelected ())
                selection.add (slot);
        }
        return selection.toArray (new SlotImpl [selection.size ()]);
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getSelectedSlot ()
    {
        for (final ISlot slot: this.slots)
        {
            if (slot.isSelected ())
                return slot;
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getEmptySlot (final int startFrom)
    {
        final int start = startFrom >= 0 ? startFrom : 0;
        for (int i = 0; i < this.slots.length; i++)
        {
            final int pos = (start + i) % this.slots.length;
            if (!this.slots[pos].hasContent ())
                return this.slots[pos];
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlaying ()
    {
        return !this.track.isStopped ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        this.track.stop ();
    }


    /** {@inheritDoc} */
    @Override
    public void returnToArrangement ()
    {
        this.track.returnToArrangement ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollClipPageForwards ()
    {
        this.track.clipLauncherSlotBank ().scrollPageForwards ();
    }
}
