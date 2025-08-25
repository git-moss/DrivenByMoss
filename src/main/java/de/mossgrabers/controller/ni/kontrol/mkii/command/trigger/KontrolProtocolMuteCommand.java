// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.command.trigger;

import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolConfiguration;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocolControlSurface;
import de.mossgrabers.controller.ni.kontrol.mkii.mode.LayerMode;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * A command to toggle mute on a track or layer.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolProtocolMuteCommand extends AbstractTriggerCommand<KontrolProtocolControlSurface, KontrolProtocolConfiguration>
{
    /**
     * Constructor. Toggles the mute of the track at the given index in the page of the current
     * track bank.
     *
     * @param model The model
     * @param surface The surface
     */
    public KontrolProtocolMuteCommand (final IModel model, final KontrolProtocolControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int index)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.DEVICE_LAYER))
        {
            if (modeManager.get (Modes.DEVICE_LAYER) instanceof final LayerMode mode)
                mode.toggleMute (index);
        }
        else
            this.model.getCurrentTrackBank ().getItem (index).toggleMute ();
    }
}
