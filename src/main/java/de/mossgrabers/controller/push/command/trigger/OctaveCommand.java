// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.TransposeView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Command for the octave up/down keys.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OctaveCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    private boolean isUp;


    /**
     * Constructor.
     *
     * @param isUp True if octave up otherwise down
     * @param model The model
     * @param surface The surface
     */
    public OctaveCommand (final boolean isUp, final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
        this.isUp = isUp;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        if (viewManager.isActiveView (Views.SESSION))
        {
            if (event != ButtonEvent.DOWN)
                return;
            final ISceneBank sceneBank = this.model.getCurrentTrackBank ().getSceneBank ();
            if (this.isUp)
                sceneBank.selectPreviousPage ();
            else
                sceneBank.selectNextPage ();
            return;
        }

        final View activeView = viewManager.getActiveView ();
        if (!(activeView instanceof TransposeView))
            return;

        if (this.isUp)
            ((TransposeView) activeView).onOctaveUp (event);
        else
            ((TransposeView) activeView).onOctaveDown (event);
    }
}
