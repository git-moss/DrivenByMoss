// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.device;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.BaseMode;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing user parameters.
 *
 * @author Jürgen Moßgraber
 */
public class UserMode extends BaseMode<IParameter>
{
    protected IParameterProvider projectParameterProvider;
    protected IParameterProvider trackParameterProvider;
    protected boolean            isProjectMode = true;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public UserMode (final MCUControlSurface surface, final IModel model)
    {
        super ("User Parameters", surface, model, model.getProject ().getParameterBank ());

        final int surfaceID = surface.getSurfaceID ();
        this.projectParameterProvider = new RangeFilterParameterProvider (new BankParameterProvider (model.getProject ().getParameterBank ()), surfaceID * 8, 8);
        this.trackParameterProvider = new RangeFilterParameterProvider (new BankParameterProvider (model.getCursorTrack ().getParameterBank ()), surfaceID * 8, 8);
        this.setParameterProvider (this.projectParameterProvider);
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
            d.setCell (0, i, param.doesExist () ? StringUtils.shortenAndFixASCII (param.getName (textLength), textLength) : "").setCell (1, i, StringUtils.shortenAndFixASCII (param.getDisplayedValue (textLength), textLength));
            colors[i] = param.doesExist () ? ColorEx.WHITE : ColorEx.BLACK;
        }

        d.allDone ();

        this.surface.sendDisplayColor (colors);
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


    /**
     * Set the project or track parameters mode.
     *
     * @param isProjectMode
     */
    public void setMode (final boolean isProjectMode)
    {
        this.isProjectMode = isProjectMode;
        this.switchBanks (this.isProjectMode ? this.model.getProject ().getParameterBank () : this.model.getCursorTrack ().getParameterBank ());
        this.setParameterProvider (this.isProjectMode ? this.projectParameterProvider : this.trackParameterProvider);
        this.bindControls ();
    }


    /**
     * Get the currently selected bank.
     *
     * @return The bank
     */
    public IBank<?> getParameterBank ()
    {
        return this.bank;
    }


    /**
     * Is project mode active?
     *
     * @return True if project mode active
     */
    public boolean isProjectMode ()
    {
        return this.isProjectMode;
    }
}