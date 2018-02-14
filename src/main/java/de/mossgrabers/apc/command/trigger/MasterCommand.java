// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc.command.trigger;

import de.mossgrabers.apc.APCConfiguration;
import de.mossgrabers.apc.controller.APCControlSurface;
import de.mossgrabers.apc.mode.Modes;
import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;


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
            final int mode = modeManager.getActiveModeId ().intValue ();
            if (mode >= Modes.MODE_SEND1.intValue () && mode <= Modes.MODE_SEND8.intValue ())
                modeManager.setActiveMode (Modes.MODE_PAN);
        }

        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getSelectedTrack ();
        if (track == null)
            tb.select (0);
    }
}
