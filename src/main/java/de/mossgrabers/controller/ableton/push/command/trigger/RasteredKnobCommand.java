// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.trigger;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.controller.ableton.push.mode.device.DeviceBrowserMode;
import de.mossgrabers.framework.command.TempoCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to change the tempo and scroll through lists in browser mode.
 *
 * @author Jürgen Moßgraber
 */
public class RasteredKnobCommand extends TempoCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public RasteredKnobCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.BROWSER))
        {
            final DeviceBrowserMode mode = (DeviceBrowserMode) modeManager.get (Modes.BROWSER);
            mode.changeSelectedColumnValue (value);
            return;
        }

        if (this.surface.isSelectPressed ())
        {
            this.transport.changeLoopStart (this.model.getValueChanger ().isIncrease (value), this.surface.isKnobSensitivitySlow ());
            return;
        }

        super.execute (value);
        this.mvHelper.notifyTempo ();
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final boolean activate = event != ButtonEvent.UP;

        if (this.surface.isSelectPressed ())
        {
            if (activate)
                this.mvHelper.delayDisplay ( () -> "Loop Start: " + this.transport.getLoopStartBeatText ());
            return;
        }

        this.transport.setTempoIndication (activate);
        if (activate)
            this.mvHelper.notifyTempo ();
    }
}
