// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.command.trigger;

import de.mossgrabers.controller.mcu.MCUConfiguration;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mcu.mode.Modes;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;

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
    public SendSelectCommand (final IModel model, final MCUControlSurface surface)
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

        final Integer activeModeId = this.surface.getModeManager ().getActiveOrTempModeId ();
        int index = this.modeIds.indexOf (activeModeId) + 1;
        if (index < 0 || index >= this.modeIds.size ())
            index = 0;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getItem (0);
        if (tb.canEditSend (index))
            this.surface.getModeManager ().setActiveMode (this.modeIds.get (index));
        else if (track.getSendBank ().getItem (0).doesExist ())
            this.surface.getModeManager ().setActiveMode (this.modeIds.get (0));
    }
}
