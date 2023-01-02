// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import de.mossgrabers.framework.controller.color.ColorEx;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.HardwareLightVisualState;
import com.bitwig.extension.controller.api.InternalHardwareLightState;


/**
 * Creates visual states from raw colors.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RawColorLightState extends InternalHardwareLightState
{
    private final ColorEx colorState;


    /**
     * Constructor.
     *
     * @param colorState The color that represents the lighting state of the LED
     */
    public RawColorLightState (final ColorEx colorState)
    {
        this.colorState = colorState;
    }


    /** {@inheritDoc}} */
    @Override
    public HardwareLightVisualState getVisualState ()
    {
        final Color color = Color.fromRGB (this.colorState.getRed (), this.colorState.getGreen (), this.colorState.getBlue ());
        final ColorEx contrastColorEx = ColorEx.calcContrastColor (this.colorState);
        final Color contrastColor = Color.fromRGB (contrastColorEx.getRed (), contrastColorEx.getGreen (), contrastColorEx.getBlue ());
        return HardwareLightVisualState.createForColor (color, contrastColor);
    }


    /** {@inheritDoc}} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.colorState.encode ();
        return result;
    }


    /** {@inheritDoc}} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || this.getClass () != obj.getClass ())
            return false;
        final RawColorLightState other = (RawColorLightState) obj;
        return this.colorState.encode () == other.colorState.encode ();
    }
}
