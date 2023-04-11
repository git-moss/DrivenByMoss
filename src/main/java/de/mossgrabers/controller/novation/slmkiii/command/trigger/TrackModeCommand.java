// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.command.trigger;

import de.mossgrabers.controller.novation.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to trigger the Track modes.
 *
 * @author Jürgen Moßgraber
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

        this.modeSelectCommand = new ModeMultiSelectCommand<> (this.model, surface, Modes.TRACK, Modes.VOLUME, Modes.PAN, Modes.SEND1, Modes.SEND2, Modes.SEND3, Modes.SEND4, Modes.SEND5, Modes.SEND6, Modes.SEND7, Modes.SEND8);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.UP)
            return;

        final IBrowser browser = this.model.getBrowser ();
        if (browser != null && browser.isActive ())
            browser.stopBrowsing (!this.surface.isShiftPressed ());

        this.modeSelectCommand.execute (ButtonEvent.UP, 127);
    }
}
