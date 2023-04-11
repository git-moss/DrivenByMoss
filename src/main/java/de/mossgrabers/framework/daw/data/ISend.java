// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * Interface to a send.
 *
 * @author Jürgen Moßgraber
 */
public interface ISend extends IParameter
{
    /**
     * Get the color of the send channel.
     *
     * @return The color in RGB
     */
    ColorEx getColor ();


    /**
     * Returns true if the send is enabled.
     *
     * @return True if enabled
     */
    boolean isEnabled ();


    /**
     * Toggle the enabled state of the send.
     */
    void toggleEnabled ();
}