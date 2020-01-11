// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to dis-/enable the metronome. Also toggles metronome ticks when Shift is pressed. Long
 * pressing activates the metronome mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushMetronomeCommand extends MetronomeCommand<PushControlSurface, PushConfiguration>
{
    private ModeManager modeManager;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PushMetronomeCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);

        this.modeManager = this.surface.getModeManager ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.LONG)
        {
            this.surface.setTriggerConsumed (ButtonID.METRONOME);
            this.modeManager.setActiveMode (Modes.TRANSPORT);
            return;
        }

        if (event == ButtonEvent.UP && this.modeManager.isActiveOrTempMode (Modes.TRANSPORT))
        {
            this.modeManager.restoreMode ();
            return;
        }

        super.executeNormal (event);
    }
}
