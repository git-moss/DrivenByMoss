// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.utils.LatestTaskExecutor;

import java.util.Optional;


/**
 * Abstract base mode for all track modes.
 *
 * @author Jürgen Moßgraber
 */
public class ClipLauncherNavigator
{
    private final IModel             model;
    private final Object             navigateLock       = new Object ();
    private final LatestTaskExecutor slotScrollExecutor = new LatestTaskExecutor ();
    private long                     lastEdit;
    private int                      targetSlot         = -1;


    /**
     * Constructor.
     *
     * @param model The model
     */
    public ClipLauncherNavigator (final IModel model)
    {
        this.model = model;
    }


    /**
     * Must be called!
     */
    public void shutdown ()
    {
        this.slotScrollExecutor.shutdown ();
    }


    /**
     * Navigate to the previous or next scene (if any).
     *
     * @param isLeft Select the previous scene if true
     */
    public void navigateScenes (final boolean isLeft)
    {
        final ISceneBank sceneBank = this.model.getSceneBank ();
        if (sceneBank == null)
            return;
        if (isLeft)
            sceneBank.selectPreviousItem ();
        else
            sceneBank.selectNextItem ();
    }


    /**
     * Navigate to the previous or next clip of the selected track (if any).
     *
     * @param isLeft Select the previous clip if true
     */
    public void navigateClips (final boolean isLeft)
    {
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (!cursorTrack.doesExist ())
            return;
        final ISlotBank slotBank = cursorTrack.getSlotBank ();
        if (isLeft)
            slotBank.selectPreviousItem ();
        else
            slotBank.selectNextItem ();
    }


    /**
     * Navigate to the previous or next track (if any). Contains complex workaround to make sure
     * that the same slot is selected a newly selected track as well.
     *
     * @param isLeft Select the previous track if true
     */
    public void navigateTracks (final boolean isLeft)
    {
        final ICursorTrack cursorTrack = this.model.getCursorTrack ();
        if (!cursorTrack.doesExist ())
        {
            final ITrack track = this.model.getTrackBank ().getItem (0);
            if (!track.doesExist ())
                return;
            track.select ();
        }

        // Move the selected slot as well
        synchronized (this.navigateLock)
        {
            final ISlotBank slotBank = cursorTrack.getSlotBank ();
            final Optional<ISlot> selectedSlot = slotBank.getSelectedItem ();

            // Are we already moving?
            if (this.targetSlot == -1)
            {
                if (selectedSlot.isPresent ())
                    this.targetSlot = selectedSlot.get ().getIndex ();
                else
                    this.targetSlot = slotBank.getItemCount () > 0 && slotBank.getItem (0).doesExist () ? 0 : -1;
            }

            if (isLeft)
                cursorTrack.selectPrevious ();
            else
                cursorTrack.selectNext ();

            this.lastEdit = System.currentTimeMillis ();
            this.slotScrollExecutor.execute (this::selectSlot);
        }
    }


    /**
     * Select a track in the current track bank page. Contains complex workaround to make sure that
     * the same slot is selected a newly selected track as well.
     * 
     * @param index The index of the track
     */
    public void selectTrack (final int index)
    {
        final ITrack track = this.model.getTrackBank ().getItem (index);
        if (!track.doesExist ())
            return;

        if (track.isSelected ())
        {
            if (track.isGroup ())
                track.toggleGroupExpanded ();
            return;
        }

        // Move the selected slot as well
        synchronized (this.navigateLock)
        {
            final ICursorTrack cursorTrack = this.model.getCursorTrack ();
            final boolean doesExist = cursorTrack.doesExist ();
            if (doesExist)
            {
                final ISlotBank slotBank = cursorTrack.getSlotBank ();
                final Optional<ISlot> selectedSlot = slotBank.getSelectedItem ();

                // Are we already moving?
                if (this.targetSlot == -1)
                {
                    if (selectedSlot.isPresent ())
                        this.targetSlot = selectedSlot.get ().getIndex ();
                    else
                        this.targetSlot = slotBank.getItemCount () > 0 && slotBank.getItem (0).doesExist () ? 0 : -1;
                }
            }

            track.select ();

            if (doesExist)
            {
                this.lastEdit = System.currentTimeMillis ();
                this.slotScrollExecutor.execute (this::selectSlot);
            }
        }
    }


    private void selectSlot ()
    {
        synchronized (this.navigateLock)
        {
            if (this.targetSlot < 0)
                return;
        }

        try
        {
            Thread.sleep (50);
        }
        catch (final InterruptedException ex)
        {
            Thread.currentThread ().interrupt ();
            return;
        }

        synchronized (this.navigateLock)
        {
            final long diff = System.currentTimeMillis () - this.lastEdit;

            // Finally done
            if (diff > 2000)
            {
                this.targetSlot = -1;
                return;
            }

            // Update the selection but do not yet clear it!
            if (diff > 200)
            {
                final ICursorTrack cursorTrack = this.model.getCursorTrack ();
                cursorTrack.getSlotBank ().getItem (this.targetSlot).select ();
            }

            this.slotScrollExecutor.execute (this::selectSlot);
        }
    }
}
