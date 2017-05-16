// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.launchpad.LaunchpadConfiguration;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.launchpad.view.DrumView;
import de.mossgrabers.launchpad.view.Views;


/**
 * Command to quantize the currently selected clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class QuantizeCommand extends AbstractTriggerCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public QuantizeCommand (final Model model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        // We can use any cursor clip, e.g. the one of the drum view
        final DrumView view = (DrumView) this.surface.getViewManager ().getView (Views.VIEW_DRUM);
        view.getClip ().quantize (this.surface.getConfiguration ().getQuantizeAmount () / 100.0);
    }
}
