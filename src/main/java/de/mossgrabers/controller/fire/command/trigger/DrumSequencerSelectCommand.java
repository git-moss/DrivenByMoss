// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.command.trigger;

import de.mossgrabers.controller.fire.FireConfiguration;
import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to toggle between Drum 4 and Drum Sequencer. Additional, tap tempo when used with Shift
 * button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumSequencerSelectCommand extends ViewMultiSelectCommand<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public DrumSequencerSelectCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface, true, Views.DRUM4, Views.DRUM64);
    }


    /** {@inheritDoc}} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        // Use button up as the trigger to allow to keep the button pressed for button combinations
        if (event != ButtonEvent.UP)
            return;

        super.executeNormal (ButtonEvent.DOWN);

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
        final ITransport transport = this.model.getTransport ();
        transport.tapTempo ();
        this.mvHelper.delayDisplay ( () -> String.format ("Tempo: %.02f", Double.valueOf (transport.getTempo ())));
    }
}
