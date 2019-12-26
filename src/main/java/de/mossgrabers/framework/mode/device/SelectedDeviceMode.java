// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.device;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.mode.AbstractMode;


/**
 * The selected device parameter mode. All knobs control the value of the selected parameter of the
 * cursor device.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectedDeviceMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractMode<S, C>
{
    int index = 0;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SelectedDeviceMode (final S surface, final IModel model)
    {
        super ("Parameters", surface, model, false);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
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
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (cursorDevice == null)
            return;
        final IParameter item = cursorDevice.getParameterBank ().getItem (this.index);
        if (!item.doesExist ())
            return;

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            item.resetValue ();
        }
        item.touchValue (isTouched);
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


    /** {@inheritDoc} */
    @Override
    protected IParameterBank getBank ()
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        return cursorDevice == null ? null : cursorDevice.getParameterBank ();
    }
}