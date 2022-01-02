// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.featuregroup;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.FrameworkException;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BooleanSupplier;


/**
 * Abstract class for all modes.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 * @param <B> The type of the item bank
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractMode<S extends IControlSurface<C>, C extends Configuration, B extends IItem> extends AbstractFeatureGroup<S, C> implements IMode, IParametersAdjustObserver
{
    /** Color identifier for a mode button which is highlighted. */
    public static final String                  BUTTON_COLOR_HI    = "BUTTON_COLOR_HI";
    /** Color identifier for a mode button which is on (second row). */
    public static final String                  BUTTON_COLOR2_ON   = "BUTTON_COLOR2_ON";
    /** Color identifier for a mode button which is highlighted (second row). */
    public static final String                  BUTTON_COLOR2_HI   = "BUTTON_COLOR2_HI";

    /** Default knobs 1 to 8. **/
    public static final List<ContinuousID>      DEFAULT_KNOB_IDS   = Collections.unmodifiableList (ContinuousID.createSequentialList (ContinuousID.KNOB1, 8));

    protected BooleanSupplier                   isAlternativeFunction;

    protected IParameterProvider                defaultParameterProvider;
    protected Map<ButtonID, IParameterProvider> parameterProviders = new EnumMap<> (ButtonID.class);
    protected IBank<B>                          bank;
    protected List<ContinuousID>                controls;
    protected boolean                           isAbsolute;
    protected boolean                           isActive;
    protected boolean []                        isKnobTouched;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    protected AbstractMode (final String name, final S surface, final IModel model)
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
    protected AbstractMode (final String name, final S surface, final IModel model, final boolean isAbsolute)
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
    protected AbstractMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final BooleanSupplier isAlternativeFunction)
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
    protected AbstractMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final IBank<B> bank)
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
    protected AbstractMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final IBank<B> bank, final List<ContinuousID> controls)
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
    protected AbstractMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final IBank<B> bank, final List<ContinuousID> controls, final BooleanSupplier isAlternativeFunction)
    {
        super (name, surface, model);

        this.isAlternativeFunction = isAlternativeFunction;
        this.isAbsolute = isAbsolute;
        this.bank = bank;

        this.setControls (controls);
    }


    protected void setControls (final List<ContinuousID> controls)
    {
        this.controls = controls == null ? Collections.emptyList () : controls;
        this.isKnobTouched = new boolean [this.controls.size ()];
        Arrays.fill (this.isKnobTouched, false);
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
        if (this.controls.size () != parameterProvider.size ())
            throw new FrameworkException ("Number of knobs must match the number of parameters!");

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
        this.isActive = true;

        if (this.defaultParameterProvider == null)
            return;
        this.defaultParameterProvider.addParametersObserver (this);
        this.bindControls ();
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        this.isActive = false;

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
    public void updateDisplay ()
    {
        // Intentionally empty
    }


    /**
     * Get if absolute or relative value changing is enabled for the mode.
     *
     * @return True if absolute mode is on
     */
    public boolean isAbsolute ()
    {
        return this.isAbsolute;
    }


    /**
     * Get if absolute or relative value changing is enabled for the mode.
     *
     * @param isAbsolute True if absolute mode is on
     */
    public void setAbsolute (final boolean isAbsolute)
    {
        this.isAbsolute = isAbsolute;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        // Intentionally empty
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
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAnyKnobTouched ()
    {
        for (final boolean isTouched: this.isKnobTouched)
        {
            if (isTouched)
                return true;
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public int getTouchedKnob ()
    {
        for (int i = 0; i < this.isKnobTouched.length; i++)
        {
            if (this.isKnobTouched[i])
                return i;
        }
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isKnobTouched (final int index)
    {
        return index < this.isKnobTouched.length && this.isKnobTouched[index];
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
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
     * Test if the given button ID is part of one of the button rows (ROW1_1 to ROW_6_8).
     *
     * @param row The row to test for (0-5)
     * @param buttonID The button ID
     * @return The index of the button in the row or -1 if the button is not part of the row.
     */
    protected int isButtonRow (final int row, final ButtonID buttonID)
    {
        final int ordinal = buttonID.ordinal ();
        if (row == 0 && ordinal >= ButtonID.ROW1_1.ordinal () && ordinal <= ButtonID.ROW1_8.ordinal ())
            return ordinal - ButtonID.ROW1_1.ordinal ();
        if (row == 1 && ordinal >= ButtonID.ROW2_1.ordinal () && ordinal <= ButtonID.ROW2_8.ordinal ())
            return ordinal - ButtonID.ROW2_1.ordinal ();
        if (row == 2 && ordinal >= ButtonID.ROW3_1.ordinal () && ordinal <= ButtonID.ROW3_8.ordinal ())
            return ordinal - ButtonID.ROW3_1.ordinal ();
        if (row == 3 && ordinal >= ButtonID.ROW4_1.ordinal () && ordinal <= ButtonID.ROW4_8.ordinal ())
            return ordinal - ButtonID.ROW4_1.ordinal ();
        if (row == 4 && ordinal >= ButtonID.ROW5_1.ordinal () && ordinal <= ButtonID.ROW5_8.ordinal ())
            return ordinal - ButtonID.ROW5_1.ordinal ();
        if (row == 5 && ordinal >= ButtonID.ROW6_1.ordinal () && ordinal <= ButtonID.ROW6_8.ordinal ())
            return ordinal - ButtonID.ROW6_1.ordinal ();
        return -1;
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
            this.surface.getContinuous (this.controls.get (i)).bind (parameterProvider.get (i));
    }


    /**
     * Get the currently active parameter provider, depending on pressed buttons.
     *
     * @return The active parameter provider, might be null if none is set
     */
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