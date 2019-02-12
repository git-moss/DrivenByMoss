// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.view;

import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.device.SelectedDeviceMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The Parameter view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterView extends BaseView
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public ParameterView (final MaschineMikroMk3ControlSurface surface, final IModel model)
    {
        super ("Parameter", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    protected void executeFunction (final int padIndex)
    {
        ((SelectedDeviceMode<?, ?>) this.surface.getModeManager ().getMode (Modes.MODE_DEVICE_PARAMS)).selectParameter (padIndex);
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        this.model.getHost ().scheduleTask ( () -> this.surface.getDisplay ().notify (cursorDevice.getParameterPageBank ().getSelectedItem () + ": " + cursorDevice.getParameterBank ().getItem (padIndex).getName ()), 200);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (!cursorDevice.doesExist ())
            return;

        switch (index)
        {
            case 0:
                cursorDevice.getParameterBank ().scrollBackwards ();
                this.model.getHost ().scheduleTask ( () -> this.surface.getDisplay ().notify (cursorDevice.getParameterPageBank ().getSelectedItem ()), 200);
                break;
            case 1:
                cursorDevice.getParameterBank ().scrollForwards ();
                this.model.getHost ().scheduleTask ( () -> this.surface.getDisplay ().notify (cursorDevice.getParameterPageBank ().getSelectedItem ()), 200);
                break;
            case 2:
                cursorDevice.selectPrevious ();
                break;
            case 3:
                cursorDevice.selectNext ();
                break;
            default:
                // Not used
                break;
        }
    }
}