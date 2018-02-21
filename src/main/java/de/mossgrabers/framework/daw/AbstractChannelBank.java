// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * An abstract channel bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractChannelBank implements IChannelBank
{
    protected static final int                  NOTE_OFF      = 0;
    protected static final int                  NOTE_ON       = 1;
    protected static final int                  NOTE_ON_NEW   = 2;

    protected int                               numTracks;
    protected int                               numScenes;
    protected int                               numSends;

    protected ITrack []                         tracks;
    protected ISceneBank                        sceneBank;

    protected final ValueChanger                valueChanger;
    protected final Set<NoteObserver>           noteObservers = new HashSet<> ();
    protected final Set<TrackSelectionObserver> observers     = new HashSet<> ();
    protected final int [] []                   noteCache;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param numTracks The number of tracks of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param numSends The number of sends of a bank page
     */
    public AbstractChannelBank (final ValueChanger valueChanger, final int numTracks, final int numScenes, final int numSends)
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
            if (slots == null)
                continue;
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


    /** {@inheritDoc} */
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
    public String getColorOfFirstClipInScene (final int scene)
    {
        for (int t = 0; t < this.getNumTracks (); t++)
        {
            final ISlot [] slots = this.getTrack (t).getSlots ();
            if (slots == null)
                continue;
            final ISlot slotData = slots[scene];
            if (slotData.doesExist () && slotData.hasContent ())
                return BitwigColors.getColorIndex (slotData.getColor ());
        }

        return BitwigColors.BITWIG_COLOR_GREEN;
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        this.sceneBank.stop ();
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank getSceneBank ()
    {
        return this.sceneBank;
    }


    /** {@inheritDoc} */
    @Override
    public void launchScene (final int scene)
    {
        this.sceneBank.launchScene (scene);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollScenesUp ()
    {
        return this.sceneBank != null ? this.sceneBank.canScrollScenesUp () : false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollScenesDown ()
    {
        return this.sceneBank != null ? this.sceneBank.canScrollScenesDown () : false;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesUp ()
    {
        this.sceneBank.scrollScenesUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesDown ()
    {
        this.sceneBank.scrollScenesDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesPageUp ()
    {
        this.sceneBank.scrollScenesPageUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesPageDown ()
    {
        this.sceneBank.scrollScenesPageDown ();
    }


    /** {@inheritDoc} */
    @Override
    public int getScenePosition ()
    {
        return this.sceneBank.getScrollPosition ();
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
}
