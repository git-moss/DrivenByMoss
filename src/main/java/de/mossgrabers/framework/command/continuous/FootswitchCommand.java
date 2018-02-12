// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.continuous;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.data.SlotData;
import de.mossgrabers.framework.daw.data.TrackData;


/**
 * Command for different functionalities of a foot switch.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FootswitchCommand<S extends ControlSurface<C>, C extends Configuration> extends AbstractContinuousCommand<S, C> implements TriggerCommand
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public FootswitchCommand (final Model model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        this.execute (value == 127 ? ButtonEvent.DOWN : ButtonEvent.UP);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        switch (this.getSetting ())
        {
            case AbstractConfiguration.FOOTSWITCH_2_TOGGLE_PLAY:
                this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_PLAY, event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_TOGGLE_RECORD:
                this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_RECORD, event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_STOP_ALL_CLIPS:
                if (event == ButtonEvent.DOWN)
                    this.model.getCurrentTrackBank ().stop ();
                break;

            case AbstractConfiguration.FOOTSWITCH_2_TOGGLE_CLIP_OVERDUB:
                if (event == ButtonEvent.DOWN)
                    this.model.getTransport ().toggleLauncherOverdub ();
                break;

            case AbstractConfiguration.FOOTSWITCH_2_UNDO:
                this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_UNDO, event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_TAP_TEMPO:
                this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_TAP_TEMPO, event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_NEW_BUTTON:
                this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_NEW, event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_CLIP_BASED_LOOPER:
                final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
                final TrackData track = tb.getSelectedTrack ();
                if (track == null)
                {
                    this.surface.getDisplay ().notify ("Please select an Instrument track first.", true, true);
                    return;
                }

                final SlotData selectedSlot = tb.getSelectedSlot (track.getIndex ());
                final SlotData slot = selectedSlot == null ? track.getSlots ()[0] : selectedSlot;
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
                        tb.selectClip (track.getIndex (), slot.getIndex ());
                        this.model.getTransport ().setLauncherOverdub (true);
                    }
                }
                else
                {
                    // Releasing it would turn off LauncherOverdub.
                    this.model.getTransport ().setLauncherOverdub (false);
                }
                // Start transport if not already playing
                tb.launchClip (track.getIndex (), slot.getIndex ());
                break;

            case AbstractConfiguration.FOOTSWITCH_2_PANEL_LAYOUT_ARRANGE:
                if (event == ButtonEvent.DOWN)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_ARRANGE);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_PANEL_LAYOUT_MIX:
                if (event == ButtonEvent.DOWN)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_MIX);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_PANEL_LAYOUT_EDIT:
                if (event == ButtonEvent.DOWN)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_EDIT);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_ADD_INSTRUMENT_TRACK:
                if (event == ButtonEvent.DOWN)
                    this.model.getApplication ().addInstrumentTrack ();
                break;

            case AbstractConfiguration.FOOTSWITCH_2_ADD_AUDIO_TRACK:
                if (event == ButtonEvent.DOWN)
                    this.model.getApplication ().addAudioTrack ();
                break;

            case AbstractConfiguration.FOOTSWITCH_2_ADD_EFFECT_TRACK:
                if (event == ButtonEvent.DOWN)
                    this.model.getApplication ().addEffectTrack ();
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        // Intentionally empty
    }


    /**
     * Get the configuration setting.
     *
     * @return The setting
     */
    protected int getSetting ()
    {
        return this.surface.getConfiguration ().getFootswitch2 ();
    }
}
