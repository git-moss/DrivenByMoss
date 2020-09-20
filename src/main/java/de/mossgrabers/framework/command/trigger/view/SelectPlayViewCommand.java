// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Selects the next play/sequencer view from a list. If the last element is reached it wraps around
 * to the first. Stores the new play view selection for the selected track.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectPlayViewCommand<S extends IControlSurface<C>, C extends Configuration> extends ViewMultiSelectCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param displayName Displays a popup with the views name if true
     * @param viewIds The list with IDs of the views to select
     */
    public SelectPlayViewCommand (final IModel model, final S surface, final boolean displayName, final Views... viewIds)
    {
        super (model, surface, displayName, viewIds);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        // Restore the previous play view if coming from one not on the list
        final ViewManager viewManager = this.surface.getViewManager ();
        final Views activeViewId = viewManager.getActiveViewId ();
        if (!this.viewIds.contains (activeViewId))
        {
            final ITrack selectedTrack = this.model.getSelectedTrack ();
            if (selectedTrack != null)
            {
                final Views viewID = viewManager.getPreferredView (selectedTrack.getPosition ());
                if (viewID != null)
                {
                    viewManager.setActiveView (viewID);
                    this.surface.getDisplay ().notify (viewManager.getView (viewID).getName ());
                    return;
                }
            }
        }

        super.executeNormal (event);

        final ITrack selectedTrack = this.model.getSelectedTrack ();
        if (selectedTrack == null)
            return;

        // Store the newly selected view for the current track
        viewManager.setPreferredView (selectedTrack.getPosition (), viewManager.getActiveViewId ());
    }
}
