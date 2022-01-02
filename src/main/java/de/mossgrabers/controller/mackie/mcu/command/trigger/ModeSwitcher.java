// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;


/**
 * Helper class for cycling through modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModeSwitcher
{
    private final MCUControlSurface surface;


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
        final Modes activeModeId = modeManager.getActiveID ();
        if (Modes.PAN == activeModeId)
        {
            modeManager.setActive (Modes.VOLUME);
            this.surface.getDisplay ().notify ("Volume");
        }
        else if (Modes.VOLUME == activeModeId)
        {
            modeManager.setActive (Modes.TRACK);
            this.surface.getDisplay ().notify ("Track");
        }
        else if (Modes.TRACK == activeModeId)
        {
            modeManager.setActive (Modes.DEVICE_PARAMS);
            this.surface.getDisplay ().notify ("Parameters");
        }
        else
        {
            modeManager.setActive (Modes.PAN);
            this.surface.getDisplay ().notify ("Panorama");
        }
    }


    /**
     * Scroll downwards through the modes.
     */
    public void scrollDown ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        final Modes activeModeId = modeManager.getActiveID ();
        if (Modes.PAN == activeModeId)
        {
            modeManager.setActive (Modes.DEVICE_PARAMS);
            this.surface.getDisplay ().notify ("Parameters");
        }
        else if (Modes.VOLUME == activeModeId)
        {
            modeManager.setActive (Modes.PAN);
            this.surface.getDisplay ().notify ("Panorama");
        }
        else if (Modes.TRACK == activeModeId)
        {
            modeManager.setActive (Modes.VOLUME);
            this.surface.getDisplay ().notify ("Volume");
        }
        else
        {
            modeManager.setActive (Modes.TRACK);
            this.surface.getDisplay ().notify ("Track");
        }
    }
}
