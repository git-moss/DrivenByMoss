// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.continuous;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;


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
    public SmallKnobTouchCommand (final IModel model, final PushControlSurface surface)
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
        if (modeManager.isActiveOrTempMode (Modes.MODE_BROWSER))
            return;

        // Prevent flickering if a knob is touched accidentally while fiddling with other knobs
        final Mode activeMode = modeManager.getActiveOrTempMode ();
        if (activeMode instanceof BaseMode && ((BaseMode) activeMode).isAKnobTouched ())
            return;

        this.model.getTransport ().setTempoIndication (isTouched);
        if (isTouched)
            modeManager.setActiveMode (Modes.MODE_TRANSPORT);
        else
            modeManager.restoreMode ();
    }
}
