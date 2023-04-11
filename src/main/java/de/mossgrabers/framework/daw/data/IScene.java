// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;


/**
 * Interface to a scene.
 *
 * @author Jürgen Moßgraber
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
     * Launch a scene.
     *
     * @param isPressed If false, the release action is triggered as configured in the DAW.
     * @param isAlternative If true, launch the alternative launch option as configured in the DAW.
     */
    void launch (boolean isPressed, boolean isAlternative);


    /**
     * Delete the scene.
     */
    void remove ();


    /**
     * Duplicate the scene.
     */
    void duplicate ();
}