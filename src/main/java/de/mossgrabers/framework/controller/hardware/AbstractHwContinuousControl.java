// Written by Jürgen Moßgraber - mossgrabers.de
// protected c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.command.core.PitchbendCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;


/**
 * A control on a hardware controller.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractHwContinuousControl extends AbstractHwInputControl implements IHwContinuousControl
{
    private static final int    BUTTON_STATE_INTERVAL = 400;

    protected ContinuousCommand command;
    protected TriggerCommand    touchCommand;
    protected PitchbendCommand  pitchbendCommand;

    protected ButtonEvent       state;
    protected IntSupplier       supplier;
    protected IntConsumer       consumer;
    protected int               outputValue           = -1;


    /**
     * Constructor.
     *
     * @param host The host
     * @param label The label of the control
     */
    protected AbstractHwContinuousControl (final IHost host, final String label)
    {
        super (host, label);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final ContinuousCommand command)
    {
        this.command = command;
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final PitchbendCommand command)
    {
        this.pitchbendCommand = command;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isBound ()
    {
        return this.command != null || this.touchCommand != null || this.pitchbendCommand != null;
    }


    /** {@inheritDoc} */
    @Override
    public ContinuousCommand getCommand ()
    {
        return this.command;
    }


    /** {@inheritDoc} */
    @Override
    public TriggerCommand getTouchCommand ()
    {
        return this.touchCommand;
    }


    /** {@inheritDoc} */
    @Override
    public PitchbendCommand getPitchbendCommand ()
    {
        return this.pitchbendCommand;
    }


    /** {@inheritDoc} */
    @Override
    public void triggerTouch (final boolean isDown)
    {
        if (this.touchCommand == null)
            return;

        this.host.scheduleTask (this::checkButtonState, BUTTON_STATE_INTERVAL);

        this.state = isDown ? ButtonEvent.DOWN : ButtonEvent.UP;
        this.touchCommand.execute (this.state, isDown ? 127 : 0);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTouched ()
    {
        return this.state == ButtonEvent.DOWN || this.state == ButtonEvent.LONG;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLongTouched ()
    {
        return this.state == ButtonEvent.LONG;
    }


    /** {@inheritDoc} */
    @Override
    public void addOutput (final IntSupplier supplier, final IntConsumer consumer)
    {
        this.supplier = supplier;
        this.consumer = consumer;
    }


    /** {@inheritDoc} */
    @Override
    public void forceFlush ()
    {
        this.outputValue = -1;
    }


    /** {@inheritDoc} */
    @Override
    public void turnOff ()
    {
        this.supplier = null;
        this.outputValue = -1;
        if (this.consumer != null)
            this.consumer.accept (0);
    }


    /** {@inheritDoc} */
    @Override
    public void update ()
    {
        if (this.supplier == null)
            return;

        final int value = this.supplier.getAsInt ();
        if (value == this.outputValue)
            return;
        this.outputValue = value;
        this.consumer.accept (this.outputValue);
    }


    /**
     * If the state of the given button is still down, the state is set to long and an event gets
     * fired.
     */
    private void checkButtonState ()
    {
        if (!this.isTouched ())
            return;
        this.state = ButtonEvent.LONG;
        if (this.touchCommand != null)
            this.touchCommand.execute (ButtonEvent.LONG, 127);
    }
}
