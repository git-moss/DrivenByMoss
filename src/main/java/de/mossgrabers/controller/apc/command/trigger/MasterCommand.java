// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.command.trigger;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The master button command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MasterCommand extends AbstractTriggerCommand<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MasterCommand (final IModel model, final APCControlSurface surface)
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
            final int mode = modeManager.getActiveOrTempModeId ().intValue ();
            if (mode >= Modes.MODE_SEND1.intValue () && mode <= Modes.MODE_SEND8.intValue ())
                modeManager.setActiveMode (Modes.MODE_PAN);
        }

        if (this.model.getSelectedTrack () == null)
            this.model.getCurrentTrackBank ().getItem (0).select ();
    }
}
