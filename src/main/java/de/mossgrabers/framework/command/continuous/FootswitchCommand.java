// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.continuous;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IClip;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISlotBank;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for different functionalities of a foot switch.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FootswitchCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public FootswitchCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.handleViewCommand (event))
            return;

        if (event != ButtonEvent.DOWN)
            return;

        switch (this.getSetting ())
        {
            case AbstractConfiguration.FOOTSWITCH_2_STOP_ALL_CLIPS:
                this.model.getCurrentTrackBank ().stop ();
                break;

            case AbstractConfiguration.FOOTSWITCH_2_TOGGLE_CLIP_OVERDUB:
                this.model.getTransport ().toggleLauncherOverdub ();
                break;

            case AbstractConfiguration.FOOTSWITCH_2_PANEL_LAYOUT_ARRANGE:
                this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_ARRANGE);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_PANEL_LAYOUT_MIX:
                this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_MIX);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_PANEL_LAYOUT_EDIT:
                this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_EDIT);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_ADD_INSTRUMENT_TRACK:
                this.model.getApplication ().addInstrumentTrack ();
                break;

            case AbstractConfiguration.FOOTSWITCH_2_ADD_AUDIO_TRACK:
                this.model.getApplication ().addAudioTrack ();
                break;

            case AbstractConfiguration.FOOTSWITCH_2_ADD_EFFECT_TRACK:
                this.model.getApplication ().addEffectTrack ();
                break;

            case AbstractConfiguration.FOOTSWITCH_2_QUANTIZE:
                final IClip clip = this.model.getClip ();
                if (clip.doesExist ())
                    clip.quantize (this.surface.getConfiguration ().getQuantizeAmount () / 100.0);
                break;

            default:
                this.model.getHost ().error ("Unknown footswitch command called: " + this.getSetting ());
                break;
        }
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


    /**
     * Handles all view related commands.
     *
     * @param event The event
     * @return True if handled
     */
    private boolean handleViewCommand (final ButtonEvent event)
    {
        switch (this.getSetting ())
        {
            case AbstractConfiguration.FOOTSWITCH_2_TOGGLE_PLAY:
                this.surface.getButton (ButtonID.PLAY).trigger (event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_TOGGLE_RECORD:
                this.surface.getButton (ButtonID.RECORD).trigger (event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_UNDO:
                this.surface.getButton (ButtonID.UNDO).trigger (event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_TAP_TEMPO:
                this.surface.getButton (ButtonID.TAP_TEMPO).trigger (event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_NEW_BUTTON:
                this.surface.getButton (ButtonID.NEW).trigger (event);
                break;

            case AbstractConfiguration.FOOTSWITCH_2_CLIP_BASED_LOOPER:
                this.handleLooper (event);
                break;

            default:
                return false;
        }
        return true;
    }


    /**
     * Handle clip looper.
     *
     * @param event The button event
     */
    private void handleLooper (final ButtonEvent event)
    {
        final ITrack track = this.model.getSelectedTrack ();
        if (track == null)
        {
            this.surface.getDisplay ().notify ("Please select an Instrument track first.");
            return;
        }

        final ISlotBank slotBank = track.getSlotBank ();
        final ISlot selectedSlot = slotBank.getSelectedItem ();
        final ISlot slot = selectedSlot == null ? slotBank.getItem (0) : selectedSlot;
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
                this.surface.getButton (ButtonID.NEW).trigger (event);
                slot.select ();
                this.model.getTransport ().setLauncherOverdub (true);
            }
        }
        else
        {
            // Releasing it would turn off LauncherOverdub.
            this.model.getTransport ().setLauncherOverdub (false);
        }
        // Start transport if not already playing
        slot.launch ();
    }
}
