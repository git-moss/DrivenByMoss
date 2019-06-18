// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.command.trigger;

import de.mossgrabers.controller.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to trigger the Track modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackModeCommand extends AbstractTriggerCommand<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    private final ModeMultiSelectCommand<SLMkIIIControlSurface, SLMkIIIConfiguration> modeSelectCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public TrackModeCommand (final IModel model, final SLMkIIIControlSurface surface)
    {
        super (model, surface);

        this.modeSelectCommand = new ModeMultiSelectCommand<> (this.model, surface, Modes.MODE_TRACK, Modes.MODE_VOLUME, Modes.MODE_PAN, Modes.MODE_SEND1, Modes.MODE_SEND2, Modes.MODE_SEND3, Modes.MODE_SEND4, Modes.MODE_SEND5, Modes.MODE_SEND6, Modes.MODE_SEND7, Modes.MODE_SEND8);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.modeSelectCommand.execute (ButtonEvent.DOWN);

    }
}
