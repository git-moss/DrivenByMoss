// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.MCUConfiguration.MainDisplay;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;


/**
 * Command to switch between modes. Only notifies about the switch if it is not a Asparion device.
 *
 * @author Jürgen Moßgraber
 */
public class MCUMultiModeSwitcherCommand extends ModeMultiSelectCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param modeIds The list with IDs of the modes to select
     */
    public MCUMultiModeSwitcherCommand (final IModel model, final MCUControlSurface surface, final Modes... modeIds)
    {
        super (model, surface, modeIds);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean shouldNotify ()
    {
        return this.surface.getConfiguration ().getMainDisplayType () != MainDisplay.ASPARION;
    }
}
