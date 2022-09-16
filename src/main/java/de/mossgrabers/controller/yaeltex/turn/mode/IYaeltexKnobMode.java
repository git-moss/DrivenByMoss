// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.yaeltex.turn.mode;

import de.mossgrabers.framework.controller.color.ColorEx;


/**
 * Interface for additional Yaeltex specific mode methods.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IYaeltexKnobMode
{
    /**
     * Get the color for the knobs LED ring.
     *
     * @param index The index of the relative knob (0-31)
     * @return The color to set
     */
    ColorEx getKnobColor (int index);
}
