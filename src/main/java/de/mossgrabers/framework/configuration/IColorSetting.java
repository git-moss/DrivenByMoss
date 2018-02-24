// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

/**
 * A color setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IColorSetting
{
    /**
     * Set the string value.
     * 
     * @param red The red component
     * @param green The green component
     * @param blue The blue component
     */
    void set (double red, double green, double blue);


    /**
     * Add an observer for a change of the value.
     *
     * @param observer The observer
     */
    void addValueObserver (IValueObserver<double []> observer);
}
