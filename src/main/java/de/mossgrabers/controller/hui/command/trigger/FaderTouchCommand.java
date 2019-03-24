// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.hui.command.trigger;

import de.mossgrabers.controller.hui.HUIConfiguration;
import de.mossgrabers.controller.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.command.trigger.track.SelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * A select track command which activates the volume mode temporarily.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
        if (this.index < 8)
        {
            final ModeManager modeManager = this.surface.getModeManager ();
            if (event == ButtonEvent.DOWN)
                modeManager.setActiveMode (Modes.MODE_VOLUME);
            else if (event == ButtonEvent.UP)
                modeManager.restoreMode ();
        }

        if (configuration.isTouchChannel ())
            super.executeNormal (event);
    }
}
