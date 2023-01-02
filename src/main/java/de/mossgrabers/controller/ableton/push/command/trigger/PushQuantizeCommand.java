// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.trigger;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to quantize the currently selected clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushQuantizeCommand extends QuantizeCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PushQuantizeCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (event == ButtonEvent.LONG || event == ButtonEvent.DOWN && this.surface.isShiftPressed ())
        {
            modeManager.setTemporary (Modes.REC_ARM);
            this.surface.setTriggerConsumed (ButtonID.QUANTIZE);
            return;
        }

        if (event != ButtonEvent.UP)
            return;

        if (Modes.REC_ARM.equals (modeManager.getActiveID ()))
            modeManager.restore ();
        else
            this.quantize ();
    }
}
