// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep.command.trigger;

import de.mossgrabers.beatstep.BeatstepConfiguration;
import de.mossgrabers.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.beatstep.view.Views;
import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;


/**
 * Use the steps buttons of the Beatstep Pro for better navigation of the views.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StepCommand extends AbstractTriggerCommand<BeatstepControlSurface, BeatstepConfiguration>
{
    private int step;


    /**
     * Constructor.
     *
     * @param step The step index
     * @param model The model
     * @param surface The surface
     */
    public StepCommand (final int step, final Model model, final BeatstepControlSurface surface)
    {
        super (model, surface);
        this.step = step;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        this.surface.getViewManager ().getView (Views.VIEW_SHIFT).onGridNote (36 + this.step, event == ButtonEvent.DOWN ? 127 : 0);
    }
}
