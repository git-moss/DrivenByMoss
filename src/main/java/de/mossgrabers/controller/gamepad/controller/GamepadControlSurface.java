// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.gamepad.controller;

import de.mossgrabers.controller.gamepad.GamepadConfiguration;
import de.mossgrabers.controller.gamepad.GamepadFunctionHandler;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.IMidiInput;

import com.studiohartman.jamepad.ControllerManager;


/**
 * The Gamepad.
 *
 * @author Jürgen Moßgraber
 */
public class GamepadControlSurface extends AbstractControlSurface<GamepadConfiguration>
{
    private final GamepadControllerInputThread gameControllerInputThread;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param input The MIDI input
     * @param gamepadManager The gamepad manager to get access to the available gamepads
     * @param model The model
     */
    public GamepadControlSurface (final IHost host, final GamepadConfiguration configuration, final ColorManager colorManager, final IMidiInput input, final ControllerManager gamepadManager, final IModel model)
    {
        super (host, configuration, colorManager, null, input, null, 10, 10);

        this.gameControllerInputThread = new GamepadControllerInputThread (host, gamepadManager, new GamepadFunctionHandler (this, model));
        this.gameControllerInputThread.start ();
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        this.gameControllerInputThread.stop ();
    }


    /**
     * Select the controller at the given index.
     *
     * @param selectedGamepad The index of the controller to select
     */
    public void selectGamepad (final int selectedGamepad)
    {
        this.gameControllerInputThread.selectController (selectedGamepad);
    }
}
