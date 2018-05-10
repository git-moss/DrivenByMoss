// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

import de.mossgrabers.framework.controller.color.ColorEx;


/**
 * A color setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IColorSetting extends ISetting<double []>
{
    /**
     * Set the RGB color value.
     *
     * @param red The red component
     * @param green The green component
     * @param blue The blue component
     */
    void set (double red, double green, double blue);


    /**
     * Set the color.
     *
     * @param color The color
     */
    void set (ColorEx color);
}
