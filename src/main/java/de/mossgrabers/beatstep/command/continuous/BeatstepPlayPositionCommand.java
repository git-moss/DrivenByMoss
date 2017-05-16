// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep.command.continuous;

import de.mossgrabers.beatstep.BeatstepConfiguration;
import de.mossgrabers.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.continuous.PlayPositionCommand;


/**
 * Command to change the time (play position).
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BeatstepPlayPositionCommand extends PlayPositionCommand<BeatstepControlSurface, BeatstepConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public BeatstepPlayPositionCommand (final Model model, final BeatstepControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        this.model.getTransport ().changePosition (value >= 65);
    }
}
