// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.trigger;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the select button.
 *
 * @author Jürgen Moßgraber
 */
public class SelectCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SelectCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        // Update for key combinations
        this.surface.getViewManager ().getActive ().updateNoteMapping ();

        final ModeManager modeManager = this.surface.getModeManager ();

        // Don't do anything in browser mode
        if (modeManager.isActive (Modes.BROWSER))
            return;

        if (event == ButtonEvent.UP)
        {
            if (modeManager.isActive (Modes.TRACK_DETAILS, Modes.DEVICE_LAYER_DETAILS))
                modeManager.restore ();
            else
                modeManager.setTemporary (Modes.isLayerMode (modeManager.getActiveID ()) ? Modes.DEVICE_LAYER_DETAILS : Modes.TRACK_DETAILS);
        }
    }
}
