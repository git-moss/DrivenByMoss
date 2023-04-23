// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.command.trigger;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.INoteMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to toggle between Sequencer and Poly-Sequencer. Additional, toggles Accent when used with
 * Shift button.
 *
 * @author Jürgen Moßgraber
 */
public class StepSequencerSelectCommand extends AbstractFireViewMultiSelectCommand
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public StepSequencerSelectCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface, Views.SEQUENCER, Views.POLY_SEQUENCER);
    }


    /** {@inheritDoc}} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();

        // Toggle note mode
        if (this.surface.isPressed (ButtonID.ALT))
        {
            if (modeManager.isActive (Modes.NOTE))
                modeManager.restore ();
            else
                modeManager.setActive (Modes.NOTE);
            this.surface.getDisplay ().notify ("Edit Notes: " + (modeManager.isActive (Modes.NOTE) ? "On" : "Off"));
            ((INoteMode) modeManager.get (Modes.NOTE)).clearNotes ();
            return;
        }

        ((INoteMode) modeManager.get (Modes.NOTE)).clearNotes ();

        final ITrack cursorTrack = this.model.getCursorTrack ();
        final boolean doesExist = cursorTrack.doesExist ();
        final int position = cursorTrack.getPosition ();

        final ViewManager viewManager = this.surface.getViewManager ();
        if (viewManager.isActive (Views.SESSION, Views.MIX) && doesExist)
        {
            final Views preferredView = viewManager.getPreferredView (position);
            if (preferredView != null && this.viewIds.contains (preferredView))
            {
                viewManager.setActive (preferredView);
                return;
            }
        }

        super.executeNormal (event);

        if (doesExist)
            viewManager.setPreferredView (position, viewManager.getActiveID ());
    }


    /** {@inheritDoc}} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final FireConfiguration configuration = this.surface.getConfiguration ();
        configuration.setAccentEnabled (!configuration.isAccentActive ());
    }
}
