// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.command.trigger;

import de.mossgrabers.controller.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to trigger the global Options mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OptionsCommand extends AbstractTriggerCommand<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public OptionsCommand (final IModel model, final SLMkIIIControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.surface.getModeManager ().setActiveMode (Modes.MODE_FUNCTIONS);
        else if (event == ButtonEvent.UP)
            this.surface.getModeManager ().restoreMode ();
    }
}
