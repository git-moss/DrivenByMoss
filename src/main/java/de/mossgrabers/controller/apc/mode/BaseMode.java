// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.mode;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Date;


/**
 * Base class for knob modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseMode extends AbstractMode<APCControlSurface, APCConfiguration>
{
    private boolean isKnobMoving;
    private long    moveStartTime;
    private int     defaultValue;
    private int     ledMode;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param ledMode The mode for the knob LEDs
     * @param defaultValue Default value to use
     * @param bank The parameter bank to control with this mode, might be null
     */
    public BaseMode (final String name, final APCControlSurface surface, final IModel model, final int ledMode, final int defaultValue, final IBank<? extends IItem> bank)
    {
        super (name, surface, model, false, bank, DEFAULT_KNOB_IDS);

        this.ledMode = ledMode;
        this.defaultValue = defaultValue;

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
    public void onActivate ()
    {
        super.onActivate ();

        for (int i = 0; i < 8; i++)
            this.surface.setLED (APCControlSurface.APC_KNOB_TRACK_KNOB_LED_1 + i, this.ledMode);
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
            this.surface.setLED (APCControlSurface.APC_KNOB_TRACK_KNOB_1 + i, value < 0 ? this.defaultValue : value);
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