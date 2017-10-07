// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.daw.data.SlotData;
import de.mossgrabers.framework.daw.data.TrackData;

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
public abstract class AbstractTrackBankProxy
{
    private static final int                  NOTE_OFF      = 0;
    private static final int                  NOTE_ON       = 1;
    private static final int                  NOTE_ON_NEW   = 2;

    protected int                             numTracks;
    protected int                             numScenes;
    protected int                             numSends;

    protected TrackBank                       trackBank;
    protected TrackData []                    tracks;
    private SceneBankProxy                    sceneBankProxy;

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
            t.addIsSelectedInEditorObserver (value -> this.handleBankTrackSelection (index, value));
            t.playingNotes ().addValueObserver (value -> this.handleNotes (index, value));
        }

        this.trackBank.channelCount ().markInterested ();
        this.trackBank.scrollPosition ().markInterested ();
        this.trackBank.canScrollChannelsUp ().markInterested ();
        this.trackBank.canScrollChannelsDown ().markInterested ();
        this.sceneBankProxy = new SceneBankProxy (this.trackBank.sceneBank (), this.numScenes);
    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
    public void enableObservers (final boolean enable)
    {
        for (final TrackData track: this.tracks)
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


    /**
     * Get the number of all tracks.
     *
     * @return The number of all tracks
     */
    public int getTrackCount ()
    {
        return this.trackBank.channelCount ().get ();
    }


    /**
     * Is there a page left of the current?
     *
     * @return True if there is a page left of the current
     */
    public boolean canScrollTracksUp ()
    {
        return this.trackBank.canScrollChannelsUp ().get ();
    }


    /**
     * Is there a page right of the current?
     *
     * @return True if there is a page right of the current
     */
    public boolean canScrollTracksDown ()
    {
        return this.trackBank.canScrollChannelsDown ().get ();
    }


    /**
     * Is there a scene page left of the current?
     *
     * @return True if there is a scene page left of the current
     */
    public boolean canScrollScenesUp ()
    {
        return this.sceneBankProxy.canScrollScenesUp ();
    }


    /**
     * Is there a scene page right of the current?
     *
     * @return True if there is a scene page right of the current
     */
    public boolean canScrollScenesDown ()
    {
        return this.sceneBankProxy.canScrollScenesDown ();
    }


    /**
     * Registers a track selection observer.
     *
     * @param observer The observer to register
     */
    public void addTrackSelectionObserver (final TrackSelectionObserver observer)
    {
        this.observers.add (observer);
    }


    /**
     * Returns true if one of the clips of the current bank page is recording.
     *
     * @return True if one of the clips of the current bank page is recording
     */
    public boolean isClipRecording ()
    {
        for (int t = 0; t < this.numTracks; t++)
        {
            final SlotData [] slots = this.tracks[t].getSlots ();
            for (int s = 0; s < this.numScenes; s++)
            {
                if (slots[s].isRecording ())
                    return true;
            }
        }
        return false;
    }


    /**
     * Get a Track value object.
     *
     * @param index The index of the track in the bank page
     * @return The track
     */
    public TrackData getTrack (final int index)
    {
        return this.tracks[index];
    }


    /**
     * Get the first selected Track value object of the current page.
     *
     * @return The selected track or null if no track is selected on the current page
     */
    public TrackData getSelectedTrack ()
    {
        for (int i = 0; i < this.numTracks; i++)
        {
            if (this.tracks[i].isSelected ())
                return this.tracks[i];
        }
        return null;
    }


    /**
     * Get the color ID of the current track.
     *
     * @return The color ID
     */
    public String getSelectedTrackColorEntry ()
    {
        final TrackData selectedTrack = this.getSelectedTrack ();
        if (selectedTrack == null)
            return BitwigColors.COLOR_OFF;
        final double [] color = selectedTrack.getColor ();
        return BitwigColors.getColorIndex (color[0], color[1], color[2]);
    }


    /**
     * Select a track in the current bank page.
     *
     * @param index The index in the page
     */
    public void select (final int index)
    {
        final Track t = this.trackBank.getChannel (index);
        if (t == null)
            return;
        t.selectInEditor ();
        t.selectInMixer ();
    }


    /**
     * Duplicate a track in the current bank page.
     *
     * @param index The index in the page
     */
    public void duplicate (final int index)
    {
        final Track t = this.trackBank.getChannel (index);
        if (t != null)
            t.duplicate ();
    }


    /**
     * Make a track in the current bank page visible (scrolls to the track in Bitwig).
     *
     * @param index The index in the page
     */
    public void makeVisible (final int index)
    {
        final Track t = this.trackBank.getChannel (index);
        if (t == null)
            return;
        t.makeVisibleInArranger ();
        t.makeVisibleInMixer ();
    }


    /**
     * Change the volume.
     *
     * @param index The index of the track
     * @param control The control value
     */
    public void changeVolume (final int index, final int control)
    {
        this.trackBank.getChannel (index).getVolume ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Set the volume.
     *
     * @param index The index of the track
     * @param value The new value
     */
    public void setVolume (final int index, final double value)
    {
        this.trackBank.getChannel (index).getVolume ().set (Double.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Set the volume.
     *
     * @param index The index of the track
     * @param value The new value
     * @param upperBound The upper bound
     */
    public void setVolume (final int index, final double value, final int upperBound)
    {
        this.trackBank.getChannel (index).getVolume ().set (Double.valueOf (value), Integer.valueOf (upperBound));
    }


    /**
     * Reset the volume to its default value.
     *
     * @param index The index of the track
     */
    public void resetVolume (final int index)
    {
        this.trackBank.getChannel (index).getVolume ().reset ();
    }


    /**
     * Signal that the volume fader/knob is touched for automation recording.
     *
     * @param index The index of the track
     * @param isBeingTouched True if touched
     */
    public void touchVolume (final int index, final boolean isBeingTouched)
    {
        this.trackBank.getChannel (index).getVolume ().touch (isBeingTouched);
    }


    /**
     * Signal that the volume is edited.
     *
     * @param index The index of the track
     * @param indicate True if edited
     */
    public void setVolumeIndication (final int index, final boolean indicate)
    {
        this.trackBank.getChannel (index).getVolume ().setIndication (indicate);
    }


    /**
     * Change the panorama.
     *
     * @param index The index of the track
     * @param control The control value
     */
    public void changePan (final int index, final int control)
    {
        this.trackBank.getChannel (index).getPan ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Set the panorama.
     *
     * @param index The index of the track
     * @param value The new value
     */
    public void setPan (final int index, final double value)
    {
        this.trackBank.getChannel (index).getPan ().set (Double.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Reset the panorama to its default value.
     *
     * @param index The index of the track
     */
    public void resetPan (final int index)
    {
        this.trackBank.getChannel (index).getPan ().reset ();
    }


    /**
     * Signal that the panorama fader/knob is touched for automation recording.
     *
     * @param index The index of the track
     * @param isBeingTouched True if touched
     */
    public void touchPan (final int index, final boolean isBeingTouched)
    {
        this.trackBank.getChannel (index).getPan ().touch (isBeingTouched);
    }


    /**
     * Signal that the panorama is edited.
     *
     * @param index The index of the track
     * @param indicate True if edited
     */
    public void setPanIndication (final int index, final boolean indicate)
    {
        this.trackBank.getChannel (index).getPan ().setIndication (indicate);
    }


    /**
     * Sets the activated state of the track.
     *
     * @param index The index of the track
     * @param value True to activate
     */
    public void setIsActivated (final int index, final boolean value)
    {
        this.trackBank.getChannel (index).isActivated ().set (value);
    }


    /**
     * Toggle the activated state of the track.
     *
     * @param index The index of the track
     */
    public void toggleIsActivated (final int index)
    {
        this.trackBank.getChannel (index).isActivated ().toggle ();
    }


    /**
     * Turn on/off mute.
     *
     * @param index The index of the track
     * @param value True to turn on mute, otherwise off
     */
    public void setMute (final int index, final boolean value)
    {
        this.trackBank.getChannel (index).getMute ().set (value);
    }


    /**
     * Toggle mute.
     *
     * @param index The index of the track
     */
    public void toggleMute (final int index)
    {
        this.setMute (index, !this.getTrack (index).isMute ());
    }


    /**
     * Turn on/off solo.
     *
     * @param index The index of the track
     * @param value True to turn on solo, otherwise off
     */
    public void setSolo (final int index, final boolean value)
    {
        this.trackBank.getChannel (index).getSolo ().set (value);
    }


    /**
     * Toggle solo.
     *
     * @param index The index of the track
     */
    public void toggleSolo (final int index)
    {
        this.setSolo (index, !this.getTrack (index).isSolo ());
    }


    /**
     * Turn on/off record arm.
     *
     * @param index The index of the track
     * @param value True to turn arm the track for recording, otherwise off
     */
    public void setArm (final int index, final boolean value)
    {
        this.trackBank.getChannel (index).getArm ().set (value);
    }


    /**
     * Toggle record arm.
     *
     * @param index The index of the track
     */
    public void toggleArm (final int index)
    {
        this.setArm (index, !this.getTrack (index).isRecArm ());
    }


    /**
     * Turn on/off track monitoring.
     *
     * @param index The index of the track
     * @param value True to turn on track monitoring, otherwise off
     */
    public void setMonitor (final int index, final boolean value)
    {
        this.trackBank.getChannel (index).getMonitor ().set (value);
    }


    /**
     * Toggle monitor.
     *
     * @param index The index of the track
     */
    public void toggleMonitor (final int index)
    {
        this.trackBank.getChannel (index).getMonitor ().toggle ();
    }


    /**
     * Turn on/off auto track monitoring.
     *
     * @param index The index of the track
     * @param value True to turn on auto track monitoring, otherwise off
     */
    public void setAutoMonitor (final int index, final boolean value)
    {
        this.trackBank.getChannel (index).getAutoMonitor ().set (value);
    }


    /**
     * Toggle auto monitor.
     *
     * @param index The index of the track
     */
    public void toggleAutoMonitor (final int index)
    {
        this.trackBank.getChannel (index).getAutoMonitor ().toggle ();
    }


    /**
     * Change the crossfade mode.
     *
     * @param index The index of the track
     * @param control The control value
     */
    public void changeCrossfadeModeAsNumber (final int index, final int control)
    {
        this.setCrossfadeModeAsNumber (index, this.valueChanger.changeValue (control, this.getCrossfadeModeAsNumber (index), 1, 3));
    }


    /**
     * Get the crossfade mode.
     *
     * @param index The index of the track
     * @return The crossfade mode A, AB or B
     */
    public String getCrossfadeMode (final int index)
    {
        return this.tracks[index].getCrossfadeMode ();
    }


    /**
     * Set the crossfade mode.
     *
     * @param index The index of the track
     * @param mode The crossfade mode A, AB or B
     */
    public void setCrossfadeMode (final int index, final String mode)
    {
        this.trackBank.getChannel (index).getCrossFadeMode ().set (mode);
    }


    /**
     * Get the crossfade mode as a number.
     *
     * @param index The index of the track
     * @return The crossfade mode 0, 1 or 2
     */
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


    /**
     * Set the crossfade mode as a number.
     *
     * @param index The index of the track
     * @param modeValue The crossfade mode 0, 1 or 2
     */
    public void setCrossfadeModeAsNumber (final int index, final int modeValue)
    {
        this.setCrossfadeMode (index, modeValue == 0 ? "A" : modeValue == 1 ? "AB" : "B");
    }


    /**
     * Set the crossfade mode to the next value.
     *
     * @param index The index of the track
     */
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


    /**
     * Set the color of a track as a RGB value.
     *
     * @param index The index of the track
     * @param red The red part of the color
     * @param green The green part of the color
     * @param blue The blue part of the color
     */
    public void setTrackColor (final int index, final double red, final double green, final double blue)
    {
        this.trackBank.getChannel (index).color ().set ((float) red, (float) green, (float) blue);
    }


    /**
     * Get the color of a track.
     *
     * @param index The index of the track
     * @return The color as RGB, 0 = red, 1 = green, 2 = blue
     */
    public double [] getTrackColorEntry (final int index)
    {
        return this.getTrack (index).getColor ();
    }


    /**
     * Stop playback on the track.
     *
     * @param index The index of the track
     */
    public void stop (final int index)
    {
        this.trackBank.getChannel (index).stop ();
    }


    /**
     * Stop all playing clips.
     */
    public void stop ()
    {
        this.sceneBankProxy.stop ();
    }


    /**
     * Get the scene bank.
     *
     * @return The scene bank.
     */
    public SceneBankProxy getSceneBank ()
    {
        return this.sceneBankProxy;
    }


    /**
     * Launch a scene.
     *
     * @param scene The index of the scene
     */
    public void launchScene (final int scene)
    {
        this.sceneBankProxy.launchScene (scene);
    }


    /**
     * Switch playback back to the arrangement.
     *
     * @param index The index of the track
     */
    public void returnToArrangement (final int index)
    {
        this.trackBank.getChannel (index).returnToArrangement ();
    }


    /**
     * Scroll up tracks by 1.
     */
    public void scrollTracksUp ()
    {
        this.trackBank.scrollChannelsUp ();
    }


    /**
     * Scroll down tracks by 1.
     */
    public void scrollTracksDown ()
    {
        this.trackBank.scrollChannelsDown ();
    }


    /**
     * Scroll up tracks by 1 page.
     */
    public void scrollTracksPageUp ()
    {
        this.trackBank.scrollChannelsPageUp ();
    }


    /**
     * Scroll down tracks by 1 page.
     */
    public void scrollTracksPageDown ()
    {
        this.trackBank.scrollChannelsPageDown ();
    }


    /**
     * Scrolls the channel bank window so that the channel at the given position becomes visible as
     * part of the window.
     *
     * @param channel The index of the channel to scroll to
     */
    public void scrollToChannel (final int channel)
    {
        if (channel >= 0 && channel < this.getTrackCount ())
        {
            final int pos = channel / this.numTracks * this.numTracks;
            this.trackBank.scrollToChannel (pos);
            // TODO Bugfix required - Call it twice to work around a Bitwig bug
            // https://github.com/teotigraphix/Framework4Bitwig/issues/103
            this.trackBank.scrollToChannel (pos);
        }
    }


    /**
     * Scroll up scenes by 1.
     */
    public void scrollScenesUp ()
    {
        this.sceneBankProxy.scrollScenesUp ();
    }


    /**
     * Scroll down scenes by 1.
     */
    public void scrollScenesDown ()
    {
        this.sceneBankProxy.scrollScenesDown ();
    }


    /**
     * Scroll up scenes by 1 page.
     */
    public void scrollScenesPageUp ()
    {
        this.sceneBankProxy.scrollScenesPageUp ();
    }


    /**
     * Scroll down scenes by 1 page.
     */
    public void scrollScenesPageDown ()
    {
        this.sceneBankProxy.scrollScenesPageDown ();
    }


    /**
     * Scroll the scenes to the given position.
     *
     * @param position The new position
     */
    public void scrollToScene (final int position)
    {
        this.trackBank.scrollToScene (position);
        // TODO Bugfix required - Call it twice to work around a Bitwig bug
        // https://github.com/teotigraphix/Framework4Bitwig/issues/103
        this.trackBank.scrollToScene (position);
    }


    /**
     * Set indication for all clips of the size of the number of tracks and scenes.
     *
     * @param enable True to enable
     */
    public void setIndication (final boolean enable)
    {
        for (int index = 0; index < this.numTracks; index++)
            this.getClipLauncherSlots (index).setIndication (enable);
    }


    /**
     * Get the clip launcher slots of a track.
     *
     * @param index The index of the track
     * @return The clip launcher slots
     */
    public ClipLauncherSlotBank getClipLauncherSlots (final int index)
    {
        return this.trackBank.getChannel (index).clipLauncherSlotBank ();
    }


    /**
     * Returns an array with the selected slots of a track.
     *
     * @param index The index of the track
     * @return The array is empty if none is selected.
     */
    public SlotData [] getSelectedSlots (final int index)
    {
        final TrackData track = this.getTrack (index);
        final List<SlotData> selection = new ArrayList<> ();
        final SlotData [] slots = track.getSlots ();
        for (final SlotData slot: slots)
        {
            if (slot.isSelected ())
                selection.add (slot);
        }
        return selection.toArray (new SlotData [selection.size ()]);
    }


    /**
     * Returns the first selected slot or null if none is selected.
     *
     * @param index The index of the track
     * @return The first selected slot or null if none is selected
     */
    public SlotData getSelectedSlot (final int index)
    {
        for (final SlotData slot: this.getTrack (index).getSlots ())
        {
            if (slot.isSelected ())
                return slot;
        }
        return null;
    }


    /**
     * Returns the first empty slot in the current clip window. If none is empty null is returned.
     * If startFrom is set the search starts from the given index (and wraps around after the last
     * one to 0).
     *
     * @param index The index of the track
     * @param startFrom At what index to start the search
     * @return The empty slot or null if none is found
     */
    public SlotData getEmptySlot (final int index, final int startFrom)
    {
        final int start = startFrom >= 0 ? startFrom : 0;
        final TrackData track = this.getTrack (index);
        final SlotData [] slots = track.getSlots ();
        for (int i = 0; i < slots.length; i++)
        {
            final int pos = (start + i) % slots.length;
            if (!slots[pos].hasContent ())
                return slots[pos];
        }
        return null;
    }


    /**
     * Show a clip ion the editor.
     *
     * @param index The index of the track
     * @param slotIndex The index of the slot
     */
    public void showClipInEditor (final int index, final int slotIndex)
    {
        final ClipLauncherSlotBank cs = this.trackBank.getChannel (index).clipLauncherSlotBank ();
        cs.select (slotIndex);
        cs.showInEditor (slotIndex);
    }


    /**
     * Get the position of the first scene of the scene banks current page.
     *
     * @return The position
     */
    public int getScenePosition ()
    {
        return this.sceneBankProxy.getScrollPosition ();
    }


    /**
     * Add a note observer.
     *
     * @param observer The note observer
     */
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


    /**
     * Get the number of tracks of a bank page.
     *
     * @return The number of tracks of a bank page
     */
    public int getNumTracks ()
    {
        return this.numTracks;
    }


    /**
     * Get the number of scenes of a bank page.
     *
     * @return The number of scenes of a bank page
     */
    public int getNumScenes ()
    {
        return this.numScenes;
    }


    /**
     * Get the number of sends of a bank page.
     *
     * @return The number of sends of a bank page
     */
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
    protected TrackData [] createTracks (final int count)
    {
        final TrackData [] trackData = new TrackData [count];
        for (int i = 0; i < count; i++)
            trackData[i] = new TrackData (this.trackBank.getChannel (i), this.valueChanger.getUpperBound (), i, this.numSends, this.numScenes);
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
        final TrackData sel = this.getSelectedTrack ();
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