// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.SceneCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.launchpad.LaunchpadConfiguration;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;


/**
 * Command to use a scene button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LPSceneCommand extends SceneCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param index The index of the scene button
     * @param model The model
     * @param surface The surface
     */
    public LPSceneCommand (final int index, final Model model, final LaunchpadControlSurface surface)
    {
        super (index, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (this.surface.isShiftPressed ())
        {
            final int index = 7 - this.scene;
            this.surface.getConfiguration ().setNewClipLength (index);
            this.surface.getDisplay ().notify (AbstractConfiguration.NEW_CLIP_LENGTH_VALUES[index]);
            return;
        }

        super.execute (event);
    }
}
