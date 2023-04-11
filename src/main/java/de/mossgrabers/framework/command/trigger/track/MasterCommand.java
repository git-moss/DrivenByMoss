// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.track;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Selects the master track. Toggles the track bank if shifted.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class MasterCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MasterCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.model.getMasterTrack ().select ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.model.toggleCurrentTrackBank ();
        if (this.model.isEffectTrackBankActive ())
        {
            // No Sends on effect tracks
            final ModeManager modeManager = this.surface.getModeManager ();
            final int mode = modeManager.getActiveID ().ordinal ();
            if (mode >= Modes.SEND1.ordinal () && mode <= Modes.SEND8.ordinal ())
                modeManager.setActive (Modes.PAN);
        }

        // Make sure that a track is selected
        if (!this.model.getCursorTrack ().doesExist ())
            this.model.getCurrentTrackBank ().getItem (0).select ();
    }
}
