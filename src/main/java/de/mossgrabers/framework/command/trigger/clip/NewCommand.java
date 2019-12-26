// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.clip;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISlotBank;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to create a new clip on the current track, start it and activate overdub.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
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


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.handleExecute (true);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.handleExecute (false);
    }


    private void handleExecute (final boolean enableOverdub)
    {
        final ITrack track = this.model.getSelectedTrack ();
        if (track == null)
        {
            this.surface.getDisplay ().notify ("Please select an Instrument track first.");
            return;
        }

        final ISlotBank slotBank = track.getSlotBank ();
        final ISlot selectedSlot = slotBank.getSelectedItem ();
        final int slotIndex = selectedSlot == null ? 0 : selectedSlot.getIndex ();
        final ISlot slot = slotBank.getEmptySlot (slotIndex);
        if (slot == null)
        {
            this.surface.getDisplay ().notify ("No empty slot in the current page. Please scroll down.");
            return;
        }

        final int lengthInBeats = this.getNewClipLenghthInBeats (this.model.getTransport ().getQuartersPerMeasure ());
        this.model.createNoteClip (track, slot, lengthInBeats, enableOverdub);
    }


    private int getNewClipLenghthInBeats (final int quartersPerMeasure)
    {
        final int length = this.getClipLength ();
        return (int) (length < 2 ? Math.pow (2, length) : Math.pow (2, length - 2.0) * quartersPerMeasure);
    }


    protected int getClipLength ()
    {
        return this.surface.getConfiguration ().getNewClipLength ();
    }
}
