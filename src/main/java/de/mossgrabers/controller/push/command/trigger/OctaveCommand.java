// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.TransposeView;


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
    public void execute (final ButtonEvent event, final int velocity)
    {
        final IView activeView = this.surface.getViewManager ().getActive ();
        if (!(activeView instanceof TransposeView))
            return;

        if (this.isUp)
            ((TransposeView) activeView).onOctaveUp (event);
        else
            ((TransposeView) activeView).onOctaveDown (event);
    }
}
