// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.device;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IParameterPageBank;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.mode.AbstractMode;


/**
 * The device parameter mode. The knobs control the value of the parameter on the parameter page.
 * device.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractMode<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     */
    public ParameterMode (final S surface, final IModel model, final boolean isAbsolute)
    {
        super ("Parameters", surface, model, isAbsolute);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (cursorDevice == null)
            return;
        final IParameter item = cursorDevice.getParameterBank ().getItem (index);
        if (item == null || !item.doesExist ())
            return;
        if (this.isAbsolute)
            item.setValue (value);
        else
            item.changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (!isTouched)
            return;
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (cursorDevice == null)
            return;
        final IParameter item = cursorDevice.getParameterBank ().getItem (index);
        if (item.doesExist ())
            item.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (cursorDevice == null)
            return -1;
        final IParameter item = cursorDevice.getParameterBank ().getItem (index);
        return item != null && item.doesExist () ? item.getValue () : -1;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedItemName ()
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (cursorDevice == null || !cursorDevice.doesExist ())
            return null;
        final IParameterPageBank parameterPageBank = cursorDevice.getParameterPageBank ();
        return cursorDevice.getName () + " - " + parameterPageBank.getSelectedItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        if (this.surface.isShiftPressed ())
            this.model.getCursorDevice ().getDeviceBank ().selectPreviousPage ();
        else
            this.model.getCursorDevice ().selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        if (this.surface.isShiftPressed ())
            this.model.getCursorDevice ().getDeviceBank ().selectNextPage ();
        else
            this.model.getCursorDevice ().selectNext ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        this.model.getCursorDevice ().getParameterBank ().scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        this.model.getCursorDevice ().getParameterBank ().scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectItem (final int index)
    {
        this.model.getCursorDevice ().getParameterBank ().selectItemAtPosition (index);
    }
}