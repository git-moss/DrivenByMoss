// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.command.trigger;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for navigating to the previous mode page. Toggles metronome on shift.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineJamPageLeftCommand extends ModeCursorCommand<MaschineJamControlSurface, MaschineJamConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MaschineJamPageLeftCommand (final IModel model, final MaschineJamControlSurface surface)
    {
        super (Direction.DOWN, model, surface, false);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.surface.isShiftPressed ())
            this.model.getTransport ().toggleMetronome ();
        else
            super.execute (event, velocity);
    }
}
