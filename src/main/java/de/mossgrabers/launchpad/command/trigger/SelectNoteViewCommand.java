// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;
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
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SelectNoteViewCommand (final Model model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData sel = tb.getSelectedTrack ();
        if (sel == null)
        {
            viewManager.setActiveView (Views.VIEW_SESSION);
            return;
        }

        Integer viewID;
        if (Views.isNoteView (viewManager.getActiveViewId ()))
        {
            if (this.surface.isShiftPressed ())
                viewID = viewManager.isActiveView (Views.VIEW_SEQUENCER) ? Views.VIEW_RAINDROPS : Views.VIEW_SEQUENCER;
            else
                viewID = viewManager.isActiveView (Views.VIEW_PLAY) ? Views.VIEW_DRUM : Views.VIEW_PLAY;
        }
        else
        {
            viewID = viewManager.getPreferredView (sel.getPosition ());
            if (viewID == null)
                viewID = this.surface.isShiftPressed () ? Views.VIEW_SEQUENCER : Views.VIEW_PLAY;
        }
        viewManager.setActiveView (viewID);
        viewManager.setPreferredView (sel.getPosition (), viewID);
    }
}
