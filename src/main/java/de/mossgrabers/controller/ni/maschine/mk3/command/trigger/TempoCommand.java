// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.command.trigger;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.controller.ni.maschine.mk3.mode.EditNoteMode;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.NoteAttribute;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to edit the tempo. Switch between Transpose and Pressure in note edit mode.
 *
 * @author Jürgen Moßgraber
 */
public class TempoCommand extends ModeSelectCommand<MaschineControlSurface, MaschineConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public TempoCommand (final IModel model, final MaschineControlSurface surface)
    {
        super (model, surface, Modes.TEMPO);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final ModeManager modeManager = this.surface.getModeManager ();

        // Switch Transpose/Pressure note edit parameters
        if (modeManager.isActive (Modes.NOTE) && this.surface.getViewManager ().isActive (Views.DRUM, Views.PLAY))
        {
            if (event == ButtonEvent.DOWN)
            {
                final EditNoteMode mode = (EditNoteMode) modeManager.get (Modes.NOTE);
                final boolean isTranspose = mode.getActiveParameter () == NoteAttribute.TRANSPOSE;
                mode.selectActiveParameter (isTranspose ? NoteAttribute.PRESSURE : NoteAttribute.TRANSPOSE);
                this.surface.getDisplay ().notify (isTranspose ? "Pressure" : "Pitch");
            }
            return;
        }

        super.execute (event, velocity);
    }
}
