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
            this.model.getTrackBank ().getItem (0).select ();
            return;
        }

        final Optional<ISlot> selectedSlot = cursorTrack.getSlotBank ().getSelectedItem ();
        final int slotIndex = selectedSlot.isPresent () ? selectedSlot.get ().getIndex () : -1;

        if (isLeft)
            cursorTrack.selectPrevious ();
        else
            cursorTrack.selectNext ();

        synchronized (this.navigateLock)
        {
            this.lastEdit = System.currentTimeMillis ();
        }

        this.slotScrollExecutor.execute ( () -> this.selectSlot (slotIndex));
    }


    private void selectSlot (final int slotIndex)
    {
        if (slotIndex < 0)
            return;

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
            if (System.currentTimeMillis () - this.lastEdit > 200)
            {
                final ICursorTrack cursorTrack = this.model.getCursorTrack ();
                cursorTrack.getSlotBank ().getItem (slotIndex).select ();
            }
            else
                this.slotScrollExecutor.execute ( () -> this.selectSlot (slotIndex));
        }
    }
}
