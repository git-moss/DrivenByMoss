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

import java.util.Collections;
import java.util.EnumSet;


/**
 * Command to select the session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectNoteViewCommand extends AbstractTriggerCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private final ViewMultiSelectCommand<LaunchpadControlSurface, LaunchpadConfiguration> playSelect;
    private final ViewMultiSelectCommand<LaunchpadControlSurface, LaunchpadConfiguration> seqSelect;
    private final EnumSet<Views>                                                          views = EnumSet.noneOf (Views.class);


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SelectNoteViewCommand (final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);

        this.playSelect = new ViewMultiSelectCommand<> (model, surface, true, Views.PLAY, Views.PIANO, Views.DRUM, Views.DRUM4, Views.DRUM8, Views.DRUM64);
        this.seqSelect = new ViewMultiSelectCommand<> (model, surface, true, Views.SEQUENCER, Views.RAINDROPS);
        Collections.addAll (this.views, Views.PLAY, Views.PIANO, Views.DRUM, Views.DRUM4, Views.DRUM8, Views.DRUM64, Views.SEQUENCER, Views.RAINDROPS);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        final ITrack sel = this.model.getSelectedTrack ();
        if (sel == null)
        {
            viewManager.setActiveView (Views.SESSION);
            return;
        }

        final boolean isShifted = this.surface.isShiftPressed ();
        final Views viewId = isShifted ? viewManager.getPreviousViewId () : viewManager.getActiveViewId ();

        if (this.views.contains (viewId))
        {
            if (isShifted)
                this.seqSelect.executeNormal (event);
            else
                this.playSelect.executeNormal (event);
        }
        else
        {
            final Views viewID = viewManager.getPreferredView (sel.getPosition ());
            viewManager.setActiveView (viewID == null ? Views.PLAY : viewID);
        }

        viewManager.setPreferredView (sel.getPosition (), viewManager.getActiveViewId ());
        this.surface.getDisplay ().notify (viewManager.getActiveView ().getName ());
    }
}
