// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.command.trigger;

import de.mossgrabers.controller.mcu.MCUConfiguration;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * A select track command which activates the volume mode temporarily.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FaderTouchCommand extends SelectCommand
{
    /**
     * Constructor.
     *
     * @param index The channel index
     * @param model The model
     * @param surface The surface
     */
    public FaderTouchCommand (final int index, final IModel model, final MCUControlSurface surface)
    {
        super (index, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.LONG)
            return;
        final boolean isTouched = event == ButtonEvent.DOWN;

        if (this.index == 8)
        {
            if (isTouched)
                this.model.getMasterTrack ().select ();
            return;
        }

        final MCUConfiguration configuration = this.surface.getConfiguration ();
        if (configuration.isTouchChannel ())
            super.executeNormal (event);

        if (this.index < 8)
        {
            final ModeManager modeManager = this.surface.getModeManager ();
            if (configuration.useFadersAsKnobs ())
            {
                modeManager.getActiveOrTempMode ().onKnobTouch (this.index, isTouched);
                return;
            }
            modeManager.getMode (Modes.VOLUME).onKnobTouch (this.index, isTouched);

            if (isTouched)
            {
                if (modeManager.isActiveOrTempMode (Modes.VOLUME))
                    modeManager.setPreviousMode (Modes.VOLUME);
                else
                    modeManager.setActiveMode (Modes.VOLUME);
            }
            else
                modeManager.restoreMode ();
        }
    }
}
