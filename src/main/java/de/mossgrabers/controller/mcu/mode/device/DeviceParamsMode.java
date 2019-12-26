// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.mode.device;

import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mcu.mode.BaseMode;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.utils.StringUtils;


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
    public DeviceParamsMode (final MCUControlSurface surface, final IModel model)
    {
        super ("Parameters", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final int extenderOffset = this.surface.getExtenderOffset ();
        this.model.getCursorDevice ().getParameterBank ().getItem (extenderOffset + index).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.drawDisplay2 ();

        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.doesExist ())
        {
            d.notify ("Please select a Device...");
            return;
        }

        // Row 1 & 2
        final int extenderOffset = this.surface.getExtenderOffset ();
        final IParameterBank parameterBank = cd.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = parameterBank.getItem (extenderOffset + i);
            d.setCell (0, i, param.doesExist () ? StringUtils.fixASCII (param.getName ()) : "").setCell (1, i, param.getDisplayedValue (8));
        }

        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        final ICursorDevice cd = this.model.getCursorDevice ();
        final int extenderOffset = this.surface.getExtenderOffset ();
        final IParameterBank parameterBank = cd.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = parameterBank.getItem (extenderOffset + i);
            this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, param.doesExist () ? param.getValue () : 0, upperBound);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final int extenderOffset = this.surface.getExtenderOffset ();
        this.model.getCursorDevice ().getParameterBank ().getItem (extenderOffset + index).resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    protected IParameterBank getBank ()
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        return cursorDevice == null ? null : cursorDevice.getParameterBank ();
    }
}