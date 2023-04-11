// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.command.trigger;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.controller.akai.apc.view.DrumView;
import de.mossgrabers.framework.command.trigger.clip.StopClipCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * Stop the playing clip on the given track. Return to arrangement if shifted. If a sequencer view
 * is active, selects the resolution.
 *
 * @author Jürgen Moßgraber
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
        if (view instanceof final DrumView drumView)
        {
            drumView.handleStopButtons (this.index);
            return;
        }
        if (view instanceof final AbstractSequencerView<?, ?> sequencerView)
        {
            sequencerView.setResolutionIndex (this.index);
            return;
        }

        super.executeNormal (ButtonEvent.DOWN);
    }


    /**
     * Get the color for the stop buttons.
     *
     * @param stopButtonID The ID of the stop button
     * @return The color index
     */
    public int getButtonColor (final ButtonID stopButtonID)
    {
        final IView view = this.surface.getViewManager ().getActive ();

        if (view instanceof final DrumView drumView)
            return drumView.getStopButtonColor (this.index);

        if (view instanceof final AbstractSequencerView<?, ?> sequencerView)
            return sequencerView.getResolutionIndex () == this.index ? 1 : 0;

        return this.surface.isPressed (stopButtonID) ? 1 : 0;
    }
}
