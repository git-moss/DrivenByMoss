// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc.command.trigger;

import de.mossgrabers.apc.APCConfiguration;
import de.mossgrabers.apc.controller.APCControlSurface;
import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;


/**
 * The Stop all clips command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StopAllClipsCommand extends AbstractTriggerCommand<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public StopAllClipsCommand (final Model model, final APCControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.model.getCurrentTrackBank ().stop ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        this.surface.getViewManager ().getActiveView ().getTriggerCommand (Commands.COMMAND_BROWSE).executeNormal (event);
    }
}
