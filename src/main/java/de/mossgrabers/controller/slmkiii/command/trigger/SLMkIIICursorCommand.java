// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.command.trigger;

import de.mossgrabers.controller.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColors;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.slmkiii.mode.device.ParametersMode;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;


/**
 * Command to use the Track Left and Track right buttons as cursor left/right buttons.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLMkIIICursorCommand extends CursorCommand<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public SLMkIIICursorCommand (Direction direction, IModel model, SLMkIIIControlSurface surface)
    {
        super (direction, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected int getButtonOnColor ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();

        if (Modes.isTrackMode (modeManager.getActiveModeId ()))
            return SLMkIIIColors.SLMKIII_GREEN_HALF;

        if (modeManager.isActiveMode (Modes.MODE_DEVICE_PARAMS))
        {
            if (((ParametersMode) modeManager.getMode (Modes.MODE_DEVICE_PARAMS)).isShowDevices ())
                return SLMkIIIColors.SLMKIII_MINT_HALF;
            return SLMkIIIColors.SLMKIII_PURPLE_HALF;
        }

        return super.getButtonOnColor ();
    }
}
