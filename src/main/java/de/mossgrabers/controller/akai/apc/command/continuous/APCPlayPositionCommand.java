// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.command.continuous;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.TempoCommand;
import de.mossgrabers.framework.command.continuous.PlayPositionCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.Timeout;
import de.mossgrabers.framework.view.Views;


/**
 * Additionally, display BPM on the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCPlayPositionCommand extends PlayPositionCommand<APCControlSurface, APCConfiguration>
{
    private final TempoCommand<APCControlSurface, APCConfiguration> tempoCommand;
    private final Timeout                                           timeout;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param timeout The timeout object
     */
    public APCPlayPositionCommand (final IModel model, final APCControlSurface surface, final Timeout timeout)
    {
        super (model, surface);

        this.tempoCommand = new TempoCommand<> (model, surface);
        this.timeout = timeout;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        if (this.surface.isPressed (ButtonID.TAP_TEMPO))
        {
            this.surface.setTriggerConsumed (ButtonID.TAP_TEMPO);

            this.tempoCommand.execute (value);

            final ViewManager viewManager = this.surface.getViewManager ();
            if (!viewManager.isActive (Views.TEMPO))
                viewManager.setTemporary (Views.TEMPO);
            this.timeout.delay (viewManager::restore);

            return;
        }

        if (this.surface.isPressed (ButtonID.ARROW_LEFT))
        {
            this.surface.setTriggerConsumed (ButtonID.ARROW_LEFT);
            this.transport.changeLoopStart (this.model.getValueChanger ().isIncrease (value), this.surface.isKnobSensitivitySlow ());
            this.mvHelper.delayDisplay ( () -> "Loop Start: " + this.transport.getLoopStartBeatText ());
            return;
        }

        if (this.surface.isPressed (ButtonID.ARROW_RIGHT))
        {
            this.surface.setTriggerConsumed (ButtonID.ARROW_RIGHT);
            this.transport.changeLoopLength (this.model.getValueChanger ().isIncrease (value), this.surface.isKnobSensitivitySlow ());
            this.mvHelper.delayDisplay ( () -> "Loop Length: " + this.transport.getLoopLengthBeatText ());
            return;
        }

        super.execute (value);
    }
}
