// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.utils.ButtonEvent;


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
    public static final String   BUTTON_COLOR_OFF = "BUTTON_COLOR_OFF";
    /** Color identifier for a mode button which is on. */
    public static final String   BUTTON_COLOR_ON  = "BUTTON_COLOR_ON";
    /** Color identifier for a mode button which is hilighted. */
    public static final String   BUTTON_COLOR_HI  = "BUTTON_COLOR_HI";
    /** Color identifier for a mode button which is on (second row). */
    public static final String   BUTTON_COLOR2_ON = "BUTTON_COLOR2_ON";
    /** Color identifier for a mode button which is hilighted (second row). */
    public static final String   BUTTON_COLOR2_HI = "BUTTON_COLOR2_HI";

    private final String         name;
    protected final S            surface;
    protected final IModel       model;
    protected final ColorManager colorManager;
    protected boolean            isTemporary;
    protected boolean            isAbsolute;


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
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     */
    public AbstractMode (final String name, final S surface, final IModel model, final boolean isAbsolute)
    {
        this.name = name;
        this.surface = surface;
        this.model = model;
        this.colorManager = this.model.getColorManager ();
        this.isAbsolute = isAbsolute;

        this.isTemporary = true;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.name;
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        // Intentionally empty
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
    public boolean isTemporary ()
    {
        return this.isTemporary;
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
        final IBank<? extends IItem> bank = this.getBank ();
        if (bank != null)
            bank.getItem (index).select ();
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

        final IBank<? extends IItem> bank = this.getBank ();
        if (bank != null)
            bank.selectPreviousItem ();
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

        final IBank<? extends IItem> bank = this.getBank ();
        if (bank != null)
            bank.selectNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        final IBank<? extends IItem> bank = this.getBank ();
        if (bank != null)
            bank.selectPreviousPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        final IBank<? extends IItem> bank = this.getBank ();
        if (bank != null)
            bank.selectNextPage ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        final IBank<? extends IItem> bank = this.getBank ();
        return bank != null && bank.canScrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        final IBank<? extends IItem> bank = this.getBank ();
        return bank != null && bank.canScrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        final IBank<? extends IItem> bank = this.getBank ();
        return bank != null && bank.canScrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        final IBank<? extends IItem> bank = this.getBank ();
        return bank != null && bank.canScrollPageForwards ();
    }


    /**
     * Get the item bank, if any.
     *
     * @return The bank or null
     */
    protected IBank<? extends IItem> getBank ()
    {
        return null;
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
}