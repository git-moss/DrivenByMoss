// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.command.trigger;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.controller.intuitiveinstruments.exquis.mode.ExquisParameterMode;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for selecting either editing cursor device parameters and toggle between parameters 1..4
 * and 5..8.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisDeviceParameterModeSelectCommand extends ModeSelectCommand<ExquisControlSurface, ExquisConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ExquisDeviceParameterModeSelectCommand (final IModel model, final ExquisControlSurface surface)
    {
        super (model, surface, Modes.DEVICE_PARAMS);

        this.notify = false;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ExquisParameterMode mode = (ExquisParameterMode) this.modeManager.get (Modes.DEVICE_PARAMS);

        final boolean isAlreadyActive = this.modeManager.isActive (Modes.DEVICE_PARAMS);
        if (isAlreadyActive)
            mode.toggleParameters ();
        else
            super.executeNormal (event);

        final String message = "Cursor Device Parameters " + (mode.are1To4Bound () ? "1 - 4" : "5 - 8");
        this.surface.getDisplay ().notify (message);
    }
}
