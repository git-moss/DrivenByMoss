// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.mode.device;

import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing project remote parameters.
 *
 * @author Jürgen Moßgraber
 */
public class SLMkIIIProjectParamsMode extends AbstractParametersMode<IParameter>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SLMkIIIProjectParamsMode (final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Project Parameters", surface, model, model.getProject ().getParameterBank ());

        this.setParameterProvider (new BankParameterProvider (this.bank));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        // Combination with Arrow Up
        if (this.surface.isLongPressed (ButtonID.ARROW_UP))
        {
            this.onButtonArrowUp (index);
            return;
        }

        // Normal behavior - user parameters
        this.selectItemPage (index);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        // Colors in combination with Arrow Up
        if (this.surface.isLongPressed (ButtonID.ARROW_UP))
            return this.getButtonColorArrowUp (buttonID);

        final int index = this.isButtonRow (0, buttonID);
        final IParameterPageBank parameterPageBank = ((IParameterBank) this.bank).getPageBank ();
        final int selectedPage = parameterPageBank.getSelectedItemIndex ();
        return index == selectedPage ? SLMkIIIColorManager.SLMKIII_WHITE : SLMkIIIColorManager.SLMKIII_WHITE_HALF;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();

        d.setCell (0, 8, "Projct FX").setCell (1, 8, "Page");

        // Row 1 & 2
        for (int i = 0; i < this.bank.getPageSize (); i++)
        {
            final IParameter param = this.bank.getItem (i);
            d.setCell (0, i, param.doesExist () ? StringUtils.fixASCII (param.getName (9)) : "").setCell (1, i, param.getDisplayedValue (9));

            final int color = param.doesExist () ? SLMkIIIColorManager.SLMKIII_WHITE : SLMkIIIColorManager.SLMKIII_BLACK;
            d.setPropertyColor (i, 0, color);
            d.setPropertyColor (i, 1, color);
        }

        // Row 4
        this.drawRow4 (d);

        this.setButtonInfo (d);
        d.allDone ();
    }


    private void drawRow4 (final SLMkIIIDisplay d)
    {
        if (this.surface.isLongPressed (ButtonID.ARROW_UP))
        {
            this.drawRow4ArrowUp (d);
            return;
        }

        final IParameterPageBank parameterPageBank = ((IParameterBank) this.bank).getPageBank ();
        final int selectedPage = parameterPageBank.getSelectedItemIndex ();

        for (int i = 0; i < this.bank.getPageSize (); i++)
        {
            final String pageName = StringUtils.limit (parameterPageBank.getItem (i), 9);
            d.setCell (3, i, pageName);
            d.setPropertyColor (i, 2, pageName.isBlank () ? SLMkIIIColorManager.SLMKIII_BLACK : SLMkIIIColorManager.SLMKIII_WHITE);
            d.setPropertyValue (i, 1, selectedPage == i ? 1 : 0);
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getModeColor ()
    {
        return SLMkIIIColorManager.SLMKIII_WHITE;
    }
}