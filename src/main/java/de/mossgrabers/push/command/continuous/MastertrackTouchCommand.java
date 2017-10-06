// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.continuous;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;


/**
 * Command for touching the master track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MastertrackTouchCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MastertrackTouchCommand (final Model model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        final boolean isTouched = event == ButtonEvent.DOWN;

        // Avoid accidentally leaving the browser
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_BROWSER))
            return;

        if (this.surface.isDeletePressed ())
        {
            this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_DELETE);
            this.model.getMasterTrack ().resetVolume ();
            return;
        }

        final boolean isMasterMode = modeManager.isActiveMode (Modes.MODE_MASTER);
        if (isTouched && isMasterMode)
            return;

        if (isTouched)
            modeManager.setActiveMode (Modes.MODE_MASTER_TEMP);
        else if (!isMasterMode)
            modeManager.restoreMode ();
    }
}
