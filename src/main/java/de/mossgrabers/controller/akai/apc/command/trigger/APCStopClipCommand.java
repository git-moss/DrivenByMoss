// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.command.trigger;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.trigger.clip.StopClipCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSequencerView;


/**
 * Stop the playing clip on the given track. Return to arrangement if shifted. If a sequencer view
 * is active, selects the resolution.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCStopClipCommand extends StopClipCommand<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param index The channel index
     * @param model The model
     * @param surface The surface
     */
    public APCStopClipCommand (final int index, final IModel model, final APCControlSurface surface)
    {
        super (index, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        // Set the step resolution in sequencer modes
        final IView view = this.surface.getViewManager ().getActive ();
        if (view instanceof final AbstractSequencerView<?, ?> sequencerView)
        {
            sequencerView.setResolutionIndex (this.index);
            return;
        }

        super.executeNormal (ButtonEvent.DOWN);
    }
}
