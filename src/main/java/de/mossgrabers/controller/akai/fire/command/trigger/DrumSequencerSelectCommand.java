// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.command.trigger;

import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.INoteEditor;
import de.mossgrabers.framework.mode.INoteEditorMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to toggle between Drum 4 and Drum Sequencer. Additional, tap tempo when used with Shift
 * button.
 *
 * @author Jürgen Moßgraber
 */
public class DrumSequencerSelectCommand extends AbstractFireViewMultiSelectCommand
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public DrumSequencerSelectCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface, Views.DRUM, Views.DRUM4, Views.DRUM64);
    }


    /**
     * Get the color index for the activation state of the views.
     * 
     * @return The color index
     */
    public int getViewActivationColor ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        if (viewManager.isActive (Views.DRUM))
            return 1;
        if (viewManager.isActive (Views.DRUM4))
            return 2;
        if (viewManager.isActive (Views.DRUM64))
            return 3;
        return 0;
    }


    /** {@inheritDoc}} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        this.getNoteEditor ().clearNotes ();

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


    private INoteEditor getNoteEditor ()
    {
        return ((INoteEditorMode) this.surface.getModeManager ().get (Modes.NOTE)).getNoteEditor ();
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
