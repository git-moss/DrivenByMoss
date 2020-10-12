// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.bitwig.framework.daw.ApplicationImpl;
import de.mossgrabers.bitwig.framework.daw.data.bank.SlotBankImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.constants.RecordQuantization;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.observer.INoteObserver;

import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.PlayingNote;
import com.bitwig.extension.controller.api.Track;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * The data of a track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackImpl extends ChannelImpl implements ITrack
{
    protected static final int       NOTE_OFF      = 0;
    protected static final int       NOTE_ON       = 1;
    protected static final int       NOTE_ON_NEW   = 2;

    protected final Track            track;

    private final BooleanValue       isTopGroup;
    private final ApplicationImpl    application;
    private final ISlotBank          slotBank;
    private final int []             noteCache     = new int [128];
    private final Set<INoteObserver> noteObservers = new HashSet<> ();
    private final CursorTrack        cursorTrack;
    private final IHost              host;


    private enum CrossfadeSetting
    {
        A,
        AB,
        B
    }


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The valueChanger
     * @param application The application
     * @param cursorTrack The cursor track of the bank to which this track belongs, required for
     *            group navigation
     * @param rootGroup The root track
     * @param track The track
     * @param index The index of the track in the page
     * @param numSends The number of sends of a bank
     * @param numScenes The number of scenes of a bank
     */
    public TrackImpl (final IHost host, final IValueChanger valueChanger, final ApplicationImpl application, final CursorTrack cursorTrack, final Track rootGroup, final Track track, final int index, final int numSends, final int numScenes)
    {
        super (null, host, valueChanger, track, index, numSends);

        this.host = host;
        this.cursorTrack = cursorTrack;
        this.track = track;
        this.application = application;

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
        track.playingNotes ().addValueObserver (this::handleNotes);

        this.isTopGroup = track.createParentTrack (0, 0).createEqualsValue (rootGroup);
        this.isTopGroup.markInterested ();

        this.slotBank = new SlotBankImpl (host, valueChanger, this, track.clipLauncherSlotBank (), numScenes);

        Arrays.fill (this.noteCache, NOTE_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        Util.setIsSubscribed (this.track.trackType (), enable);
        Util.setIsSubscribed (this.track.position (), enable);
        Util.setIsSubscribed (this.track.isGroup (), enable);
        Util.setIsSubscribed (this.track.arm (), enable);
        Util.setIsSubscribed (this.track.monitor (), enable);
        Util.setIsSubscribed (this.track.autoMonitor (), enable);
        Util.setIsSubscribed (this.track.crossFadeMode (), enable);
        Util.setIsSubscribed (this.track.canHoldNoteData (), enable);
        Util.setIsSubscribed (this.track.canHoldAudioData (), enable);
        Util.setIsSubscribed (this.track.isStopped (), enable);
        Util.setIsSubscribed (this.track.playingNotes (), enable);

        this.slotBank.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void enter ()
    {
        // Only group tracks can be entered
        if (!this.isGroup ())
            return;

        // If this track is already the cursor track, enter it straight away
        if (this.isSelected ())
        {
            this.cursorTrack.selectFirstChild ();
            return;
        }

        // Make the track cursor track
        this.select ();
        // Delay the child selection a bit to ensure the track is selected
        this.host.scheduleTask (this.cursorTrack::selectFirstChild, 100);
    }


    /** {@inheritDoc} */
    @Override
    public ChannelType getType ()
    {
        final String typeID = this.track.trackType ().get ();
        return typeID.isEmpty () ? ChannelType.UNKNOWN : ChannelType.valueOf (typeID.toUpperCase ());
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
    public boolean hasParent ()
    {
        return !this.isTopGroup.get ();
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
        this.setCrossfadeModeAsNumber (this.valueChanger.changeValue (control, this.getCrossfadeModeAsNumber (), -100, 3));
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
        try
        {
            return CrossfadeSetting.valueOf (this.getCrossfadeMode ()).ordinal ();
        }
        catch (final IllegalArgumentException | NullPointerException ex)
        {
            return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setCrossfadeModeAsNumber (final int modeValue)
    {
        final CrossfadeSetting [] values = CrossfadeSetting.values ();
        if (modeValue < values.length)
            this.setCrossfadeMode (values[modeValue].name ());
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
    public ISlotBank getSlotBank ()
    {
        return this.slotBank;
    }


    /** {@inheritDoc} */
    @Override
    public void createClip (final int slotIndex, final int lengthInBeats)
    {
        this.track.createNewLauncherClip (slotIndex, lengthInBeats);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecordQuantizationNoteLength ()
    {
        return this.application.isRecordQuantizationNoteLength ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleRecordQuantizationNoteLength ()
    {
        this.application.toggleRecordQuantizationNoteLength ();
    }


    /** {@inheritDoc} */
    @Override
    public RecordQuantization getRecordQuantizationGrid ()
    {
        return this.application.getRecordQuantizationGrid ();
    }


    /** {@inheritDoc} */
    @Override
    public void setRecordQuantizationGrid (final RecordQuantization recordQuantization)
    {
        this.application.setRecordQuantizationGrid (recordQuantization);
    }


    /** {@inheritDoc} */
    @Override
    public void addEqualizerDevice ()
    {
        if (this.doesExist ())
            this.track.endOfDeviceChainInsertionPoint ().insertBitwigDevice (EqualizerDeviceImpl.ID_BITWIG_EQ_PLUS);
    }


    /**
     * Add a note observer.
     *
     * @param observer The note observer
     */
    public void addNoteObserver (final INoteObserver observer)
    {
        this.noteObservers.add (observer);
    }


    /**
     * Handles the updates on all playing notes. Translates the note array into individual note
     * observer updates of start and stopped notes.
     *
     * @param notes The currently playing notes
     */
    private void handleNotes (final PlayingNote [] notes)
    {
        synchronized (this.noteCache)
        {
            // Send the new notes
            for (final PlayingNote note: notes)
            {
                final int pitch = note.pitch ();
                this.noteCache[pitch] = NOTE_ON_NEW;
                this.notifyNoteObservers (pitch, note.velocity ());
            }
            // Send note offs
            for (int i = 0; i < this.noteCache.length; i++)
            {
                if (this.noteCache[i] == NOTE_ON_NEW)
                    this.noteCache[i] = NOTE_ON;
                else if (this.noteCache[i] == NOTE_ON)
                {
                    this.noteCache[i] = NOTE_OFF;
                    this.notifyNoteObservers (i, 0);
                }
            }
        }
    }


    /**
     * Notify all registered note observers.
     *
     * @param note The note which is playing or stopped
     * @param velocity The velocity of the note, note is stopped if 0
     */
    protected void notifyNoteObservers (final int note, final int velocity)
    {
        for (final INoteObserver noteObserver: this.noteObservers)
            noteObserver.call (this.index, note, velocity);
    }
}
