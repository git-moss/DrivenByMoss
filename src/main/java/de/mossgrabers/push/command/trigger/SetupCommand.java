// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;


/**
 * Command to Setup the hardware of Push 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SetupCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    private Integer mode;


    /**
     * Constructor.
     *
     * @param isPush2 Whether to edit Push 1 or Push 2 hardware settings
     * @param model The model
     * @param surface The surface
     */
    public SetupCommand (final boolean isPush2, final Model model, final PushControlSurface surface)
    {
        super (model, surface);
        this.mode = isPush2 ? Modes.MODE_SETUP : Modes.MODE_CONFIGURATION;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (this.mode))
            modeManager.restoreMode ();
        else
            modeManager.setActiveMode (this.mode);
    }
}
