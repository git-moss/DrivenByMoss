// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.utils;

/**
 * Wraps some send info.
 *
 * @author Jürgen Moßgraber
 *
 * @param enabled Is the send enabled?
 * @param name The name of the send
 * @param text The description text
 * @param value The value
 * @param modulatedValue The modulated value
 * @param edited Is selected for editing
 */
public record SendData (boolean enabled, String name, String text, int value, int modulatedValue, boolean edited)
{
    // Intentionally empty
}
