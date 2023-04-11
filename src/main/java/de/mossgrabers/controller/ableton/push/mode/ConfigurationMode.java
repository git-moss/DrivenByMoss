// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode;

import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Configuration settings for Push 1.
 *
 * @author Jürgen Moßgraber
 */
public class ConfigurationMode extends BaseMode<IItem>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ConfigurationMode (final PushControlSurface surface, final IModel model)
    {
        super ("Configuration", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (index == 0 || index == 1)
            this.surface.getConfiguration ().changePadThreshold (value);
        else if (index == 2 || index == 3)
            this.surface.getConfiguration ().changeVelocityCurve (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        display.setBlock (0, 0, "Pad Threshold").setBlock (1, 0, this.surface.getSelectedPadThreshold ());
        display.setBlock (0, 1, "Velocity Curve").setBlock (1, 1, this.surface.getSelectedVelocityCurve ());
        display.setBlock (0, 3, "Firmware: " + this.surface.getMajorVersion () + "." + this.surface.getMinorVersion ());
        if (this.surface.getConfiguration ().getPadThreshold () < 20)
            display.setRow (3, StringUtils.pad ("Low threshold maycause stuck pads!", 68));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        // Intentionally empty - mode is only for Push 1
    }
}