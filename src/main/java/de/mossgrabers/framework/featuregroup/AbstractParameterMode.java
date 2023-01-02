// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.featuregroup;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.FrameworkException;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BooleanSupplier;


/**
 * Abstract class for all modes which can bind parameters.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 * @param <B> The type of the item bank
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractParameterMode<S extends IControlSurface<C>, C extends Configuration, B extends IItem> extends AbstractMode<S, C> implements IParametersAdjustObserver
{
    /** Default knobs 1 to 8. **/
    public static final List<ContinuousID>      DEFAULT_KNOB_IDS   = Collections.unmodifiableList (ContinuousID.createSequentialList (ContinuousID.KNOB1, 8));

    protected BooleanSupplier                   isAlternativeFunction;

    protected IParameterProvider                defaultParameterProvider;
    protected Map<ButtonID, IParameterProvider> parameterProviders = new EnumMap<> (ButtonID.class);
    protected IBank<B>                          bank;
    protected List<ContinuousID>                controls;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    protected AbstractParameterMode (final String name, final S surface, final IModel model)
    {
        this (name, surface, model, true);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is using a setter otherwise relative change method
     *            is used
     */
    protected AbstractParameterMode (final String name, final S surface, final IModel model, final boolean isAbsolute)
    {
        this (name, surface, model, isAbsolute, (IBank<B>) null);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param isAlternativeFunction Callback function to execute the secondary function, e.g. a
     *            shift button
     */
    protected AbstractParameterMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final BooleanSupplier isAlternativeFunction)
    {
        this (name, surface, model, isAbsolute, null, null, isAlternativeFunction);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param bank The parameter bank to control with this mode, might be null
     */
    protected AbstractParameterMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final IBank<B> bank)
    {
        this (name, surface, model, isAbsolute, bank, null, surface::isShiftPressed);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param bank The parameter bank to control with this mode, might be null
     * @param controls The IDs of the knobs or faders to control this mode
     */
    protected AbstractParameterMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final IBank<B> bank, final List<ContinuousID> controls)
    {
        this (name, surface, model, isAbsolute, bank, controls, surface::isShiftPressed);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param bank The parameter bank to control with this mode, might be null
     * @param controls The IDs of the knobs or faders to control this mode
     * @param isAlternativeFunction Callback function to execute the secondary function, e.g. a
     *            shift button
     */
    protected AbstractParameterMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final IBank<B> bank, final List<ContinuousID> controls, final BooleanSupplier isAlternativeFunction)
    {
        super (name, surface, model, isAbsolute);

        this.isAlternativeFunction = isAlternativeFunction;
        this.bank = bank;

        this.setControls (controls);
    }


    protected void setControls (final List<ContinuousID> controls)
    {
        this.controls = controls == null ? Collections.emptyList () : controls;
        this.initTouchedStates (this.controls.size ());
    }


    /**
     * Set the parameters controlled by this mode.
     *
     * @param parameterProvider Interface to get a number of parameters
     */
    protected void setParameterProvider (final IParameterProvider parameterProvider)
    {
        this.setParameterProvider (null, parameterProvider);
    }


    /**
     * Set the parameters controlled by this mode if used with the given button combination.
     *
     * @param buttonID The ID of the button which can activate the given parameters
     * @param parameterProvider Interface to get a number of parameters
     */
    protected void setParameterProvider (final ButtonID buttonID, final IParameterProvider parameterProvider)
    {
        final int controlsSize = this.controls.size ();
        final int parameterSize = parameterProvider.size ();
        if (controlsSize != parameterSize)
            throw new FrameworkException ("Number of knobs (" + controlsSize + ") must match the number of parameters (" + parameterSize + ")!");

        if (buttonID == null)
        {
            this.defaultParameterProvider = parameterProvider;
            return;
        }

        final IHwButton button = this.surface.getButton (buttonID);
        if (button == null)
            throw new FrameworkException ("Attempt to set parameters for non-existing button " + buttonID + "!");
        this.parameterProviders.put (buttonID, parameterProvider);
        button.addEventHandler (ButtonEvent.DOWN, event -> this.bindControls ());
        button.addEventHandler (ButtonEvent.UP, event -> this.bindControls ());
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        if (this.defaultParameterProvider == null)
            return;
        this.defaultParameterProvider.addParametersObserver (this);
        this.bindControls ();
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        if (this.defaultParameterProvider == null)
            return;
        this.defaultParameterProvider.removeParametersObserver (this);
        this.unbindControls ();
    }


    /** {@inheritDoc} */
    @Override
    public void parametersAdjusted ()
    {
        this.bindControls ();
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final IParameterProvider parameterProvider = this.getParameterProvider ();
        if (parameterProvider == null)
            return -1;
        final IParameter param = parameterProvider.get (index);
        return param.doesExist () ? param.getValue () : -1;
    }


    /** {@inheritDoc} */
    @Override
    public void selectItem (final int index)
    {
        if (this.bank != null)
            this.bank.getItem (index).select ();
    }


    /** {@inheritDoc} */
    @Override
    public Optional<String> getSelectedItemName ()
    {
        return Optional.empty ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        if (this.isAlternativeFunction.getAsBoolean ())
        {
            this.selectPreviousItemPage ();
            return;
        }

        if (this.bank != null)
            this.bank.selectPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        if (this.isAlternativeFunction.getAsBoolean ())
        {
            this.selectNextItemPage ();
            return;
        }

        if (this.bank != null)
            this.bank.selectNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        if (this.bank != null)
            this.bank.selectPreviousPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        if (this.bank != null)
            this.bank.selectNextPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemPage (final int page)
    {
        if (this.bank == null)
            return;
        final int position = page * this.bank.getPageSize ();
        this.bank.scrollTo (position);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        return this.bank != null && this.bank.canScrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        return this.bank != null && this.bank.canScrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        return this.bank != null && this.bank.canScrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        return this.bank != null && this.bank.canScrollPageForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public String formatPageRange (final String format)
    {
        if (this.bank == null)
            return "";

        final int positionFirst = this.bank.getScrollPosition ();
        if (positionFirst < 0)
            return "";

        final int positionLast = this.bank.getPositionOfLastItem ();
        return String.format (format, Integer.valueOf (positionFirst + 1), Integer.valueOf (positionLast + 1));
    }


    /**
     * Switch the bank object.
     *
     * @param bank The new bank to use
     */
    protected void switchBanks (final IBank<B> bank)
    {
        this.bank = bank;
    }


    /**
     * Update the binding to the parameter bank controlled by this mode.
     */
    protected void bindControls ()
    {
        if (!this.isActive || this.defaultParameterProvider == null)
            return;

        final IParameterProvider parameterProvider = this.getParameterProvider ();
        for (int i = 0; i < this.controls.size (); i++)
        {
            final IParameter parameter = parameterProvider.get (i);
            this.surface.getContinuous (this.controls.get (i)).bind (parameter);
        }
    }


    /** {@inheritDoc} */
    @Override
    public IParameterProvider getParameterProvider ()
    {
        for (final Entry<ButtonID, IParameterProvider> entry: this.parameterProviders.entrySet ())
        {
            if (this.surface.isPressed (entry.getKey ()))
                return entry.getValue ();
        }
        return this.defaultParameterProvider;
    }


    /**
     * Get the bank.
     *
     * @return The bank, might be null
     */
    public IBank<B> getBank ()
    {
        return this.bank;
    }


    /**
     * Set the binding to default handler for the parameter bank controlled by this mode.
     */
    private void unbindControls ()
    {
        if (this.defaultParameterProvider == null)
            return;

        for (final ContinuousID controlID: this.controls)
            this.surface.getContinuous (controlID).bind ((IParameter) null);
    }
}