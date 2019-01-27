// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.command.trigger;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.SceneCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


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
    public LPSceneCommand (final int index, final IModel model, final LaunchpadControlSurface surface)
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
            this.surface.getDisplay ().notify (AbstractConfiguration.getNewClipLengthValue (index));
            return;
        }

        super.execute (event);
    }
}
