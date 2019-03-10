// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.command.trigger;

import de.mossgrabers.controller.mcu.MCUConfiguration;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;


/**
 * Selects the next Send mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendSelectCommand extends ModeMultiSelectCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SendSelectCommand (final IModel model, final MCUControlSurface surface)
    {
        super (model, surface, Modes.MODE_SEND1, Modes.MODE_SEND2, Modes.MODE_SEND3, Modes.MODE_SEND4, Modes.MODE_SEND5, Modes.MODE_SEND6, Modes.MODE_SEND7, Modes.MODE_SEND8);
    }
}
