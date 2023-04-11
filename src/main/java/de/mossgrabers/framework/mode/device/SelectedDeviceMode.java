// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.device;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameter.IParameter;

import java.util.List;
import java.util.function.BooleanSupplier;


/**
 * The selected device parameter mode. All knobs control the value of the selected parameter of the
 * cursor device.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class SelectedDeviceMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractParameterMode<S, C, IParameter>
{
    private int selParam = 0;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param knobs The IDs of the knob to control this mode
     */
    public SelectedDeviceMode (final S surface, final IModel model, final List<ContinuousID> knobs)
    {
        super ("Parameters", surface, model, false, model.getCursorDevice ().getParameterBank (), knobs);
    }


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param knobs The IDs of the knob to control this mode
     * @param isAlternativeFunction Callback function to execute the secondary function, e.g. a
     *            shift button
     */
    public SelectedDeviceMode (final S surface, final IModel model, final List<ContinuousID> knobs, final BooleanSupplier isAlternativeFunction)
    {
        super ("Parameters", surface, model, false, model.getCursorDevice ().getParameterBank (), knobs, isAlternativeFunction);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (cursorDevice == null)
            return;
        final IParameter item = cursorDevice.getParameterBank ().getItem (index < 0 ? this.selParam : index);
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
        final IParameter item = cursorDevice.getParameterBank ().getItem (index < 0 ? this.selParam : index);
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
}