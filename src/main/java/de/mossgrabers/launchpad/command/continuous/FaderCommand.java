// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.command.continuous;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.launchpad.LaunchpadConfiguration;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.launchpad.view.AbstractFaderView;


/**
 * Command to delegate the moves of a fader row to the active fader view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FaderCommand extends AbstractContinuousCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private int index;


    /**
     * Constructor.
     *
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public FaderCommand (final int index, final Model model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final View view = this.surface.getViewManager ().getActiveView ();
        if (view instanceof AbstractFaderView)
            ((AbstractFaderView) view).onValueKnob (this.index, value);
    }
}
