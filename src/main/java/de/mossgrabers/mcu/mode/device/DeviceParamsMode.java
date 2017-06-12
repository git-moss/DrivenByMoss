// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.mode.device;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.data.ParameterData;
import de.mossgrabers.mcu.controller.MCUControlSurface;
import de.mossgrabers.mcu.mode.BaseMode;


/**
 * Mode for editing device remote control parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceParamsMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceParamsMode (final MCUControlSurface surface, final Model model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.model.getCursorDevice ().changeParameter (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.drawDisplay2 ();

        final Display d = this.surface.getDisplay ().clear ();

        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        if (!cd.hasSelectedDevice ())
        {
            d.notify ("Please select a Device...", true, false);
            return;
        }

        // Row 1 & 2
        for (int i = 0; i < 8; i++)
        {
            final ParameterData param = cd.getFXParam (i);
            d.setCell (0, i, param.doesExist () ? param.getName () : "").setCell (1, i, param.getDisplayedValue (8));
        }

        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    protected void updateKnobLEDs ()
    {
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        for (int i = 0; i < 8; i++)
        {
            final ParameterData param = cd.getFXParam (i);
            this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, param.doesExist () ? param.getValue () : 0, upperBound);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        this.model.getCursorDevice ().resetParameter (index);
    }
}