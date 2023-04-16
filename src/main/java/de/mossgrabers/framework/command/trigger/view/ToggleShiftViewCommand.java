// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.view;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.ISessionAlternative;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractShiftView;
import de.mossgrabers.framework.view.Views;


/**
 * Base command to show/hide the shift view. Furthermore, it allows button combinations in session
 * view. Additionally, toggles the knob speed. This command requires that a session and shift view
 * is registered.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class ToggleShiftViewCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    protected final ViewManager         viewManager;
    protected final ISessionAlternative sessionAlternative;
    protected final IView               shiftView;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ToggleShiftViewCommand (final IModel model, final S surface)
    {
        super (model, surface);

        this.viewManager = this.surface.getViewManager ();
        this.sessionAlternative = (ISessionAlternative) this.viewManager.get (Views.SESSION);
        this.shiftView = this.viewManager.get (Views.SHIFT);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        switch (event)
        {
            case DOWN:
                this.handleOnDown ();
                break;

            case LONG:
                // Not used
                return;

            case UP:
                this.handleUp ();
                break;
        }

        this.surface.setKnobSensitivityIsSlow (this.surface.isShiftPressed ());
    }


    private void handleOnDown ()
    {
        if (this.viewManager.isActive (Views.SHIFT))
        {
            this.viewManager.restore ();
            this.surface.setTriggerConsumed (ButtonID.SHIFT);
            return;
        }

        // Do not trigger SHIFT view on button down if the active view is a session view since we
        // need this for the alternate clip/scene commands!
        if (!(this.viewManager.getActive () instanceof ISessionAlternative))
            this.viewManager.setTemporary (Views.SHIFT);
    }


    private void handleUp ()
    {
        // If shift mode was already used, close it down otherwise leave it open for further
        // interaction
        if (this.viewManager.isActive (Views.SHIFT) && this.wasShiftViewUsed ())
        {
            this.viewManager.restore ();
            return;
        }

        if (this.viewManager.getActive () instanceof ISessionAlternative)
        {
            if (this.wasAlternateInteractionUsed ())
                this.clearAlternateInteractionUsed ();
            else
                this.viewManager.setTemporary (Views.SHIFT);
        }
    }


    /**
     * Clear the state that the alternative interaction was used.
     */
    protected void clearAlternateInteractionUsed ()
    {
        this.sessionAlternative.setAlternateInteractionUsed (false);
    }


    /**
     * Check if the alternative interaction was used.
     *
     * @return The state
     */
    protected boolean wasAlternateInteractionUsed ()
    {
        return this.sessionAlternative.wasAlternateInteractionUsed ();
    }


    /**
     * Check a function in the shift view was used.
     *
     * @return The state
     */
    protected boolean wasShiftViewUsed ()
    {
        return ((AbstractShiftView<?, ?>) this.shiftView).wasUsed ();
    }
}
