// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.command.trigger;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.controller.akai.fire.mode.FireNoteMode;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The button of the SELECT knob.
 *
 * @author Jürgen Moßgraber
 */
public class FireSelectButtonCommand extends AbstractTriggerCommand<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public FireSelectButtonCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (velocity > 0)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();

        if (modeManager.isActive (Modes.NOTE))
        {
            ((FireNoteMode) modeManager.get (Modes.NOTE)).resetTranspose ();
            return;
        }

        if (modeManager.isActive (Modes.BROWSER))
        {
            ((FireBrowserCommand) this.surface.getButton (ButtonID.BROWSE).getCommand ()).discardBrowser (true);
            return;
        }

        final FireConfiguration configuration = this.surface.getConfiguration ();
        if (modeManager.isActive (Modes.DEVICE_PARAMS))
        {
            if (configuration.isDeleteModeActive ())
            {
                this.model.getCursorDevice ().remove ();
                configuration.toggleDeleteModeActive ();
                return;
            }

            if (this.surface.isPressed (ButtonID.ALT) && this.surface.isPressed (ButtonID.SHIFT))
            {
                this.model.getCursorDevice ().toggleEnabledState ();
                return;
            }
        }

        if (this.surface.isPressed (ButtonID.ALT))
            this.model.getCursorDevice ().toggleWindowOpen ();
        else
        {
            configuration.toggleControlLastParam ();
            this.mvHelper.delayDisplay ( () -> "Last Param: " + (configuration.isControlLastParam () ? "ON" : "OFF"));
        }
    }
}
