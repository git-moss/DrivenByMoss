// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.command.trigger;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Start playback. Execute the new command if shifted..
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayAndNewCommand extends AbstractTriggerCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private final PlayCommand<LaunchpadControlSurface, LaunchpadConfiguration> playCommand;
    private final NewCommand<LaunchpadControlSurface, LaunchpadConfiguration>  newCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PlayAndNewCommand (final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);

        this.playCommand = new PlayCommand<> (model, surface);
        this.newCommand = new NewCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        this.playCommand.executeNormal (event);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        this.newCommand.executeNormal (event);
    }
}
