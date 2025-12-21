// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.device;

import java.util.Arrays;
import java.util.Optional;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration.MainDisplay;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.BaseMode;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing track parameters.
 *
 * @author Jürgen Moßgraber
 */
public class MCUTrackParametersMode extends BaseMode<IParameter>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MCUTrackParametersMode (final MCUControlSurface surface, final IModel model)
    {
        super ("Track Parameters", surface, model, model.getCursorTrack ().getParameterBank ());

        final int surfaceID = surface.getSurfaceID ();
        this.setParameterProvider (new RangeFilterParameterProvider (new BankParameterProvider (model.getCursorTrack ().getParameterBank ()), surfaceID * 8, 8));
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final int extenderOffset = this.surface.getExtenderOffset ();
        this.bank.getItem (extenderOffset + index).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);

        final IParameter param = this.bank.getItem (index);
        if (param.doesExist ())
            param.touchValue (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.drawDisplay2 ();

        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        final ColorEx [] colors = new ColorEx [8];

        // Row 1 & 2
        final int extenderOffset = this.surface.getExtenderOffset ();
        final int textLength = this.getTextLength ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = this.bank.getItem (extenderOffset + i);
            d.setCell (0, i, param.doesExist () ? StringUtils.shortenAndFixASCII (param.getName (textLength), textLength) : "");
            d.setCell (1, i, StringUtils.shortenAndFixASCII (param.getDisplayedValue (textLength), textLength));
            colors[i] = param.doesExist () ? ColorEx.WHITE : ColorEx.BLACK;
        }

        if (this.surface.getConfiguration ().getMainDisplayType () == MainDisplay.ASPARION && this.surface.getSurfaceID () == 0)
        {
            d.clearRow (0);
            d.setCell (0, 0, "Track");
            d.setCell (0, 1, "Parameters");

            if (this.bank instanceof final IParameterBank parameterBank)
            {
                final Optional<String> selectedPage = parameterBank.getPageBank ().getSelectedItem ();
                if (selectedPage.isPresent ())
                    d.setCell (0, 2, StringUtils.shortenAndFixASCII (selectedPage.get (), this.getTextLength ()));
            }
        }

        d.allDone ();

        this.surface.sendDisplayColor (colors);

        final int [] indices = new int [8];
        Arrays.fill (indices, 0);
        if (this.getExtenderOffset () == 0)
        {
            if (this.bank instanceof final IParameterBank parameterBank)
                indices[2] = parameterBank.getPageBank ().getSelectedItemIndex () + 1;
        }
        this.surface.setItemIndices (indices);
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        final int extenderOffset = this.surface.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = this.bank.getItem (extenderOffset + i);
            this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_SINGLE_DOT, param.doesExist () ? Math.max (1, param.getValue ()) : 0, upperBound);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final int extenderOffset = this.surface.getExtenderOffset ();
        this.resetParameter (this.bank.getItem (extenderOffset + index));
    }
}