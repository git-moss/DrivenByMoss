// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.configuration;

import de.mossgrabers.framework.configuration.IColorSetting;
import de.mossgrabers.framework.configuration.IValueObserver;
import de.mossgrabers.framework.controller.color.ColorEx;

import com.bitwig.extension.controller.api.SettableColorValue;
import com.bitwig.extension.controller.api.Setting;


/**
 * Bitwig implementation of a color setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ColorSettingImpl extends AbstractSetting<double []> implements IColorSetting
{
    private SettableColorValue colorValue;


    /**
     * Constructor.
     *
     * @param colorValue The color value
     */
    public ColorSettingImpl (final SettableColorValue colorValue)
    {
        super ((Setting) colorValue);

        this.colorValue = colorValue;
    }


    /** {@inheritDoc} */
    @Override
    public void set (final double red, final double green, final double blue)
    {
        this.colorValue.set ((float) red, (float) green, (float) blue);
    }


    /** {@inheritDoc} */
    @Override
    public void set (double [] value)
    {
        this.set (value[0], value[1], value[2]);
    }


    /** {@inheritDoc} */
    @Override
    public void set (final ColorEx color)
    {
        this.set (color.getRed () / 255.0, color.getGreen () / 255.0, color.getBlue () / 255.0);
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IValueObserver<double []> observer)
    {
        this.colorValue.addValueObserver ( (red, green, blue) -> observer.update (new double []
        {
            red,
            green,
            blue
        }));
    }
}
