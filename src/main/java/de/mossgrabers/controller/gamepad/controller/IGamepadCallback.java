// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.gamepad.controller;

import de.mossgrabers.framework.utils.ButtonEvent;

import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;


/**
 * Interface for a callback when data from a Gamepad controller is received.
 *
 * @author Jürgen Moßgraber
 */
public interface IGamepadCallback
{
    /**
     * Called when ready to process the results.
     *
     * @param button The pressed or released gamepad button
     * @param event The button event
     */
    void process (ControllerButton button, ButtonEvent event);


    /**
     * Called when ready to process the results.
     *
     * @param continuous The continuous axis controller on the gamepad
     * @param value The new value
     */
    void process (ControllerAxis continuous, float value);
}
