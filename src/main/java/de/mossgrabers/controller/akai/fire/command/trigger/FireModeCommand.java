// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.command.trigger;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the MODE button.
 *
 * @author Jürgen Moßgraber
 */
public class FireModeCommand extends ModeMultiSelectCommand<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param modeIds The list with IDs of the modes to select
     */
    public FireModeCommand (final IModel model, final FireControlSurface surface, final Modes [] modeIds)
    {
        super (model, surface, modeIds);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isPressed (ButtonID.ALT))
        {
            if (event == ButtonEvent.UP)
                return;

            // Toggle 16 track displays in channel and mixer modes
            final ModeManager modeManager = this.surface.getModeManager ();
            switch (modeManager.getActiveID ())
            {
                case DEVICE_LAYER:
                    modeManager.setActive (Modes.DEVICE_LAYER_VOLUME);
                    break;
                case DEVICE_LAYER_VOLUME:
                    modeManager.setActive (Modes.DEVICE_LAYER);
                    break;
                case TRACK:
                    modeManager.setActive (Modes.VOLUME);
                    break;
                case VOLUME:
                    modeManager.setActive (Modes.TRACK);
                    break;
                default:
                    // Do nothing
                    break;
            }
            this.model.getHost ().showNotification (modeManager.getActive ().getName ());
            return;
        }

        super.execute (event, velocity);
    }
}
