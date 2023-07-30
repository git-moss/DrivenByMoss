// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.trigger;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.controller.ableton.push.mode.device.DeviceBrowserMode;
import de.mossgrabers.framework.command.TempoCommand;
import de.mossgrabers.framework.daw.GrooveParameterID;
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
    private boolean isTempoMode = true;


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
        // Executed from knob turn

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

        if (this.isTempoMode)
        {
            super.execute (value);
            this.mvHelper.notifyTempo ();
        }
        else
        {
            this.model.getGroove ().getParameter (GrooveParameterID.SHUFFLE_AMOUNT).changeValue (value);
            this.mvHelper.notifyShuffle ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        // Executed from knob touch

        final boolean activate = event != ButtonEvent.UP;

        if (this.surface.isSelectPressed ())
        {
            if (activate)
                this.mvHelper.delayDisplay ( () -> "Loop Start: " + this.transport.getLoopStartBeatText ());
            return;
        }

        if (this.isTempoMode)
        {
            this.transport.setTempoIndication (activate);
            if (activate)
                this.mvHelper.notifyTempo ();
        }
        else
        {
            if (activate)
                this.mvHelper.notifyShuffle ();
        }
    }


    /**
     * Display the mode and value.
     */
    public void notifyMode ()
    {
        if (this.isTempoMode)
            this.mvHelper.notifyTempo ();
        else
            this.mvHelper.notifyShuffle ();
    }


    /**
     * Toggle mode between tempo and swing change.
     */
    public void toggleMode ()
    {
        this.isTempoMode = !this.isTempoMode;
    }
}
