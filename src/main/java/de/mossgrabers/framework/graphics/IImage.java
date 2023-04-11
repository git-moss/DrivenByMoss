// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

/**
 * An interface to an image.
 *
 * @author Jürgen Moßgraber
 */
public interface IImage
{
    /**
     * Get the width of the image.
     *
     * @return The width
     */
    double getWidth ();


    /**
     * Get the height of the image.
     *
     * @return The height
     */
    int getHeight ();
}
