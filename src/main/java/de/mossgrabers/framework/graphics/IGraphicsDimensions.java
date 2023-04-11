// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

/**
 * Interface to pre-calculated grid dimensions.
 *
 * @author Jürgen Moßgraber
 */
public interface IGraphicsDimensions
{
    /**
     * Get the width of the graphics.
     *
     * @return The width
     */
    int getWidth ();


    /**
     * Get the height of the graphics.
     *
     * @return The height
     */
    int getHeight ();


    /**
     * Get the size of separators.
     *
     * @return The size
     */
    double getSeparatorSize ();


    /**
     * Get the height of a menu.
     *
     * @return The height
     */
    double getMenuHeight ();


    /**
     * Get the size of a unit.
     *
     * @return The size
     */
    double getUnit ();


    /**
     * Half of the unit.
     *
     * @return Half of the unit
     */
    double getHalfUnit ();


    /**
     * Double of the unit.
     *
     * @return Double of the unit
     */
    double getDoubleUnit ();


    /**
     * The height of the controls menu on top.
     *
     * @return The height
     */
    double getControlsTop ();


    /**
     * Get the size of inset.
     *
     * @return The size
     */
    double getInset ();


    /**
     * Get the limit for the maximum value for parameters. The value is in the range of 0 to upper
     * bound - 1.
     *
     * @return The upper bound value
     */
    int getParameterUpperBound ();
}
