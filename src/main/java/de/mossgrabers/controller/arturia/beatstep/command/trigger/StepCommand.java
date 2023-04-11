// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.command.trigger;

import de.mossgrabers.controller.arturia.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Use the steps buttons of the Beatstep Pro for better navigation of the views.
 *
 * @author Jürgen Moßgraber
 */
public class StepCommand extends AbstractTriggerCommand<BeatstepControlSurface, BeatstepConfiguration>
{
    private final int step;


    /**
     * Constructor.
     *
     * @param step The step index
     * @param model The model
     * @param surface The surface
     */
    public StepCommand (final int step, final IModel model, final BeatstepControlSurface surface)
    {
        super (model, surface);
        this.step = step;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        this.surface.getViewManager ().get (Views.SHIFT).onGridNote (36 + this.step, event == ButtonEvent.DOWN ? 127 : 0);
    }
}
