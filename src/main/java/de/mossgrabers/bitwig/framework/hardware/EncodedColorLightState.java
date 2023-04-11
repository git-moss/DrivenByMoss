// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import de.mossgrabers.framework.controller.color.ColorEx;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.HardwareLightVisualState;
import com.bitwig.extension.controller.api.InternalHardwareLightState;

import java.util.function.IntFunction;


/**
 * Creates visual states from encoded colors.
 *
 * @author Jürgen Moßgraber
 */
public class EncodedColorLightState extends InternalHardwareLightState
{
    private final int                  encodedColorState;
    private final IntFunction<ColorEx> stateToColorFunction;


    /**
     * Constructor.
     *
     * @param encodedColorState The color and blink information
     * @param stateToColorFunction Convert the state of the light to a color, which can be displayed
     *            in the simulated GUI
     */
    public EncodedColorLightState (final int encodedColorState, final IntFunction<ColorEx> stateToColorFunction)
    {
        this.encodedColorState = encodedColorState;
        this.stateToColorFunction = stateToColorFunction;
    }


    /** {@inheritDoc}} */
    @Override
    public HardwareLightVisualState getVisualState ()
    {
        if (this.encodedColorState == -1)
            return HardwareLightVisualState.createForColor (Color.blackColor (), Color.whiteColor ());

        final int colorIndex = this.encodedColorState & 0xFF;
        int blinkColorIndex = this.encodedColorState >> 8 & 0xFF;
        if (blinkColorIndex == 128)
            blinkColorIndex = -1;

        final boolean blinkFast = (this.encodedColorState >> 16 & 1) > 0;

        final ColorEx colorEx = this.stateToColorFunction.apply (colorIndex);
        final Color color = Color.fromRGB (colorEx.getRed (), colorEx.getGreen (), colorEx.getBlue ());
        final ColorEx contrastColorEx = ColorEx.calcContrastColor (colorEx);
        final Color contrastColor = Color.fromRGB (contrastColorEx.getRed (), contrastColorEx.getGreen (), contrastColorEx.getBlue ());

        if (blinkColorIndex <= 0 || blinkColorIndex >= 128)
            return HardwareLightVisualState.createForColor (color, contrastColor);

        final ColorEx blinkColorEx = this.stateToColorFunction.apply (blinkColorIndex);
        final Color blinkColor = Color.fromRGB (blinkColorEx.getRed (), blinkColorEx.getGreen (), blinkColorEx.getBlue ());
        final ColorEx contrastBlinkColorEx = ColorEx.calcContrastColor (blinkColorEx);
        final Color contrastBlinkColor = Color.fromRGB (contrastBlinkColorEx.getRed (), contrastBlinkColorEx.getGreen (), contrastBlinkColorEx.getBlue ());

        final double blinkTimeInSec = blinkFast ? 0.5 : 1;
        return HardwareLightVisualState.createBlinking (blinkColor, color, contrastBlinkColor, contrastColor, blinkTimeInSec, blinkTimeInSec);
    }


    /** {@inheritDoc}} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.encodedColorState;
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
        final EncodedColorLightState other = (EncodedColorLightState) obj;
        return this.encodedColorState == other.encodedColorState;
    }
}
