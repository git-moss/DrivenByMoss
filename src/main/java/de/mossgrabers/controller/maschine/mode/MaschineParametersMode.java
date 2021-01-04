// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mode;

import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.mode.device.SelectedDeviceMode;
import de.mossgrabers.framework.parameterprovider.BankParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Arrays;


/**
 * Mode for editing the parameters of the selected device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineParametersMode extends SelectedDeviceMode<MaschineControlSurface, MaschineConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MaschineParametersMode (final MaschineControlSurface surface, final IModel model)
    {
        super (surface, model, surface.getMaschine ().hasMCUDisplay () ? DEFAULT_KNOB_IDS : null);

        if (surface.getMaschine ().hasMCUDisplay ())
            this.setParameters (new BankParameterProvider ((IParameterBank) this.bank));

        this.isKnobTouched = new boolean [9];
        Arrays.fill (this.isKnobTouched, false);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.doesExist ())
        {
            d.notify ("Please select a device...");
            return;
        }

        // Row 1 & 2
        final IParameterBank parameterBank = cd.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = parameterBank.getItem (i);
            String name = param.doesExist () ? StringUtils.shortenAndFixASCII (param.getName (), 6) : "";
            if (i == this.getSelectedParameter ())
                name = ">" + name;
            d.setCell (0, i, name).setCell (1, i, StringUtils.shortenAndFixASCII (param.getDisplayedValue (8), 8));
        }

        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        if (index < 8)
            super.onKnobTouch (index, isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        final int selectedParameter = this.getSelectedParameter ();
        if (selectedParameter == 0)
        {
            super.selectPreviousItem ();
            this.selectParameter (7);
        }
        else
            this.selectParameter (selectedParameter - 1);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        final int selectedParameter = this.getSelectedParameter ();
        if (selectedParameter == 7)
        {
            super.selectNextItem ();
            this.selectParameter (0);
        }
        else
            this.selectParameter (selectedParameter + 1);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        super.selectPreviousItem ();
        this.mvHelper.notifySelectedParameterPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        super.selectNextItem ();
        this.mvHelper.notifySelectedParameterPage ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        return this.getSelectedParameter () > 0 || super.hasPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        return this.getSelectedParameter () < 7 || super.hasNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        return super.hasPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        return super.hasNextItem ();
    }
}
