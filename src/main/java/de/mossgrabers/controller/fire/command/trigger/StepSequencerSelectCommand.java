// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.command.trigger;

import de.mossgrabers.controller.fire.FireConfiguration;
import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to toggle between Sequencer and Poly Sequencer. Additional, toggles Accent when used with
 * Shift button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StepSequencerSelectCommand extends ViewMultiSelectCommand<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public StepSequencerSelectCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface, true, Views.SEQUENCER, Views.POLY_SEQUENCER);
    }


    /** {@inheritDoc}} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        super.executeNormal (event);

        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ())
        {
            final ViewManager viewManager = this.surface.getViewManager ();
            viewManager.setPreferredView (cursorTrack.getPosition (), viewManager.getActiveID ());
        }
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
