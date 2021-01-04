// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.command.trigger;

import de.mossgrabers.controller.fire.FireConfiguration;
import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.controller.fire.view.SessionView;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to toggle between the Session orientation (flip). Additional, toggles to Birdseye view
 * when used with Shift button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionSelectCommand extends ViewMultiSelectCommand<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SessionSelectCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface, true, Views.SESSION, Views.MIX);
    }


    /** {@inheritDoc}} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final IView view = this.surface.getViewManager ().get (Views.SESSION);
        if (view instanceof SessionView)
            ((SessionView) view).toggleBirdsEyeView ();
    }
}
