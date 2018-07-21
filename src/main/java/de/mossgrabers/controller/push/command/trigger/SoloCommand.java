// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.PushConfiguration.TrackState;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the Solo button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SoloCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SoloCommand (final IModel model, final PushControlSurface surface)
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
            config.setTrackState (TrackState.SOLO);
            return;
        }

        // Toggle solo lock mode
        if (this.surface.isShiftPressed ())
        {
            if (event == ButtonEvent.UP)
                config.setMuteSoloLocked (!config.isMuteSoloLocked ());
            return;
        }

        // Behaviour like Push 1
        if (config.isMuteSoloLocked ())
        {
            config.setTrackState (TrackState.SOLO);
            return;
        }

        if (event == ButtonEvent.DOWN)
        {
            config.setIsSoloLongPressed (false);
            return;
        }

        if (event == ButtonEvent.LONG)
        {
            config.setIsSoloLongPressed (true);
            config.setTrackState (TrackState.SOLO);
            return;
        }

        if (config.isSoloLongPressed ())
        {
            config.setIsSoloLongPressed (false);
            return;
        }

        final Integer activeModeId = this.surface.getModeManager ().getActiveOrTempModeId ();
        if (Modes.isTrackMode (activeModeId))
        {
            final ITrack selTrack = this.model.getSelectedTrack ();
            if (selTrack != null)
                selTrack.toggleSolo ();
        }
        else if (Modes.isLayerMode (activeModeId))
        {
            final ICursorDevice cd = this.model.getCursorDevice ();
            final IChannel layer = cd.getSelectedLayerOrDrumPad ();
            if (layer != null)
                cd.toggleLayerOrDrumPadSolo (layer.getIndex ());
        }
        else if (activeModeId == Modes.MODE_MASTER)
            this.model.getMasterTrack ().toggleSolo ();
    }
}
