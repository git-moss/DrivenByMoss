// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.clip;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * Command to create a new clip on the current track, start it and activate overdub.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class NewCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public NewCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /**
     * Execute the default function.
     */
    public void execute ()
    {
        this.handleExecute (true);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.handleExecute (true);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.handleExecute (false);
    }


    /**
     * Execute the new command.
     *
     * @param enableOverdub True to enable overdub for the new clip
     */
    public void handleExecute (final boolean enableOverdub)
    {
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (!cursorTrack.doesExist ())
        {
            this.surface.getDisplay ().notify ("Please select an Instrument track first.");
            return;
        }

        final ISlotBank slotBank = cursorTrack.getSlotBank ();
        final Optional<ISlot> selectedSlot = slotBank.getSelectedItem ();
        final int slotIndex = selectedSlot.isEmpty () ? 0 : selectedSlot.get ().getIndex ();
        final Optional<ISlot> slot = slotBank.getEmptySlot (slotIndex);
        if (slot.isEmpty ())
        {
            this.surface.getDisplay ().notify ("No empty slot in the current page. Please scroll down.");
            return;
        }

        this.model.createNoteClip (cursorTrack, slot.get (), this.getClipLength (), enableOverdub);
    }


    protected int getClipLength ()
    {
        return this.surface.getConfiguration ().getNewClipLenghthInBeats (this.model.getTransport ().getQuartersPerMeasure ());
    }
}
