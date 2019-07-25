// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.command.trigger;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
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
    private final ViewMultiSelectCommand<LaunchpadControlSurface, LaunchpadConfiguration> playSelect;
    private final ViewMultiSelectCommand<LaunchpadControlSurface, LaunchpadConfiguration> seqSelect;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SelectNoteViewCommand (final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);

        if (this.model.getHost ().hasDrumDevice ())
        {
            this.playSelect = new ViewMultiSelectCommand<> (model, surface, true, Views.VIEW_PLAY, Views.VIEW_PIANO, Views.VIEW_DRUM, Views.VIEW_DRUM4, Views.VIEW_DRUM8, Views.VIEW_DRUM64);
            this.seqSelect = new ViewMultiSelectCommand<> (model, surface, true, Views.VIEW_SEQUENCER, Views.VIEW_RAINDROPS);
        }
        else
        {
            this.playSelect = new ViewMultiSelectCommand<> (model, surface, true, Views.VIEW_PLAY, Views.VIEW_PIANO, Views.VIEW_DRUM64);
            this.seqSelect = this.playSelect;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        final ITrack sel = this.model.getSelectedTrack ();
        if (sel == null)
        {
            viewManager.setActiveView (Views.VIEW_SESSION);
            return;
        }

        final boolean isShifted = this.surface.isShiftPressed ();
        if (Views.isNoteView (isShifted ? viewManager.getPreviousViewId () : viewManager.getActiveViewId ()))
        {
            if (isShifted)
                this.seqSelect.executeNormal (event);
            else
                this.playSelect.executeNormal (event);
        }
        else
        {
            final Views viewID = viewManager.getPreferredView (sel.getPosition ());
            if (viewID == null)
                this.seqSelect.executeNormal (event);
            else
                viewManager.setActiveView (viewID);
        }

        viewManager.setPreferredView (sel.getPosition (), viewManager.getActiveViewId ());
        this.surface.getDisplay ().notify (viewManager.getActiveView ().getName ());
    }
}
