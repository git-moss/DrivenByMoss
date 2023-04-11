// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

/**
 * Interface for a proxy to a display on a hardware controller.
 *
 * @author Jürgen Moßgraber
 */
public interface IHwTextDisplay extends IHwControl
{
    /**
     * Set a line of the display.
     *
     * @param line The line
     * @param text The text to set
     */
    void setLine (int line, String text);
}
