// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.continuous;

import java.util.Optional;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IFocusedParameter;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to trigger the configuration for pitchbend.
 *
 * @author Jürgen Moßgraber
 */
public class ConfigurePitchbendCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ConfigurePitchbendCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        // Reset parameters if Delete button is held

        if (event != ButtonEvent.DOWN || !this.surface.isDeletePressed ())
            return;

        this.surface.setTriggerConsumed (ButtonID.DELETE);

        IParameter parameter = null;

        switch (this.surface.getConfiguration ().getRibbonMode ())
        {
            case PushConfiguration.RIBBON_MODE_FADER:
                parameter = this.model.getCursorTrack ().getVolumeParameter ();
                break;

            case PushConfiguration.RIBBON_MODE_LAST_TOUCHED:
                final Optional<IFocusedParameter> focusedParameter = this.model.getFocusedParameter ();
                parameter = focusedParameter.isPresent () ? focusedParameter.get () : null;
                break;

            default:
                // Not used in other modes
                break;
        }

        if (parameter != null)
            parameter.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final ModeManager modeManager = this.surface.getModeManager ();
        if (!modeManager.isActive (Modes.RIBBON))
            modeManager.setTemporary (Modes.RIBBON);
    }
}
