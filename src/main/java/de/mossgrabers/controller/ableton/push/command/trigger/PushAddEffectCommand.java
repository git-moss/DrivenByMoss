// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.trigger;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.controller.ableton.push.mode.track.AddMode;
import de.mossgrabers.controller.ableton.push.mode.track.AddTrackMode;
import de.mossgrabers.framework.command.trigger.device.AddEffectCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to trigger the Add effect. Opens the favorite menu when combined with Select button.
 *
 * @author Jürgen Moßgraber
 */
public class PushAddEffectCommand extends AddEffectCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PushAddEffectCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface, ButtonID.SHIFT, null);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isSelectPressed () && event == ButtonEvent.UP)
        {
            final ModeManager modeManager = this.surface.getModeManager ();
            final AddTrackMode mode = (AddTrackMode) modeManager.get (Modes.ADD_TRACK);
            mode.setAddMode (AddMode.DEVICE);
            modeManager.setActive (Modes.ADD_TRACK);
            this.surface.setTriggerConsumed (ButtonID.SELECT);
            return;
        }

        super.execute (event, velocity);
    }
}
