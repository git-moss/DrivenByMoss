// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl.mode.device;

import de.mossgrabers.controller.sl.SLConfiguration;
import de.mossgrabers.controller.sl.controller.SLControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Edit parameters mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceParamsMode extends AbstractMode<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DeviceParamsMode (final SLControlSurface surface, final IModel model)
    {
        super ("Parameters", surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clearRow (0).clearRow (2);

        final ICursorDevice cd = this.model.getCursorDevice ();
        final IParameterBank parameterBank = cd.getParameterBank ();
        if (cd.doesExist ())
        {
            for (int i = 0; i < 8; i++)
            {
                final IParameter param = parameterBank.getItem (i);
                d.setCell (0, i, param.doesExist () ? StringUtils.shortenAndFixASCII (param.getName (), 8) : "").setCell (2, i, param.getDisplayedValue (8));
            }
        }
        else
            d.setRow (0, "                       Please select a device...                       ");
        d.done (0).done (2);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        this.model.getCursorDevice ().getParameterBank ().getItem (index).changeValue (value);
    }


    /**
     * Move to the previous parameter page.
     */
    public void previousPage ()
    {
        this.model.getCursorDevice ().getParameterBank ().scrollBackwards ();
    }


    /**
     * Move to the next parameter page.
     */
    public void nextPage ()
    {
        this.model.getCursorDevice ().getParameterBank ().scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected IParameterBank getBank ()
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        return cursorDevice == null ? null : cursorDevice.getParameterBank ();
    }
}