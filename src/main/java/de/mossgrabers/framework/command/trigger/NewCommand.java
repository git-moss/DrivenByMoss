// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.SlotData;
import de.mossgrabers.framework.daw.data.TrackData;

import com.bitwig.extension.controller.api.ClipLauncherSlotBank;


/**
 * Command to create a new clip on the current track, start it and activate overdub.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NewCommand<S extends ControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public NewCommand (final Model model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        this.handleExecute (event, true);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        this.handleExecute (event, false);
    }


    private void handleExecute (final ButtonEvent event, final boolean enableOverdub)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData track = tb.getSelectedTrack ();
        if (track == null)
        {
            this.surface.getDisplay ().notify ("Please select an Instrument track first.", true, true);
            return;
        }

        final int trackIndex = track.getIndex ();
        final SlotData selectedSlot = tb.getSelectedSlot (trackIndex);
        final int slotIndex = selectedSlot == null ? 0 : selectedSlot.getIndex ();
        final SlotData slot = tb.getEmptySlot (trackIndex, slotIndex);
        if (slot == null)
        {
            this.surface.getDisplay ().notify ("In the current selected grid view there is no empty slot. Please scroll down.", true, true);
            return;
        }

        final int index = slot.getIndex ();
        this.createClip (trackIndex, index);
        final ClipLauncherSlotBank slots = tb.getClipLauncherSlots (trackIndex);
        if (slotIndex != index)
            slots.select (index);
        slots.launch (index);
        if (enableOverdub)
            this.model.getTransport ().setLauncherOverdub (true);
    }


    private void createClip (final int trackIndex, final int slotIndex)
    {
        final int quartersPerMeasure = this.model.getQuartersPerMeasure ();
        final int newCLipLength = this.surface.getConfiguration ().getNewClipLength ();
        final int beats = (int) (newCLipLength < 2 ? Math.pow (2, newCLipLength) : Math.pow (2, newCLipLength - 2) * quartersPerMeasure);
        this.model.getCurrentTrackBank ().getClipLauncherSlots (trackIndex).createEmptyClip (slotIndex, beats);
    }
}
