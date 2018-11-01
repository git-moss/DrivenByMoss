// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.mode;

import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;


/**
 * The device parameter mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceMode extends BaseMode
{
    int index = 0;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceMode (final MaschineMikroMk3ControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (cursorDevice == null)
            return;
        final IParameter item = cursorDevice.getParameterBank ().getItem (this.index);
        if (item.doesExist ())
            item.changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (int index, boolean isTouched)
    {
        if (!isTouched)
            return;
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (cursorDevice == null)
            return;
        final IParameter item = cursorDevice.getParameterBank ().getItem (this.index);
        if (item.doesExist ())
            item.resetValue ();
    }


    /**
     * Set the selected parameter.
     *
     * @param index The index of the parameter (0-15)
     */
    public void selectParameter (final int index)
    {
        this.index = index;
    }


    /**
     * Get the index of the selected parameter.
     *
     * @return The index 0-15
     */
    public int getSelectedParameter ()
    {
        return this.index;
    }
}