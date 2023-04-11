// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui.command.trigger;

import de.mossgrabers.controller.mackie.hui.HUIConfiguration;
import de.mossgrabers.controller.mackie.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.command.trigger.track.SelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * A select track command which activates the volume mode temporarily.
 *
 * @author Jürgen Moßgraber
 */
public class FaderTouchCommand extends SelectCommand<HUIControlSurface, HUIConfiguration>
{
    /**
     * Constructor.
     *
     * @param index The channel index
     * @param model The model
     * @param surface The surface
     */
    public FaderTouchCommand (final int index, final IModel model, final HUIControlSurface surface)
    {
        super (index, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        final HUIConfiguration configuration = this.surface.getConfiguration ();
        final ModeManager modeManager = this.surface.getModeManager ();
        if (event == ButtonEvent.DOWN)
            modeManager.setActive (Modes.VOLUME);
        else if (event == ButtonEvent.UP)
            modeManager.restore ();

        if (configuration.isTouchChannel ())
            super.executeNormal (event);
    }
}
