// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.command.trigger;

import de.mossgrabers.controller.maschine.mikro.mk3.MaschineMikroMk3Configuration;
import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;


/**
 * Command to edit the Pan and Sends of tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VolumePanSendCommand extends ModeMultiSelectCommand<MaschineMikroMk3ControlSurface, MaschineMikroMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public VolumePanSendCommand (final IModel model, final MaschineMikroMk3ControlSurface surface)
    {
        super (model, surface, Modes.MODE_VOLUME, Modes.MODE_PAN, Modes.MODE_SEND1, Modes.MODE_SEND2, Modes.MODE_SEND3, Modes.MODE_SEND4, Modes.MODE_SEND5, Modes.MODE_SEND6, Modes.MODE_SEND7, Modes.MODE_SEND8);
    }
}
