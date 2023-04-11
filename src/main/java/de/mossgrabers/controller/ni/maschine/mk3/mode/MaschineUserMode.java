// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.mode;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing the user parameters.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineUserMode extends AbstractParameterMode<MaschineControlSurface, MaschineConfiguration, IParameter>
{
    private int selParam = 0;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MaschineUserMode (final MaschineControlSurface surface, final IModel model)
    {
        super ("User", surface, model, false, model.getUserParameterBank (), DEFAULT_KNOB_IDS);

        this.setParameterProvider (new BankParameterProvider (this.bank));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        // Row 1 & 2
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = this.bank.getItem (i);
            String name = param.doesExist () ? StringUtils.shortenAndFixASCII (param.getName (), 6) : "";
            if (i == this.getSelectedParameter ())
                name = ">" + name;
            d.setCell (0, i, name).setCell (1, i, param.getDisplayedValue (8));
        }

        d.allDone ();
    }


    /**
     * Set the selected parameter.
     *
     * @param index The index of the parameter (0-15)
     */
    public void selectParameter (final int index)
    {
        this.selParam = index;
    }


    /**
     * Get the index of the selected parameter.
     *
     * @return The index 0-15
     */
    public int getSelectedParameter ()
    {
        return this.selParam == -1 ? 0 : this.selParam;
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
        this.mvHelper.notifySelectedUserPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        super.selectNextItem ();
        this.mvHelper.notifySelectedUserPage ();
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
