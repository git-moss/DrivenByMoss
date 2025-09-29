// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.command.trigger;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.command.continuous.MainKnobRowModeCommand;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle pressing the main encoder button.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineEncoderPressCommand extends AbstractTriggerCommand<MaschineControlSurface, MaschineConfiguration>
{
    final MainKnobRowModeCommand mainKnobCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param mainKnobCommand
     */
    public MaschineEncoderPressCommand (final IModel model, final MaschineControlSurface surface, final MainKnobRowModeCommand mainKnobCommand)
    {
        super (model, surface);

        this.mainKnobCommand = mainKnobCommand;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.getActiveID () == Modes.BROWSER)
        {
            this.model.getBrowser ().stopBrowsing (true);
            modeManager.restore ();
        }
        else
        {
            this.mainKnobCommand.toggleControlLastParamActive ();
            this.surface.getDisplay ().notify ("Last Param: " + (this.mainKnobCommand.isControlLastParamActive () ? " ON" : "OFF"));
        }
    }
}
