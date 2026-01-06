// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.device;

import java.util.List;
import java.util.function.BooleanSupplier;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;


/**
 * Mode for editing project or track remote control parameters.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class ProjectParamsMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractParameterMode<S, C, IParameter>
{
    protected IParameterProvider projectParameterProvider;
    protected IParameterProvider trackParameterProvider;
    protected boolean            isProjectMode    = true;
    protected boolean            notifyPageChange = true;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param knobs The IDs of the knob to control this mode
     */
    public ProjectParamsMode (final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> knobs)
    {
        this (surface, model, isAbsolute, knobs, surface::isShiftPressed);
    }


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param knobs The IDs of the knob to control this mode
     * @param isAlternativeFunction Callback function to execute the secondary function, e.g. a
     *            shift button
     */
    public ProjectParamsMode (final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> knobs, final BooleanSupplier isAlternativeFunction)
    {
        this (surface, model, isAbsolute, knobs, isAlternativeFunction, true);
    }


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param knobs The IDs of the knob to control this mode
     * @param isAlternativeFunction Callback function to execute the secondary function, e.g. a
     *            shift button
     * @param addParameterProvider True to add the default parameter provider
     */
    public ProjectParamsMode (final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> knobs, final BooleanSupplier isAlternativeFunction, final boolean addParameterProvider)
    {
        super ("Project Parameters", surface, model, isAbsolute, model.getProject ().getParameterBank (), knobs, isAlternativeFunction);

        if (addParameterProvider && knobs != null)
        {
            this.projectParameterProvider = new BankParameterProvider (model.getProject ().getParameterBank ());
            this.trackParameterProvider = new BankParameterProvider (model.getCursorTrack ().getParameterBank ());
            this.setParameterProvider (this.projectParameterProvider);
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.isProjectMode ? "Project Parameters" : "Track Parameters";
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);

        final IParameter param = this.bank.getItem (index);
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

        this.notifyPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        super.selectNextItem ();

        this.notifyPage ();
    }


    /**
     * Toggle the project and track parameters mode.
     */
    public void toggleMode ()
    {
        this.setMode (!this.isProjectMode);
    }


    /**
     * Set the project or track parameters mode.
     *
     * @param isProjectMode
     */
    public void setMode (final boolean isProjectMode)
    {
        this.isProjectMode = isProjectMode;
        this.switchBanks (this.isProjectMode ? this.model.getProject ().getParameterBank () : this.model.getCursorTrack ().getParameterBank ());
        if (this.controls != null && !this.controls.isEmpty ())
            this.setParameterProvider (this.isProjectMode ? this.projectParameterProvider : this.trackParameterProvider);
        this.bindControls ();
    }


    private void notifyPage ()
    {
        if (this.notifyPageChange)
        {
            if (this.isProjectMode)
                this.mvHelper.notifySelectedProjectParameterPage ();
            else
                this.mvHelper.notifySelectedTrackParameterPage ();
        }
    }
}