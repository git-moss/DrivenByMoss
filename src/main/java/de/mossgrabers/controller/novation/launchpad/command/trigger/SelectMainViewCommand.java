// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.command.trigger;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * Command to select a view (play, (drum-) sequencers and clip views).
 *
 * @author Jürgen Moßgraber
 */
public class SelectMainViewCommand extends AbstractTriggerCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private final Set<Views> allViewIds = new HashSet<> ();


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param allViewIds The views for checking for previous views
     */
    public SelectMainViewCommand (final IModel model, final LaunchpadControlSurface surface, final Collection<Views> allViewIds)
    {
        super (model, surface);

        this.allViewIds.addAll (allViewIds);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.UP)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        final IDisplay display = this.surface.getDisplay ();

        if (viewManager.isActive (Views.CONTROL))
        {
            viewManager.restore ();
            display.notify (viewManager.getActive ().getName ());
            return;
        }

        if (this.allViewIds.contains (viewManager.getActiveID ()))
        {
            viewManager.setActive (Views.CONTROL);
            display.notify ("Note / sequencer mode selection");
            return;
        }

        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (!cursorTrack.doesExist ())
        {
            viewManager.setActive (Views.SESSION);
            display.notify ("Session");
            return;
        }

        this.surface.recallPreferredView (this.model.getCursorTrack ());
    }
}
