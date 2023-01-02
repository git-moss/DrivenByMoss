// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.view;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to relay a button event to the active view. Use in combination with a
 * {@link FeatureGroupButtonColorSupplier}.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ViewButtonCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    protected final ViewManager viewManager;
    protected ButtonID          buttonID;


    /**
     * Constructor.
     *
     * @param buttonID The button which events to relay
     * @param surface The surface
     */
    public ViewButtonCommand (final ButtonID buttonID, final S surface)
    {
        this (buttonID, null, surface);
    }


    /**
     * Constructor.
     *
     * @param buttonID The button which events to relay
     * @param model The model
     * @param surface The surface
     */
    public ViewButtonCommand (final ButtonID buttonID, final IModel model, final S surface)
    {
        super (model, surface);

        this.buttonID = buttonID;
        this.viewManager = this.surface.getViewManager ();
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final IView view = this.viewManager.getActive ();
        if (view != null)
            view.onButton (this.buttonID, event, velocity);
    }
}
