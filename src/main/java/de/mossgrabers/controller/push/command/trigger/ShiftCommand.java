// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the shift button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ShiftCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ShiftCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        final boolean isUp = event == ButtonEvent.UP;
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SHIFT, isUp ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_HI);

        final ModeManager modeManager = this.surface.getModeManager ();
        final Integer cm = modeManager.getActiveOrTempModeId ();
        if (event == ButtonEvent.DOWN && Modes.MODE_SCALES.equals (cm))
            modeManager.setActiveMode (Modes.MODE_SCALE_LAYOUT);
        else if (isUp && Modes.MODE_SCALE_LAYOUT.equals (cm))
            modeManager.restoreMode ();

        this.model.getValueChanger ().setSpeed (this.surface.isShiftPressed ());
    }
}
