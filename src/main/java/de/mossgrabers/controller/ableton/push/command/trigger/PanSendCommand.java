// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.trigger;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


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
    public PanSendCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        final Modes currentMode = modeManager.getActiveIDIgnoreTemporary ();

        // Layer mode selection for Push 1
        Modes mode;
        final PushConfiguration config = this.surface.getConfiguration ();
        if (!config.isPush2 () && this.surface.isSelectPressed () && Modes.isLayerMode (currentMode))
        {
            if (this.model.isEffectTrackBankActive ())
            {
                // No Sends on FX tracks
                mode = Modes.DEVICE_LAYER_PAN;
            }
            else
            {
                mode = Modes.get (currentMode, 1);
                // Wrap
                if (mode.ordinal () < Modes.DEVICE_LAYER_PAN.ordinal () || mode.ordinal () > Modes.DEVICE_LAYER_SEND8.ordinal ())
                    mode = Modes.DEVICE_LAYER_PAN;
            }
            modeManager.setActive (mode);
            return;
        }

        if (this.model.isEffectTrackBankActive ())
        {
            // No Sends on FX tracks
            mode = Modes.PAN;
        }
        else
        {
            if (currentMode.ordinal () < Modes.SEND1.ordinal () || currentMode.ordinal () > Modes.SEND8.ordinal ())
                mode = Modes.SEND1;
            else
            {
                mode = Modes.get (currentMode, 1);
                if (mode.ordinal () > Modes.SEND8.ordinal ())
                    mode = Modes.PAN;
            }

            // Check if Send channel exists
            final ITrackBank tb = this.model.getTrackBank ();
            if (mode.ordinal () < Modes.SEND1.ordinal () || mode.ordinal () > Modes.SEND8.ordinal () || !tb.canEditSend (mode.ordinal () - Modes.SEND1.ordinal ()))
                mode = Modes.PAN;
        }
        modeManager.setActive (mode);
    }
}
