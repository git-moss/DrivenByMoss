// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
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
 * Command to Setup the hardware of Push 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SetupCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    private final boolean isPush2;


    /**
     * Constructor.
     *
     * @param isPush2 Whether to edit Push 1 or Push 2 hardware settings
     * @param model The model
     * @param surface The surface
     */
    public SetupCommand (final boolean isPush2, final IModel model, final PushControlSurface surface)
    {
        super (model, surface);

        this.isPush2 = isPush2;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final ModeManager modeManager = this.surface.getModeManager ();

        final Modes mode = this.getMode ();

        if (modeManager.isActive (mode))
            modeManager.restore ();
        else
            modeManager.setTemporary (mode);
    }


    private Modes getMode ()
    {
        if (this.isPush2)
            return Modes.SETUP;

        if (this.surface.isShiftPressed ())
            return Modes.CONFIGURATION;

        return Modes.USER;
    }
}
