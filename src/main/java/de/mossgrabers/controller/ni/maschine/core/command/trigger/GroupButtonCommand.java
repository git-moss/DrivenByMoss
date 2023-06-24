// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.core.command.trigger;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;

import java.util.Optional;


/**
 * Commands execute by the Maschine Jam group A-H buttons.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class GroupButtonCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final int index;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param index The index of the button
     */
    public GroupButtonCommand (final IModel model, final S surface, final int index)
    {
        super (model, surface);

        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        // Send selection
        if (this.surface.isPressed (ButtonID.SENDS))
        {
            this.surface.setTriggerConsumed (ButtonID.SENDS);
            final Modes modeID = Modes.get (Modes.SEND1, this.index);
            final ModeManager modeManager = this.surface.getModeManager ();
            modeManager.setActive (modeID);
            final String notification = ModeMultiSelectCommand.getSendModeNotification (modeID, modeManager.getActive ().getName (), this.model.getTrackBank ());
            this.model.getHost ().showNotification (notification);
            return;
        }

        final ITrackBank trackBank = this.model.getCurrentTrackBank ();
        final ITrack track = trackBank.getItem (this.index);
        if (!track.doesExist ())
            return;

        // Solo track
        if (this.surface.isPressed (ButtonID.SOLO))
        {
            this.surface.setTriggerConsumed (ButtonID.SOLO);
            track.toggleSolo ();
            return;
        }

        // Mute track
        if (this.surface.isPressed (ButtonID.MUTE))
        {
            this.surface.setTriggerConsumed (ButtonID.MUTE);
            track.toggleMute ();
            return;
        }

        // Record arm the track
        if (this.surface.isPressed (ButtonID.RECORD))
        {
            this.surface.setTriggerConsumed (ButtonID.RECORD);
            track.toggleRecArm ();
            return;
        }

        // Delete the track
        if (this.surface.isPressed (ButtonID.DELETE))
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            track.remove ();
            return;
        }

        // Duplicate the track
        if (this.surface.isPressed (ButtonID.DUPLICATE))
        {
            this.surface.setTriggerConsumed (ButtonID.DUPLICATE);
            track.duplicate ();
            return;
        }

        // Set sequencer resolution
        if (this.surface.isPressed (ButtonID.GROOVE))
        {
            final IView active = this.surface.getViewManager ().getActive ();
            if (active instanceof final AbstractSequencerView<?, ?> sequencerView)
                sequencerView.setResolutionIndex (this.index);
            return;
        }

        // Track selection or group expansion
        track.selectOrExpandGroup ();
    }


    /**
     * Get the button color depending of the currently active button presses.
     *
     * @return The color index
     */
    public int getButtonColor ()
    {
        final ITrackBank trackBank = this.model.getCurrentTrackBank ();

        // Send selection
        if (this.surface.isPressed (ButtonID.SENDS))
        {
            Optional<ITrack> selectedTrack = trackBank.getSelectedItem ();
            if (selectedTrack.isEmpty ())
            {
                final ITrack item = trackBank.getItem (0);
                selectedTrack = item.doesExist () ? Optional.of (item) : Optional.empty ();
            }
            if (selectedTrack.isPresent ())
            {
                final ISend send = selectedTrack.get ().getSendBank ().getItem (this.index);
                if (send.doesExist ())
                    return MaschineColorManager.COLOR_WHITE;
            }

            return MaschineColorManager.COLOR_BLACK;
        }

        final ITrack track = trackBank.getItem (this.index);

        if (!track.doesExist ())
            return MaschineColorManager.COLOR_BLACK;

        if (this.surface.isPressed (ButtonID.SOLO))
            return track.isSolo () ? MaschineColorManager.COLOR_BLUE : MaschineColorManager.COLOR_DARK_GREY;

        if (this.surface.isPressed (ButtonID.MUTE))
            return track.isMute () ? MaschineColorManager.COLOR_YELLOW : MaschineColorManager.COLOR_DARK_GREY;

        if (this.surface.isPressed (ButtonID.RECORD))
            return track.isRecArm () ? MaschineColorManager.COLOR_RED : MaschineColorManager.COLOR_DARK_GREY;

        if (this.surface.isPressed (ButtonID.GROOVE))
        {
            final IView active = this.surface.getViewManager ().getActive ();
            if (active instanceof final AbstractSequencerView<?, ?> sequencerView)
                return sequencerView.getResolutionIndex () == this.index ? MaschineColorManager.COLOR_GREEN : MaschineColorManager.COLOR_DARK_GREY;
            return MaschineColorManager.COLOR_BLACK;
        }

        // Track selection
        return ((MaschineColorManager) this.model.getColorManager ()).dimOrHighlightColor (track.getColor (), track.isSelected ());
    }
}
