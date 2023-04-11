// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.command.trigger;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for the Maschine Jam Aux button.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamAuxCommand extends ModeMultiSelectCommand<MaschineJamControlSurface, MaschineJamConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MaschineJamAuxCommand (final IModel model, final MaschineJamControlSurface surface)
    {
        super (model, surface, Modes.SEND1, Modes.SEND2, Modes.SEND3, Modes.SEND4, Modes.SEND5, Modes.SEND6, Modes.SEND7, Modes.SEND8);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        if (this.surface.isPressed (ButtonID.SELECT))
            this.model.getApplication ().addEffectTrack ();
        else
            super.executeNormal (event);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        this.model.toggleCurrentTrackBank ();
        this.mvHelper.delayDisplay ( () -> this.model.isEffectTrackBankActive () ? "Effect Tracks" : "Audio/Instrument Tracks");
    }
}
