// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.command.trigger.ModeMultiSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.mcu.MCUConfiguration;
import de.mossgrabers.mcu.controller.MCUControlSurface;
import de.mossgrabers.mcu.mode.Modes;


/**
 * Command to switch to the track modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TracksCommand extends ModeMultiSelectCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public TracksCommand (final IModel model, final MCUControlSurface surface)
    {
        super (model, surface, Modes.MODE_VOLUME, Modes.MODE_TRACK);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (this.surface.isPressed (MCUControlSurface.MCU_OPTION))
        {
            if (event == ButtonEvent.DOWN)
                this.model.toggleCursorTrackPinned ();
            return;
        }

        super.execute (event);
    }
}
