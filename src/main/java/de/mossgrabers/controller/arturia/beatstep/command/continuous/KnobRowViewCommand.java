// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.command.continuous;

import de.mossgrabers.controller.arturia.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.controller.arturia.beatstep.view.BeatstepView;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IView;


/**
 * Command to delegate the moves of a knob to a view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KnobRowViewCommand extends AbstractContinuousCommand<BeatstepControlSurface, BeatstepConfiguration>
{
    private final int index;


    /**
     * Constructor.
     *
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public KnobRowViewCommand (final int index, final IModel model, final BeatstepControlSurface surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final IView v = this.surface.getViewManager ().getActive ();
        if (v == null)
            return;
        // Ignore centered messages of Beatstep Pro, which are sent before each real knob movement
        if (value != 64)
            ((BeatstepView) v).onKnob (this.index, value, value < 64);
    }
}
