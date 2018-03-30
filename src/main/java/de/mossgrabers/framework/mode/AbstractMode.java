// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;


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
    public static final String BUTTON_COLOR_OFF = "BUTTON_COLOR_OFF";
    /** Color identifier for a mode button which is on. */
    public static final String BUTTON_COLOR_ON  = "BUTTON_COLOR_ON";
    /** Color identifier for a mode button which is hilighted. */
    public static final String BUTTON_COLOR_HI  = "BUTTON_COLOR_HI";
    /** Color identifier for a mode button which is on (second row). */
    public static final String BUTTON_COLOR2_ON = "BUTTON_COLOR2_ON";
    /** Color identifier for a mode button which is hilighted (second row). */
    public static final String BUTTON_COLOR2_HI = "BUTTON_COLOR2_HI";

    protected S                surface;
    protected IModel           model;
    protected boolean          isTemporary;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public AbstractMode (final S surface, final IModel model)
    {
        this.surface = surface;
        this.model = model;
        this.isTemporary = true;
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
    public void onValueKnob (final int index, final int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTemporary ()
    {
        return this.isTemporary;
    }
}