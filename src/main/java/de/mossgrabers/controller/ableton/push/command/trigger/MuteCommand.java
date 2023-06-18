// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.trigger;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.PushConfiguration.TrackState;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * Command to handle the Mute button.
 *
 * @author Jürgen Moßgraber
 */
public class MuteCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MuteCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        // Update for key combinations
        this.surface.getViewManager ().getActive ().updateNoteMapping ();

        if (this.surface.isSelectPressed ())
        {
            if (event == ButtonEvent.UP)
                this.model.getProject ().clearMute ();
            return;
        }

        final PushConfiguration config = this.surface.getConfiguration ();
        if (!config.isPushModern ())
        {
            config.setTrackState (TrackState.MUTE);
            return;
        }

        // Toggle mute lock mode
        if (this.surface.isShiftPressed ())
        {
            if (event == ButtonEvent.UP)
            {
                if (config.isMuteSoloLocked () && config.isMuteState ())
                    config.setMuteSoloLocked (false);
                else
                {
                    config.setMuteSoloLocked (true);
                    config.setTrackState (TrackState.MUTE);
                }
            }
            return;
        }

        // Behaviour like Push 1
        if (config.isMuteSoloLocked ())
        {
            config.setTrackState (TrackState.MUTE);
            return;
        }

        if (event == ButtonEvent.DOWN)
        {
            config.setIsMuteLongPressed (false);
            return;
        }

        if (event == ButtonEvent.LONG)
        {
            config.setIsMuteLongPressed (true);
            config.setTrackState (TrackState.MUTE);
            return;
        }

        if (config.isMuteLongPressed ())
        {
            config.setIsMuteLongPressed (false);
            return;
        }

        final Modes activeModeId = this.surface.getModeManager ().getActiveID ();
        if (Modes.isLayerMode (activeModeId))
        {
            final ICursorDevice cd = this.model.getCursorDevice ();
            final Optional<?> layer = cd.getLayerBank ().getSelectedItem ();
            if (layer.isPresent ())
                ((ILayer) layer.get ()).toggleMute ();
        }
        else if (Modes.MASTER.equals (activeModeId))
            this.model.getMasterTrack ().toggleMute ();
        else
            this.model.getCursorTrack ().toggleMute ();
    }
}
