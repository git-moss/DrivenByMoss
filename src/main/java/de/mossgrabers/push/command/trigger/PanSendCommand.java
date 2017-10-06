// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.EffectTrackBankProxy;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;


/**
 * Command to edit the Pan and Sends of tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PanSendCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PanSendCommand (final Model model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();
        final ModeManager modeManager = this.surface.getModeManager ();
        final Integer currentMode = modeManager.getActiveModeId ();

        // Layer mode selection for Push 1
        Integer mode;
        final PushConfiguration config = this.surface.getConfiguration ();
        if (!config.isPush2 () && this.surface.isSelectPressed () && Modes.isLayerMode (currentMode))
        {
            if (this.model.isEffectTrackBankActive ())
            {
                // No Sends on FX tracks
                mode = Modes.MODE_DEVICE_LAYER_PAN;
            }
            else
            {
                mode = currentMode.intValue() + 1;
                // Wrap
                if (mode < Modes.MODE_DEVICE_LAYER_PAN || mode > Modes.MODE_DEVICE_LAYER_SEND6)
                    mode = Modes.MODE_DEVICE_LAYER_PAN;
                // Check if Send channel exists
                if (mode >= Modes.MODE_DEVICE_LAYER_SEND1 && mode <= Modes.MODE_DEVICE_LAYER_SEND6 && fxTrackBank != null && !fxTrackBank.getTrack (
                        mode - Modes.MODE_DEVICE_LAYER_SEND1).doesExist ())
                    mode = Modes.MODE_DEVICE_LAYER_PAN;
            }
            modeManager.setActiveMode (mode);
            return;
        }

        if (this.model.isEffectTrackBankActive ())
        {
            // No Sends on FX tracks
            mode = Modes.MODE_PAN;
        }
        else
        {
            mode = currentMode.intValue() + 1;
            // Wrap
            if (mode < Modes.MODE_PAN || mode > Modes.MODE_SEND6)
                mode = Modes.MODE_PAN;
            // Check if Send channel exists
            if (mode >= Modes.MODE_SEND1 && mode <= Modes.MODE_SEND6 && fxTrackBank != null && !fxTrackBank.getTrack (
                    mode - Modes.MODE_SEND1).doesExist ())
                mode = Modes.MODE_PAN;
        }
        modeManager.setActiveMode (mode);
    }
}
