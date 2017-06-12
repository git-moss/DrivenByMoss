// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.SendData;
import de.mossgrabers.mcu.MCUConfiguration;
import de.mossgrabers.mcu.controller.MCUControlSurface;
import de.mossgrabers.mcu.mode.Modes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Selects the next Send mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendSelectCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    private List<Integer> modeIds = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SendSelectCommand (final Model model, final MCUControlSurface surface)
    {
        super (model, surface);

        Collections.addAll (this.modeIds, Modes.MODE_SEND1, Modes.MODE_SEND2, Modes.MODE_SEND3, Modes.MODE_SEND4, Modes.MODE_SEND5, Modes.MODE_SEND6, Modes.MODE_SEND7, Modes.MODE_SEND8);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        if (this.model.isEffectTrackBankActive ())
            return;

        final Integer activeModeId = this.surface.getModeManager ().getActiveModeId ();
        int index = this.modeIds.indexOf (activeModeId) + 1;
        if (index < 0 || index >= this.modeIds.size ())
            index = 0;

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final SendData [] sends = tb.getTrack (0).getSends ();
        if (sends[index].doesExist ())
            this.surface.getModeManager ().setActiveMode (this.modeIds.get (index));
        else if (sends[0].doesExist ())
            this.surface.getModeManager ().setActiveMode (this.modeIds.get (0));
    }
}
