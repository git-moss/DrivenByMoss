// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.command.trigger;

import de.mossgrabers.controller.maschine.mikro.mk3.MaschineMikroMk3Configuration;
import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.controller.maschine.mikro.mk3.mode.Modes;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to edit the Pan and Sends of tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VolumePanSendCommand extends AbstractTriggerCommand<MaschineMikroMk3ControlSurface, MaschineMikroMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public VolumePanSendCommand (final IModel model, final MaschineMikroMk3ControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final Integer nextMode = this.getNextMode ();
        this.surface.getModeManager ().setActiveMode (nextMode);
        if (nextMode == Modes.MODE_VOLUME)
            this.surface.getDisplay ().notify ("Volume");
        else if (nextMode == Modes.MODE_PAN)
            this.surface.getDisplay ().notify ("Pan");
        else
        {
            final int sendIndex = nextMode.intValue () - Modes.MODE_SEND1.intValue ();
            String message = "Send " + (sendIndex + 1);
            final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
            if (selectedTrack != null)
                message += ": " + selectedTrack.getSendBank ().getItem (sendIndex).getName ();
            this.surface.getDisplay ().notify (message);
        }
    }


    private Integer getNextMode ()
    {
        final Integer currentMode = this.surface.getModeManager ().getActiveOrTempModeId ();

        // Outside of Volume, Pan, Send range?
        if (currentMode == null || currentMode.intValue () > Modes.MODE_SEND8.intValue ())
            return Modes.MODE_VOLUME;

        // Move up, again if outside range return Volume
        final int newMode = currentMode.intValue () + 1;
        if (newMode > Modes.MODE_SEND8.intValue ())
            return Modes.MODE_VOLUME;

        // If not a send return as well
        if (newMode < Modes.MODE_SEND1.intValue ())
            return Integer.valueOf (newMode);

        // No sends for Send channels
        if (this.model.isEffectTrackBankActive ())
            return Modes.MODE_VOLUME;

        // Check if Send channel exists
        return this.model.getTrackBank ().canEditSend (newMode - Modes.MODE_SEND1.intValue ()) ? Integer.valueOf (newMode) : Modes.MODE_VOLUME;
    }
}
