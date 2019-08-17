// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.display;

import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.display.DisplayModel;


/**
 * Interface to a graphics display.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IGraphicsDisplay extends IDisplay
{
    /**
     * Get the display model.
     *
     * @return The display model
     */
    DisplayModel getModel ();


    /**
     * Show the debug window for the graphics display.
     */
    void showDebugWindow ();


    /**
     * Send the buffered image to the graphics display.
     *
     * @param image An image
     */
    void send (final IBitmap image);
}
