// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.PushConfiguration.TrackState;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;


/**
 * Command to handle the Mute button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
    public void execute (final ButtonEvent event)
    {
        // Update for key combinations
        this.surface.getViewManager ().getActiveView ().updateNoteMapping ();

        final PushConfiguration config = this.surface.getConfiguration ();
        if (!config.isPush2 ())
        {
            config.setTrackState (TrackState.MUTE);
            return;
        }

        // Toggle mute lock mode
        if (this.surface.isShiftPressed ())
        {
            if (event == ButtonEvent.UP)
                config.setMuteSoloLocked (!config.isMuteSoloLocked ());
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

        final Integer activeModeId = this.surface.getModeManager ().getActiveModeId ();
        if (Modes.isTrackMode (activeModeId))
        {
            final IChannelBank tb = this.model.getCurrentTrackBank ();
            final ITrack selTrack = tb.getSelectedTrack ();
            if (selTrack != null)
                tb.toggleMute (selTrack.getIndex ());
        }
        else if (Modes.isLayerMode (activeModeId))
        {
            final ICursorDevice cd = this.model.getCursorDevice ();
            final IChannel layer = cd.getSelectedLayerOrDrumPad ();
            if (layer != null)
                cd.toggleLayerOrDrumPadMute (layer.getIndex ());
        }
        else if (activeModeId == Modes.MODE_MASTER)
            this.model.getMasterTrack ().toggleMute ();
    }
}
