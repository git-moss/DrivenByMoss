// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;


/**
 * Interface to a scene.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IScene extends IItem
{
    /**
     * Get the color of the scene.
     *
     * @return The color
     */
    ColorEx getColor ();


    /**
     * Set the color of the scene.
     *
     * @param color The color
     */
    void setColor (ColorEx color);


    /**
     * Launches the scene.
     */
    void launch ();


    /**
     * Delete the scene.
     */
    void remove ();


    /**
     * Duplicate the scene.
     */
    void duplicate ();
}