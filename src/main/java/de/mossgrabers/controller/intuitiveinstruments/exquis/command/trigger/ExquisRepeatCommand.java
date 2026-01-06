// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.command.trigger;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * The repeat button. Toggles arranger loop on/off and activates selection view on long press.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisRepeatCommand extends ToggleLoopCommand<ExquisControlSurface, ExquisConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ExquisRepeatCommand (final IModel model, final ExquisControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final ViewManager viewManager = this.surface.getViewManager ();

        if (event == ButtonEvent.DOWN && viewManager.isActive (Views.TRACK_SELECT))
        {
            this.surface.setTriggerConsumed (ButtonID.LOOP);
            viewManager.restore ();
            return;
        }

        if (event == ButtonEvent.LONG)
        {
            this.surface.setTriggerConsumed (ButtonID.LOOP);
            viewManager.setTemporary (Views.TRACK_SELECT);
            return;
        }

        super.execute (event, velocity);
    }
}
