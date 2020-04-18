// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.MVHelper;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.IHwContinuousControl;
import de.mossgrabers.framework.daw.IBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Arrays;


/**
 * Abstract class for all modes.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractMode<S extends IControlSurface<C>, C extends Configuration> implements Mode
{
    /** Color identifier for a mode button which is off. */
    public static final String       BUTTON_COLOR_OFF = "BUTTON_COLOR_OFF";
    /** Color identifier for a mode button which is on. */
    public static final String       BUTTON_COLOR_ON  = "BUTTON_COLOR_ON";
    /** Color identifier for a mode button which is hilighted. */
    public static final String       BUTTON_COLOR_HI  = "BUTTON_COLOR_HI";
    /** Color identifier for a mode button which is on (second row). */
    public static final String       BUTTON_COLOR2_ON = "BUTTON_COLOR2_ON";
    /** Color identifier for a mode button which is hilighted (second row). */
    public static final String       BUTTON_COLOR2_HI = "BUTTON_COLOR2_HI";

    private final String             name;
    protected final S                surface;
    protected final IModel           model;
    protected final ColorManager     colorManager;
    protected final ContinuousID     firstKnob;
    protected final int              numberOfKnobs;
    protected final boolean []       isKnobTouched;

    protected IBank<? extends IItem> bank;
    protected final MVHelper<S, C>   mvHelper;
    protected boolean                isTemporary;
    protected boolean                isAbsolute;
    private boolean                  isActive;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    public AbstractMode (final String name, final S surface, final IModel model)
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
    public AbstractMode (final String name, final S surface, final IModel model, final boolean isAbsolute)
    {
        this (name, surface, model, isAbsolute, null, null, 0);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     * @param bank The parameter bank to control with this mode, might be null
     * @param firstKnob The ID of the first knob to control this mode, all other knobs must be
     *            follow up IDs
     * @param numberOfKnobs The number of knobs available to control this mode
     */
    public AbstractMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final IBank<? extends IItem> bank, final ContinuousID firstKnob, final int numberOfKnobs)
    {
        this.name = name;
        this.surface = surface;
        this.model = model;
        this.colorManager = this.model.getColorManager ();
        this.isAbsolute = isAbsolute;
        this.bank = bank;
        this.firstKnob = firstKnob;
        this.numberOfKnobs = numberOfKnobs;

        this.isTemporary = true;

        this.mvHelper = new MVHelper<> (model, surface);

        this.isKnobTouched = new boolean [this.numberOfKnobs];
        Arrays.fill (this.isKnobTouched, false);
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.name;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTemporary ()
    {
        return this.isTemporary;
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.isActive = true;
        this.bindKnobs ();
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        this.isActive = false;
        this.unbindKnobs ();
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
        return -1;
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
        for (final boolean anIsKnobTouched: this.isKnobTouched)
        {
            if (anIsKnobTouched)
                return true;
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isKnobTouched (final int index)
    {
        return index < this.isKnobTouched.length && this.isKnobTouched[index];
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return this.colorManager.getColorIndex (this.getButtonColorID (buttonID));
    }


    /**
     * Get the color ID for a button, which is controlled by the view.
     *
     * @param buttonID The ID of the button
     * @return A color ID
     */
    protected String getButtonColorID (final ButtonID buttonID)
    {
        return AbstractMode.BUTTON_COLOR_OFF;
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
    public String getSelectedItemName ()
    {
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        if (this.surface.isShiftPressed ())
        {
            this.selectPreviousItemPage ();
            return;
        }

        if (this.bank == null)
            return;
        this.bank.selectPreviousItem ();
        this.bindKnobs ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        if (this.surface.isShiftPressed ())
        {
            this.selectNextItemPage ();
            return;
        }

        if (this.bank == null)
            return;
        this.bank.selectNextItem ();
        this.bindKnobs ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        if (this.bank == null)
            return;
        this.bank.selectPreviousPage ();
        this.bindKnobs ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        if (this.bank == null)
            return;
        this.bank.selectNextPage ();
        this.bindKnobs ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemPage (final int page)
    {
        if (this.bank == null)
            return;
        final int position = page * this.bank.getPageSize ();
        this.bank.scrollTo (position);
        this.bindKnobs ();
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


    /**
     * Switch the bank object.
     *
     * @param bank The new bank to use
     */
    protected void switchBanks (final IBank<? extends IItem> bank)
    {
        this.bank = bank;

        this.bindKnobs ();
    }


    /**
     * Get the item bank, if any.
     *
     * @return The bank or null
     */
    protected final IBank<? extends IItem> getBank ()
    {
        return this.bank;
    }


    /**
     * Test if the given button ID is part of one of the button rows (ROW1_1 to ROW_6_8).
     *
     * @param row The row to test for (0-5)
     * @param buttonID THe button ID
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
    protected void bindKnobs ()
    {
        if (!this.isActive || this.bank == null || this.firstKnob == null)
            return;

        for (int i = 0; i < this.numberOfKnobs; i++)
        {
            final ContinuousID knobID = ContinuousID.get (this.firstKnob, i);
            final IItem item = this.bank.getItem (i);
            if (item instanceof IParameter)
                this.surface.getContinuous (knobID).bind ((IParameter) item);
        }
    }


    /**
     * Set the binding to default handler for the parameter bank controlled by this mode.
     */
    private void unbindKnobs ()
    {
        if (this.firstKnob == null)
            return;

        for (int i = 0; i < this.numberOfKnobs; i++)
        {
            final ContinuousID knobID = ContinuousID.get (this.firstKnob, i);
            final IHwContinuousControl continuous = this.surface.getContinuous (knobID);
            if (continuous != null)
                continuous.bind ((IParameter) null);
        }
    }
}