// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.TrackImpl;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.AbstractChannelBank;
import de.mossgrabers.framework.daw.data.ITrack;

import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.PlayingNote;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;


/**
 * An abstract track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackBankImpl extends AbstractChannelBank
{
    protected TrackBank trackBank;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param numTracks The number of tracks of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param numSends The number of sends of a bank page
     */
    public AbstractTrackBankImpl (final IValueChanger valueChanger, final int numTracks, final int numScenes, final int numSends)
    {
        super (valueChanger, numTracks, numScenes, numSends);
    }


    /**
     * Initialise all observers.
     */
    public void init ()
    {
        this.tracks = this.createTracks (this.numTracks);

        for (int i = 0; i < this.numTracks; i++)
        {
            final int index = i;
            final Track t = this.trackBank.getItemAt (i);
            t.playingNotes ().addValueObserver (value -> this.handleNotes (index, value));
        }

        this.trackBank.cursorIndex ().addValueObserver (index -> {
            for (int i = 0; i < this.numTracks; i++)
            {
                final boolean isSelected = index == i;
                if (this.tracks[i].isSelected () != isSelected)
                    this.handleBankTrackSelection (i, isSelected);
            }
        });

        this.trackBank.channelCount ().markInterested ();
        this.trackBank.scrollPosition ().markInterested ();
        this.trackBank.canScrollChannelsUp ().markInterested ();
        this.trackBank.canScrollChannelsDown ().markInterested ();
        if (this.numScenes > 0)
            this.sceneBank = new SceneBankImpl (this.trackBank.sceneBank (), this.numScenes);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        for (final ITrack track: this.tracks)
        {
            track.enableObservers (enable);
            this.trackBank.getItemAt (track.getIndex ()).playingNotes ().setIsSubscribed (enable);
        }

        this.trackBank.channelCount ().setIsSubscribed (enable);
        this.trackBank.scrollPosition ().setIsSubscribed (enable);
        this.trackBank.canScrollChannelsUp ().setIsSubscribed (enable);
        this.trackBank.canScrollChannelsDown ().setIsSubscribed (enable);

        this.sceneBank.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getTrackCount ()
    {
        return this.trackBank.channelCount ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollTracksUp ()
    {
        return this.trackBank.canScrollChannelsUp ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollTracksDown ()
    {
        return this.trackBank.canScrollChannelsDown ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTracksUp ()
    {
        this.trackBank.scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTracksDown ()
    {
        this.trackBank.scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTracksPageUp ()
    {
        this.trackBank.scrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTracksPageDown ()
    {
        this.trackBank.scrollPageForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollToChannel (final int channel)
    {
        if (channel >= 0 && channel < this.getTrackCount ())
            this.trackBank.scrollPosition ().set (channel / this.numTracks * this.numTracks);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollToScene (final int position)
    {
        this.sceneBank.scrollTo (position);
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        for (int index = 0; index < this.numTracks; index++)
            this.getClipLauncherSlots (index).setIndication (enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getTrackPositionFirst ()
    {
        return this.getTrack (0).getPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public int getTrackPositionLast ()
    {
        for (int i = 7; i >= 0; i--)
        {
            final int pos = this.getTrack (i).getPosition ();
            if (pos >= 0)
                return pos;
        }
        return -1;
    }


    /**
     * Get the clip launcher slots of a track.
     *
     * @param index The index of the track
     * @return The clip launcher slots
     */
    private ClipLauncherSlotBank getClipLauncherSlots (final int index)
    {
        return this.trackBank.getItemAt (index).clipLauncherSlotBank ();
    }


    /**
     * Create all track data and setup observers.
     *
     * @param count The number of tracks of the track bank page
     * @return The created data
     */
    protected ITrack [] createTracks (final int count)
    {
        final ITrack [] trackData = new TrackImpl [count];
        for (int i = 0; i < count; i++)
            trackData[i] = new TrackImpl (this.trackBank.getItemAt (i), this.valueChanger, i, this.numSends, this.numScenes);
        return trackData;
    }


    /**
     * Handles the updates on all playing notes. Translates the note array into individual note
     * observer updates of start and stopped notes.
     *
     * @param index The index of a track
     * @param notes The currently playing notes
     */
    private void handleNotes (final int index, final PlayingNote [] notes)
    {
        final ITrack sel = this.getSelectedTrack ();
        if (sel == null || sel.getIndex () != index)
            return;

        final int [] nc = this.noteCache[index];
        synchronized (nc)
        {
            // Send the new notes
            for (final PlayingNote note: notes)
            {
                final int pitch = note.pitch ();
                nc[pitch] = NOTE_ON_NEW;
                this.notifyNoteObservers (pitch, note.velocity ());
            }
            // Send note offs
            for (int i = 0; i < nc.length; i++)
            {
                if (nc[i] == NOTE_ON_NEW)
                    nc[i] = NOTE_ON;
                else if (nc[i] == NOTE_ON)
                {
                    nc[i] = NOTE_OFF;
                    this.notifyNoteObservers (i, 0);
                }
            }
        }
    }


    /**
     * Handles track changes. Notifies all track change observers.
     *
     * @param index The index of the newly de-/selected track
     * @param isSelected True if selected
     */
    private void handleBankTrackSelection (final int index, final boolean isSelected)
    {
        this.getTrack (index).setSelected (isSelected);
        this.notifyTrackSelectionObservers (index, isSelected);
    }
}