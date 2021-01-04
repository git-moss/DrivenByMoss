// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.device.DeviceBrowserMode;
import de.mossgrabers.framework.command.continuous.TempoCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to change the tempo and scroll through lists in browser mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RasteredKnobCommand extends TempoCommand<PushControlSurface, PushConfiguration> implements TriggerCommand
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

        super.execute (value);

        this.mvHelper.notifyTempo ();
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final boolean activate = event != ButtonEvent.UP;
        this.transport.setTempoIndication (activate);
        if (activate)
            this.mvHelper.notifyTempo ();
    }
}
