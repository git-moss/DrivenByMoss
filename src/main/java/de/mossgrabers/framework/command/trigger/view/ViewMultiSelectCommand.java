// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Selects the next view from a list. If the last element is reached it wraps around to the first.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class ViewMultiSelectCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    protected final List<Views> viewIds = new ArrayList<> ();
    private final boolean       displayName;
    private final ButtonEvent   triggerEvent;
    private boolean             storePreferred;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param viewIds The list with IDs of the views to select
     */
    public ViewMultiSelectCommand (final IModel model, final S surface, final Views... viewIds)
    {
        this (model, surface, true, ButtonEvent.DOWN, viewIds);
    }


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param storePreferred If true the selected views are stored as the favorite view of the
     *            currently selected track
     * @param viewIds The list with IDs of the views to select
     */
    public ViewMultiSelectCommand (final IModel model, final S surface, final boolean storePreferred, final Views... viewIds)
    {
        this (model, surface, true, ButtonEvent.DOWN, storePreferred, viewIds);
    }


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param displayName Displays a pop-up with the views name if true
     * @param triggerEvent The event to trigger this command
     * @param viewIds The list with IDs of the views to select
     */
    public ViewMultiSelectCommand (final IModel model, final S surface, final boolean displayName, final ButtonEvent triggerEvent, final Views... viewIds)
    {
        this (model, surface, displayName, triggerEvent, false, viewIds);
    }


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param displayName Displays a pop-up with the views name if true
     * @param triggerEvent The event to trigger this command
     * @param storePreferred If true the selected views are stored as the favorite view of the
     *            currently selected track
     * @param viewIds The list with IDs of the views to select
     */
    public ViewMultiSelectCommand (final IModel model, final S surface, final boolean displayName, final ButtonEvent triggerEvent, final boolean storePreferred, final Views... viewIds)
    {
        super (model, surface);

        this.displayName = displayName;
        this.triggerEvent = triggerEvent;
        this.storePreferred = storePreferred;
        this.viewIds.addAll (Arrays.asList (viewIds));
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != this.triggerEvent)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        final Views activeViewId = viewManager.getActiveID ();
        int index = this.viewIds.indexOf (activeViewId) + 1;
        if (index < 0 || index >= this.viewIds.size ())
            index = 0;
        final Views viewId = this.viewIds.get (index);
        if (viewManager.isActive (viewId))
            return;
        if (this.storePreferred)
            this.activatePreferredView (viewId);
        else
            viewManager.setActive (viewId);
        if (this.displayName)
            this.surface.getDisplay ().notify (viewManager.get (viewId).getName ());
    }
}
