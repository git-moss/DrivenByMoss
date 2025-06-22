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
 * Command for selecting either editing project or track parameters and can toggle between
 * parameters 1..4 and 5..8.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisProjectTrackParameterModeSelectCommand extends ModeSelectCommand<ExquisControlSurface, ExquisConfiguration>
{
    private final boolean triggerProject;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param triggerProject Triggers the project parameters otherwise the track parameters
     */
    public ExquisProjectTrackParameterModeSelectCommand (final IModel model, final ExquisControlSurface surface, final boolean triggerProject)
    {
        super (model, surface, Modes.PROJECT);

        this.notify = false;
        this.triggerProject = triggerProject;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ExquisProjectTrackParameterMode mode = (ExquisProjectTrackParameterMode) this.modeManager.get (Modes.PROJECT);

        // First check if the Project mode is already active, if not activate it
        boolean isAlreadyActive = this.modeManager.isActive (Modes.PROJECT);
        if (!isAlreadyActive)
            super.executeNormal (event);

        // Are the correct parameters bound?
        final boolean areProjectParametersActive = mode.areProjectParametersActive ();
        if (this.triggerProject && !areProjectParametersActive || !this.triggerProject && areProjectParametersActive)
        {
            isAlreadyActive = false;
            mode.toggleMode ();
        }

        // If the mode was already active and no parameter adjustment was necessary, toggle between
        // parameters 1..4 and 5..8
        if (isAlreadyActive)
            mode.toggleParameters ();

        final String message = (mode.areProjectParametersActive () ? "Project Parameters " : "Track Parameters ") + (mode.are1To4Bound () ? "1 - 4" : "5 - 8");
        this.surface.getDisplay ().notify (message);
    }
}
