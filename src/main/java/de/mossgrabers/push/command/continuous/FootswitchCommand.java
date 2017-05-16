// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.continuous;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.SlotData;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;

import com.bitwig.extension.controller.api.ClipLauncherSlotBank;


/**
 * Command for different functionalities of a foot switch.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FootswitchCommand extends AbstractContinuousCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public FootswitchCommand (final Model model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final ButtonEvent event = value == 127 ? ButtonEvent.DOWN : ButtonEvent.UP;
        switch (this.surface.getConfiguration ().getFootswitch2 ())
        {
            case PushConfiguration.FOOTSWITCH_2_TOGGLE_PLAY:
                this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_PLAY, event);
                break;

            case PushConfiguration.FOOTSWITCH_2_TOGGLE_RECORD:
                this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_RECORD, event);
                break;

            case PushConfiguration.FOOTSWITCH_2_STOP_ALL_CLIPS:
                if (event == ButtonEvent.DOWN)
                    this.model.getCurrentTrackBank ().stop ();
                break;

            case PushConfiguration.FOOTSWITCH_2_TOGGLE_CLIP_OVERDUB:
                if (event == ButtonEvent.DOWN)
                    this.model.getTransport ().toggleLauncherOverdub ();
                break;

            case PushConfiguration.FOOTSWITCH_2_UNDO:
                this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_UNDO, event);
                break;

            case PushConfiguration.FOOTSWITCH_2_TAP_TEMPO:
                this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_TAP_TEMPO, event);
                break;

            case PushConfiguration.FOOTSWITCH_2_NEW_BUTTON:
                this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_NEW, event);
                break;

            case PushConfiguration.FOOTSWITCH_2_CLIP_BASED_LOOPER:
                final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
                final TrackData track = tb.getSelectedTrack ();
                if (track == null)
                {
                    this.surface.getDisplay ().notify ("Please select an Instrument track first.", true, true);
                    return;
                }

                final SlotData selectedSlot = tb.getSelectedSlot (track.getIndex ());
                final SlotData slot = selectedSlot == null ? track.getSlots ()[0] : selectedSlot;
                final ClipLauncherSlotBank slots = tb.getClipLauncherSlots (track.getIndex ());
                if (event == ButtonEvent.DOWN)
                {
                    if (slot.hasContent ())
                    {
                        // If there is a clip in the selected slot, enable (not toggle)
                        // LauncherOverdub.
                        this.model.getTransport ().setLauncherOverdub (true);
                    }
                    else
                    {
                        // If there is no clip in the selected slot, create a clip and begin record
                        // mode. Releasing it ends record mode.
                        this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_NEW, event);
                        slots.select (slot.getIndex ());
                        this.model.getTransport ().setLauncherOverdub (true);
                    }
                }
                else
                {
                    // Releasing it would turn off LauncherOverdub.
                    this.model.getTransport ().setLauncherOverdub (false);
                }
                // Start transport if not already playing
                slots.launch (slot.getIndex ());
                break;
        }
    }
}
