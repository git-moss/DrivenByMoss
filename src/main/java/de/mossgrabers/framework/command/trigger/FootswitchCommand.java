// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


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
    private final NewCommand<S, C>      newCommand;
    private final RecordCommand<S, C>   recordCommand;
    private final UndoCommand<S, C>     undoCommand;
    private final TapTempoCommand<S, C> tapTempoCommand;
    private final PlayCommand<S, C>     playCommand;

    protected final int                 index;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param index The index of the footswitch
     */
    public FootswitchCommand (final IModel model, final S surface, final int index)
    {
        super (model, surface);

        this.index = index;

        this.newCommand = new NewCommand<> (model, surface);
        this.recordCommand = new RecordCommand<> (model, surface);
        this.undoCommand = new UndoCommand<> (this.model, surface);
        this.tapTempoCommand = new TapTempoCommand<> (this.model, surface);
        this.playCommand = new PlayCommand<> (this.model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.handleViewCommand (event) || event == ButtonEvent.LONG)
            return;

        final boolean isDown = event == ButtonEvent.DOWN;

        switch (this.getSetting ())
        {
            case AbstractConfiguration.FOOTSWITCH_STOP_ALL_CLIPS:
                if (isDown)
                    this.model.getCurrentTrackBank ().stop ();
                break;

            case AbstractConfiguration.FOOTSWITCH_TOGGLE_CLIP_OVERDUB:
                if (isDown)
                    this.model.getTransport ().toggleLauncherOverdub ();
                break;

            case AbstractConfiguration.FOOTSWITCH_PANEL_LAYOUT_ARRANGE:
                if (isDown)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_ARRANGE);
                break;

            case AbstractConfiguration.FOOTSWITCH_PANEL_LAYOUT_MIX:
                if (isDown)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_MIX);
                break;

            case AbstractConfiguration.FOOTSWITCH_PANEL_LAYOUT_EDIT:
                if (isDown)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_EDIT);
                break;

            case AbstractConfiguration.FOOTSWITCH_ADD_INSTRUMENT_TRACK:
                if (isDown)
                    this.model.getTrackBank ().addChannel (ChannelType.INSTRUMENT);
                break;

            case AbstractConfiguration.FOOTSWITCH_ADD_AUDIO_TRACK:
                if (isDown)
                    this.model.getTrackBank ().addChannel (ChannelType.AUDIO);
                break;

            case AbstractConfiguration.FOOTSWITCH_ADD_EFFECT_TRACK:
                if (isDown)
                    this.model.getApplication ().addEffectTrack ();
                break;

            case AbstractConfiguration.FOOTSWITCH_QUANTIZE:
                if (isDown)
                {
                    final IClip clip = this.model.getCursorClip ();
                    if (clip.doesExist ())
                        clip.quantize (this.surface.getConfiguration ().getQuantizeAmount () / 100.0);
                }
                break;

            case AbstractConfiguration.FOOTSWITCH_SUSTAIN_PEDAL:
                this.surface.getMidiInput ().sendRawMidiEvent (MidiConstants.CMD_CC, 64, isDown ? 127 : 0);
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
        return this.surface.getConfiguration ().getFootswitch (this.index);
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
            case AbstractConfiguration.FOOTSWITCH_TOGGLE_PLAY:
                this.playCommand.execute (event, 127);
                break;

            case AbstractConfiguration.FOOTSWITCH_TOGGLE_RECORD:
                this.recordCommand.execute (event, 127);
                break;

            case AbstractConfiguration.FOOTSWITCH_UNDO:
                this.undoCommand.execute (event, 127);
                break;

            case AbstractConfiguration.FOOTSWITCH_TAP_TEMPO:
                this.tapTempoCommand.execute (event, 127);
                break;

            case AbstractConfiguration.FOOTSWITCH_NEW_BUTTON:
                this.newCommand.execute (event, 127);
                break;

            case AbstractConfiguration.FOOTSWITCH_CLIP_BASED_LOOPER:
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
        if (event == ButtonEvent.LONG)
            return;

        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (!cursorTrack.doesExist ())
        {
            this.surface.getDisplay ().notify ("Please select an Instrument track first.");
            return;
        }

        final ISlotBank slotBank = cursorTrack.getSlotBank ();
        final Optional<ISlot> selectedSlot = slotBank.getSelectedItem ();
        final ISlot slot = selectedSlot.isEmpty () ? slotBank.getItem (0) : selectedSlot.get ();

        final boolean isDown = event == ButtonEvent.DOWN;
        if (isDown && !slot.hasContent ())
        {
            // If there is no clip in the selected slot, create a clip and begin record
            // mode. Releasing it ends record mode.
            this.newCommand.execute ();
            slot.select ();
        }

        this.model.getTransport ().setLauncherOverdub (isDown);

        // Start transport if not already playing
        slot.launch ();
    }
}
