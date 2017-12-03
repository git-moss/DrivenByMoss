// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.view.ViewManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Selects the next view from a list. If the last element is reached it wraps around to the first.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ViewMultiSelectCommand<S extends ControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private List<Integer> viewIds = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param viewIds The list with IDs of the views to select
     */
    public ViewMultiSelectCommand (final Model model, final S surface, final Integer... viewIds)
    {
        super (model, surface);

        this.viewIds.addAll (Arrays.asList (viewIds));
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        final Integer activeViewId = viewManager.getActiveViewId ();
        int index = this.viewIds.indexOf (activeViewId) + 1;
        if (index < 0 || index >= this.viewIds.size ())
            index = 0;
        viewManager.setActiveView (this.viewIds.get (index));
    }
}
