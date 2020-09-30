// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.command.trigger;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Select a send mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendModeCommand extends AbstractTriggerCommand<APCControlSurface, APCConfiguration>
{
    private int sendIndex;


    /**
     * Constructor.
     *
     * @param sendIndex The channel index
     * @param model The model
     * @param surface The surface
     */
    public SendModeCommand (final int sendIndex, final IModel model, final APCControlSurface surface)
    {
        super (model, surface);
        this.sendIndex = sendIndex;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        this.handleExecute (event, this.sendIndex);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        this.handleExecute (event, this.sendIndex + 3);
    }


    private void handleExecute (final ButtonEvent event, final int index)
    {
        if (event != ButtonEvent.DOWN)
            return;

        // No Sends on FX tracks
        if (this.model.isEffectTrackBankActive ())
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        modeManager.setActiveMode (Modes.get (Modes.SEND1, index));

        String modeName = "Send " + (index + 1) + ": ";
        final ITrackBank trackBank = this.model.getTrackBank ();
        ITrack selectedTrack = trackBank.getSelectedItem ();
        if (selectedTrack == null)
            selectedTrack = trackBank.getItem (0);
        if (selectedTrack != null)
            modeName += selectedTrack.getSendBank ().getItem (index).getName ();
        else
            modeName += "-";

        this.model.getHost ().showNotification (modeName);
    }
}
