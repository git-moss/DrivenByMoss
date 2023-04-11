package de.mossgrabers.framework.featuregroup;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Arrays;
import java.util.Optional;


/**
 * Abstract class for all modes.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractFeatureGroup<S, C> implements IMode
{
    /** Color identifier for a mode button which is highlighted. */
    public static final String BUTTON_COLOR_HI  = "BUTTON_COLOR_HI";
    /** Color identifier for a mode button which is on (second row). */
    public static final String BUTTON_COLOR2_ON = "BUTTON_COLOR2_ON";
    /** Color identifier for a mode button which is highlighted (second row). */
    public static final String BUTTON_COLOR2_HI = "BUTTON_COLOR2_HI";

    protected boolean          isAbsolute;
    protected boolean          isActive;

    private boolean []         isKnobTouched;
    private int                lastTouchedKnob;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     */
    protected AbstractMode (final String name, final S surface, final IModel model, final boolean isAbsolute)
    {
        super (name, surface, model);

        this.isAbsolute = isAbsolute;
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.isActive = true;
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        this.isActive = false;
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
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobColor (final int index)
    {
        return 0;
    }


    /**
     * Initializes the touch states to the given size.
     *
     * @param size The size for the touch states
     */
    protected final void initTouchedStates (final int size)
    {
        this.isKnobTouched = new boolean [size];
        Arrays.fill (this.isKnobTouched, false);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setTouchedKnob (final int knobIndex, final boolean isTouched)
    {
        this.isKnobTouched[knobIndex] = isTouched;
        if (isTouched)
            this.lastTouchedKnob = knobIndex;
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
    public int getLastTouchedKnob ()
    {
        return this.lastTouchedKnob;
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
        // Intentionally empty
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
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemPage (final int page)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String formatPageRange (final String format)
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public IParameterProvider getParameterProvider ()
    {
        return null;
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
     * Test if the given button ID is part of one of the button rows (ROW1_1 to ROW_6_8). If yes it
     * returns the row.
     *
     * @param buttonID The button ID
     * @return The index of the button row (zero based) or -1 if the button is not a row button.
     */
    protected int getButtonRow (final ButtonID buttonID)
    {
        final int ordinal = buttonID.ordinal ();
        if (ordinal >= ButtonID.ROW1_1.ordinal () && ordinal <= ButtonID.ROW1_8.ordinal ())
            return 0;
        if (ordinal >= ButtonID.ROW2_1.ordinal () && ordinal <= ButtonID.ROW2_8.ordinal ())
            return 1;
        if (ordinal >= ButtonID.ROW3_1.ordinal () && ordinal <= ButtonID.ROW3_8.ordinal ())
            return 2;
        if (ordinal >= ButtonID.ROW4_1.ordinal () && ordinal <= ButtonID.ROW4_8.ordinal ())
            return 3;
        if (ordinal >= ButtonID.ROW5_1.ordinal () && ordinal <= ButtonID.ROW5_8.ordinal ())
            return 4;
        if (ordinal >= ButtonID.ROW6_1.ordinal () && ordinal <= ButtonID.ROW6_8.ordinal ())
            return 5;
        return -1;
    }
}
