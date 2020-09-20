// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.device;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.mode.AbstractMode;

import java.util.List;


/**
 * Mode for editing user control parameters.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UserMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractMode<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     * @param knobs The IDs of the knob to control this mode
     */
    public UserMode (final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> knobs)
    {
        super ("User Controls", surface, model, isAbsolute, model.getUserParameterBank (), knobs);

        this.setParametersFromBank ();

        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        final IParameter param = this.model.getUserParameterBank ().getItem (index);
        if (!param.doesExist ())
            return;

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            param.resetValue ();
        }
        param.touchValue (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        super.selectPreviousItem ();

        this.mvHelper.notifySelectedUserPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        super.selectNextItem ();

        this.mvHelper.notifySelectedUserPage ();
    }
}