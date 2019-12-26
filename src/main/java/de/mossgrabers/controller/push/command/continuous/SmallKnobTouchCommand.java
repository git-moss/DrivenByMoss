// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.continuous;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.controller.push.mode.TransportMode;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for touching the small knob 1 and 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SmallKnobTouchCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    private boolean        isTempo;

    private static boolean isTempoTouched    = false;
    private static boolean isPositionTouched = false;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param isTempo True for tempo change otherwise play position change
     */
    public SmallKnobTouchCommand (final IModel model, final PushControlSurface surface, final boolean isTempo)
    {
        super (model, surface);
        this.isTempo = isTempo;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final boolean isTouched = event == ButtonEvent.DOWN;

        // Avoid accidentally leaving the browser
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveOrTempMode (Modes.BROWSER))
            return;

        // Prevent flickering if a knob is touched accidentally while fiddling with other knobs
        final Mode activeMode = modeManager.getActiveOrTempMode ();
        if (activeMode instanceof BaseMode && ((BaseMode) activeMode).isAKnobTouched () && !(activeMode instanceof TransportMode))
            return;

        if (this.isTempo)
            this.model.getTransport ().setTempoIndication (isTouched);

        if (isTouched)
        {
            if (!isTempoTouched && !isPositionTouched)
                modeManager.setActiveMode (Modes.TRANSPORT);
            if (this.isTempo)
                isTempoTouched = true;
            else
                isPositionTouched = true;
        }
        else
        {
            if (this.isTempo)
                isTempoTouched = false;
            else
                isPositionTouched = false;

            if (!isTempoTouched && !isPositionTouched)
                modeManager.restoreMode ();
        }

        modeManager.getMode (Modes.TRANSPORT).onKnobTouch (this.isTempo ? 4 : 6, isTouched);
    }
}
