// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.trigger;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.PushConfiguration.LockState;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * Command to handle the Solo button.
 *
 * @author Jürgen Moßgraber
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
    public void execute (final ButtonEvent event, final int velocity)
    {
        // Update for key combinations
        this.surface.getViewManager ().getActive ().updateNoteMapping ();

        if (this.surface.isSelectPressed ())
        {
            if (event == ButtonEvent.UP)
                this.model.getProject ().clearSolo ();
            return;
        }

        final PushConfiguration config = this.surface.getConfiguration ();
        if (!config.isPushModern ())
        {
            if (event == ButtonEvent.DOWN)
                config.setLockState (LockState.SOLO);
            return;
        }

        if (event != ButtonEvent.UP)
            return;

        // Toggle solo lock mode
        if (this.surface.isShiftPressed () || this.surface.isPressed (ButtonID.LOCK_MODE))
        {
            config.setLockState (config.getLockState () == LockState.SOLO ? LockState.OFF : LockState.SOLO);
            return;
        }

        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.MASTER))
        {
            this.model.getMasterTrack ().toggleSolo ();
            return;
        }

        // Behavior like Push 1
        if (config.getLockState () == LockState.SOLO)
            return;

        if (Modes.isLayerMode (modeManager.getActiveID ()))
        {
            final ICursorDevice cd = this.model.getCursorDevice ();
            final Optional<?> layer = cd.getLayerBank ().getSelectedItem ();
            if (layer.isPresent ())
                ((ILayer) layer.get ()).toggleSolo ();
        }
        else
            this.model.getCursorTrack ().toggleSolo ();
    }
}
