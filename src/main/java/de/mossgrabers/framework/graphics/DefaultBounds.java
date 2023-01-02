// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

/**
 * Default implementation for boundaries.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 *
 * @param left The left bound of the drawing area
 * @param top The top bound of the drawing area
 * @param width The width of the drawing area
 * @param height The height of the drawing area
 */
public record DefaultBounds (double left, double top, double width, double height) implements IBounds
{
    // Intentionally empty
}
