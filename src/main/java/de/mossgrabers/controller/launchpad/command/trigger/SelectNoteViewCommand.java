// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.command.trigger;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Command to select the session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectNoteViewCommand extends AbstractTriggerCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SelectNoteViewCommand (final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.UP)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();

        if (viewManager.isActiveView (Views.CONTROL))
        {
            viewManager.restoreView ();
            return;
        }

        Views viewID = viewManager.getActiveViewId ();
        if (Views.isNoteView (viewID) || Views.isSequencerView (viewID))
        {
            viewManager.setActiveView (Views.CONTROL);
            return;
        }

        final ITrack sel = this.model.getSelectedTrack ();
        if (sel == null)
        {
            viewManager.setActiveView (Views.SESSION);
            return;
        }

        viewID = viewManager.getPreferredView (sel.getPosition ());
        if (viewID == null)
            viewID = Views.PLAY;
        viewManager.setActiveView (viewID);
        this.surface.getDisplay ().notify (viewManager.getView (viewID).getName ());
    }
}
