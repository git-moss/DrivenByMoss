// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.mode;

import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * Helper class for different slow/fast change options for the faders.
 *
 * @author Jürgen Moßgraber
 */
public class FaderSlowChange
{
    private int lastChangeValue = -1;


    /**
     * Change the value. If Shift is pressed the change is slow and relative.
     *
     * @param surface The surface
     * @param parameter The parameter to modify
     * @param value The new value of the fader
     */
    public void changeValue (final MaschineJamControlSurface surface, final IParameter parameter, final int value)
    {
        if (surface.isShiftPressed () || surface.getConfiguration ().isSlowFaderChange ())
        {
            if (this.lastChangeValue == 0)
            {
                this.lastChangeValue = value;
                return;
            }
            final boolean inc = value - this.lastChangeValue > 0;
            parameter.inc (inc ? 1 : -1);
            this.lastChangeValue = value;
        }
        else
            parameter.setValue (value);
    }
}
