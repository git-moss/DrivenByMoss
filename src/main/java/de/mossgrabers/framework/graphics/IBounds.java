// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

/**
 * Boundaries to draw in.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IBounds
{
    /**
     * The left bound.
     *
     * @return The value
     */
    double getLeft ();


    /**
     * The top bound.
     *
     * @return The value
     */
    double getTop ();


    /**
     * The width bound.
     *
     * @return The value
     */
    double getWidth ();


    /**
     * The height bound.
     *
     * @return The value
     */
    double getHeight ();
}
