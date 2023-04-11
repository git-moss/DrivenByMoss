// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.continuous;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for touching the master track.
 *
 * @author Jürgen Moßgraber
 */
public class MastertrackTouchCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MastertrackTouchCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final boolean isTouched = event != ButtonEvent.UP;

        // Avoid accidentally leaving the browser
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.BROWSER))
            return;

        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        masterTrack.touchVolume (isTouched);

        if (this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            masterTrack.resetVolume ();
            return;
        }

        final boolean isMasterMode = modeManager.isActive (Modes.MASTER);
        if (isTouched && isMasterMode)
            return;

        if (isTouched)
            modeManager.setTemporary (Modes.MASTER_TEMP);
        else if (!isMasterMode)
            modeManager.restore ();
    }
}
