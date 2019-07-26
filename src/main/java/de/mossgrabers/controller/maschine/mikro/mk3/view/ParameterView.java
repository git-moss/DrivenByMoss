// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.view;

import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;
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
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final Display display = this.surface.getDisplay ();
        if (!cursorDevice.doesExist ())
        {
            display.notify ("No device selected.");
            return;
        }

        ((SelectedDeviceMode<?, ?>) this.surface.getModeManager ().getMode (Modes.MODE_DEVICE_PARAMS)).selectParameter (padIndex);
        this.model.getHost ().scheduleTask ( () -> {

            final StringBuilder message = new StringBuilder ();
            final String selectedPage = cursorDevice.getParameterPageBank ().getSelectedItem ();
            if (selectedPage == null)
                message.append ("No parameters available.");
            else
            {
                message.append (selectedPage).append (": ");
                final IParameter item = cursorDevice.getParameterBank ().getItem (padIndex);
                if (item.doesExist ())
                    message.append (item.getName ());
                else
                    message.append ("None");
            }
            display.notify (message.toString ());

        }, 200);
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