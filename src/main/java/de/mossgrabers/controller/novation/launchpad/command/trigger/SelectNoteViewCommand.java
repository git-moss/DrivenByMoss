// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.command.trigger;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
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

        if (viewManager.isActive (Views.CONTROL))
        {
            viewManager.restore ();
            this.surface.getDisplay ().notify (viewManager.getActive ().getName ());
            return;
        }

        Views viewID = viewManager.getActiveID ();
        if (Views.isNoteView (viewID) || Views.isSequencerView (viewID))
        {
            viewManager.setActive (Views.CONTROL);
            this.surface.getDisplay ().notify ("Note / sequencer mode selection");
            return;
        }

        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (!cursorTrack.doesExist ())
        {
            viewManager.setActive (Views.SESSION);
            this.surface.getDisplay ().notify ("Session");
            return;
        }

        viewID = viewManager.getPreferredView (cursorTrack.getPosition ());
        if (viewID == null)
            viewID = Views.PLAY;
        viewManager.setActive (viewID);
        this.surface.getDisplay ().notify (viewManager.get (viewID).getName ());
    }
}
