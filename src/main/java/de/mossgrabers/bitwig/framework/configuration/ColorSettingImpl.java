// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.configuration;

import de.mossgrabers.framework.configuration.IColorSetting;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.SettableColorValue;
import com.bitwig.extension.controller.api.Setting;


/**
 * Bitwig implementation of a color setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ColorSettingImpl extends AbstractSetting implements IColorSetting
{
    private final SettableColorValue colorValue;


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
    public void set (final double [] rgb)
    {
        this.set (rgb[0], rgb[1], rgb[2]);
    }


    /** {@inheritDoc} */
    @Override
    public void set (final ColorEx color)
    {
        this.set (color.getRed (), color.getGreen (), color.getBlue ());
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx get ()
    {
        final Color color = this.colorValue.get ();
        if (color == null)
            return ColorEx.BLACK;
        return new ColorEx (color.getRed (), color.getGreen (), color.getBlue ());
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IValueObserver<ColorEx> observer)
    {
        this.colorValue.addValueObserver ( (red, green, blue) -> observer.update (new ColorEx (red, green, blue)));

        // Directly fire the current value
        final Color color = this.colorValue.get ();
        observer.update (new ColorEx (color.getRed (), color.getGreen (), color.getBlue ()));
    }
}
