// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.command.trigger;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.controller.ni.maschine.mk3.view.DrumView;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.INoteEditorMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command for the pad mode button which activates the drum view.
 *
 * @author Jürgen Moßgraber
 */
public class PadModeCommand extends AbstractTriggerCommand<MaschineControlSurface, MaschineConfiguration>
{
    private final KeyboardCommand keyboardCommand;


    /**
     * Constructor.
     *
     * @param keyboardCommand
     *
     * @param model The model
     * @param surface The surface
     */
    public PadModeCommand (final KeyboardCommand keyboardCommand, final IModel model, final MaschineControlSurface surface)
    {
        super (model, surface);

        this.keyboardCommand = keyboardCommand;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        if (viewManager.isActive (Views.DRUM))
        {
            if (!this.surface.getMaschine ().hasMCUDisplay ())
                ((DrumView) viewManager.get (Views.DRUM)).toggleShifted ();

            final ModeManager modeManager = this.surface.getModeManager ();
            if (modeManager.isActive (Modes.PLAY_OPTIONS))
                modeManager.restore ();
            else
                modeManager.setActive (Modes.PLAY_OPTIONS);
        }
        else
        {
            viewManager.setActive (Views.DRUM);
            ((INoteEditorMode) this.surface.getModeManager ().get (Modes.NOTE)).getNoteEditor ().clearNotes ();

            // Store the newly selected view for the current track
            final ITrack cursorTrack = this.model.getCursorTrack ();
            if (cursorTrack.doesExist ())
                viewManager.setPreferredView (cursorTrack.getPosition (), Views.DRUM);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        this.surface.setStopConsumed ();
        this.keyboardCommand.executeNormal (event);
    }
}
