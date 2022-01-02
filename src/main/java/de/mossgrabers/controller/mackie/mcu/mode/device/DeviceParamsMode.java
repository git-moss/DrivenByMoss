// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.device;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.BaseMode;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing device remote control parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceParamsMode extends BaseMode<IParameter>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceParamsMode (final MCUControlSurface surface, final IModel model)
    {
        super ("Parameters", surface, model, model.getCursorDevice ().getParameterBank ());

        final int surfaceID = surface.getSurfaceID ();
        this.setParameterProvider (new RangeFilterParameterProvider (new BankParameterProvider (model.getCursorDevice ().getParameterBank ()), surfaceID * 8, 8));
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        final ICursorDevice cd = this.model.getCursorDevice ();
        final IParameter param = cd.getParameterBank ().getItem (index);
        if (param.doesExist ())
            param.touchValue (isTouched);
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
            d.notify ("Please select a device...");
            return;
        }

        // Row 1 & 2
        final int extenderOffset = this.surface.getExtenderOffset ();
        final IParameterBank parameterBank = cd.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = parameterBank.getItem (extenderOffset + i);
            d.setCell (0, i, param.doesExist () ? StringUtils.shortenAndFixASCII (param.getName (6), 6) : "").setCell (1, i, StringUtils.shortenAndFixASCII (param.getDisplayedValue (6), 6));
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
            this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_SINGLE_DOT, param.doesExist () ? Math.max (1, param.getValue ()) : 0, upperBound);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final int extenderOffset = this.surface.getExtenderOffset ();
        this.model.getCursorDevice ().getParameterBank ().getItem (extenderOffset + index).resetValue ();
    }
}