// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apcmini.command.trigger;

import de.mossgrabers.controller.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Command to show/hide the shift view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ShiftCommand extends AbstractTriggerCommand<APCminiControlSurface, APCminiConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ShiftCommand (final IModel model, final APCminiControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        if (event == ButtonEvent.DOWN && !viewManager.isActiveView (Views.VIEW_SHIFT))
            viewManager.setActiveView (Views.VIEW_SHIFT);
        else if (event == ButtonEvent.UP && viewManager.isActiveView (Views.VIEW_SHIFT))
            viewManager.restoreView ();

        this.model.getValueChanger ().setSpeed (this.surface.isShiftPressed ());
    }
}
