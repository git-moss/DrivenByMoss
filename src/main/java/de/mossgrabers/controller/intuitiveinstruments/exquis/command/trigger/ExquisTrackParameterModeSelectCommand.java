// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.command.trigger;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.controller.intuitiveinstruments.exquis.mode.ExquisProjectTrackParameterMode;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to select editing track parameters and can toggle between parameters 1..4 and 5..8.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisTrackParameterModeSelectCommand extends ModeSelectCommand<ExquisControlSurface, ExquisConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ExquisTrackParameterModeSelectCommand (final IModel model, final ExquisControlSurface surface)
    {
        super (model, surface, Modes.TRACK_PARAMETERS);
        this.notify = false;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            return;

        if (this.modeManager.isActive (Modes.REPEAT_NOTE))
        {
            this.modeManager.get (Modes.REPEAT_NOTE).onKnobTouch (1, true);
            return;
        }

        final ExquisProjectTrackParameterMode mode = (ExquisProjectTrackParameterMode) this.modeManager.get (Modes.TRACK_PARAMETERS);
        if (this.modeManager.isActive (Modes.TRACK_PARAMETERS))
            mode.toggleParameters ();
        else
            super.executeNormal (ButtonEvent.DOWN);

        final String message = "Track Parameters " + (mode.are1To4Bound () ? "1 - 4" : "5 - 8");
        this.surface.getDisplay ().notify (message);
    }
}
