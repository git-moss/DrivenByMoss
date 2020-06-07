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
    private static final boolean [] isTrackTouched = new boolean [8 * 4];


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

        // Master Channel
        if (this.index == 8)
        {
            if (isTouched)
                this.model.getMasterTrack ().select ();
            return;
        }

        // Select channel
        final MCUConfiguration configuration = this.surface.getConfiguration ();
        if (configuration.isTouchChannel ())
            super.executeNormal (event);

        final ModeManager modeManager = this.surface.getModeManager ();
        if (configuration.useFadersAsKnobs ())
        {
            modeManager.getActiveOrTempMode ().onKnobTouch (this.index, isTouched);
            return;
        }
        modeManager.getMode (Modes.VOLUME).onKnobTouch (this.index, isTouched);

        final int pos = this.surface.getSurfaceID () * 8 + this.index;

        // Temporarily enable volume mode
        if (isTouched)
        {
            if (!hasTouchedFader ())
            {
                if (modeManager.isActiveOrTempMode (Modes.VOLUME))
                    modeManager.setPreviousMode (Modes.VOLUME);
                else
                    modeManager.setActiveMode (Modes.VOLUME);
            }
            isTrackTouched[pos] = true;
        }
        else
        {
            isTrackTouched[pos] = false;
            if (!hasTouchedFader ())
                modeManager.restoreMode ();
        }

    }


    private static boolean hasTouchedFader ()
    {
        for (final boolean element: isTrackTouched)
        {
            if (element)
                return true;
        }
        return false;
    }
}
