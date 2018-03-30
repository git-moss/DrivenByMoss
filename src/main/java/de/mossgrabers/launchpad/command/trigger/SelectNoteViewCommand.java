// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.command.trigger;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.ViewMultiSelectCommand;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.launchpad.LaunchpadConfiguration;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.launchpad.view.Views;


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

        this.playSelect = new ViewMultiSelectCommand<> (model, surface, Views.VIEW_PLAY, Views.VIEW_DRUM, Views.VIEW_DRUM4, Views.VIEW_DRUM8, Views.VIEW_DRUM64);
        this.seqSelect = new ViewMultiSelectCommand<> (model, surface, Views.VIEW_SEQUENCER, Views.VIEW_RAINDROPS);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack sel = tb.getSelectedTrack ();
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
            final Integer viewID = viewManager.getPreferredView (sel.getPosition ());
            if (viewID == null)
                this.seqSelect.executeNormal (event);
            else
                viewManager.setActiveView (viewID);
        }

        viewManager.setPreferredView (sel.getPosition (), viewManager.getActiveViewId ());
        this.surface.getDisplay ().notify (viewManager.getActiveView ().getName ());
    }
}
