// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.mode.device;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Edit parameters mode.
 *
 * @author Jürgen Moßgraber
 */
public class SLParameterMode extends ParameterMode<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SLParameterMode (final SLControlSurface surface, final IModel model)
    {
        super (surface, model, false, ContinuousID.createSequentialList (ContinuousID.DEVICE_KNOB1, 8));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clearRow (0).clearRow (1);
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.doesExist ())
        {
            d.setRow (0, "                       Please select a device...                       ");
            d.done (0).done (1);
            return;
        }

        final IParameterBank parameterBank = cd.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = parameterBank.getItem (i);
            d.setCell (0, i, param.doesExist () ? StringUtils.shortenAndFixASCII (param.getName (), 8) : "");
            d.setCell (1, i, StringUtils.shortenAndFixASCII (param.getDisplayedValue (8), 8));
        }
        d.done (0).done (1);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        // Only bind once to knobs!
        if (!this.isActive)
            super.onActivate ();
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        // Never deactivate
    }
}