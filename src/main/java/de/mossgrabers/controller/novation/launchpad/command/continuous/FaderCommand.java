// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.command.continuous;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.novation.launchpad.view.AbstractFaderView;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IView;


/**
 * Command to delegate the moves of a fader row to the active fader view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FaderCommand extends AbstractContinuousCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private final int index;


    /**
     * Constructor.
     *
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public FaderCommand (final int index, final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final IView view = this.surface.getViewManager ().getActive ();
        if (view instanceof final AbstractFaderView faderView)
            faderView.onValueKnob (this.index, value);
    }
}
