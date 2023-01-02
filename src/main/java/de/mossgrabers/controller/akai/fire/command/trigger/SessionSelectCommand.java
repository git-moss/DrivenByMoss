// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.command.trigger;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.controller.akai.fire.view.SessionView;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to toggle between the Session orientation (flip). Additional, toggles to birdseye view
 * when used with Shift button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionSelectCommand extends AbstractTriggerCommand<FireControlSurface, FireConfiguration>
{
    private Views sessionViewID = Views.SESSION;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SessionSelectCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc}} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();

        if (viewManager.isActive (Views.SESSION))
            this.sessionViewID = Views.MIX;
        else if (viewManager.isActive (Views.MIX))
            this.sessionViewID = Views.SESSION;

        viewManager.setActive (this.sessionViewID);
        this.surface.getDisplay ().notify (viewManager.get (this.sessionViewID).getName ());
    }


    /** {@inheritDoc}} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final IView view = this.surface.getViewManager ().get (Views.SESSION);
        if (view instanceof final SessionView sessionView)
            sessionView.toggleBirdsEyeView ();
    }
}
