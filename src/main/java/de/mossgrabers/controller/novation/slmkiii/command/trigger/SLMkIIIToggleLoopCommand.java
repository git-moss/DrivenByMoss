// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.command.trigger;

import de.mossgrabers.controller.novation.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command handle the Loop/Repeat button. Shift + Loop toggles Clip Automation.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLMkIIIToggleLoopCommand extends ToggleLoopCommand<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SLMkIIIToggleLoopCommand (final IModel model, final SLMkIIIControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.model.getTransport ().toggleWriteClipLauncherAutomation ();
    }
}
