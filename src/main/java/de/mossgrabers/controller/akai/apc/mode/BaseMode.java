// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.mode;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Date;


/**
 * Base class for knob modes.
 *
 * @param <B> The type of the item bank
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseMode<B extends IItem> extends AbstractParameterMode<APCControlSurface, APCConfiguration, B>
{
    private boolean   isKnobMoving;
    private long      moveStartTime;
    private final int ledMode;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param ledMode The mode for the knob LEDs
     * @param bank The parameter bank to control with this mode, might be null
     */
    protected BaseMode (final String name, final APCControlSurface surface, final IModel model, final int ledMode, final IBank<B> bank)
    {
        super (name, surface, model, false, bank, DEFAULT_KNOB_IDS);

        this.ledMode = ledMode;

        this.isKnobMoving = false;
        this.moveStartTime = 0;
    }


    /**
     * Set a knob value.
     *
     * @param index The index of the knob
     * @param value The value
     */
    public void setValue (final int index, final int value)
    {
        // Overwrite for modes which cannot use direct parameter mapping
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        this.setValue (index, value);

        this.moveStartTime = new Date ().getTime ();
        if (this.isKnobMoving)
            return;

        this.isKnobMoving = true;
        this.startCheckKnobMovement ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (this.isKnobMoving)
            return;
        for (int i = 0; i < 8; i++)
        {
            final int value = this.getKnobValue (i);
            final boolean isOff = value < 0;
            this.surface.setLED (APCControlSurface.APC_KNOB_TRACK_KNOB_LED_MODE_1 + i, isOff ? APCControlSurface.LED_MODE_VOLUME : this.ledMode);
            this.surface.setLED (APCControlSurface.APC_KNOB_TRACK_KNOB_1 + i, isOff ? 0 : value);
        }
    }


    protected void startCheckKnobMovement ()
    {
        this.surface.scheduleTask (this::checkKnobMovement, 100);
    }


    protected void checkKnobMovement ()
    {
        if (!this.isKnobMoving)
            return;
        if (new Date ().getTime () - this.moveStartTime > 200)
            this.isKnobMoving = false;
        else
            this.startCheckKnobMovement ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}