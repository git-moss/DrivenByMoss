// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.view;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to show/hide the shift view. Additionally, toggles the knob speed.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class ToggleShiftViewCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final ViewManager viewManager;
    private boolean           isCombi;


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
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        switch (event)
        {
            case DOWN:
                this.isCombi = false;
                if (this.viewManager.isActive (Views.SHIFT))
                    this.viewManager.restore ();
                else
                {
                    // Do not activate when ALT is already pressed
                    if (!this.surface.isPressed (ButtonID.ALT))
                        this.viewManager.setTemporary (Views.SHIFT);
                }
                break;

            case LONG:
                this.isCombi = true;
                return;

            case UP:
                if (this.isCombi && this.viewManager.isActive (Views.SHIFT))
                    this.viewManager.restore ();
                break;
        }

        this.surface.setKnobSensitivityIsSlow (this.surface.isShiftPressed ());
    }
}
