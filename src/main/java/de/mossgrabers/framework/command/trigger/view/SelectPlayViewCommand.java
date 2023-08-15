// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * Selects the next play/sequencer view from a list. If the last element is reached it wraps around
 * to the first. Stores the new play view selection for the selected track.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class SelectPlayViewCommand<S extends IControlSurface<C>, C extends Configuration> extends ViewMultiSelectCommand<S, C>
{
    private final Set<Views> allViewIds = new HashSet<> ();


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param viewIds The list with IDs of the views to select
     * @param allViewIds The views for checking for previous play views
     */
    public SelectPlayViewCommand (final IModel model, final S surface, final Views [] viewIds, final Collection<Views> allViewIds)
    {
        super (model, surface, viewIds);

        this.allViewIds.addAll (allViewIds);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        // Restore the previous play view if coming from one not on the list
        final ViewManager viewManager = this.surface.getViewManager ();
        if (!this.allViewIds.contains (viewManager.getActiveID ()))
        {
            this.surface.recallPreferredView (this.model.getCursorTrack ());
            return;
        }

        super.executeNormal (event);

        // Store the newly selected view for the current track
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ())
            viewManager.setPreferredView (cursorTrack.getPosition (), viewManager.getActiveID ());
    }
}
