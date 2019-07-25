// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.command.trigger;

import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;


/**
 * Helper class for cycling through modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModeSwitcher
{
    private MCUControlSurface surface;


    /**
     * Constructor.
     *
     * @param surface The surface device
     */
    public ModeSwitcher (final MCUControlSurface surface)
    {
        this.surface = surface;
    }


    /**
     * Scroll upwards through the modes.
     */
    public void scrollUp ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        final Modes activeModeId = modeManager.getActiveOrTempModeId ();
        if (Modes.MODE_PAN == activeModeId)
        {
            modeManager.setActiveMode (Modes.MODE_VOLUME);
            this.surface.getDisplay ().notify ("Volume");
        }
        else if (Modes.MODE_VOLUME == activeModeId)
        {
            modeManager.setActiveMode (Modes.MODE_TRACK);
            this.surface.getDisplay ().notify ("Track");
        }
        else if (Modes.MODE_TRACK == activeModeId)
        {
            modeManager.setActiveMode (Modes.MODE_DEVICE_PARAMS);
            this.surface.getDisplay ().notify ("Parameters");
        }
        else
        {
            modeManager.setActiveMode (Modes.MODE_PAN);
            this.surface.getDisplay ().notify ("Panorama");
        }
    }


    /**
     * Scroll downwards through the modes.
     */
    public void scrollDown ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        final Modes activeModeId = modeManager.getActiveOrTempModeId ();
        if (Modes.MODE_PAN == activeModeId)
        {
            modeManager.setActiveMode (Modes.MODE_DEVICE_PARAMS);
            this.surface.getDisplay ().notify ("Parameters");
        }
        else if (Modes.MODE_VOLUME == activeModeId)
        {
            modeManager.setActiveMode (Modes.MODE_PAN);
            this.surface.getDisplay ().notify ("Panorama");
        }
        else if (Modes.MODE_TRACK == activeModeId)
        {
            modeManager.setActiveMode (Modes.MODE_VOLUME);
            this.surface.getDisplay ().notify ("Volume");
        }
        else
        {
            modeManager.setActiveMode (Modes.MODE_TRACK);
            this.surface.getDisplay ().notify ("Track");
        }
    }
}
