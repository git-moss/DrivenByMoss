// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.core;

import de.mossgrabers.framework.MVHelper;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Abstract base class for trigger commands.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractTriggerCommand<S extends IControlSurface<C>, C extends Configuration> implements TriggerCommand
{
    protected final IModel         model;
    protected final S              surface;
    protected final MVHelper<S, C> mvHelper;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    protected AbstractTriggerCommand (final IModel model, final S surface)
    {
        this.model = model;
        this.surface = surface;
        this.mvHelper = new MVHelper<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isShiftPressed ())
            this.executeShifted (event);
        else
            this.executeNormal (event);
    }


    /**
     * Execute normal (without shift pressed).
     *
     * @param event The button event
     */
    public void executeNormal (final ButtonEvent event)
    {
        // Intentionally empty
    }


    /**
     * Execute when shift is pressed.
     *
     * @param event The button event
     */
    public void executeShifted (final ButtonEvent event)
    {
        // Intentionally empty
    }


    /**
     * Activates the given view and stores it as the preferred view for the currently active track.
     *
     * @param viewID The ID of the view to activate and store
     */
    protected void activatePreferredView (final Views viewID)
    {
        if (viewID == null)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        if (viewManager.get (viewID) == null)
            return;

        viewManager.setActive (viewID);
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ())
            viewManager.setPreferredView (cursorTrack.getPosition (), viewID);
    }
}
