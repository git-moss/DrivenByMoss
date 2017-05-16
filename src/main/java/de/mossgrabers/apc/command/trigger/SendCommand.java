// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc.command.trigger;

import de.mossgrabers.apc.APCConfiguration;
import de.mossgrabers.apc.controller.APCControlSurface;
import de.mossgrabers.apc.mode.Modes;
import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;


/**
 * Select a send mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendCommand extends AbstractTriggerCommand<APCControlSurface, APCConfiguration>
{
    private int sendIndex;


    /**
     * Constructor.
     *
     * @param sendIndex The channel index
     * @param model The model
     * @param surface The surface
     */
    public SendCommand (final int sendIndex, final Model model, final APCControlSurface surface)
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

        this.surface.getModeManager ().setActiveMode (Integer.valueOf (Modes.MODE_SEND1.intValue () + index));
        this.surface.getDisplay ().notify ("Send " + (index + 1));
    }
}
