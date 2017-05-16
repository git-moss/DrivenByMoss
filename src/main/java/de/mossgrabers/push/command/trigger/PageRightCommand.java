// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;


/**
 * Command to dive out the layer / drum pads.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PageRightCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PageRightCommand (final Model model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    public void execute (final ButtonEvent event)
    {
        final View activeView = this.surface.getViewManager ().getActiveView ();
        if (activeView instanceof AbstractSequencerView)
            ((AbstractSequencerView) activeView).onRight (event);
    }
}
