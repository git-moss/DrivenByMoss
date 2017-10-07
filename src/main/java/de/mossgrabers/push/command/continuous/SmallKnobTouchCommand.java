// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.continuous;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.BaseMode;
import de.mossgrabers.push.mode.Modes;


/**
 * Command for touching the small knob 1 and 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SmallKnobTouchCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SmallKnobTouchCommand (final Model model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        final boolean isTouched = event == ButtonEvent.DOWN;

        // Avoid accidentally leaving the browser
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_BROWSER))
            return;

        // Prevent flickering if a knob is touched accidentally while fiddling with other knobs
        final Mode activeMode = modeManager.getActiveMode ();
        if (activeMode instanceof BaseMode && ((BaseMode) activeMode).isAKnobTouched ())
            return;

        this.model.getTransport ().setTempoIndication (isTouched);
        if (isTouched)
            modeManager.setActiveMode (Modes.MODE_TRANSPORT);
        else
            modeManager.restoreMode ();
    }
}
