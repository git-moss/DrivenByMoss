// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc.command.trigger;

import de.mossgrabers.apc.APCConfiguration;
import de.mossgrabers.apc.controller.APCControlSurface;
import de.mossgrabers.apc.view.Views;
import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.view.ViewManager;


/**
 * Command to show/hide the shift view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ShiftCommand extends AbstractTriggerCommand<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ShiftCommand (final Model model, final APCControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event == ButtonEvent.LONG)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        if (event == ButtonEvent.DOWN && !viewManager.isActiveView (Views.VIEW_SHIFT))
            viewManager.setActiveView (Views.VIEW_SHIFT);
        else if (event == ButtonEvent.UP && viewManager.isActiveView (Views.VIEW_SHIFT))
            viewManager.restoreView ();

        this.model.getValueChanger ().setSpeed (this.surface.isShiftPressed ());
    }
}
