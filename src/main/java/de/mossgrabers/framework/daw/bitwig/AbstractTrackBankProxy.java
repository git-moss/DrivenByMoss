// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.bitwig;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.NoteObserver;
import de.mossgrabers.framework.daw.TrackSelectionObserver;
import de.mossgrabers.framework.daw.bitwig.data.SlotImpl;
import de.mossgrabers.framework.daw.bitwig.data.TrackImpl;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;

import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.PlayingNote;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * An abstract track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackBankProxy implements IChannelBank
{
    private static final int                  NOTE_OFF      = 0;
    private static final int                  NOTE_ON       = 1;
    private static final int                  NOTE_ON_NEW   = 2;

    protected int                             numTracks;
    protected int                             numScenes;
    protected int                             numSends;

    protected TrackBank                       trackBank;
    protected ITrack []                       tracks;
    private ISceneBank                        sceneBankProxy;

    protected final ValueChanger              valueChanger;
    private final Set<NoteObserver>           noteObservers = new HashSet<> ();
    private final Set<TrackSelectionObserver> observers     = new HashSet<> ();
    private final int [] []                   noteCache;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param numTracks The number of tracks of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param numSends The number of sends of a bank page
     */
    public AbstractTrackBankProxy (final ValueChanger valueChanger, final int numTracks, final int numScenes, final int numSends)
    {
        this.valueChanger = valueChanger;

        this.numTracks = numTracks;
        this.numScenes = numScenes;
        this.numSends = numSends;

        this.noteCache = new int [numTracks] [];
        for (int i = 0; i < numTracks; i++)
        {
            this.noteCache[i] = new int [128];
            Arrays.fill (this.noteCache[i], NOTE_OFF);
        }
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
            final Track t = this.trackBank.getChannel (i);
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
            this.sceneBankProxy = new SceneBankProxy (this.trackBank.sceneBank (), this.numScenes);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        for (final ITrack track: this.tracks)
        {
            track.enableObservers (enable);
            this.trackBank.getChannel (track.getIndex ()).playingNotes ().setIsSubscribed (enable);
        }

        this.trackBank.channelCount ().setIsSubscribed (enable);
        this.trackBank.scrollPosition ().setIsSubscribed (enable);
        this.trackBank.canScrollChannelsUp ().setIsSubscribed (enable);
        this.trackBank.canScrollChannelsDown ().setIsSubscribed (enable);

        this.sceneBankProxy.enableObservers (enable);
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
    public boolean canScrollScenesUp ()
    {
        return this.sceneBankProxy.canScrollScenesUp ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollScenesDown ()
    {
        return this.sceneBankProxy.canScrollScenesDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void addTrackSelectionObserver (final TrackSelectionObserver observer)
    {
        this.observers.add (observer);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isClipRecording ()
    {
        for (int t = 0; t < this.numTracks; t++)
        {
            final ISlot [] slots = this.tracks[t].getSlots ();
            for (int s = 0; s < this.numScenes; s++)
            {
                if (slots[s].isRecording ())
                    return true;
            }
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public ITrack getTrack (final int index)
    {
        return this.tracks[index];
    }


    /**
     * /** {@inheritDoc}
     */
    @Override
    public ITrack getSelectedTrack ()
    {
        for (int i = 0; i < this.numTracks; i++)
        {
            if (this.tracks[i].isSelected ())
                return this.tracks[i];
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedTrackColorEntry ()
    {
        final ITrack selectedTrack = this.getSelectedTrack ();
        if (selectedTrack == null)
            return BitwigColors.COLOR_OFF;
        final double [] color = selectedTrack.getColor ();
        return BitwigColors.getColorIndex (color[0], color[1], color[2]);
    }


    /** {@inheritDoc} */
    @Override
    public void select (final int index)
    {
        final Track t = this.trackBank.getChannel (index);
        if (t != null)
            this.trackBank.cursorIndex ().set (index);
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate (final int index)
    {
        final Track t = this.trackBank.getChannel (index);
        if (t != null)
            t.duplicate ();
    }


    /** {@inheritDoc} */
    @Override
    public void makeVisible (final int index)
    {
        final Track t = this.trackBank.getChannel (index);
        if (t == null)
            return;
        t.makeVisibleInArranger ();
        t.makeVisibleInMixer ();
    }


    /** {@inheritDoc} */
    @Override
    public void changeVolume (final int index, final int control)
    {
        this.trackBank.getChannel (index).volume ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setVolume (final int index, final double value)
    {
        this.trackBank.getChannel (index).volume ().set (Double.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void resetVolume (final int index)
    {
        this.trackBank.getChannel (index).volume ().reset ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchVolume (final int index, final boolean isBeingTouched)
    {
        this.trackBank.getChannel (index).volume ().touch (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setVolumeIndication (final int index, final boolean indicate)
    {
        this.trackBank.getChannel (index).volume ().setIndication (indicate);
    }


    /** {@inheritDoc} */
    @Override
    public void changePan (final int index, final int control)
    {
        this.trackBank.getChannel (index).pan ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setPan (final int index, final double value)
    {
        this.trackBank.getChannel (index).pan ().set (Double.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void resetPan (final int index)
    {
        this.trackBank.getChannel (index).pan ().reset ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchPan (final int index, final boolean isBeingTouched)
    {
        this.trackBank.getChannel (index).pan ().touch (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setPanIndication (final int index, final boolean indicate)
    {
        this.trackBank.getChannel (index).pan ().setIndication (indicate);
    }


    /** {@inheritDoc} */
    @Override
    public void setIsActivated (final int index, final boolean value)
    {
        this.trackBank.getChannel (index).isActivated ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIsActivated (final int index)
    {
        this.trackBank.getChannel (index).isActivated ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setMute (final int index, final boolean value)
    {
        this.trackBank.getChannel (index).mute ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMute (final int index)
    {
        this.setMute (index, !this.getTrack (index).isMute ());
    }


    /** {@inheritDoc} */
    @Override
    public void setSolo (final int index, final boolean value)
    {
        this.trackBank.getChannel (index).solo ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleSolo (final int index)
    {
        this.setSolo (index, !this.getTrack (index).isSolo ());
    }


    /** {@inheritDoc} */
    @Override
    public void setArm (final int index, final boolean value)
    {
        this.trackBank.getChannel (index).arm ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleArm (final int index)
    {
        this.setArm (index, !this.getTrack (index).isRecArm ());
    }


    /** {@inheritDoc} */
    @Override
    public void setMonitor (final int index, final boolean value)
    {
        this.trackBank.getChannel (index).monitor ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMonitor (final int index)
    {
        this.trackBank.getChannel (index).monitor ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setAutoMonitor (final int index, final boolean value)
    {
        this.trackBank.getChannel (index).autoMonitor ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleAutoMonitor (final int index)
    {
        this.trackBank.getChannel (index).autoMonitor ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void changeCrossfadeModeAsNumber (final int index, final int control)
    {
        this.setCrossfadeModeAsNumber (index, this.valueChanger.changeValue (control, this.getCrossfadeModeAsNumber (index), 1, 3));
    }


    /** {@inheritDoc} */
    @Override
    public String getCrossfadeMode (final int index)
    {
        return this.tracks[index].getCrossfadeMode ();
    }


    /** {@inheritDoc} */
    @Override
    public void setCrossfadeMode (final int index, final String mode)
    {
        this.trackBank.getChannel (index).crossFadeMode ().set (mode);
    }


    /** {@inheritDoc} */
    @Override
    public int getCrossfadeModeAsNumber (final int index)
    {
        switch (this.getCrossfadeMode (index))
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
    public void setCrossfadeModeAsNumber (final int index, final int modeValue)
    {
        this.setCrossfadeMode (index, modeValue == 0 ? "A" : modeValue == 1 ? "AB" : "B");
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCrossfadeMode (final int index)
    {
        switch (this.getCrossfadeMode (index))
        {
            case "A":
                this.setCrossfadeMode (index, "B");
                break;
            case "B":
                this.setCrossfadeMode (index, "AB");
                break;
            case "AB":
                this.setCrossfadeMode (index, "A");
                break;
            default:
                // Not possible
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setTrackColor (final int index, final double red, final double green, final double blue)
    {
        this.trackBank.getChannel (index).color ().set ((float) red, (float) green, (float) blue);
    }


    /** {@inheritDoc} */
    @Override
    public double [] getTrackColorEntry (final int index)
    {
        return this.getTrack (index).getColor ();
    }


    /** {@inheritDoc} */
    @Override
    public void stop (final int index)
    {
        this.trackBank.getChannel (index).stop ();
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        this.sceneBankProxy.stop ();
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank getSceneBank ()
    {
        return this.sceneBankProxy;
    }


    /** {@inheritDoc} */
    @Override
    public String getColorOfFirstClipInScene (final int scene)
    {
        for (int t = 0; t < this.getNumTracks (); t++)
        {
            final ISlot slotData = this.getTrack (t).getSlots ()[scene];
            if (slotData.doesExist () && slotData.hasContent ())
                return BitwigColors.getColorIndex (slotData.getColor ());
        }

        return BitwigColors.BITWIG_COLOR_GREEN;
    }


    /** {@inheritDoc} */
    @Override
    public void launchScene (final int scene)
    {
        this.sceneBankProxy.launchScene (scene);
    }


    /** {@inheritDoc} */
    @Override
    public void returnToArrangement (final int index)
    {
        this.trackBank.getChannel (index).returnToArrangement ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTracksUp ()
    {
        this.trackBank.scrollChannelsUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTracksDown ()
    {
        this.trackBank.scrollChannelsDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTracksPageUp ()
    {
        this.trackBank.scrollChannelsPageUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTracksPageDown ()
    {
        this.trackBank.scrollChannelsPageDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollToChannel (final int channel)
    {
        if (channel >= 0 && channel < this.getTrackCount ())
            this.trackBank.scrollToChannel (channel / this.numTracks * this.numTracks);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesUp ()
    {
        this.sceneBankProxy.scrollScenesUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesDown ()
    {
        this.sceneBankProxy.scrollScenesDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesPageUp ()
    {
        this.sceneBankProxy.scrollScenesPageUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesPageDown ()
    {
        this.sceneBankProxy.scrollScenesPageDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollToScene (final int position)
    {
        this.trackBank.sceneBank ().scrollPosition ().set (position);
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
    public void selectClip (final int trackIndex, final int slotIndex)
    {
        this.getClipLauncherSlots (trackIndex).select (slotIndex);
    }


    /** {@inheritDoc} */
    @Override
    public void launchClip (final int trackIndex, final int slotIndex)
    {
        this.getClipLauncherSlots (trackIndex).launch (slotIndex);
    }


    /** {@inheritDoc} */
    @Override
    public void recordClip (final int trackIndex, final int slotIndex)
    {
        this.getClipLauncherSlots (trackIndex).record (slotIndex);
    }


    /** {@inheritDoc} */
    @Override
    public void createClip (final int trackIndex, final int slotIndex, final int length)
    {
        this.getClipLauncherSlots (trackIndex).createEmptyClip (slotIndex, length);
    }


    /** {@inheritDoc} */
    @Override
    public void deleteClip (final int trackIndex, final int slotIndex)
    {
        this.getClipLauncherSlots (trackIndex).deleteClip (slotIndex);
    }


    /** {@inheritDoc} */
    @Override
    public void duplicateClip (final int trackIndex, final int slotIndex)
    {
        this.getClipLauncherSlots (trackIndex).duplicateClip (slotIndex);
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertClip (final int trackIndex, final int slotIndex)
    {
        this.getClipLauncherSlots (trackIndex).getItemAt (slotIndex).browseToInsertClip ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollClipPageForwards (final int trackIndex)
    {
        this.getClipLauncherSlots (trackIndex).scrollPageForwards ();
    }


    /**
     * Get the clip launcher slots of a track.
     *
     * @param index The index of the track
     * @return The clip launcher slots
     */
    private ClipLauncherSlotBank getClipLauncherSlots (final int index)
    {
        return this.trackBank.getChannel (index).clipLauncherSlotBank ();
    }


    /** {@inheritDoc} */
    @Override
    public ISlot [] getSelectedSlots (final int index)
    {
        final ITrack track = this.getTrack (index);
        final List<ISlot> selection = new ArrayList<> ();
        final ISlot [] slots = track.getSlots ();
        for (final ISlot slot: slots)
        {
            if (slot.isSelected ())
                selection.add (slot);
        }
        return selection.toArray (new SlotImpl [selection.size ()]);
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getSelectedSlot (final int index)
    {
        for (final ISlot slot: this.getTrack (index).getSlots ())
        {
            if (slot.isSelected ())
                return slot;
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getEmptySlot (final int index, final int startFrom)
    {
        final int start = startFrom >= 0 ? startFrom : 0;
        final ITrack track = this.getTrack (index);
        final ISlot [] slots = track.getSlots ();
        for (int i = 0; i < slots.length; i++)
        {
            final int pos = (start + i) % slots.length;
            if (!slots[pos].hasContent ())
                return slots[pos];
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public int getScenePosition ()
    {
        return this.sceneBankProxy.getScrollPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public void addNoteObserver (final NoteObserver observer)
    {
        this.noteObservers.add (observer);
    }


    /**
     * Notify all registered note observers.
     *
     * @param note The note which is playing or stopped
     * @param velocity The velocity of the note, note is stopped if 0
     */
    protected void notifyNoteObservers (final int note, final int velocity)
    {
        for (final NoteObserver noteObserver: this.noteObservers)
            noteObserver.call (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public int getNumTracks ()
    {
        return this.numTracks;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumScenes ()
    {
        return this.numScenes;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumSends ()
    {
        return this.numSends;
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
            trackData[i] = new TrackImpl (this.trackBank.getChannel (i), this.valueChanger.getUpperBound (), i, this.numSends, this.numScenes);
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

        for (final TrackSelectionObserver observer: this.observers)
            observer.call (index, isSelected);
    }
}