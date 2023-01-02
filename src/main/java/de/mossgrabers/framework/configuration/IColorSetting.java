// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

import de.mossgrabers.framework.controller.color.ColorEx;


/**
 * A color setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IColorSetting extends IValueSetting<ColorEx>
{
    /**
     * Set the RGB color value.
     *
     * @param red The red component (0..1)
     * @param green The green component (0..1)
     * @param blue The blue component (0..1)
     */
    void set (double red, double green, double blue);


    /**
     * Set the color.
     *
     * @param rgb The 3 rgb values (0..1)
     */
    void set (final double [] rgb);
}
