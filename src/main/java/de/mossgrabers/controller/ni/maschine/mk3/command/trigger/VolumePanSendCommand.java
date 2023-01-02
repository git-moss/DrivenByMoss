// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.command.trigger;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.controller.ni.maschine.mk3.mode.EditNoteMode;
import de.mossgrabers.controller.ni.maschine.mk3.mode.MaschineVolumeMode;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.NoteAttribute;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to edit the Volume, Pan and Sends of tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VolumePanSendCommand extends ModeMultiSelectCommand<MaschineControlSurface, MaschineConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public VolumePanSendCommand (final IModel model, final MaschineControlSurface surface)
    {
        super (model, surface, Modes.VOLUME, Modes.PAN, Modes.SEND1, Modes.SEND2, Modes.SEND3, Modes.SEND4, Modes.SEND5, Modes.SEND6, Modes.SEND7, Modes.SEND8);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final ModeManager modeManager = this.surface.getModeManager ();

        // Switch Gain/Velocity note edit parameters
        if (modeManager.isActive (Modes.NOTE) && this.surface.getViewManager ().isActive (Views.DRUM, Views.PLAY))
        {
            if (event == ButtonEvent.DOWN)
            {
                final EditNoteMode mode = (EditNoteMode) modeManager.get (Modes.NOTE);
                final boolean isVelocity = mode.getActiveParameter () == NoteAttribute.VELOCITY;
                mode.selectActiveParameter (isVelocity ? NoteAttribute.GAIN : NoteAttribute.VELOCITY);
                this.surface.getDisplay ().notify (isVelocity ? "Gain" : "Velocity");
            }
            return;
        }

        // Toggle VU display
        if (this.surface.isShiftPressed ())
        {
            if (event == ButtonEvent.DOWN)
            {
                this.surface.setStopConsumed ();
                ((MaschineVolumeMode) modeManager.get (Modes.VOLUME)).toggleDisplayVU ();
            }
            return;
        }

        super.execute (event, velocity);
    }
}
