// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;


/**
 * Configuration settings for Push 1.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ConfigurationMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ConfigurationMode (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        if (index == 0 || index == 1)
            this.surface.getConfiguration ().changePadThreshold (value);
        else if (index == 2 || index == 3)
            this.surface.getConfiguration ().changeVelocityCurve (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        d.clear ().setBlock (0, 0, "Pad Threshold").setBlock (1, 0, this.surface.getSelectedPadThreshold ());
        d.setBlock (0, 1, "Velocity Curve").setBlock (1, 1, this.surface.getSelectedVelocityCurve ());
        d.setBlock (0, 3, "Firmware: " + this.surface.getMajorVersion () + "." + this.surface.getMinorVersion ()).allDone ();
        if (this.surface.getConfiguration ().getPadThreshold () < 20)
            d.setRow (3, " Warning:Low threshold maycause stuck pads ");
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        // Intentionally empty - mode is only for Push 1
    }
}