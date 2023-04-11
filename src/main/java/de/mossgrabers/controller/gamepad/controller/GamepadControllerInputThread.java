// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.gamepad.controller;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.utils.ButtonEvent;

import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;
import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerUnpluggedException;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Thread to monitor the input coming from a Gamepad.
 *
 * @author Jürgen Moßgraber
 */
public class GamepadControllerInputThread implements Runnable
{
    private static final long                    INTERVAL           = 1;

    private final AtomicBoolean                  running            = new AtomicBoolean (false);

    private final IHost                          host;
    private final ControllerManager              gamepadManager;
    private final IGamepadCallback               gamepadCallback;
    private final Map<ControllerButton, Boolean> buttonStates       = new EnumMap<> (ControllerButton.class);
    private final Map<ControllerAxis, Float>     axisStates         = new EnumMap<> (ControllerAxis.class);

    private int                                  selectedController = -1;


    /**
     * Constructor.
     *
     * @param host The controller host
     * @param gamepadManager The manager for connected gamepads
     * @param gamepadCallback Callback for events coming from the selected gamepad
     */
    public GamepadControllerInputThread (final IHost host, final ControllerManager gamepadManager, final IGamepadCallback gamepadCallback)
    {
        this.host = host;
        this.gamepadManager = gamepadManager;
        this.gamepadCallback = gamepadCallback;
    }


    /**
     * Start the monitoring thread.
     */
    public void start ()
    {
        new Thread (this).start ();
    }


    /**
     * Stop the monitoring thread.
     */
    public void stop ()
    {
        this.running.set (false);
    }


    /**
     * Select the controller at the given index.
     *
     * @param controllerIndex The index of the controller to select
     */
    public void selectController (final int controllerIndex)
    {
        synchronized (this.gamepadManager)
        {
            if (this.selectedController >= 0)
            {
                final ControllerIndex gamepad = this.gamepadManager.getControllerIndex (this.selectedController);
                if (gamepad.isConnected ())
                    gamepad.close ();
            }

            this.selectedController = controllerIndex;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void run ()
    {
        this.running.set (true);

        try
        {
            while (this.running.get ())
            {
                // Hand over to other running threads
                try
                {
                    Thread.sleep (INTERVAL);
                }
                catch (final InterruptedException ex)
                {
                    Thread.currentThread ().interrupt ();
                }

                if (!this.running.get ())
                    break;

                synchronized (this.gamepadManager)
                {
                    if (this.selectedController < 0 || this.selectedController >= this.gamepadManager.getNumControllers ())
                        continue;

                    final ControllerIndex currController = this.gamepadManager.getControllerIndex (this.selectedController);
                    if (!currController.isConnected ())
                    {
                        this.gamepadManager.update ();
                        if (this.selectedController >= 0 && this.selectedController < this.gamepadManager.getNumControllers ())
                            currController.reconnectController ();
                        continue;
                    }

                    try
                    {
                        for (final ControllerButton button: ControllerButton.values ())
                        {
                            final boolean isPressed = currController.isButtonPressed (button);
                            final Boolean state = this.buttonStates.getOrDefault (button, Boolean.FALSE);
                            if (state.booleanValue () == isPressed)
                                continue;

                            this.buttonStates.put (button, Boolean.valueOf (isPressed));
                            this.host.scheduleTask ( () -> this.gamepadCallback.process (button, isPressed ? ButtonEvent.DOWN : ButtonEvent.UP), 0);
                        }

                        for (final ControllerAxis axis: ControllerAxis.values ())
                        {
                            final float position = currController.getAxisState (axis);
                            final Float state = this.axisStates.getOrDefault (axis, Float.valueOf (0));
                            if (state.floatValue () == position)
                                continue;

                            this.axisStates.put (axis, Float.valueOf (position));
                            this.host.scheduleTask ( () -> this.gamepadCallback.process (axis, position), 0);
                        }
                    }
                    catch (final ControllerUnpluggedException ex)
                    {
                        this.host.error ("Controller not connected.");
                    }
                }
            }
        }
        catch (final RuntimeException ex)
        {
            this.host.error ("Controller error.", ex);
        }
    }
}
