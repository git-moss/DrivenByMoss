// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

/**
 * An interface to a bitmap, which can also be displayed in a window.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IBitmap
{
    /**
     * Set a title for the window, which displays the Bitmap.
     *
     * @param title The title
     */
    void setDisplayWindowTitle (String title);


    /**
     * Show the display window.
     */
    void showDisplayWindow ();


    /**
     * Render the content of the bitmap.
     *
     * @param enableAntialias True to enable anti aliasing
     * @param renderer The renderer to draw on the bitmap
     */
    void render (boolean enableAntialias, IRenderer renderer);


    /**
     * Encode the bitmap data into a different format.
     *
     * @param encoder The encoder to use
     */
    void encode (IEncoder encoder);
}
