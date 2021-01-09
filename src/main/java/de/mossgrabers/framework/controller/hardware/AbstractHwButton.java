// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.ArrayList;
import java.util.List;


/**
 * Abstract implementation of a proxy to a button on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractHwButton extends AbstractHwInputControl implements IHwButton
{
    private static final int               BUTTON_STATE_INTERVAL = 400;

    protected TriggerCommand               command;
    protected IHwLight                     light;

    private ButtonEvent                    state;
    private boolean                        isConsumed;
    private int                            pressedVelocity       = 0;

    private final List<ButtonEventHandler> downEventHandlers     = new ArrayList<> ();
    private final List<ButtonEventHandler> upEventHandlers       = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param host The host
     * @param label The label of the button
     */
    public AbstractHwButton (final IHost host, final String label)
    {
        super (host, label);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isBound ()
    {
        return this.command != null;
    }


    /**
     * Handle a button press.
     *
     * @param value The pressure value
     */
    protected void handleButtonPressed (final double value)
    {
        if (value == 0)
            return;

        this.state = ButtonEvent.DOWN;
        this.isConsumed = false;

        this.host.scheduleTask (this::checkButtonState, BUTTON_STATE_INTERVAL);
        this.pressedVelocity = (int) (value * 127.0);
        if (this.command != null)
            this.command.execute (ButtonEvent.DOWN, this.pressedVelocity);

        this.downEventHandlers.forEach (handler -> handler.handle (ButtonEvent.DOWN));
    }


    /**
     * Handle a button release.
     */
    protected void handleButtonRelease ()
    {
        if (!this.isBound ())
            return;

        this.state = ButtonEvent.UP;
        if (this.command != null && !this.isConsumed)
            this.command.execute (ButtonEvent.UP, 0);

        this.upEventHandlers.forEach (handler -> handler.handle (ButtonEvent.UP));
    }


    /** {@inheritDoc} */
    @Override
    public void addLight (final IHwLight light)
    {
        this.light = light;
    }


    /** {@inheritDoc} */
    @Override
    public IHwLight getLight ()
    {
        return this.light;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPressed ()
    {
        return this.state == ButtonEvent.DOWN || this.state == ButtonEvent.LONG;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLongPressed ()
    {
        return this.state == ButtonEvent.LONG;
    }


    /** {@inheritDoc} */
    @Override
    public void addEventHandler (final ButtonEvent event, final ButtonEventHandler eventHandler)
    {
        if (event == ButtonEvent.DOWN)
            this.downEventHandlers.add (eventHandler);
        else if (event == ButtonEvent.UP)
            this.upEventHandlers.add (eventHandler);
    }


    /** {@inheritDoc} */
    @Override
    public void setConsumed ()
    {
        this.isConsumed = true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isConsumed ()
    {
        return this.isConsumed;
    }


    /** {@inheritDoc} */
    @Override
    public void trigger (final ButtonEvent event)
    {
        this.trigger (event, 1.0);
    }


    /** {@inheritDoc} */
    @Override
    public void trigger (final ButtonEvent event, final double velocity)
    {
        if (event == ButtonEvent.DOWN)
            this.handleButtonPressed (velocity);
        else if (event == ButtonEvent.UP)
            this.handleButtonRelease ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPressedVelocity ()
    {
        return this.pressedVelocity;
    }


    /** {@inheritDoc} */
    @Override
    public TriggerCommand getCommand ()
    {
        return this.command;
    }


    /**
     * If the state of the given button is still down, the state is set to long and an event gets
     * fired.
     */
    private void checkButtonState ()
    {
        if (!this.isPressed ())
            return;
        this.state = ButtonEvent.LONG;

        if (this.command != null)
            this.command.execute (ButtonEvent.LONG, this.pressedVelocity);
    }
}
