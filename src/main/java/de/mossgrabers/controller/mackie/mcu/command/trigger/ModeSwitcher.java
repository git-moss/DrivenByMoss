// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;

import java.util.EnumMap;
import java.util.Map;


/**
 * Helper class for cycling through modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModeSwitcher
{
    private final MCUControlSurface        surface;

    private static final Map<Modes, Modes> PREV_MODES = new EnumMap<> (Modes.class);
    private static final Map<Modes, Modes> NEXT_MODES = new EnumMap<> (Modes.class);

    static
    {
        PREV_MODES.put (Modes.PAN, Modes.VOLUME);
        PREV_MODES.put (Modes.VOLUME, Modes.TRACK);
        PREV_MODES.put (Modes.TRACK, Modes.DEVICE_PARAMS);
        PREV_MODES.put (Modes.DEVICE_PARAMS, Modes.PAN);

        NEXT_MODES.put (Modes.TRACK, Modes.VOLUME);
        NEXT_MODES.put (Modes.VOLUME, Modes.PAN);
        NEXT_MODES.put (Modes.PAN, Modes.DEVICE_PARAMS);
        NEXT_MODES.put (Modes.DEVICE_PARAMS, Modes.TRACK);
    }


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
        modeManager.setActive (PREV_MODES.getOrDefault (modeManager.getActiveID (), Modes.PAN));
        this.surface.getHost ().showNotification (modeManager.getActive ().getName ());
    }


    /**
     * Scroll downwards through the modes.
     */
    public void scrollDown ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        modeManager.setActive (NEXT_MODES.getOrDefault (modeManager.getActiveID (), Modes.TRACK));
        this.surface.getHost ().showNotification (modeManager.getActive ().getName ());
    }
}
