// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

import de.mossgrabers.framework.controller.color.ColorEx;


/**
 * Interface to color and font configurations for drawing.
 *
 * @author Jürgen Moßgraber
 */
public interface IGraphicsConfiguration
{
    /**
     * Get the text color of an element.
     *
     * @return The text color of an element.
     */
    ColorEx getColorText ();


    /**
     * Get the background color of an element.
     *
     * @return The background color of an element.
     */
    ColorEx getColorBackground ();


    /**
     * Get the background darker color of an element.
     *
     * @return The background color of an element.
     */
    ColorEx getColorBackgroundDarker ();


    /**
     * Get the background lighter color of an element.
     *
     * @return The background color of an element.
     */
    ColorEx getColorBackgroundLighter ();


    /**
     * Get the border color of an element.
     *
     * @return The border color of an element.
     */
    ColorEx getColorBorder ();


    /**
     * Get the edit color of an element.
     *
     * @return The edit color of an element.
     */
    ColorEx getColorEdit ();


    /**
     * Get the fader color of an element.
     *
     * @return The fader color of an element.
     */
    ColorEx getColorFader ();


    /**
     * Get the VU color of an element.
     *
     * @return The VU color of an element.
     */
    ColorEx getColorVu ();


    /**
     * Get the record color of an element.
     *
     * @return The record color of an element.
     */
    ColorEx getColorRecord ();


    /**
     * Get the solo color of an element.
     *
     * @return The solo color of an element.
     */
    ColorEx getColorSolo ();


    /**
     * Get the mute color of an element.
     *
     * @return The border mute of an element.
     */
    ColorEx getColorMute ();


    /**
     * Should anti-aliasing be applied?
     *
     * @return True if enabled
     */
    boolean isAntialiasEnabled ();
}
