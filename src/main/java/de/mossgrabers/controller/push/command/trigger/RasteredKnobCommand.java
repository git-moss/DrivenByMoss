// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.controller.push.mode.device.DeviceBrowserMode;
import de.mossgrabers.framework.command.continuous.TempoCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;


/**
 * Command to change the tempo and scroll through lists in browser mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RasteredKnobCommand extends TempoCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public RasteredKnobCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveOrTempMode (Modes.MODE_BROWSER))
        {
            final DeviceBrowserMode mode = (DeviceBrowserMode) modeManager.getMode (Modes.MODE_BROWSER);
            mode.changeSelectedColumnValue (value);
            return;
        }

        this.model.getTransport ().changeTempo (value == 1);
    }
}
