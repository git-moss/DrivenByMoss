// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.command.trigger;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command handle the nudge buttons.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NudgeCommand extends AbstractTriggerCommand<APCControlSurface, APCConfiguration>
{
    private boolean isPlus;
    private boolean isTempoChange;


    /**
     * Constructor.
     *
     * @param isPlus True if nudge positive
     * @param model The model
     * @param surface The surface
     */
    public NudgeCommand (final boolean isPlus, final IModel model, final APCControlSurface surface)
    {
        super (model, surface);
        this.isPlus = isPlus;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.isTempoChange = true;
        else if (event == ButtonEvent.UP)
            this.isTempoChange = false;
        this.doChangeTempo ();
    }


    private void doChangeTempo ()
    {
        if (!this.isTempoChange)
            return;
        this.model.getTransport ().changeTempo (this.isPlus);
        this.surface.scheduleTask (this::doChangeTempo, 200);
    }
}
