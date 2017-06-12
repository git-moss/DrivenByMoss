// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.mcu.MCUConfiguration;
import de.mossgrabers.mcu.controller.MCUControlSurface;


/**
 * Command for toggling the display header.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ToggleDisplayCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     * 
     * @param model The model
     * @param surface The surface
     */
    public ToggleDisplayCommand (final Model model, final MCUControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.surface.getConfiguration ().toggleDisplayTrackNames ();
    }
}
